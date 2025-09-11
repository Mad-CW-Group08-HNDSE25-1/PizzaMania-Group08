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
import com.androidapp.pizzamania.model.MenuItem;

public class UpdateMenuItemActivity extends AppCompatActivity {
    private ImageView dImage;
    private EditText nameTxt, descriptionTxt, priceTxt;
    private String menuItemId, name, description, price, branch;
    private Button updateMenuItemBtn;
    private UserController userController;
    private MenuItemController menuItemController;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_menu_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dImage = findViewById(R.id.dImage);
        nameTxt = findViewById(R.id.nameTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        priceTxt = findViewById(R.id.priceTxt);
        updateMenuItemBtn = findViewById(R.id.updateMenuItemBtn);
        userController = new UserController();
        menuItemController = new MenuItemController();
        authController = new AuthController();

        menuItemId = getIntent().getStringExtra("menuItemId");
        menuItemController.readMenuItemById(menuItemId)
                .addOnSuccessListener(menuItem -> {
                    if (menuItem != null) {
                        nameTxt.setText(menuItem.getName());
                        descriptionTxt.setText(menuItem.getDescription());
                        priceTxt.setText(menuItem.getPrice());
                        branch = menuItem.getBranch(); // keep branch for update
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load menu item", Toast.LENGTH_SHORT).show();
                    Log.e("Error", "Failed to fetch menu item", e);
                });

        updateMenuItemBtn.setOnClickListener(view -> {
            name = nameTxt.getText().toString();
            description = descriptionTxt.getText().toString();
            price = priceTxt.getText().toString();

            if(name.isEmpty() || description.isEmpty() || price.isEmpty()){
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            menuItemController.updateMenuItem(menuItemId, name, description, price, branch)
                    .addOnSuccessListener( task -> {
                        Toast.makeText(this, "Item updated Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UpdateMenuItemActivity.this, ManageMenuActivity.class));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update the item", Toast.LENGTH_SHORT).show();
                        Log.d("Error", "Failed to update the item"+e);
                    });
        });
    }
}