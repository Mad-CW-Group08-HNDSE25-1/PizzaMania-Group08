package com.androidapp.pizzamania;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private EditText etName, etPhone;
    private TextView tvEmail, tvRole, tvLastLogin, tvBranch;
    private Button btnUpdate, btnResetPassword, editPicBtn;

    private Uri imageUri;
    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private String currentUserId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_ui);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserId = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        imgProfile = findViewById(R.id.profileImage);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        tvEmail = findViewById(R.id.txtEmail);
        tvRole = findViewById(R.id.tvRole);
        tvLastLogin = findViewById(R.id.tvLastLogin);
        tvBranch = findViewById(R.id.tvBranch);

        btnUpdate = findViewById(R.id.saveBtn);
        btnResetPassword = findViewById(R.id.saveAuthBtn);
        editPicBtn = findViewById(R.id.changeImageBtn);

        // Set click listeners
        btnUpdate.setOnClickListener(v -> updateProfile());
        btnResetPassword.setOnClickListener(v -> resetPassword());
        editPicBtn.setOnClickListener(this::capturePic);

        loadProfileData();

        // Request Camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }
    }


    private void loadProfileData() {
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String role = snapshot.child("role").getValue(String.class);
                String branch = snapshot.child("branch").getValue(String.class);
                String profileUrl = snapshot.child("profileImageUrl").getValue(String.class);
                String lastLogin = snapshot.child("lastLogin").getValue(String.class);

                etName.setText(name != null ? name : "");
                etPhone.setText(phone != null ? phone : "");
                tvEmail.setText(email != null ? email : "");
                tvRole.setText("Role: " + (role != null ? role : "N/A"));

                if ("super_admin".equals(role)) {
                    tvBranch.setText("Branch: Head Office");
                } else {
                    tvBranch.setText("Branch: " + (branch != null ? branch : "N/A"));
                }

                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                tvLastLogin.setText("Last Login: " + (lastLogin != null ? lastLogin : currentTime));

                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Glide.with(AdminProfileActivity.this).load(profileUrl).into(imgProfile);
                }

                // Update last login in Firebase
                usersRef.child(currentUserId).child("lastLogin").setValue(currentTime)
                        .addOnFailureListener(e -> Toast.makeText(AdminProfileActivity.this, "Failed to update last login", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        String role = tvRole.getText().toString().replace("Role: ", "");
        String branch = tvBranch.getText().toString().replace("Branch: ", "");

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            // 1. Create a reference in Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profileImages/" + currentUserId);

            // 2. Upload the image
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    // 3. Get the download URL after upload
                    storageRef.getDownloadUrl().addOnSuccessListener(uri ->
                            // 4. Save all user details including image URL
                            saveProfileData(name, phone, branch, role, uri.toString())
                    ).addOnFailureListener(e ->
                            Toast.makeText(AdminProfileActivity.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    )
            ).addOnFailureListener(e ->
                    Toast.makeText(AdminProfileActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        } else {
            // If no image taken, just save text details
            saveProfileData(name, phone, branch, role, null);
        }
    }

    private void saveProfileData(String name, String phone, String branch, String role, @Nullable String profileUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("role", role);
        updates.put("branch", branch);

        if (profileUrl != null) updates.put("profileImageUrl", profileUrl);

        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        updates.put("lastLogin", currentTime);
        tvLastLogin.setText("Last Login: " + currentTime);

        // Update data in Firebase Realtime Database
        usersRef.child(currentUserId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void resetPassword() {
        String email = tvEmail.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void capturePic(View view) {
        Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cam, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            // Save bitmap to cache and get Uri
            try {
                File file = new File(getCacheDir(), "profile_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                imageUri = Uri.fromFile(file);

                imgProfile.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to prepare image", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
