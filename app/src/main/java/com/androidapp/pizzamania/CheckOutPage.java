package com.androidapp.pizzamania;

import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CheckOutPage extends AppCompatActivity {

    private static final String TAG = "CheckoutPage";

    private RadioGroup radioGroup;
    private Button btnCheckOut;
    private LinearLayout cardDetails;

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

        radioGroup.setOnCheckedChangeListener(((group, checkedId) -> {
            cardDetails.setVisibility(checkedId == R.id.cpmt ? View.VISIBLE : View.GONE);
            String payMethod = checkedId == R.id.cpmt ? "Card" : "Cash on Delivery";
            Log.d(TAG, "Payment method Selected: " + payMethod);
            Toast.makeText(this, "Selected Method: " + payMethod, Toast.LENGTH_SHORT).show();
        }));

        btnCheckOut.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please Select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            String branchID = "branch39";
            double latitude = 6.9271;
            double longitude = 79.8612;

            String userId = "user69";

            double totalPrice = 6578;
            List<ItemDTO> itemDTOList = new ArrayList<>();
            itemDTOList.add(new ItemDTO("pizza1", 2, 50));
            itemDTOList.add(new ItemDTO("pizza2", 1, 150));
            itemDTOList.add(new ItemDTO("LavaCake1", 2, 20));

            double tot = itemDTOList.stream().mapToDouble(item ->
                    item.getQty() * item.getPrice()
            ).sum();

            Log.d(TAG, "Creating order: orderID=generated, userId" + userId + ", branchID" + branchID + ", totalPrice= " + totalPrice);

            OrderDTO.Location location = new OrderDTO.Location(latitude, longitude);
            String orderID = FirebaseDatabase.getInstance().getReference("orders").push().getKey();
            String createdAt = String.valueOf(System.currentTimeMillis());

            if (selectedId == R.id.cod) {
                OrderDTO orderDTO = new OrderDTO(orderID, userId, branchID, itemDTOList, totalPrice, "pending", createdAt, location);
                saveOrder(orderDTO);
            } else if (selectedId == R.id.cpmt) {

                EditText txtCardName = findViewById(R.id.txtCardName);
                EditText txtCardNumber = findViewById(R.id.txtCardNumber);
                EditText txtExpireDate = findViewById(R.id.txtExpireDate);
                EditText txtCvv = findViewById(R.id.txtCvv);

                String name = txtCardName.getText().toString().trim();
                String number = txtCardNumber.getText().toString().trim();
                String expiry = txtExpireDate.getText().toString().trim();
                String cvv = txtCvv.getText().toString().trim();

                Log.d(TAG, "Processing card payment: name=" + name + ", number=****" + number.substring(number.length() - 4));
                Toast.makeText(this, "Processing card payment...", Toast.LENGTH_SHORT).show();

                if(paymentGateway(name, number, expiry, cvv, totalPrice)){
                    OrderDTO order = new OrderDTO(orderID, userId, branchID, itemDTOList, totalPrice, "pending", createdAt, location);
                    saveOrder(order);
                }else{
                    Log.d(TAG, "Card payment failed: Invalid card details");
                    Toast.makeText(this, "Invalid card details or payment failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean paymentGateway(String name, String number, String expDate, String cvv, double totPrice) {

        if ((name.isEmpty()) || (number.length() != 16) || (cvv.length() != 3)) {
            Log.d(TAG, "Mock payment failed: Invalid input (name=" + name.isEmpty() + ", number=" + number.length() + ", cvv=" + cvv.length() + ")");
            Toast.makeText(this, "Invalid payment input", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidCardNumber(number)) {
            Log.d(TAG, "Mock payment failed: Invalid card number (Luhn check)");
            Toast.makeText(this, "Invalid card number", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {

            String[] expParts = expDate.split("/");
            if (expParts.length != 2) {
                Log.d(TAG, "Mock payment failed: Invalid expiry format");
                Toast.makeText(this, "Invalid expiry month", Toast.LENGTH_SHORT).show();
                return false;
            }

            int month = Integer.parseInt(expParts[0].trim());
            int year = Integer.parseInt(expParts[1].trim()) + 2000;

            if ((month < 1) || (month > 12)) {
                Log.d(TAG, "Mock payment failed: Invalid month=" + month);
                Toast.makeText(this, "Invalid expiry month", Toast.LENGTH_SHORT).show();
                return false;
            }

            YearMonth expiryDate = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                expiryDate = YearMonth.of(year, month);
            }
            YearMonth currentDate = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                currentDate = YearMonth.now();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (expiryDate.isBefore(currentDate)) {
                    Log.d(TAG, "Mock payment failed: Card expired");
                    Toast.makeText(this, "Card has expired", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "Mock payment failed: Expiry parsing error - " + e.getMessage());
            return false;
        }

        String lastDigit4 = number.substring(number.length() - 4);
        int intLast4;
        try {
            intLast4 = Integer.parseInt(lastDigit4);
        } catch (NumberFormatException e) {
            intLast4 = lastDigit4.hashCode();
        }

        double mockBalance = 100 + (intLast4 % 5000);
        Log.d(TAG, "Mock balance for card ****" + intLast4 + " is: " + mockBalance);

        if (totPrice > mockBalance){
            Log.d(TAG, "Mock payment failed: Insufficient funds. Required=" + totPrice + " balance=" + mockBalance);
            Toast.makeText(this, "Payment failed: insufficient funds", Toast.LENGTH_LONG).show();
            return false;
        }

        Log.d(TAG, "Mock payment succeeded");
        return true;
    }

    private boolean isValidCardNumber(String cardNumber) {
        int sum = 0;
        boolean alt = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alt) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alt = !alt;
        }

        return (sum % 10 == 0);
    }

    public void saveOrder(OrderDTO orderDTO) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders").child(orderDTO.getOrderID());
        reference.setValue(orderDTO)
                .addOnSuccessListener(successVoid -> {
                    Toast.makeText(this, "Order is successful", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(failedVoid -> {
                    Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
                });

        SqlLiteHelper helper = new SqlLiteHelper(this);
        helper.InsertOrder(orderDTO);

        Toast.makeText(this, "Order placed successfully with status: " + orderDTO.getOrderStatus(), Toast.LENGTH_SHORT).show();

    }
}