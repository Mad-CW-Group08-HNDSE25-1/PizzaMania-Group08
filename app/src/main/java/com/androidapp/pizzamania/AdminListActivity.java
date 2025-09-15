package com.androidapp.pizzamania;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminListActivity extends AppCompatActivity {

    private RecyclerView recyclerAdmins;
    private AdminListAdapter adapter;
    private List<AdminModel> adminList;

    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        recyclerAdmins = findViewById(R.id.recyclerAdmins);
        recyclerAdmins.setLayoutManager(new LinearLayoutManager(this));

        adminList = new ArrayList<>();
        adapter = new AdminListAdapter(this, adminList);
        recyclerAdmins.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("Users");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminList.clear();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String role = userSnap.child("role").getValue(String.class);
                    if (role != null && (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_admin"))) {
                        AdminModel admin = userSnap.getValue(AdminModel.class);
                        if (admin != null) {
                            admin.setUserId(userSnap.getKey());
                            adminList.add(admin);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
   });
}
}
