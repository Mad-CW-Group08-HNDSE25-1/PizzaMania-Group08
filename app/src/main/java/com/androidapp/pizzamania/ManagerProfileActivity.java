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

import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.controller.AuthController;
import com.androidapp.pizzamania.controller.UserController;
import com.androidapp.pizzamania.model.User;

public class ManagerProfileActivity extends AppCompatActivity {
    private Button backBtn, editImageBtn, saveBtn, passResetBtn, deleteAccBtn;
    private ImageView profileImage;
    private EditText nameTxt, phoneTxt, branchTxt, emailTxt;
    private String uid, name, phone, role, branch, email;
    private AuthController authController;
    private UserController userController;
    private User user;

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

        backBtn = findViewById(R.id.backBtn);
        editImageBtn = findViewById(R.id.editImageBtn);
        saveBtn = findViewById(R.id.saveBtn);
        passResetBtn = findViewById(R.id.passResetBtn);
        deleteAccBtn = findViewById(R.id.deleteAccBtn);
        profileImage = findViewById(R.id.profileImage);
        nameTxt= findViewById(R.id.nameTxt);
        phoneTxt= findViewById(R.id.phoneTxt);
        branchTxt = findViewById(R.id.branchTxt);
        emailTxt= findViewById(R.id.emailTxt);
        authController = new AuthController();
        userController = new UserController();

        uid = authController.getCurrentUserId();
        userController.getUserById(uid, new OnResultListener<User>() {
            @Override
            public void onSuccess(User result) {
                user = result;
                nameTxt.setText(user.getName());
                phoneTxt.setText(user.getPhone());
                branchTxt.setText(user.getBranchId());
                emailTxt.setText(user.getEmail());
                role = user.getRole();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManagerProfileActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(view -> onBackPressed());

        editImageBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Image");
            builder.setMessage("Do you want to remove this image?");
            builder.setCancelable(true);

            builder.setPositiveButton("Remove", (DialogInterface.OnClickListener)(dialog, which) -> {
                dialog.cancel();
            });

            builder.setNegativeButton("Edit", (DialogInterface.OnClickListener)(dialog, which) -> {
                dialog.cancel();
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        saveBtn.setOnClickListener(view -> {
            name = nameTxt.getText().toString();
            phone = phoneTxt.getText().toString();
            branch = branchTxt.getText().toString();
            email = emailTxt.getText().toString();

            if(name.isEmpty()){
                nameTxt.setError("Please enter your full name");
            }
            else if(phone.isEmpty()){
                phoneTxt.setError("Please enter your phone number");
            }
            else if(email.isEmpty()){
                emailTxt.setError("Please enter the email address");
            }
            else {
                User updatedUser = new User(uid, name, phone, email, role, branch, null);
                userController.updateUser(updatedUser, new OnResultListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(ManagerProfileActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new  Intent(getIntent()));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ManagerProfileActivity.this, "Error updating data", Toast.LENGTH_SHORT).show();
                        Log.d("Error", "Error updating data "+e);
                    }
                });
            }
        });

        passResetBtn.setOnClickListener(view -> {
            authController.resetPass(email)
                    .addOnSuccessListener(task -> {
                        Toast.makeText(ManagerProfileActivity.this, "Link sent to your email address", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ManagerProfileActivity.this, "Error sending the reset link", Toast.LENGTH_SHORT).show();
                    });
        });

        deleteAccBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning!!");
            builder.setMessage("Do you want to delete this account?");
            builder.setCancelable(false);

            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener)(dialog, which) -> {
                authController.deleteAuth()
                        .addOnSuccessListener(task2 -> {
                            Toast.makeText(ManagerProfileActivity.this, "Auth deleted", Toast.LENGTH_SHORT).show();
                            userController.deleteUser(uid, new OnResultListener<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    Toast.makeText(ManagerProfileActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                    authController.logout()
                                            .addOnSuccessListener(task -> {
                                                startActivity(new Intent(ManagerProfileActivity.this, SplashScreen.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(ManagerProfileActivity.this, "logout failed", Toast.LENGTH_SHORT).show();
                                                Log.d("Error", "logout failed "+e);
                                            });
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(ManagerProfileActivity.this, "Error deleting data", Toast.LENGTH_SHORT).show();
                                    Log.d("Error", "Error deleting data "+e);
                                }
                            });
                        })
                        .addOnFailureListener(e2 -> {
                            Toast.makeText(ManagerProfileActivity.this, "Auth delete failed", Toast.LENGTH_SHORT).show();
                            Log.d("Error", "Auth delete failed "+e2);
                        });



            });

            builder.setNegativeButton("No", (DialogInterface.OnClickListener)(dialog, which) -> {
                dialog.cancel();
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }
}