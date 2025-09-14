package com.androidapp.pizzamania;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvPendingOrders, tvLowStock, tvBranches;
    private Button btnManageBranches, btnManageMenu, btnManageStock, btnManageOrders, btnProfile, btnSignOut;

    private DatabaseReference ordersRef, stockRef, branchesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Init views
        tvPendingOrders = findViewById(R.id.tvPendingOrders);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvBranches = findViewById(R.id.tvBranches);

        btnManageBranches = findViewById(R.id.btnManageBranches);
        btnManageMenu = findViewById(R.id.btnManageMenu);
        btnManageStock = findViewById(R.id.btnManageStock);
        btnManageOrders = findViewById(R.id.btnManageOrders);
        btnProfile = findViewById(R.id.btnProfile);
        btnSignOut = findViewById(R.id.btnSignOut);

        // Firebase references
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        ordersRef = rootRef.child("orders");
        stockRef = rootRef.child("BranchStock");
        branchesRef = rootRef.child("branches");

        // Button clicks
        btnManageBranches.setOnClickListener(v -> startActivity(new Intent(this, BranchManagementActivity.class)));
        btnManageMenu.setOnClickListener(v -> startActivity(new Intent(this, MenuManagementActivity.class)));
        btnManageStock.setOnClickListener(v -> startActivity(new Intent(this, StockManagementActivity.class)));
        btnManageOrders.setOnClickListener(v -> startActivity(new Intent(this, OrderManagementActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, AdminProfileActivity.class)));
        btnSignOut.setOnClickListener(v -> signOut());

        // Listen real-time stats
        listenPendingOrders();
        listenLowStockCount();
        listenBranchesCount();
    }

    private void listenPendingOrders() {
        ordersRef.orderByChild("status").equalTo("pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount();
                        tvPendingOrders.setText("Pending\n" + count);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void listenLowStockCount() {
        stockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int low = 0;
                for (DataSnapshot branchSnap : snapshot.getChildren()) {
                    for (DataSnapshot itemSnap : branchSnap.getChildren()) {
                        Long q = itemSnap.child("quantity").getValue(Long.class);
                        if (q != null && q < 5) low++;
                    }
                }
                tvLowStock.setText("Low Stock\n" + low);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void listenBranchesCount() {
        branchesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvBranches.setText("Branches\n" + snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_USER_SESSION, null, null);
        db.close();

        Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
