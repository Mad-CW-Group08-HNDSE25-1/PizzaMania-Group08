package com.androidapp.pizzamania;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddAdminActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private EditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private Spinner spRole, spBranch;
    private Button btnChoosePic, btnAddUser;

    private Uri imageUri;
    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Initialize views
        imgProfile = findViewById(R.id.imgProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spRole = findViewById(R.id.spRole);
        spBranch = findViewById(R.id.spBranch);
        btnChoosePic = findViewById(R.id.btnChoosePic);
        btnAddUser = findViewById(R.id.btnAddUser);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Setup Spinners
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(this,
                R.array.admin_staff_roles, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(this,
                R.array.admin_branches, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBranch.setAdapter(branchAdapter);

        // Click listeners
        imgProfile.setOnClickListener(v -> chooseImage());
        btnChoosePic.setOnClickListener(v -> chooseImage());
        btnAddUser.setOnClickListener(v -> addAdmin());
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
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

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Snackbar.make(etName, "All fields are required", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(etEmail, "Enter a valid email", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Snackbar.make(etConfirmPassword, "Passwords do not match", Snackbar.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Adding " + role + "...");
        progressDialog.show();

        // Create Firebase Auth user
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            FirebaseUser user = authResult.getUser();
            if (user != null) {
                String userId = user.getUid();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("profiles/" + userId + ".jpg");

                if (imageUri != null) {
                    storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                            storageRef.getDownloadUrl().addOnSuccessListener(uri ->
                                    saveToDatabase(userId, name, email, phone, role, branch, uri.toString())
                            )
                    ).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Snackbar.make(btnAddUser, "Image upload failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    });
                } else {
                    saveToDatabase(userId, name, email, phone, role, branch, "");
                }

            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Snackbar.make(btnAddUser, "Failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        });
    }

    private void saveToDatabase(String userId, String name, String email, String phone, String role, String branch, String profileUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("phone", phone);
        map.put("role", role);
        map.put("branch", branch);
        map.put("profileImageUrl", profileUrl);
        map.put("lastLogin", "null"); // initially null

        usersRef.child(userId).setValue(map).addOnSuccessListener(unused -> {
            progressDialog.dismiss();
            Snackbar.make(btnAddUser, role + " added successfully!", Snackbar.LENGTH_SHORT).show();
            finish(); // close activity
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Snackbar.make(btnAddUser, "Database error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
       });
    }
}
