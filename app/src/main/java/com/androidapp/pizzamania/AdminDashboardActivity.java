package com.androidapp.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvPendingOrders, tvLowStock, tvBranches;
    private Button btnManageBranches, btnManageMenu, btnManageStock, btnManageOrders, btnProfile, btnSignOut;

    DatabaseReference ordersRef, branchesRef;
    String testAdminId = "z2lDvFK5VOZ0rXWHcvHXgZg7NLD2"; // TEMP: use your admin UID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvPendingOrders = findViewById(R.id.tvPendingOrders);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvBranches = findViewById(R.id.tvBranches);

        btnManageBranches = findViewById(R.id.btnManageBranches);
        btnManageMenu = findViewById(R.id.btnManageMenu);
        btnManageStock = findViewById(R.id.btnManageStock);
        btnManageOrders = findViewById(R.id.btnManageOrders);
        btnProfile = findViewById(R.id.btnProfile);
        btnSignOut = findViewById(R.id.btnSignOut);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        branchesRef = FirebaseDatabase.getInstance().getReference("branches");

        loadStats();

        btnManageBranches.setOnClickListener(v -> startActivity(new Intent(this, BranchManagementActivity.class)));
        btnManageOrders.setOnClickListener(v -> startActivity(new Intent(this, OrderManagementActivity.class)));
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminProfileActivity.class);
            intent.putExtra("adminId", testAdminId);
            startActivity(intent);
        });
        btnSignOut.setOnClickListener(v -> finish());
    }

    private void loadStats() {
        // Pending Orders
        ordersRef.orderByChild("orderStatus").equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tvPendingOrders.setText("Pending\n" + snapshot.getChildrenCount());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Branch count
        branchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvBranches.setText("Branches\n" + snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Low stock (placeholder, until you add stock collection)
        tvLowStock.setText("Low Stock\n0");
   }
}
