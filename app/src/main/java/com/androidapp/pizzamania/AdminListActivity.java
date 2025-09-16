package com.androidapp.pizzamania;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminListActivity extends AppCompatActivity {

    private RecyclerView recyclerAdmins;
    private AdminListAdapter adapter;
    private List<AdminModel> adminList = new ArrayList<>();
    private String currentUserRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        recyclerAdmins = findViewById(R.id.recyclerAdmins);
        recyclerAdmins.setLayoutManager(new LinearLayoutManager(this));

        checkCurrentUserRole();
    }

    // Check if current user is Super Admin
    private void checkCurrentUserRole() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUserRole = snapshot.getValue(String.class);

                        if (!"super_admin".equals(currentUserRole)) {
                            finish(); // Normal admins cannot access this screen
                        } else {
                            loadAdminList();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadAdminList() {
        FirebaseDatabase.getInstance().getReference("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adminList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String role = ds.child("role").getValue(String.class);
                            if ("admin".equals(role) || "super_admin".equals(role)) {
                                AdminModel admin = ds.getValue(AdminModel.class);
                                adminList.add(admin);
                            }
                        }
                        adapter = new AdminListAdapter(AdminListActivity.this, adminList, currentUserRole);
                        recyclerAdmins.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
    });
}
}
