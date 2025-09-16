package com.androidapp.pizzamania;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.adapter.ToggleRvAdapter;
import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.controller.AuthController;
import com.androidapp.pizzamania.controller.CategoryController;
import com.androidapp.pizzamania.controller.MenuItemController;
import com.androidapp.pizzamania.controller.SizeController;
import com.androidapp.pizzamania.controller.ToppingController;
import com.androidapp.pizzamania.controller.UserController;
import com.androidapp.pizzamania.model.Category;
import com.androidapp.pizzamania.model.MenuItem;
import com.androidapp.pizzamania.model.Size;
import com.androidapp.pizzamania.model.Topping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditMenuItemActivity extends AppCompatActivity {
    private Button editImageBtn, saveMenuItemBtn, deleteBtn;

    private ImageButton backBtn;
    private ImageView itemImage;
    private EditText nameTxt, descriptionTxt, priceTxt;
    private String name, description, price, itemId, branchId, selectedCategoryId;
    private RecyclerView sizesRv, toppingsRv;
    private Spinner categorySpin;
    private MenuItem menuItem;
    private List<Size> sizesList;
    private List<Topping> toppingsList;
    private List<Category> categoryList;
    private ToggleRvAdapter<Size> sizeAdapter;
    private ToggleRvAdapter<Topping> toppingAdapter;
    private Map<String, Boolean> selectedSizes, selectedToppings;
    private MenuItemController menuItemController;
    private AuthController authController;
    private UserController userController;
    private CategoryController categoryController;
    private SizeController sizeController;
    private ToppingController toppingController;
    private boolean itemLoaded, sizesLoaded, toppingsLoaded;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_menu_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        editImageBtn = findViewById(R.id.editImageBtn);
        saveMenuItemBtn = findViewById(R.id.saveMenuItemBtn);
        itemImage = findViewById(R.id.itemImage);
        nameTxt = findViewById(R.id.nameTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        priceTxt = findViewById(R.id.priceTxt);
        categorySpin = findViewById(R.id.categorSpin);
        sizesRv = findViewById(R.id.sizesRv);
        toppingsRv = findViewById(R.id.toppingsRv);
//        deleteBtn = findViewById(R.id.deleteMenuItemBtn);
        menuItemController = new MenuItemController();
        authController = new AuthController();
        userController = new UserController();
        categoryController = new CategoryController();
        sizeController = new SizeController();
        toppingController = new ToppingController();

        itemLoaded = false;
        sizesLoaded = false;
        toppingsLoaded = false;


        itemId = getIntent().getStringExtra("itemId");
        if(itemId != null){
            menuItemController.getItemById(itemId, new OnResultListener<MenuItem>() {
                @Override
                public void onSuccess(MenuItem result) {
                    if(result != null){
                        nameTxt.setText(result.getName());
                        descriptionTxt.setText(result.getDescription());
                        priceTxt.setText(result.getPrice());
                        selectedSizes = result.getSizes();
                        selectedToppings = result.getToppings();
                        selectedCategoryId = result.getCategoryId();

                        itemLoaded = true;
                        setupAdaptersIfReady();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(EditMenuItemActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Failed to load data "+e);
                }
            });
        }

        userController.getUserBranchById(authController.getCurrentUserId(), new OnResultListener<String>() {
            @Override
            public void onSuccess(String result) {
                branchId = result.toLowerCase().toString();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("Error", "Error retrieving user's branch "+e);
            }
        });

        categoryController.getAllCategories(new OnResultListener<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                categoryList = result;
                List<String> categoryNames = new ArrayList<>();
                categoryNames.add("Select Category");
                for (Category c : result) {
                    categoryNames.add(c.getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        EditMenuItemActivity.this,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpin.setAdapter(adapter);

                if (selectedCategoryId != null){
                    for (int i = 0; i < result.size(); i++){
                        if (result.get(i).getId().equals(selectedCategoryId)){
                            categorySpin.setSelection(i + 1);
                            break;
                        }
                    }
                }

                categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            selectedCategoryId = null;
                        } else {
                            selectedCategoryId = result.get(position - 1).getId();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedCategoryId = null;
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditMenuItemActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });

        sizeController.getAllSizes(new OnResultListener<List<Size>>() {
            @Override
            public void onSuccess(List<Size> result) {
                sizesList = result;
                sizesLoaded = true;
                setupAdaptersIfReady();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditMenuItemActivity.this, "Failed to load sizes", Toast.LENGTH_SHORT).show();
            }
        });

        toppingController.getAllToppings(new OnResultListener<List<Topping>>() {
            @Override
            public void onSuccess(List<Topping> result) {
                toppingsList = result;
                toppingsLoaded = true;
                setupAdaptersIfReady();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditMenuItemActivity.this, "Failed to load toppings", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(view -> {onBackPressed();});

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

        saveMenuItemBtn.setOnClickListener(view -> {
            name = nameTxt.getText().toString();
            description = descriptionTxt.getText().toString();
            price = priceTxt.getText().toString();

            selectedSizes = sizeAdapter.getSelectionMap();
            selectedToppings = toppingAdapter.getSelectionMap();

            if(name.isEmpty()){
                nameTxt.setError("Please enter the branch name");
            }
            else if(description.isEmpty()){
                descriptionTxt.setError("Please enter a description");
            }
            else if(selectedCategoryId == null){
                Toast.makeText(EditMenuItemActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
            }
            else if(price.isEmpty()){
                priceTxt.setError("Please enter the price");
            }else if(itemId != null){
                menuItem = new MenuItem(itemId, name, description, price, branchId, selectedCategoryId, selectedSizes, selectedToppings,  null);
                menuItemController.updateItem(menuItem, new OnResultListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(EditMenuItemActivity.this, "Item updated Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditMenuItemActivity.this, ManageMenuActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EditMenuItemActivity.this, "Failed to update branch in the database", Toast.LENGTH_SHORT).show();
                        Log.d("Error", "Failed to update branch in the database "+e);
                    }
                });
            }
            else {
                menuItem = new MenuItem(null, name, description, price, branchId, selectedCategoryId, selectedSizes, selectedToppings,  null);
                menuItemController.createItem(menuItem, new OnResultListener<MenuItem>() {
                    @Override
                    public void onSuccess(MenuItem result) {
                        Toast.makeText(EditMenuItemActivity.this, "Item added Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditMenuItemActivity.this, ManageMenuActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EditMenuItemActivity.this, "Failed to save item in the database", Toast.LENGTH_SHORT).show();
                        Log.d("Error", "Failed to save item in the database "+e);
                    }
                });
            }
        });
    }

    private void setupAdaptersIfReady() {
        if (sizesLoaded && toppingsLoaded) {
            if (selectedSizes != null) {
                sizeAdapter = new ToggleRvAdapter<>(sizesList, selectedSizes);
            } else {
                sizeAdapter = new ToggleRvAdapter<>(sizesList);
            }
            sizesRv.setLayoutManager(new LinearLayoutManager(this));
            sizesRv.setAdapter(sizeAdapter);

            if (selectedToppings != null) {
                toppingAdapter = new ToggleRvAdapter<>(toppingsList, selectedToppings);
            } else {
                toppingAdapter = new ToggleRvAdapter<>(toppingsList);
            }
            toppingsRv.setLayoutManager(new LinearLayoutManager(this));
            toppingsRv.setAdapter(toppingAdapter);
        }
    }
}