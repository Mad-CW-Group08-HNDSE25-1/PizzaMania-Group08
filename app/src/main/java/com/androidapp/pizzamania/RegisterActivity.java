package com.androidapp.pizzamania;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private ImageView imgProfile;
    private Button btnRegister, btnChoosePic;

    private Uri profileUri;
    private FirebaseAuth auth;
    private DatabaseReference dbRef;
    private StorageReference storageRef;
    private DatabaseHelper dbHelper;
    private ProgressDialog progressDialog;

    private static final String DEFAULT_PROFILE_URL = "https://i.pravatar.cc/150";
    private static final String ROLE_CUSTOMER = "customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Init views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        imgProfile = findViewById(R.id.imgProfile);
        btnRegister = findViewById(R.id.btnRegister);
        btnChoosePic = findViewById(R.id.btnChoosePic);

        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");
        dbHelper = new DatabaseHelper(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Click listeners
        imgProfile.setOnClickListener(v -> chooseImage());
        btnChoosePic.setOnClickListener(v -> chooseImage());
        btnRegister.setOnClickListener(v -> registerUser());

        // Optional: Toggle password visibility (long press)
        etPassword.setOnLongClickListener(v -> {
            togglePasswordVisibility(etPassword);
            return true;
        });
        etConfirmPassword.setOnLongClickListener(v -> {
            togglePasswordVisibility(etConfirmPassword);
            return true;
        });
    }

    private void togglePasswordVisibility(EditText passwordField) {
        if (passwordField.getTransformationMethod() instanceof PasswordTransformationMethod) {
            passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

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
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = ROLE_CUSTOMER; // automatically set

        // Validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String userId = result.getUser().getUid();
                    if (profileUri != null) {
                        uploadImage(userId, name, email, phone, role);
                    } else {
                        saveUser(userId, name, email, phone, DEFAULT_PROFILE_URL, role);
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Email already registered!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImage(String userId, String name, String email, String phone, String role) {
        progressDialog.setMessage("Uploading profile image...");
        StorageReference ref = storageRef.child(userId + ".jpg");
        ref.putFile(profileUri)
                .addOnSuccessListener(task -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri ->
                                saveUser(userId, name, email, phone, uri.toString(), role)))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUser(String userId, String name, String email, String phone, String imageUrl, String role) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("phone", phone);
        userMap.put("profileImageUrl", imageUrl);
        userMap.put("role", role);

        dbRef.child(userId).setValue(userMap).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
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
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to save user info!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
