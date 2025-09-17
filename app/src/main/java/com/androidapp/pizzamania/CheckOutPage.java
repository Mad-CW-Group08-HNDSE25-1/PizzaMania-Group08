package com.androidapp.pizzamania;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CheckOutPage extends AppCompatActivity {

    private static final String TAG = "CheckoutPage";
    private static final int REQ_LOCATION = 1001;
    private RadioGroup radioGroup;
    private Button btnCheckOut;
    private LinearLayout cardDetails;
    private List<ItemDTO> itemDTOList;
    private double totalAmount;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        radioGroup = findViewById(R.id.paymentOptions);
        btnCheckOut = findViewById(R.id.btnPlaceOrder);
        cardDetails = findViewById(R.id.cardDetails);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ArrayList<String> itemIds = getIntent().getStringArrayListExtra("itemIds");
        ArrayList<Integer> itemQtys = getIntent().getIntegerArrayListExtra("itemQtys");
        ArrayList<Double> itemPrices = (ArrayList<Double>) getIntent().getSerializableExtra("itemPrices");
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);

        itemDTOList = new ArrayList<>();
        if (itemIds != null && itemQtys != null && itemPrices != null) {
            for (int i = 0; i < itemIds.size(); i++) {
                itemDTOList.add(new ItemDTO(itemIds.get(i), itemQtys.get(i), itemPrices.get(i)));
            }
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            cardDetails.setVisibility(checkedId == R.id.cpmt ? View.VISIBLE : View.GONE);
            String payMethod = checkedId == R.id.cpmt ? "Card" : "Cash on Delivery";
            Log.d(TAG, "Payment method Selected: " + payMethod);
            Toast.makeText(this, "Selected Method: " + payMethod, Toast.LENGTH_SHORT).show();
        });

        btnCheckOut.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            getUserLocationAndProceed();
        });
    }

    private void getUserLocationAndProceed() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, (OnSuccessListener<Location>) location -> {
            if (location == null) {
                Toast.makeText(this, "Unable to get current location. Please enable location services.", Toast.LENGTH_LONG).show();
                return;
            }
            double userLat = location.getLatitude();
            double userLng = location.getLongitude();
            findNearestBranchAndPlaceOrder(userLat, userLng);
        });
    }

    private void findNearestBranchAndPlaceOrder(double userLat, double userLng) {
        DatabaseReference branchRef = FirebaseDatabase.getInstance().getReference("branches");
        branchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Branch nearest = null;
                double minDistance = Double.MAX_VALUE;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (snap == null) continue;

                    String key = snap.child("key").getValue(String.class);
                    String bName = snap.child("branchName").getValue(String.class);
                    Double lat = snap.child("latitude").getValue(Double.class);
                    Double lng = snap.child("longitude").getValue(Double.class);
                    if (lat == null || lng == null) continue;
                    double d = distanceKm(userLat, userLng, lat, lng);
                    if (d < minDistance) {
                        minDistance = d;
                        nearest = new Branch(key, bName, lat, lng);
                    }
                }
                if (nearest == null || minDistance > 25.0) {
                    Toast.makeText(CheckOutPage.this, "No nearby branches available within 25 km", Toast.LENGTH_LONG).show();
                    return;
                }
                proceedWithOrderForBranch(nearest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckOutPage.this, "Failed to fetch branches: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedWithOrderForBranch(Branch branch) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getUid();
        String branchID = branch.getKey();
        OrderDTO.Location location = new OrderDTO.Location(branch.getLat(), branch.getLng()); // store branch loc or user's loc as needed
        String orderID = FirebaseDatabase.getInstance().getReference("orders").push().getKey();
        String createdAt = String.valueOf(System.currentTimeMillis());

        if (selectedId == R.id.cod) {
            OrderDTO orderDTO = new OrderDTO(orderID, userId, branchID, itemDTOList, totalAmount, "pending", createdAt, location);
            saveOrder(orderDTO, branch.getBranchName());
        } else if (selectedId == R.id.cpmt) {
            EditText txtCardName = findViewById(R.id.txtCardName);
            EditText txtCardNumber = findViewById(R.id.txtCardNumber);
            EditText txtExpireDate = findViewById(R.id.txtExpireDate);
            EditText txtCvv = findViewById(R.id.txtCvv);

            String name = txtCardName.getText().toString().trim();
            String number = txtCardNumber.getText().toString().trim();
            String expiry = txtExpireDate.getText().toString().trim();
            String cvv = txtCvv.getText().toString().trim();


            if (paymentGateway(name, number, expiry, cvv, totalAmount)) {
                OrderDTO orderDTO = new OrderDTO(orderID, userId, branchID, itemDTOList, totalAmount, "pending", createdAt, location);
                saveOrder(orderDTO, branch.getBranchName());
            } else {
                Toast.makeText(this, "Invalid card details or payment failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocationAndProceed();
            } else {
                Toast.makeText(this, "Location permission is required to find nearest branch", Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private boolean paymentGateway(String name, String number, String expDate, String cvv, double totPrice) {
        if ((name.isEmpty()) || (number.length() != 16) || (cvv.length() != 3)) {
            Toast.makeText(this, "Invalid payment input", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidCardNumber(number)) {
            Toast.makeText(this, "Invalid card number", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            String[] expParts = expDate.split("/");
            if (expParts.length != 2) return false;
            int month = Integer.parseInt(expParts[0].trim());
            int year = Integer.parseInt(expParts[1].trim()) + 2000;
            if ((month < 1) || (month > 12)) return false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                YearMonth expiryDate = YearMonth.of(year, month);
                YearMonth currentDate = YearMonth.now();
                if (expiryDate.isBefore(currentDate)) return false;
            }
        } catch (Exception e) {
            return false;
        }
        String lastDigit4 = number.substring(number.length() - 4);
        int intLast4;
        try { intLast4 = Integer.parseInt(lastDigit4); } catch (NumberFormatException e) { intLast4 = lastDigit4.hashCode(); }
        double mockBalance = 100 + (intLast4 % 5000);
        if (totPrice > mockBalance) {
            Toast.makeText(this, "Payment failed: insufficient funds", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isValidCardNumber(String cardNumber) {
        int sum = 0;
        boolean alt = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alt) { n *= 2; if (n > 9) n -= 9; }
            sum += n;
            alt = !alt;
        }
        return (sum % 10 == 0);
    }

    public void saveOrder(OrderDTO orderDTO, String branchName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders").child(orderDTO.getOrderID());
        reference.setValue(orderDTO)
                .addOnSuccessListener(successVoid -> {
                    saveOrderLocally(orderDTO);
                    Toast.makeText(this, "Order placed successfully at branch: " + branchName, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(CheckOutPage.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(failedVoid -> Toast.makeText(this, "Failed to save online", Toast.LENGTH_SHORT).show());
    }

    private void saveOrderLocally(OrderDTO orderDTO) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("orderId", orderDTO.getOrderID());
        values.put("userId", orderDTO.getUserID());
        values.put("itemsJson", new Gson().toJson(orderDTO.getItemList()));
        values.put("totalPrice", orderDTO.getTotalAmount());
        values.put("status", orderDTO.getOrderStatus());
        values.put("createdAt", orderDTO.getCreatedAt());

        long rowId = db.insert(DatabaseHelper.TABLE_OFFLINE_ORDERS, null, values);
        db.close();

        if (rowId != -1) Log.d(TAG, "Order saved locally");
        else Log.d(TAG, "Failed to save locally");
    }


    public static class Branch {
        private String key;
        private String branchName;
        private Double latitude;
        private Double longitude;

        public Branch() {}

        public Branch(String key, String branchName, Double latitude, Double longitude) {
            this.key = key;
            this.branchName = branchName;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getKey() { return key; }
        public String getBranchName() { return branchName; }
        public double getLat() { return latitude == null ? 0.0 : latitude; }
        public double getLng() { return longitude == null ? 0.0 : longitude; }
    }
}
