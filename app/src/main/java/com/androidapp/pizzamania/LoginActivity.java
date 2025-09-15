package com.androidapp.pizzamania;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        progressDialog.show();

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    // Get user role from Firebase
                    userRef.child(uid).get().addOnSuccessListener(snapshot -> {
                        progressDialog.dismiss();
                        if (snapshot.exists()) {
                            String role = snapshot.child("role").getValue(String.class);
                            if (role == null) role = "customer";

                            // Open Admin Dashboard only for admin roles
                            if (role.equals("superadmin") || role.equals("admin") || role.equals("staff")) {
                                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                intent.putExtra("ROLE", role);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "You are not authorized", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
           });
}
}
