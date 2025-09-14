package com.androidapp.pizzamania;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerOrders;
    private List<OrderModel> orderList = new ArrayList<>();
    private OrderAdapter adapter;
    private DatabaseHelper dbHelper;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        dbHelper = new DatabaseHelper(this);
        orderRef = FirebaseDatabase.getInstance().getReference("Orders");

        loadOrders();
    }

    private void loadOrders() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch online
        orderRef.orderByChild("userId").equalTo(userId).get()
                .addOnSuccessListener(snapshot -> {
                    orderList.clear();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("OfflineOrders", null, null);

                    for (DataSnapshot doc : snapshot.getChildren()) {
                        String orderId = doc.getKey();
                        String status = doc.child("status").getValue(String.class);
                        Double price = doc.child("totalPrice").getValue(Double.class);
                        Long createdAt = doc.child("createdAt").getValue(Long.class);

                        OrderModel order = new OrderModel(orderId, status, price, String.valueOf(createdAt));
                        orderList.add(order);

                        // Save offline
                        ContentValues values = new ContentValues();
                        values.put("orderId", orderId);
                        values.put("userId", userId);
                        values.put("itemsJson", new Gson().toJson(doc.child("items").getValue()));
                        values.put("totalPrice", price);
                        values.put("status", status);
                        values.put("createdAt", createdAt.toString());
                        db.insert("OfflineOrders", null, values);
                    }
                    db.close();
                    showOrders();
                })
                .addOnFailureListener(e -> {
                    // Offline mode
                    orderList.clear();
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor cursor = db.query("OfflineOrders", null, null, null, null, null, null);

                    while (cursor.moveToNext()) {
                        String orderId = cursor.getString(cursor.getColumnIndexOrThrow("orderId"));
                        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                        double price = cursor.getDouble(cursor.getColumnIndexOrThrow("totalPrice"));
                        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"));

                        orderList.add(new OrderModel(orderId, status, price, createdAt));
                    }
                    cursor.close();
                    db.close();

                    showOrders();
                });
    }

    private void showOrders() {
        adapter = new OrderAdapter(orderList, order -> reorder(order));
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrders.setAdapter(adapter);
    }

    // Reorder feature
    private void reorder(OrderModel order) {
        Toast.makeText(this, "Reordered: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        // Here you can copy items back to Cart table if needed
    }
}
