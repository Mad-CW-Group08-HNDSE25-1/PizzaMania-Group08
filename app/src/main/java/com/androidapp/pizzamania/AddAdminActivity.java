package com.androidapp.pizzamania;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAdminActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private Spinner spRole, spBranch;
    private ImageView imgProfile;
    private Button btnChoosePic, btnAddAdmin;
    private Uri imageUri;
    private FirebaseAuth auth;
    private DatabaseReference usersRef, branchesRef;
    private List<String> branchList = new ArrayList<>();
    private ArrayAdapter<String> branchAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_adduser);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        branchesRef = FirebaseDatabase.getInstance().getReference("branches");

        imgProfile = findViewById(R.id.imgProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spRole = findViewById(R.id.spRole);
        spBranch = findViewById(R.id.spBranch);
        btnChoosePic = findViewById(R.id.btnChoosePic);
        btnAddAdmin = findViewById(R.id.btnAddAdmin);

        // Load branches
        branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchList);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBranch.setAdapter(branchAdapter);
        loadBranches();

        // Role spinner
        List<String> roles = new ArrayList<>();
        roles.add("admin");
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        btnChoosePic.setOnClickListener(v -> chooseImage());
        btnAddAdmin.setOnClickListener(v -> addAdmin());
    }

    private void loadBranches() {
        branchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                branchList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String branchName = ds.child("branchName").getValue(String.class);
                    branchList.add(branchName);
                }
                branchAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        }
    }

    private void addAdmin() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = spRole.getSelectedItem().toString();
        String branch = spBranch.getSelectedItem().toString();

        if(name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(confirmPassword)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    Map<String, Object> adminData = new HashMap<>();
                    adminData.put("name", name);
                    adminData.put("email", email);
                    adminData.put("phone", phone);
                    adminData.put("role", role);
                    adminData.put("branch", branch);
                    adminData.put("lastLogin", "");

                    if(imageUri != null){
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profileImages/" + uid);
                        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    adminData.put("profileImageUrl", uri.toString());
                                    usersRef.child(uid).setValue(adminData);
                                    Toast.makeText(this, "Admin added successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                        );
                    } else {
                        usersRef.child(uid).setValue(adminData);
                        Toast.makeText(this, "Admin added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
 }
}
