package com.androidapp.pizzamania;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    private EditText etSearch;
    private RecyclerView recyclerStock;
    private StockAdapter adapter;
    private List<StockItem> stockList = new ArrayList<>();
    private DatabaseReference stockRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_management);

        etSearch = findViewById(R.id.etSearch);
        recyclerStock = findViewById(R.id.recyclerStock);

        stockRef = FirebaseDatabase.getInstance().getReference("stock");

        loadStock();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStock(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void loadStock() {
        stockList.clear();
        stockRef.get().addOnSuccessListener(snapshot -> {
            for (DataSnapshot branchSnap : snapshot.getChildren()) {
                String branchName = branchSnap.getKey();
                for (DataSnapshot itemSnap : branchSnap.getChildren()) {
                    String itemId = itemSnap.getKey();
                    String itemName = itemSnap.child("name").getValue(String.class);
                    int quantity = itemSnap.child("quantity").getValue(Integer.class);

                    stockList.add(new StockItem(itemId, itemName, branchName, quantity));
                }
            }
            adapter = new StockAdapter(stockList, this::updateStock);
            recyclerStock.setLayoutManager(new LinearLayoutManager(this));
            recyclerStock.setAdapter(adapter);
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load stock: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateStock(StockItem item, int newQuantity) {
        stockRef.child(item.getBranchName())
                .child(item.getItemId())
                .child("quantity")
                .setValue(newQuantity)
                .addOnSuccessListener(a -> Toast.makeText(this, "Stock updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void filterStock(String query) {
        List<StockItem> filteredList = new ArrayList<>();
        for (StockItem item : stockList) {
            if (item.getName().toLowerCase().contains(query.toLowerCase()) ||
                    item.getBranchName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }
}
