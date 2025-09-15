package com.androidapp.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.androidapp.pizzamania.controller.AuthController;
import com.androidapp.pizzamania.controller.MenuItemController;
import com.androidapp.pizzamania.controller.UserController;

public class AddMenuItemActivity extends AppCompatActivity {
    private ImageView dImage;
    private EditText nameTxt, descriptionTxt, priceTxt;
    private String uid, name, description, price, branch;
    private Button addMenuItemBtn;
    private UserController userController;
    private MenuItemController menuItemController;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_menu_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dImage = findViewById(R.id.dImage);
        nameTxt = findViewById(R.id.nameTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        priceTxt = findViewById(R.id.priceTxt);
        addMenuItemBtn = findViewById(R.id.addMenuItemBtn);
        userController = new UserController();
        menuItemController = new MenuItemController();
        authController = new AuthController();

        userController.getUserBranchById(authController.getAuthId())
                        .addOnSuccessListener( branchName -> {
                            branch = branchName;
                        })
                                .addOnFailureListener(e -> {
                                    Log.d("Error", "Error fetching branch", e);
                                });

        addMenuItemBtn.setOnClickListener(view -> {
            name = nameTxt.getText().toString();
            description = descriptionTxt.getText().toString();
            price = priceTxt.getText().toString();

            if(name.isEmpty() || description.isEmpty() || price.isEmpty()){
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            menuItemController.createMenuItem(name,description, price, branch)
                    .addOnSuccessListener( task -> {
                        Toast.makeText(this, "Item added Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddMenuItemActivity.this, ManageMenuActivity.class));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add the item", Toast.LENGTH_SHORT).show();
                        Log.d("Error", "Failed to add the item"+e);
                    });
        });

    }
}