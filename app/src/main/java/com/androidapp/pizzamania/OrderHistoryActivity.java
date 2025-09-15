package com.androidapp.pizzamania;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private DatabaseHelper dbHelper;
    private List<OrderItem> orderList = new ArrayList<>();
    private OrderAdapter adapter;
    private DatabaseReference ordersRef;
    private DatabaseReference menuRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        dbHelper = new DatabaseHelper(this);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        menuRef = FirebaseDatabase.getInstance().getReference("MenuItems");

        adapter = new OrderAdapter(orderList, this::reorder);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrders.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        orderList.clear();

        // ⿡ Load offline orders from SQLite
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_OFFLINE_ORDERS,
                null, null, null, null, null, "createdAt DESC");

        Log.d(TAG, "Offline orders count: " + cursor.getCount());

        while (cursor.moveToNext()) {
            String orderId = cursor.getString(cursor.getColumnIndexOrThrow("orderId"));
            String userId = cursor.getString(cursor.getColumnIndexOrThrow("userId"));
            String itemsStr = cursor.getString(cursor.getColumnIndexOrThrow("items"));
            double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("totalPrice"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"));

            List<OrderItem.Item> itemsList = Utils.parseItemsString(itemsStr);

            orderList.add(new OrderItem(orderId, userId, null, itemsList, totalPrice, status, createdAt, null));
            Log.d(TAG, "Loaded offline order: " + orderId + " | items: " + (itemsList != null ? itemsList.size() : 0));
        }
        cursor.close();
        db.close();

        adapter.notifyDataSetChanged(); // update RecyclerView for offline orders

        // ⿢ Load online orders from Firebase
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // real user
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
                            adapter.notifyDataSetChanged(); // update RecyclerView for online orders
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(OrderHistoryActivity.this,
                                    "Failed to load online orders.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Firebase error: " + error.getMessage());
                        }
                    });
        }
    }

    // Reorder: clears cart and adds items from selected order
    private void reorder(OrderItem order) {
        if (order.getItemList() == null || order.getItemList().isEmpty()) {
            Toast.makeText(this, "No items to reorder!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_CART, null, null); // clear previous cart

        for (OrderItem.Item item : order.getItemList()) {
            String itemId = item.getItemID();
            int qty = item.getQty();

            menuRef.child(itemId).get().addOnSuccessListener(snapshot -> {
                String name = snapshot.child("name").getValue(String.class);
                Double price = snapshot.child("price").getValue(Double.class);

                if (name != null && price != null) {
                    db.execSQL("INSERT INTO Cart(itemId, name, price, quantity) VALUES(?,?,?,?)",
                            new Object[]{itemId, name, price, qty});
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to fetch product: " + itemId, Toast.LENGTH_SHORT).show();
            });
        }

        db.close();
        Toast.makeText(this, "Reorder items added to cart!", Toast.LENGTH_SHORT).show();
    }
}
