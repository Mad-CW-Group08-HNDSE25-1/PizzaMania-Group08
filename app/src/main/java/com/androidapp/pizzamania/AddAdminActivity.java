package com.androidapp.pizzamania;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;

public class AddAdminActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone;
    private Spinner spRole, spBranch;
    private Button btnAddAdmin;

    DatabaseReference usersRef, branchesRef;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        spRole = findViewById(R.id.spRole);
        spBranch = findViewById(R.id.spBranch);
        btnAddAdmin = findViewById(R.id.btnAddUser);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        branchesRef = FirebaseDatabase.getInstance().getReference("branches");

        loadBranches();

        btnAddAdmin.setOnClickListener(v -> addAdmin());
    }

    private void loadBranches() {
        branchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayAdapter<String> adapter;
                adapter = new ArrayAdapter<>(AddAdminActivity.this, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String branchName = ds.child("branchName").getValue(String.class);
                    adapter.add(branchName);
                }

                spBranch.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addAdmin() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhone.getText().toString();
        String role = spRole.getSelectedItem().toString().toLowerCase();
        String branch = spBranch.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = usersRef.push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("phone", phone);
        map.put("role", role);
        map.put("branch", branch);
        map.put("profileImageUrl", "");
        map.put("lastLogin", "");

        usersRef.child(userId).setValue(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Admin added", Toast.LENGTH_SHORT).show();
                finish();
            }
   });
        }
}
