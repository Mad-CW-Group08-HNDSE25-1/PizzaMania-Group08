package com.androidapp.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvAdminTitle, tvPendingOrders, tvLowStock, tvBranches;
    private Button btnAddAdmin, btnAdminList, btnManageBranches,
            btnManageMenu, btnManageStock, btnManageOrders, btnProfile, btnSignOut;

    private String role = "", branch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvAdminTitle = findViewById(R.id.tvAdminTitle);
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

        // Load user role only if logged in
        loadUserRole();

        btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(this, AdminProfileActivity.class)));
    }

    private void loadUserRole() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // No user logged in — redirect to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String userId = currentUser.getUid();

        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        role = snapshot.child("role").getValue(String.class);
                        branch = snapshot.child("branch").getValue(String.class);

                        updateDashboard(role, branch);
                    }
                })
                .addOnFailureListener(e -> {
                    // Optional: handle DB read error
                    tvAdminTitle.setText("Error loading role");
                });
    }

    private void updateDashboard(String role, String branch) {
        if (role == null) role = "User";
        tvAdminTitle.setText(role + " Dashboard");

        if ("super_admin".equals(role)) {
            btnAddAdmin.setVisibility(Button.VISIBLE);
            btnAdminList.setVisibility(Button.VISIBLE);
            btnManageBranches.setVisibility(Button.VISIBLE);
        } else if ("admin".equals(role)) {
            btnManageBranches.setVisibility(Button.GONE);
            btnAddAdmin.setVisibility(Button.GONE);
            btnAdminList.setVisibility(Button.GONE);
        } else if ("staff".equals(role)) {
            btnManageMenu.setVisibility(Button.GONE);
            btnManageStock.setVisibility(Button.GONE);
            btnManageBranches.setVisibility(Button.GONE);
            btnAddAdmin.setVisibility(Button.GONE);
            btnAdminList.setVisibility(Button.GONE);
        }

        // TODO: load stats from Firebase (orders, stock, branches)
        tvPendingOrders.setText("Pending\n12");
        tvLowStock.setText("Low Stock\n3");
        tvBranches.setText("Branches\n2");
    }
}