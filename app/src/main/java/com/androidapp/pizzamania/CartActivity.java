package com.androidapp.pizzamania;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCart;
    private TextView tvTotal;
    private Button btnCheckout;
    private DatabaseHelper dbHelper;
    private List<CartItem> cartList = new ArrayList<>();
    private CartAdapter adapter;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerCart = findViewById(R.id.recyclerCart);
        tvTotal = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);

        dbHelper = new DatabaseHelper(this);
        orderRef = FirebaseDatabase.getInstance().getReference("orders");

        // Handle Intent extras from addToCartBtn
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String itemId = intent.getStringExtra("itemId");
            String itemName = intent.getStringExtra("itemName");
            double price = intent.getDoubleExtra("total", 0.0);
            int qty = intent.getIntExtra("qty", 1);

            if (itemId != null && itemName != null && price > 0 && qty > 0) {
                addOrUpdateCartItem(itemId, itemName, price, qty);
            }
        }

        loadCart();

        btnCheckout.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prepare data to pass to CheckOutPage
            ArrayList<String> itemIds = new ArrayList<>();
            ArrayList<String> itemNames = new ArrayList<>();
            ArrayList<Integer> itemQtys = new ArrayList<>();
            ArrayList<Double> itemPrices = new ArrayList<>();
            double totalAmount = 0;

            for (CartItem item : cartList) {
                itemIds.add(item.getItemId());
                itemNames.add(item.getName());
                itemQtys.add(item.getQuantity());
                itemPrices.add(item.getPrice());
                totalAmount += item.getTotalPrice();
            }

            // Pass data via Intent
            Intent intent2 = new Intent(this, CheckOutPage.class);
            intent2.putStringArrayListExtra("itemIds", itemIds);
            intent2.putStringArrayListExtra("itemNames", itemNames);
            intent2.putIntegerArrayListExtra("itemQtys", itemQtys);
            intent2.putExtra("totalAmount", totalAmount);
            startActivity(intent2);
        });

    }

    private void addOrUpdateCartItem(String itemId, String name, double price, int qty) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Check if item already exists
        Cursor cursor = db.query(DatabaseHelper.TABLE_CART, new String[]{"quantity"}, "itemId=?", new String[]{itemId}, null, null, null);
        if (cursor.moveToFirst()) {
            // Item exists, update quantity
            int existingQty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            values.put("quantity", existingQty + qty);
            db.update(DatabaseHelper.TABLE_CART, values, "itemId=?", new String[]{itemId});
        } else {
            // Item doesn't exist, insert new item
            values.put("itemId", itemId);
            values.put("name", name);
            values.put("price", price);
            values.put("quantity", qty);
            db.insert(DatabaseHelper.TABLE_CART, null, values);
        }
        cursor.close();
        db.close();
    }

    private void loadCart() {
        cartList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CART, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String itemId = cursor.getString(cursor.getColumnIndexOrThrow("itemId"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

            cartList.add(new CartItem(itemId, name, price, qty));
        }
        cursor.close();
        db.close();

        adapter = new CartAdapter(this, cartList, this::updateTotal);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerCart.setAdapter(adapter);

        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getTotalPrice();
        }
        tvTotal.setText(String.format("Total: Rs. %.2f", total));
    }

    private void placeOrder() {
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = orderRef.push().getKey(); // unique order id
        if (orderId == null) {
            Toast.makeText(this, "Failed to generate order ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        orderMap.put("status", "pending");
        orderMap.put("createdAt", System.currentTimeMillis());

        List<Map<String, Object>> itemList = new ArrayList<>();
        double totalAmount = 0;

        for (CartItem item : cartList) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("itemID", item.getItemId());
            itemMap.put("name", item.getName());
            itemMap.put("price", item.getPrice());
            itemMap.put("qty", item.getQuantity());
            itemMap.put("totalPerItem", item.getTotalPrice());

            totalAmount += item.getTotalPrice();
            itemList.add(itemMap);
        }

        orderMap.put("itemList", itemList);
        orderMap.put("totalAmount", totalAmount);
        orderMap.put("orderID", orderId);

        orderRef.child(orderId).setValue(orderMap)
                .addOnSuccessListener(a -> {
                    clearCart();
                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close CartActivity after successful order
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Order failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearCart() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_CART, null, null);
        db.close();
        loadCart();
    }
}