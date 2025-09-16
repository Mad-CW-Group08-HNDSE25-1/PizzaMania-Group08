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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private EditText etName, etPhone;
    private TextView tvEmail, tvRole, tvLastLogin, tvBranch;
    private Spinner spRole, spBranch;
    private Button btnChangePic, btnUpdate, btnResetPassword;

    private Uri imageUri;
    private String selectedBranch;
    private FirebaseAuth auth;
    private DatabaseReference usersRef, branchesRef;
    private List<String> branchList = new ArrayList<>();
    private ArrayAdapter<String> branchAdapter;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        branchesRef = FirebaseDatabase.getInstance().getReference("branches");

        imgProfile = findViewById(R.id.imgProfile);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        tvEmail = findViewById(R.id.etEmail);
        tvRole = findViewById(R.id.tvRole);
        tvLastLogin = findViewById(R.id.tvLastLogin);
        tvBranch = findViewById(R.id.tvBranch);
        spRole = findViewById(R.id.spRole);
        spBranch = findViewById(R.id.spBranch);
        btnChangePic = findViewById(R.id.btnChangePic);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        // Setup branch spinner
        branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchList);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBranch.setAdapter(branchAdapter);
        loadBranches();

        loadProfileData();

        btnChangePic.setOnClickListener(v -> chooseImage());
        btnUpdate.setOnClickListener(v -> updateProfile());
        btnResetPassword.setOnClickListener(v -> resetPassword());
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

    private void loadProfileData() {
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String role = snapshot.child("role").getValue(String.class);
                String branch = snapshot.child("branch").getValue(String.class);
                String profileUrl = snapshot.child("profileImageUrl").getValue(String.class);
                String lastLogin = snapshot.child("lastLogin").getValue(String.class);

                etName.setText(name);
                etPhone.setText(phone);
                tvEmail.setText(email);
                tvLastLogin.setText("Last Login: " + (lastLogin != null ? lastLogin : "N/A"));

                // Profile image
                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Glide.with(AdminProfileActivity.this).load(profileUrl).into(imgProfile);
                }

                // Role-based UI
                if ("super_admin".equals(role)) {
                    // Show editable spinners
                    spRole.setVisibility(android.view.View.VISIBLE);
                    spBranch.setVisibility(android.view.View.VISIBLE);
                    tvRole.setVisibility(android.view.View.GONE);
                    tvBranch.setVisibility(android.view.View.GONE);

                    // Load role spinner
                    List<String> roles = new ArrayList<>();
                    roles.add("admin");
                    roles.add("super_admin");
                    ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(AdminProfileActivity.this, android.R.layout.simple_spinner_item, roles);
                    roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spRole.setAdapter(roleAdapter);
                    spRole.setSelection(roles.indexOf(role));

                    // Load branch spinner
                    if (branch != null && branchList.contains(branch)) {
                        spBranch.setSelection(branchList.indexOf(branch));
                    }
                } else {
                    // Normal admin: show text only
                    tvRole.setText("Role: " + role);
                    tvBranch.setText(branch != null ? branch : "N/A");
                    spRole.setVisibility(android.view.View.GONE);
                    spBranch.setVisibility(android.view.View.GONE);
                    tvRole.setVisibility(android.view.View.VISIBLE);
                    tvBranch.setVisibility(android.view.View.VISIBLE);
                }

                // Update last login automatically
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                usersRef.child(currentUserId).child("lastLogin").setValue(currentTime);
                tvLastLogin.setText("Last Login: " + currentTime);
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
        if (requestCode == 102 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        }
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String branch = spBranch.getVisibility() == android.view.View.VISIBLE ? spBranch.getSelectedItem().toString() : tvBranch.getText().toString();
        String role = spRole.getVisibility() == android.view.View.VISIBLE ? spRole.getSelectedItem().toString() : tvRole.getText().toString().replace("Role: ", "");

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profileImages/" + currentUserId);
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> saveProfileData(name, phone, branch, role, uri.toString()))
            );
        } else {
            saveProfileData(name, phone, branch, role, null);
        }
    }

    private void saveProfileData(String name, String phone, String branch, String role, @Nullable String profileUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("role", role);
        updates.put("branch", branch);

        // Update last login
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        updates.put("lastLogin", currentTime);
        tvLastLogin.setText("Last Login: " + currentTime);

        if (profileUrl != null) updates.put("profileImageUrl", profileUrl);

        usersRef.child(currentUserId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void resetPassword() {
        String email = tvEmail.getText().toString();
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
      }
}
