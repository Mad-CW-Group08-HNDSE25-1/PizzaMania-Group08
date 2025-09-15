package com.androidapp.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvPendingOrders, tvLowStock, tvBranches;
    private Button btnAddAdmin, btnAdminList, btnManageBranches, btnManageMenu,
            btnManageStock, btnManageOrders, btnProfile, btnSignOut;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        // Initialize views
        tvPendingOrders = findViewById(R.id.tvPendingOrders);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvBranches = findViewById(R.id.tvBranches);

        btnAddAdmin = findViewById(R.id.btnAddAdmin);
        btnAdminList = findViewById(R.id.btnAdminList);
        btnManageBranches = findViewById(R.id.btnManageBranches);
        btnManageMenu = findViewById(R.id.btnManageMenu);
        btnManageStock = findViewById(R.id.btnManageStock);
        btnManageOrders = findViewById(R.id.btnManageOrders);
        btnProfile = findViewById(R.id.btnProfile);
        btnSignOut = findViewById(R.id.btnSignOut);

<<<<<<< HEAD
        loadDashboardStats();
        setupRoleBasedVisibility();
        setupButtonClicks();
=======
        // Load user role only if logged in
        loadUserRole();

        btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(this, AdminProfileActivity.class)));
>>>>>>> b1d0c51ace209f528fa6f898ba77cb02fc8d06b8
    }

    private void loadDashboardStats() {
        // Pending Orders
        database.getReference("orders").orderByChild("orderStatus").equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tvPendingOrders.setText("Pending\n" + snapshot.getChildrenCount());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Branches
        database.getReference("branches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvBranches.setText("Branches\n" + snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Low Stock (assuming "stock" node in DB)
        database.getReference("stock").orderByChild("quantity").endAt(5) // quantity ≤ 5
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tvLowStock.setText("Low Stock\n" + snapshot.getChildrenCount());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void setupRoleBasedVisibility() {
        database.getReference("Users").child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String role = snapshot.child("role").getValue(String.class);
                        if (role != null && role.equals("super_admin")) {
                            btnAddAdmin.setVisibility(View.VISIBLE);
                            btnAdminList.setVisibility(View.VISIBLE);
                            btnManageBranches.setVisibility(View.VISIBLE);
                        } else if (role != null && role.equals("admin")) {
                            // Admin cannot add other admins
                            btnAddAdmin.setVisibility(View.GONE);
                            btnAdminList.setVisibility(View.GONE);
                            btnManageBranches.setVisibility(View.VISIBLE); // optional
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void setupButtonClicks() {
        btnAddAdmin.setOnClickListener(v -> startActivity(new Intent(this, AddAdminActivity.class)));
        btnAdminList.setOnClickListener(v -> startActivity(new Intent(this, AdminListActivity.class)));
        btnManageBranches.setOnClickListener(v -> Toast.makeText(this, "Branch management coming soon", Toast.LENGTH_SHORT).show());
        btnManageMenu.setOnClickListener(v -> Toast.makeText(this, "Menu management coming soon", Toast.LENGTH_SHORT).show());
        btnManageStock.setOnClickListener(v -> Toast.makeText(this, "Stock management coming soon", Toast.LENGTH_SHORT).show());
        btnManageOrders.setOnClickListener(v -> Toast.makeText(this, "Order management coming soon", Toast.LENGTH_SHORT).show());
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, AdminProfileActivity.class)));
        btnSignOut.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
