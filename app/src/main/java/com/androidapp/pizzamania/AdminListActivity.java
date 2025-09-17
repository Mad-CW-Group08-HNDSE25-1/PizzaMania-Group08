package com.androidapp.pizzamania;

import android.os.Bundle;
import android.widget.Toast;

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

    private void checkCurrentUserRole() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUserRole = snapshot.getValue(String.class);

                        if (!"super_admin".equalsIgnoreCase(currentUserRole)) {
                            Toast.makeText(AdminListActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            loadAdminList();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminListActivity.this, "Failed to load role", Toast.LENGTH_SHORT).show();
                    }
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
                            if (role != null && (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_admin"))) {

                                String id = ds.getKey();
                                String name = ds.child("name").getValue(String.class);
                                String email = ds.child("email").getValue(String.class);
                                String phone = ds.child("phone").getValue(String.class);
                                String branch = ds.child("branch").getValue(String.class);
                                String profileUrl = ds.child("profileUrl").getValue(String.class);
                                if (profileUrl == null) {
                                    profileUrl = ds.child("profileImageUrl").getValue(String.class);
                                }

                                AdminModel admin = new AdminModel(id, name, email, phone, role, branch, profileUrl);
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
