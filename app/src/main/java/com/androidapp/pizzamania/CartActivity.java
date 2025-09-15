package com.androidapp.pizzamania;

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

        loadCart();

        btnCheckout.setOnClickListener(v -> placeOrder());
    }

    private void loadCart() {
        cartList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Cart", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String itemId = cursor.getString(cursor.getColumnIndexOrThrow("itemId"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

            cartList.add(new CartItem(itemId, name, price, qty));
        }
        cursor.close();
        db.close();

        adapter = new CartAdapter(this,cartList, this::updateTotal);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerCart.setAdapter(adapter);

        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getTotalPrice();
        }
        tvTotal.setText("Total: Rs. " + total);
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
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Order failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearCart() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Cart", null, null);
        db.close();
        loadCart();
    }
}
