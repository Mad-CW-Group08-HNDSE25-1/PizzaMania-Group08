package com.androidapp.pizzamania;

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
import java.util.List;

public class AddAdminActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private Spinner spRole, spBranch;
    private Button btnAddUser, btnChoosePic;
    private ImageView imgProfile;

    private Uri imageUri;
    private String selectedRole, selectedBranch;
    private FirebaseAuth auth;
    private DatabaseReference usersRef, branchesRef;
    private List<String> branchList = new ArrayList<>();
    private ArrayAdapter<String> branchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        branchesRef = FirebaseDatabase.getInstance().getReference("branches");

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spRole = findViewById(R.id.spRole);
        spBranch = findViewById(R.id.spBranch);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnChoosePic = findViewById(R.id.btnChoosePic);
        imgProfile = findViewById(R.id.imgProfile);

        // Role spinner
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"admin"});
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        // Branch spinner
        branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchList);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBranch.setAdapter(branchAdapter);

        loadBranches();

        btnChoosePic.setOnClickListener(v -> chooseImage());

        btnAddUser.setOnClickListener(v -> addAdmin());
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
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
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
        selectedRole = spRole.getSelectedItem().toString();
        selectedBranch = spBranch.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();
                    uploadProfileImage(userId, name, email, phone);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void uploadProfileImage(String userId, String name, String email, String phone) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profileImages/" + userId);
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> saveAdminData(userId, name, email, phone, uri.toString()))
            ).addOnFailureListener(e -> saveAdminData(userId, name, email, phone, ""));
        } else {
            saveAdminData(userId, name, email, phone, "");
        }
    }

    private void saveAdminData(String userId, String name, String email, String phone, String profileUrl) {
        AdminModel admin = new AdminModel(name, email, phone, selectedBranch, selectedRole, userId, profileUrl);
        usersRef.child(userId).setValue(admin)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Admin added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
       }
}
