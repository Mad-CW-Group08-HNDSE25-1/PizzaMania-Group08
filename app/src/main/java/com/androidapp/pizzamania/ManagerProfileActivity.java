package com.androidapp.pizzamania;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.androidapp.pizzamania.controller.AuthController;
import com.androidapp.pizzamania.controller.UserController;
import com.androidapp.pizzamania.model.User;

public class ManagerProfileActivity extends AppCompatActivity {
    private ImageView imgDp;
    private EditText txtName, txtPhone, txtBranch, txtEmail, txtCurrentPass, txtNewPass, txtConfirmPass;
    private String uid, name, phone, branch, currentEmail, email, currentPass, newPass, confPass;
    private Button saveBtn, saveAuthBtn, deleteAccBtn;
    private UserController userController;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgDp = findViewById(R.id.imgDp);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtBranch = findViewById(R.id.txtBranch);
        txtEmail = findViewById(R.id.txtEmail);
        txtCurrentPass = findViewById(R.id.txtCurrentPass);
        txtNewPass = findViewById(R.id.txtNewPass);
        txtConfirmPass = findViewById(R.id.txtConfirmPass);
        saveBtn = findViewById(R.id.saveBtn);
        saveAuthBtn = findViewById(R.id.saveAuthBtn);
        deleteAccBtn = findViewById(R.id.deleteAccBtn);
        userController = new UserController();
        authController = new AuthController();
        uid = authController.getAuthId();

        userController.readUserById(uid)
                .addOnSuccessListener(user -> {
                    txtName.setText(user.getName());
                    txtPhone.setText(user.getPhone());
                    txtBranch.setText(user.getBranch());
                    txtEmail.setText(user.getEmail());
                    currentEmail = user.getEmail().toString();
                })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
                            Log.d("Error", "Failed to load data"+e);
                        });

        saveBtn.setOnClickListener(view -> {
            name = txtName.getText().toString();
            phone = txtPhone.getText().toString();
            branch = txtBranch.getText().toString();
            email = txtEmail.getText().toString();

            User updatedUser = new User();
            updatedUser.setName(name);
            updatedUser.setPhone(phone);
            updatedUser.setBranch(branch);
            updatedUser.setEmail(email);

            userController.updateUser(uid, updatedUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Details updated successfuly", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update data", Toast.LENGTH_SHORT).show();
                        Log.d("Error", "Failed to update data"+e);
                    });
        });

        saveAuthBtn.setOnClickListener(view -> {
            email = txtEmail.getText().toString();
            currentPass = txtCurrentPass.getText().toString();
            newPass = txtNewPass.getText().toString();
            confPass = txtConfirmPass.getText().toString();

            if (email.isEmpty() || currentPass.isEmpty() || newPass.isEmpty() || confPass.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!email.equals(currentEmail)){
                if(newPass.equals(currentPass)){
                    Toast.makeText(this, "The new password cannot be the current password", Toast.LENGTH_SHORT).show();
                }
                else if(!confPass.equals(newPass)) {
                    Toast.makeText(this, "Both passwords should be same", Toast.LENGTH_SHORT).show();
                }
                else{
                    authController.updateAuthEmail(email)
                            .addOnSuccessListener(task -> {
                                Toast.makeText(this, "Auth reset successful", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Auth reset failed", Toast.LENGTH_SHORT).show();
                                Log.d("Error", "Pass reset failed"+e);
                            });
                }
            }
            else{
                authController.checkCredentials(email, currentPass)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Credentials are correct", Toast.LENGTH_SHORT).show();
                            if(newPass.equals(currentPass)){
                                Toast.makeText(this, "The new password cannot be the current password", Toast.LENGTH_SHORT).show();
                            }
                            else if(!confPass.equals(newPass)) {
                                Toast.makeText(this, "Both passwords should be same", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                authController.updateAuthPass(newPass)
                                        .addOnSuccessListener(task -> {
                                            Toast.makeText(this, "Pass reset successful", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Pass reset failed", Toast.LENGTH_SHORT).show();
                                            Log.d("Error", "Pass reset failed"+e);
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Credentials are incorrect", Toast.LENGTH_SHORT).show();
                            Log.d("Error", "Credentials are incorrect"+e);
                        });
            }
        });

        deleteAccBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ManagerProfileActivity.this);
            builder.setTitle("Warning!!");
            builder.setMessage("Do you want to delete this account?");
            builder.setCancelable(false);

            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener)(dialog, which) -> {
                userController.deleteUser(uid);
                authController.logout();
                authController.deleteAuth();
                startActivity(new Intent(ManagerProfileActivity.this, SplashActivity.class));
                finish();
            });

            builder.setNegativeButton("No", (DialogInterface.OnClickListener)(dialog, which) -> {
                dialog.cancel();
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }
}