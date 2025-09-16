package com.androidapp.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private Button btnMenu, btnStock, btnOrders, btnProfile, btnSignOut;
    private Button btnAddAdmin, btnViewAdmins, btnManageBranches;

    private DatabaseReference ordersRef, stockRef, branchesRef, usersRef;
    private String currentUserId, currentUserRole = "admin"; // default admin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Firebase refs
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ordersRef = database.getReference("Orders");
        stockRef = database.getReference("BranchStock");
        branchesRef = database.getReference("Branches");
        usersRef = database.getReference("Users");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // UI
        tvPendingOrders = findViewById(R.id.tvPendingOrders);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvBranches = findViewById(R.id.tvBranches);

        btnMenu = findViewById(R.id.btnManageMenu);
        btnStock = findViewById(R.id.btnManageStock);
        btnOrders = findViewById(R.id.btnManageOrders);
        btnProfile = findViewById(R.id.btnProfile);
        btnSignOut = findViewById(R.id.btnSignOut);

//        loadDashboardStats();
//        setupRoleBasedVisibility();
//        setupButtonClicks();

        // Load user role only if logged in

        //loadUserRole();

        //loadUserRole();

        btnAddAdmin = findViewById(R.id.btnAddAdmin);
        btnViewAdmins = findViewById(R.id.btnAdminList);
        btnManageBranches = findViewById(R.id.btnManageBranches);


        // Load role & dashboard data
        loadUserRole();
        loadPendingOrders();
        loadLowStock();
        loadBranches();

        // Button clicks
        btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
        });

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(this, AdminProfileActivity.class)));


        btnOrders.setOnClickListener(v -> startActivity(new Intent(this, OrderManagementActivity.class)));
        btnStock.setOnClickListener(v -> startActivity(new Intent(this, StockManagementActivity.class)));
        btnMenu.setOnClickListener(v -> startActivity(new Intent(this, MenuManagementActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, AdminProfileActivity.class)));

        btnAddAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AddAdminActivity.class);
            intent.putExtra("mode", "add"); // optional: indicate add mode
            startActivity(intent);
        });

        btnViewAdmins.setOnClickListener(v -> startActivity(
                new Intent(AdminDashboardActivity.this, AdminListActivity.class)
        ));

        btnManageBranches.setOnClickListener(v -> startActivity(
                new Intent(AdminDashboardActivity.this, AddBranchActivity.class)
        ));

    }

    private void loadUserRole() {
        usersRef.child(currentUserId).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserRole = snapshot.getValue(String.class);
                    if ("super_admin".equals(currentUserRole)) {
                        // Show Super Admin buttons
                        btnAddAdmin.setVisibility(View.VISIBLE);
                        btnViewAdmins.setVisibility(View.VISIBLE);
                        btnManageBranches.setVisibility(View.VISIBLE);

                        // Link buttons

                    } else {
                        // Normal Admin: hide super admin buttons
                        btnAddAdmin.setVisibility(View.GONE);
                        btnViewAdmins.setVisibility(View.GONE);
                        btnManageBranches.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadPendingOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int pendingCount = 0;
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    String status = orderSnap.child("status").getValue(String.class);
                    if ("pending".equalsIgnoreCase(status)) {
                        pendingCount++;
                    }
                }
                tvPendingOrders.setText(String.valueOf(pendingCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadLowStock() {
        stockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int lowStockCount = 0;
                for (DataSnapshot branchSnap : snapshot.getChildren()) {
                    for (DataSnapshot itemSnap : branchSnap.getChildren()) {
                        Long qty = itemSnap.child("quantity").getValue(Long.class);
                        if (qty != null && qty < 5) { // threshold
                            lowStockCount++;
                        }
                    }
                }
                tvLowStock.setText(String.valueOf(lowStockCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadBranches() {
        branchesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvBranches.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
   });
}
}
