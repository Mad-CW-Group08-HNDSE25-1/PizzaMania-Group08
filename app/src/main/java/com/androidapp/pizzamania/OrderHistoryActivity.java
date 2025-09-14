package com.androidapp.pizzamania;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders;
    private DatabaseHelper dbHelper;
    private List<OrderItem> orderList = new ArrayList<>();
    private OrderAdapter adapter;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        dbHelper = new DatabaseHelper(this);
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        adapter = new OrderAdapter(orderList, this::reorder);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrders.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        orderList.clear();

        // 1️⃣ Load offline cached orders
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_OFFLINE_ORDERS, null, null, null, null, null, "createdAt DESC");
        while(cursor.moveToNext()){
            String orderId = cursor.getString(cursor.getColumnIndexOrThrow("orderId"));
            String userId = cursor.getString(cursor.getColumnIndexOrThrow("userId"));
            String itemsStr = cursor.getString(cursor.getColumnIndexOrThrow("items"));
            double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("totalPrice"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("createdAt"));

            // Convert itemsStr to Map<String, Integer>
            Map<String, Integer> itemsMap = new HashMap<>();
            String[] pairs = itemsStr.split(",");
            for(String pair : pairs){
                if(pair.contains(":")){
                    String[] kv = pair.split(":");
                    itemsMap.put(kv[0], Integer.parseInt(kv[1]));
                }
            }
            orderList.add(new OrderItem(orderId, userId, itemsMap, totalPrice, status, createdAt));
        }
        cursor.close();
        db.close();

        // 2️⃣ Load online orders from Firebase
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ordersRef.orderByChild("userId").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for(DataSnapshot snap : snapshot.getChildren()){
                            OrderItem order = snap.getValue(OrderItem.class);
                            if(order != null) orderList.add(order);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(OrderHistoryActivity.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
                    }
                });

        adapter.notifyDataSetChanged();
    }

    private void reorder(OrderItem order){
        if(order.getItems() == null || order.getItems().isEmpty()){
            Toast.makeText(this, "No items to reorder!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_CART, null, null);

        for(Map.Entry<String, Integer> entry : order.getItems().entrySet()){
            String itemId = entry.getKey();
            int qty = entry.getValue();
            // For simplicity we use itemId as name placeholder and price=100
            db.execSQL("INSERT INTO Cart(itemId, name, price, quantity) VALUES(?,?,?,?)",
                    new Object[]{itemId, itemId, 100, qty});
        }
        db.close();

        Toast.makeText(this, "Items added to cart for reorder!", Toast.LENGTH_SHORT).show();
    }
}
