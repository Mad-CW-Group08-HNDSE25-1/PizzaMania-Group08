package com.androidapp.pizzamania;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.adapter.ExtrasRvAdapter;
import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.controller.CategoryController;
import com.androidapp.pizzamania.controller.SizeController;
import com.androidapp.pizzamania.controller.ToppingController;
import com.androidapp.pizzamania.model.Category;
import com.androidapp.pizzamania.model.Size;
import com.androidapp.pizzamania.model.Topping;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ManageExtrasActivity extends AppCompatActivity {
    private RecyclerView categoriesRv, sizesRv, toppingsRv;
    private Button backBtn;
    private FloatingActionButton addCategoriesBtn, addSizesBtn, addToppingsBtn;

    private ExtrasRvAdapter categoryAdapter, sizeAdapter, toppingAdapter;
    private ArrayList<Category> categories;
    private ArrayList<Size> sizes;
    private ArrayList<Topping> toppings;
    private CategoryController categoryController;
    private SizeController sizeController;
    private ToppingController toppingController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_extras);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        categoriesRv = findViewById(R.id.categoriesRv);
        sizesRv = findViewById(R.id.sizesRv);
        toppingsRv = findViewById(R.id.toppingsRv);

        backBtn = findViewById(R.id.backBtn);

        addCategoriesBtn = findViewById(R.id.addCategoryBtn);
        addSizesBtn = findViewById(R.id.addSizeBtn);
        addToppingsBtn = findViewById(R.id.addToppingBtn);

        categories = new ArrayList<>();
        sizes = new ArrayList<>();
        toppings = new ArrayList<>();

        categoryController = new CategoryController();
        sizeController = new SizeController();
        toppingController = new ToppingController();

        categoryAdapter = new ExtrasRvAdapter<>(categories, Category::getName, (category, position) -> {
            categoryController.deleteCategory(category.getId(), new OnResultListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    categories.remove(position);
                    categoryAdapter.notifyItemRemoved(position);
                    Toast.makeText(ManageExtrasActivity.this, "Category deleted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ManageExtrasActivity.this, "Failed to delete category", Toast.LENGTH_SHORT).show();
                }
            });
        });

        sizeAdapter = new ExtrasRvAdapter<>(sizes, Size::getName, (size, position) -> {
            sizeController.deleteSize(size.getId(), new OnResultListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    sizes.remove(position);
                    sizeAdapter.notifyItemRemoved(position);
                    Toast.makeText(ManageExtrasActivity.this, "Size deleted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ManageExtrasActivity.this, "Failed to delete size", Toast.LENGTH_SHORT).show();
                }
            });
        });

        toppingAdapter = new ExtrasRvAdapter<>(toppings, Topping::getName, (topping, position) -> {
            toppingController.deleteTopping(topping.getId(), new OnResultListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    toppings.remove(position);
                    toppingAdapter.notifyItemRemoved(position);
                    Toast.makeText(ManageExtrasActivity.this, "Topping deleted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ManageExtrasActivity.this, "Failed to delete topping", Toast.LENGTH_SHORT).show();
                }
            });
        });

        categoriesRv.setLayoutManager(new LinearLayoutManager(this));
        sizesRv.setLayoutManager(new LinearLayoutManager(this));
        toppingsRv.setLayoutManager(new LinearLayoutManager(this));

        categoriesRv.setAdapter(categoryAdapter);
        sizesRv.setAdapter(sizeAdapter);
        toppingsRv.setAdapter(toppingAdapter);

        categoryController.getAllCategories(new OnResultListener<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                if (!result.isEmpty()) {
                    categories.clear();
                    categories.addAll(result);
                    categoryAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(ManageExtrasActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageExtrasActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                Log.d("Error", "Fail to get the data. "+e);
            }
        });

        sizeController.getAllSizes(new OnResultListener<List<Size>>() {
            @Override
            public void onSuccess(List<Size> result) {
                if (!result.isEmpty()) {
                    sizes.clear();
                    sizes.addAll(result);
                    sizeAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(ManageExtrasActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageExtrasActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                Log.d("Error", "Fail to get the data. "+e);
            }
        });

        toppingController.getAllToppings(new OnResultListener<List<Topping>>() {
            @Override
            public void onSuccess(List<Topping> result) {
                if (!result.isEmpty()) {
                    toppings.clear();
                    toppings.addAll(result);
                    toppingAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(ManageExtrasActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageExtrasActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                Log.d("Error", "Fail to get the data. "+e);
            }
        });

        backBtn.setOnClickListener(view -> {onBackPressed();});

        addCategoriesBtn.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);

            EditText nameTxt = dialogView.findViewById(R.id.nameTxt);
            Button saveBtn = dialogView.findViewById(R.id.saveBtn);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();

            saveBtn.setOnClickListener(view -> {
                String name = nameTxt.getText().toString();
                if (name.isEmpty()) {
                    nameTxt.setError("Required");
                    return;
                }
                else {
                    Category category = new Category(null, name);
                    categoryController.createCategory(category, new OnResultListener<Category>() {
                        @Override
                        public void onSuccess(Category result) {
                            categories.add(result);
                            categoryAdapter.notifyItemInserted(categories.size() - 1);
                            Toast.makeText(ManageExtrasActivity.this, "data added.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ManageExtrasActivity.this, "Fail to add the data.", Toast.LENGTH_SHORT).show();
                            Log.d("Error", "Fail to add the data. "+e);
                        }
                    });
                }
                dialog.dismiss();
            });
            dialog.show();
        });

        addSizesBtn.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_size, null);

            EditText nameTxt = dialogView.findViewById(R.id.nameTxt);
            EditText priceTxt = dialogView.findViewById(R.id.priceTxt);
            Button saveBtn = dialogView.findViewById(R.id.saveBtn);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();

            saveBtn.setOnClickListener(view -> {
                String name = nameTxt.getText().toString();
                String price = priceTxt.getText().toString();
                if (name.isEmpty()) {
                    nameTxt.setError("Required");
                    return;
                }
                else if(price.isEmpty()){
                    priceTxt.setError("Required");
                    return;
                }
                else {
                    Size size = new Size(null, name, price);
                    sizeController.createSize(size, new OnResultListener<Size>() {
                        @Override
                        public void onSuccess(Size result) {
                            sizes.add(result);
                            sizeAdapter.notifyItemInserted(sizes.size() - 1);
                            Toast.makeText(ManageExtrasActivity.this, "data added.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ManageExtrasActivity.this, "Fail to add the data.", Toast.LENGTH_SHORT).show();
                            Log.d("Error", "Fail to add the data. "+e);
                        }
                    });
                }
                dialog.dismiss();
            });
            dialog.show();
        });

        addToppingsBtn.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_topping, null);

            EditText nameTxt = dialogView.findViewById(R.id.nameTxt);
            EditText priceTxt = dialogView.findViewById(R.id.priceTxt);
            Button saveBtn = dialogView.findViewById(R.id.saveBtn);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();

            saveBtn.setOnClickListener(view -> {
                String name = nameTxt.getText().toString();
                String price = priceTxt.getText().toString();
                if (name.isEmpty()) {
                    nameTxt.setError("Required");
                    return;
                }
                else if(price.isEmpty()){
                    priceTxt.setError("Required");
                    return;
                }
                else {
                    Topping topping = new Topping(null, name, price);
                    toppingController.createTopping(topping, new OnResultListener<Topping>() {
                        @Override
                        public void onSuccess(Topping result) {
                            toppings.add(result);
                            toppingAdapter.notifyItemInserted(toppings.size() - 1);
                            Toast.makeText(ManageExtrasActivity.this, "data added.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ManageExtrasActivity.this, "Fail to add the data.", Toast.LENGTH_SHORT).show();
                            Log.d("Error", "Fail to add the data. "+e);
                        }
                    });
                }
                dialog.dismiss();
            });
            dialog.show();
        });
    }

}
