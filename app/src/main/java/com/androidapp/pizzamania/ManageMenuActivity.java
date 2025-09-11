package com.androidapp.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.adapter.MenuItemRvAdapter;
import com.androidapp.pizzamania.controller.AuthController;
import com.androidapp.pizzamania.controller.MenuItemController;
import com.androidapp.pizzamania.controller.UserController;
import com.androidapp.pizzamania.model.MenuItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ManageMenuActivity extends AppCompatActivity {
    private String branch;
    private Button addMenuItemBtn;
    private RecyclerView rvMenuItems;
    private ProgressBar progressBar;
    private ArrayList<MenuItem> menuItemsArrayList;
    private MenuItemRvAdapter menuItemRvAdapter;
    private MenuItemController menuItemController;
    private UserController userController;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addMenuItemBtn = findViewById(R.id.addMenuItemBtn);
        rvMenuItems = findViewById(R.id.rvMenuItems);
        progressBar = findViewById(R.id.progressBar);
        menuItemController = new MenuItemController();
        userController = new UserController();
        authController = new AuthController();

        menuItemsArrayList = new ArrayList<>();
        rvMenuItems.setHasFixedSize(true);
        rvMenuItems.setLayoutManager(new LinearLayoutManager(this));

        menuItemRvAdapter = new MenuItemRvAdapter(menuItemsArrayList, this);
        rvMenuItems.setAdapter(menuItemRvAdapter);

        userController.getUserBranchById(authController.getAuthId())
                .addOnSuccessListener( branchName -> {
                    branch = branchName;

                    menuItemController.readAllMenuItemsByBranch(branch)
                            .addOnSuccessListener(items -> {
                                progressBar.setVisibility(View.GONE);
                                if(!items.isEmpty()){
                                    menuItemsArrayList.clear();
                                    menuItemsArrayList.addAll(items);
                                    menuItemRvAdapter.notifyDataSetChanged();

                                }
                                else {
                                    Toast.makeText(ManageMenuActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ManageMenuActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                                Log.d("Error", "Fail to get the data. "+e);
                            });

                })
                .addOnFailureListener(e -> {
                    Log.d("Error", "Error fetching branch", e);
                });

        addMenuItemBtn.setOnClickListener(view -> {
            startActivity(new Intent(ManageMenuActivity.this, AddMenuItemActivity.class));
        });
    }
}