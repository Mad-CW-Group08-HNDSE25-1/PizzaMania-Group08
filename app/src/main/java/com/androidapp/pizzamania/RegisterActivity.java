package com.androidapp.pizzamania;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPhone, etPassword;
    private ImageView imgProfile;
    private Button btnRegister;
    private Uri profileUri;

    private FirebaseAuth auth;
    private DatabaseReference dbRef;
    private StorageReference storageRef;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        imgProfile = findViewById(R.id.imgProfile);
        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");
        dbHelper = new DatabaseHelper(this);

        imgProfile.setOnClickListener(v -> chooseImage());
        btnRegister.setOnClickListener(v -> registerUser());
    }

    // Select profile picture
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            profileUri = data.getData();
            imgProfile.setImageURI(profileUri);
        }
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Phone validation: 10 digits
        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email validation (simple)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String userId = result.getUser().getUid();
                    if (profileUri != null) {
                        uploadImage(userId, name, email, phone);
                    } else {
                        saveUser(userId, name, email, phone, "");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void uploadImage(String userId, String name, String email, String phone) {
        StorageReference ref = storageRef.child(userId + ".jpg");
        ref.putFile(profileUri)
                .addOnSuccessListener(task -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri ->
                                saveUser(userId, name, email, phone, uri.toString())))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    private void saveUser(String userId, String name, String email, String phone, String imageUrl) {
        // Save in Realtime Database
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("phone", phone);
        userMap.put("profileImageUrl", imageUrl);
        userMap.put("role", "customer");

        dbRef.child(userId).setValue(userMap);

        // Save session in SQLite
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_USER_SESSION, null, null);

        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        values.put("profileImageUrl", imageUrl);
        values.put("isLoggedIn", 1);

        db.insert(DatabaseHelper.TABLE_USER_SESSION, null, values);
        db.close();

        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
    }
}


