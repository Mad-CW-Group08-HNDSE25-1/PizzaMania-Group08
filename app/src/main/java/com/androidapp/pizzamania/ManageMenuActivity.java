package com.androidapp.pizzamania;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.controller.AuthController;
import com.androidapp.pizzamania.controller.MenuItemController;
import com.androidapp.pizzamania.controller.UserController;
import com.androidapp.pizzamania.model.MenuItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ManageMenuActivity extends AppCompatActivity {
    private Button backBtn, filterBtn, addMenuItemBtn;
    private EditText searchTxt;
    private String search;
    private RecyclerView manageMenuItemRv;
    private ProgressBar progressBar;
    private ArrayList<MenuItem> itemArrayList;
    private MenuItemRvAdapter menuItemRvAdapter;
    private MenuItemController menuItemController;

    @SuppressLint("MissingInflatedId")
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

        backBtn = findViewById(R.id.backBtn);
        filterBtn = findViewById(R.id.filterBtn);
        addMenuItemBtn = findViewById(R.id.addMenuItemBtn);
        searchTxt = findViewById(R.id.searchTxt);
        manageMenuItemRv = findViewById(R.id.manageMenuItemRv);
        progressBar = findViewById(R.id.progressBar);
        menuItemController = new MenuItemController();

        itemArrayList = new ArrayList<>();
        manageMenuItemRv.setHasFixedSize(true);
        manageMenuItemRv.setLayoutManager(new LinearLayoutManager(this));

        menuItemRvAdapter = new MenuItemRvAdapter(itemArrayList, this);
        manageMenuItemRv.setAdapter(menuItemRvAdapter);

        menuItemController.getAllItems(new OnResultListener<List<MenuItem>>() {
            @Override
            public void onSuccess(List<MenuItem> result) {
                progressBar.setVisibility(View.GONE);
                if (!result.isEmpty()) {
                    itemArrayList.clear();
                    itemArrayList.addAll(result);
                    menuItemRvAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ManageMenuActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageMenuActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                Log.d("Error", "Fail to get the data. "+e);
            }
        });

        filterBtn.setOnClickListener(view -> {
            String search = searchTxt.getText().toString().toLowerCase();
        });

        backBtn.setOnClickListener(view -> {onBackPressed();});

        addMenuItemBtn.setOnClickListener(view -> {
            startActivity(new Intent(ManageMenuActivity.this, EditMenuItemActivity.class));
        });
    }
}