package com.androidapp.pizzamania;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class StockManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerStock;
    private StockAdapter adapter;
    private List<StockModel> stockList = new ArrayList<>();
    private List<StockModel> fullStockList = new ArrayList<>(); // For search filtering
    private DatabaseHelper dbHelper;
    private DatabaseReference stockRef;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_management);

        recyclerStock = findViewById(R.id.recyclerStock);
        etSearch = findViewById(R.id.etSearch); // Add this EditText in XML
        dbHelper = new DatabaseHelper(this);
        stockRef = FirebaseDatabase.getInstance().getReference("BranchStock").child("branch1");

        loadStock();

        // Search/filter functionality
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStock(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) { }
        });
    }

    private void loadStock() {
        stockRef.get().addOnSuccessListener(snapshot -> {
            stockList.clear();
            fullStockList.clear();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("Cart", null, null); // Optional caching
            db.close();

            for (DataSnapshot itemSnap : snapshot.getChildren()) {
                String itemId = itemSnap.getKey();
                String name = itemSnap.child("name").getValue(String.class);
                int qty = itemSnap.child("quantity").getValue(Integer.class);

                StockModel item = new StockModel(itemId, name, qty);
                stockList.add(item);
                fullStockList.add(item);
            }

            showStock();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Offline mode: showing cached stock", Toast.LENGTH_SHORT).show();
            // TODO: load from SQLite if needed
        });
    }

    private void showStock() {
        adapter = new StockAdapter(stockList, (item, newQty) -> updateStock(item, newQty));
        recyclerStock.setLayoutManager(new LinearLayoutManager(this));
        recyclerStock.setAdapter(adapter);
    }

    private void updateStock(StockModel item, int newQty) {
        stockRef.child(item.getItemId()).child("quantity").setValue(newQty)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Updated stock", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed (offline)", Toast.LENGTH_SHORT).show());
    }

    private void filterStock(String query) {
        stockList.clear();
        if (query.isEmpty()) {
            stockList.addAll(fullStockList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (StockModel item : fullStockList) {
                if (item.getName().toLowerCase().contains(lowerQuery)) {
                    stockList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
