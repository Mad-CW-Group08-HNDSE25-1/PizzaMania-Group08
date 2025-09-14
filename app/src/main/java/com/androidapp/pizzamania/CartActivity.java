package com.androidapp.pizzamania;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String itemId = cursor.getString(cursor.getColumnIndexOrThrow("itemId"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

            cartList.add(new CartItem(id, itemId, name, price, qty));
        }
        cursor.close();
        db.close();

        adapter = new CartAdapter(cartList, this::updateTotal);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerCart.setAdapter(adapter);

        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getPrice() * item.getQuantity();
        }
        tvTotal.setText("Total: Rs. " + total);
    }


    private void placeOrder() {
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = orderRef.push().getKey();
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        Map<String, Object> items = new HashMap<>();
        double totalPrice = 0;
        for (CartItem item : cartList) {
            items.put(item.getItemId(), item.getQuantity());
            totalPrice += item.getPrice() * item.getQuantity();
        }
        orderMap.put("items", items);
        orderMap.put("totalPrice", totalPrice);
        orderMap.put("status", "pending");
        orderMap.put("createdAt", System.currentTimeMillis());

        if (orderId != null) {
            orderRef.child(orderId).setValue(orderMap)
                    .addOnSuccessListener(a -> {
                        clearCart();
                        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Order failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void clearCart() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Cart", null, null);
        db.close();
        loadCart();
    }
}
