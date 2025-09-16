package com.androidapp.pizzamania;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.androidapp.pizzamania.controller.AuthController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private TextView linkRegister;
    private AuthController authController;

    private DatabaseReference usersRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_ui);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authController = new AuthController();
        usersRef= FirebaseDatabase.getInstance().getReference("Users");

        inputEmail = findViewById(R.id.txtEmail);
        inputPassword = findViewById(R.id.txtPwd);
        btnLogin = findViewById(R.id.btnLogin);
        linkRegister = findViewById(R.id.signup);

        btnLogin.setOnClickListener(view -> {
            String email = inputEmail.getText().toString();
            String pass = inputPassword.getText().toString();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            authController.login(email, pass)
                    .addOnSuccessListener(aVoid -> {
                        checkUserRole(authController.getCurrentUserId());

                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                        Log.d("Error", "Login failed"+e);
                    });
        });

        linkRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void checkUserRole(String uid) {
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    updateLastLogin(uid);

                    // Save session
                    SessionManager session = new SessionManager(LoginActivity.this);
                    session.createSession(uid, inputEmail.getText().toString(), role);

                    // RBAC redirect
                    if ("super_admin".equals(role) || "admin".equals(role)) {
                        startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateLastLogin(String uid) {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        usersRef.child(uid).child("lastLogin").setValue(currentTime)
                .addOnSuccessListener(aVoid -> Log.d("LoginActivity", "Last login updated"))
                .addOnFailureListener(e -> Log.e("LoginActivity", "Failed to update last login", e));
      }
}
