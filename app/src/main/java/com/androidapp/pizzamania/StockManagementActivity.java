package com.androidapp.pizzamania;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerStock;
    private StockAdapter adapter;
    private List<StockItem> stockList = new ArrayList<>();
    private Map<String, BranchesDTO> branchMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_management);

        recyclerStock = findViewById(R.id.recyclerStock);
        recyclerStock.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StockAdapter(this, stockList, branchMap);
        recyclerStock.setAdapter(adapter);

        fetchBranches();
    }

    private void fetchBranches() {
        DatabaseReference branchRef = FirebaseDatabase.getInstance().getReference("branches");
        branchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                branchMap.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    BranchesDTO branch = snap.getValue(BranchesDTO.class);
                    if (branch != null) branchMap.put(snap.getKey(), branch);
                }
                fetchStock();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StockManagementActivity.this, "Failed to load branches", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStock() {
        DatabaseReference stockRef = FirebaseDatabase.getInstance().getReference("BranchStock");
        stockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stockList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    StockItem stock = snap.getValue(StockItem.class);
                    if (stock != null) {
                        stock.setStockId(snap.getKey());
                        stockList.add(stock);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StockManagementActivity.this, "Failed to fetch stock", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

