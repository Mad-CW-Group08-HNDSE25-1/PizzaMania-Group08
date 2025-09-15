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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private EditText etName, etPhone;
    private TextView tvEmail, tvRole, tvLastLogin;
    private Spinner spBranch;
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
        spBranch = findViewById(R.id.spBranch);
        btnChangePic = findViewById(R.id.btnChangePic);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        // Load branches into spinner
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
                tvRole.setText("Role: " + role);
                tvLastLogin.setText("Last Login: " + (lastLogin != null ? lastLogin : "N/A"));

                selectedBranch = branch;
                if (branchList.contains(branch)) spBranch.setSelection(branchList.indexOf(branch));

                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Glide.with(AdminProfileActivity.this).load(profileUrl).into(imgProfile);
                }
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
        selectedBranch = spBranch.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profileImages/" + currentUserId);
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> saveProfileData(name, phone, selectedBranch, uri.toString()))
            );
        } else {
            saveProfileData(name, phone, selectedBranch, null);
        }
    }

    private void saveProfileData(String name, String phone, String branch, String profileUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("branch", branch);
        if (profileUrl != null) updates.put("profileImageUrl", profileUrl);

<<<<<<< HEAD
        usersRef.child(currentUserId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
=======
        // Update last login automatically
        String lastLogin = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        map.put("lastLogin", lastLogin);
        tvLastLogin.setText("Last Login: " + lastLogin);

        userRef.updateChildren(map).addOnSuccessListener(unused -> {
            saveToSQLite(userId, name, etEmail.getText().toString(), phone, imageUrl, branch);
            progressDialog.dismiss();
            Snackbar.make(btnUpdate, "Profile Updated!", Snackbar.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Snackbar.make(btnUpdate, "Update failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        });
    }

    private void saveToSQLite(String userId, String name, String email, String phone, @Nullable String imageUrl, String branch) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        if (imageUrl != null) values.put("profileImageUrl", imageUrl);

        db.update(DatabaseHelper.TABLE_USER_SESSION, values, "userId=?", new String[]{userId});
        db.close();
>>>>>>> b1d0c51ace209f528fa6f898ba77cb02fc8d06b8
    }

    private void resetPassword() {
        String email = tvEmail.getText().toString();
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
     }
}
