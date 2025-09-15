package com.androidapp.pizzamania;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminListActivity extends AppCompatActivity {

    private RecyclerView recyclerAdmins;
    private AdminListAdapter adapter;
    private List<AdminModel> adminList = new ArrayList<>();
    private DatabaseReference adminsRef;
    private String currentUserRole = "Admin"; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        recyclerAdmins = findViewById(R.id.recyclerAdmins);
        recyclerAdmins.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminListAdapter(this, adminList);
        recyclerAdmins.setAdapter(adapter);

        adminsRef = FirebaseDatabase.getInstance().getReference("Admins");

        loadCurrentUserRole();
        loadAdmins();
    }

    private void loadCurrentUserRole() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String userId = email.replace(".", "_");

        adminsRef.child(userId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                currentUserRole = snapshot.child("role").getValue(String.class);
            }
        });
    }

    private void loadAdmins() {
        adminsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    AdminModel admin = ds.getValue(AdminModel.class);
                    if (admin != null) adminList.add(admin);
                }
                adapter.setCurrentUserRole(currentUserRole);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
   });
}
}
