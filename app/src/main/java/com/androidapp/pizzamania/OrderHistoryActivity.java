package com.androidapp.pizzamania;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private static final String TAG = "OrderHistoryActivity";

    private RecyclerView recyclerOrders;
    private SqlLiteHelper dbHelper;
    private final List<OrderItem> orderList = new ArrayList<>();
    private OrderAdapter adapter;
    private DatabaseReference ordersRef;
    private DatabaseReference menuRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        dbHelper = new SqlLiteHelper(this);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        menuRef = FirebaseDatabase.getInstance().getReference("MenuItems");

        adapter = new OrderAdapter(orderList, this::reorder);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrders.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        orderList.clear();

        String uid = "-OZnUrNQXlTG5QcMsU5R";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }


        if (uid != null) {
            List<OrderDTO> offlineOrders = dbHelper.getOrderByUser(uid);
            Log.d(TAG, "Offline orders count: " + offlineOrders.size());

            for (OrderDTO dto : offlineOrders) {
                List<OrderItem.Item> itemsList = new ArrayList<>();
                if (dto.getItemList() != null) {
                    for (ItemDTO itemDTO : dto.getItemList()) {
                        itemsList.add(new OrderItem.Item(
                                itemDTO.getItemID(),
                                itemDTO.getQty(),
                                itemDTO.getPrice()
                        ));
                    }
                }

                OrderDTO.Location dtoLoc = dto.getLocation();
                OrderItem.Location itemLoc = null;
                if (dtoLoc != null) {
                    itemLoc = new OrderItem.Location(dtoLoc.getLatitude(), dtoLoc.getLongitude());
                }

                orderList.add(new OrderItem(
                        dto.getOrderID(),
                        dto.getUserID(),
                        dto.getBranchID(),
                        itemsList,
                        dto.getTotalAmount(),
                        dto.getOrderStatus(),
                        dto.getCreatedAt(),
                        itemLoc
                ));
            }
            adapter.notifyDataSetChanged();
        }

        if (uid != null) {
            ordersRef.orderByChild("userID").equalTo(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d(TAG, "Firebase orders count: " + snapshot.getChildrenCount());

                            for (DataSnapshot snap : snapshot.getChildren()) {
                                OrderItem order = snap.getValue(OrderItem.class);
                                if (order != null) {
                                    orderList.add(order);
                                    Log.d(TAG, "Loaded Firebase order: " + order.getOrderID() +
                                            " | items: " + (order.getItemList() != null ? order.getItemList().size() : 0));
                                } else {
                                    Log.d(TAG, "Firebase order is null!");
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(OrderHistoryActivity.this,
                                    "Failed to load online orders.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Firebase error: " + error.getMessage());
                        }
                    });
        } else {
            Log.w(TAG, "No logged-in user — skipping Firebase order load");
        }
    }

    private void reorder(OrderItem order) {
        if (order.getItemList() == null || order.getItemList().isEmpty()) {
            Toast.makeText(this, "No items to reorder!", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.getWritableDatabase().delete(DatabaseHelper.TABLE_CART, null, null);

        for (OrderItem.Item item : order.getItemList()) {
            String itemId = item.getItemID();
            int qty = item.getQty();

            menuRef.child(itemId).get().addOnSuccessListener(snapshot -> {
                String name = snapshot.child("name").getValue(String.class);
                Double price = snapshot.child("price").getValue(Double.class);

                if (name != null && price != null) {
                    dbHelper.getWritableDatabase().execSQL(
                            "INSERT INTO Cart(itemId, name, price, quantity) VALUES(?,?,?,?)",
                            new Object[]{itemId, name, price, qty});
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to fetch product: " + itemId, Toast.LENGTH_SHORT).show();
            });
        }

        Toast.makeText(this, "Reorder items added to cart!", Toast.LENGTH_SHORT).show();
    }
}