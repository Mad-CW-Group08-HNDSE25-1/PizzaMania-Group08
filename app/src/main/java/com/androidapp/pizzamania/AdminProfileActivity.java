package com.androidapp.pizzamania;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private EditText etName, etEmail, etPhone;
    private TextView tvRole, tvLastLogin;
    private Spinner spBranch;
    private Button btnChangePic, btnUpdate, btnResetPassword;

    private Uri imageUri;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private DatabaseHelper dbHelper;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Initialize views
        imgProfile = findViewById(R.id.imgProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        tvRole = findViewById(R.id.tvRole);
        tvLastLogin = findViewById(R.id.tvLastLogin);
        spBranch = findViewById(R.id.spBranch);
        btnChangePic = findViewById(R.id.btnChangePic);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        dbHelper = new DatabaseHelper(this);
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Setup Branch Spinner
        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(
                this, R.array.admin_branches, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBranch.setAdapter(branchAdapter);

        loadProfile();

        imgProfile.setOnClickListener(v -> chooseImage());
        btnChangePic.setOnClickListener(v -> chooseImage());
        btnUpdate.setOnClickListener(v -> updateProfile());
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void loadProfile() {
        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                etName.setText(snapshot.child("name").getValue(String.class));
                etEmail.setText(snapshot.child("email").getValue(String.class));
                etPhone.setText(snapshot.child("phone").getValue(String.class));
                tvRole.setText("Role: " + snapshot.child("role").getValue(String.class));
                tvLastLogin.setText("Last Login: " + snapshot.child("lastLogin").getValue(String.class));

                String branch = snapshot.child("branch").getValue(String.class);
                if (branch != null) {
                    int position = ((ArrayAdapter) spBranch.getAdapter()).getPosition(branch);
                    spBranch.setSelection(position);
                }

                String profileUrl = snapshot.child("profileImageUrl").getValue(String.class);
                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Picasso.get().load(profileUrl).placeholder(R.drawable.ic_person).into(imgProfile);
                }
            }
        });
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

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String branch = spBranch.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty()) {
            Snackbar.make(etName, "Name and Phone cannot be empty", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            Snackbar.make(etPhone, "Enter a valid phone number", Snackbar.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Updating profile...");
        progressDialog.show();

        String userId = auth.getCurrentUser().getUid();

        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profiles/" + userId + ".jpg");
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> saveToFirebase(userId, name, phone, branch, uri.toString()))
            ).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Snackbar.make(btnUpdate, "Image upload failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            });
        } else {
            saveToFirebase(userId, name, phone, branch, null);
        }
    }

    private void saveToFirebase(String userId, String name, String phone, String branch, @Nullable String imageUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone", phone);
        map.put("branch", branch);
        if (imageUrl != null) map.put("profileImageUrl", imageUrl);

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
        values.put("branch", branch);
        if (imageUrl != null) values.put("profileImageUrl", imageUrl);

        db.update(DatabaseHelper.TABLE_USER_SESSION, values, "userId=?", new String[]{userId});
        db.close();
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Snackbar.make(etEmail, "Email is empty!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(unused ->
                        Snackbar.make(btnResetPassword, "Password reset email sent!", Snackbar.LENGTH_SHORT).show()
                ).addOnFailureListener(e ->
                                Snackbar.make(btnResetPassword, "Failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show()
                        );
    }
}
