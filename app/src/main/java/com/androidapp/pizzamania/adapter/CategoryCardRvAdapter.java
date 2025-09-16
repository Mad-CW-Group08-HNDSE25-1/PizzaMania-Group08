package com.androidapp.pizzamania.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.R;
import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.controller.CategoryController;
import com.androidapp.pizzamania.controller.MenuItemController;
import com.androidapp.pizzamania.model.Category;
import com.androidapp.pizzamania.model.MenuItem;
import com.androidapp.pizzamania.ui.menu.MenuItemCardRvAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryCardRvAdapter extends RecyclerView.Adapter<CategoryCardRvAdapter.ViewHolder>{
    private ArrayList<Category> itemArrayList;
    private static Context context;
    private CategoryController categoryController = new CategoryController();

    public CategoryCardRvAdapter(ArrayList<Category> itemArrayList, Context context) {
        this.itemArrayList = itemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryCardRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryCardRvAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryCardRvAdapter.ViewHolder holder, int position) {

        Category category = itemArrayList.get(position);
        holder.nameTxt.setText(category.getName());
        holder.loadMenuItems(category.getId());
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public void updateData(List<Category> newList) {
        itemArrayList.clear();
        itemArrayList.addAll(newList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTxt;
        private RecyclerView menuItemCardRv;
        private ArrayList<MenuItem> itemArrayList;
        private MenuItemCardRvAdapter menuItemCardRvAdapter;
        private MenuItemController menuItemController;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            menuItemCardRv = itemView.findViewById(R.id.menuItemCardRv);
            menuItemController = new MenuItemController();

            itemArrayList = new ArrayList<>();
            menuItemCardRv.setHasFixedSize(true);
            menuItemCardRv.setLayoutManager(new LinearLayoutManager(context));

            menuItemCardRvAdapter = new MenuItemCardRvAdapter(itemArrayList, context);
            menuItemCardRv.setAdapter(menuItemCardRvAdapter);

            menuItemController.getAllItems(new OnResultListener<List<MenuItem>>() {
                @Override
                public void onSuccess(List<MenuItem> result) {
                    if (!result.isEmpty()) {
                        itemArrayList.clear();
                        itemArrayList.addAll(result);
                        menuItemCardRvAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Fail to get the data. "+e);
                }
            });
        }

        public void loadMenuItems(String categoryId) {
            menuItemController.getAllItemsByCategoryId(categoryId, new OnResultListener<List<MenuItem>>() {
                @Override
                public void onSuccess(List<MenuItem> result) {
                    itemArrayList.clear();
                    if (!result.isEmpty()) {
                        itemArrayList.addAll(result);
                    }
                    menuItemCardRvAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "Failed to get menu items", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Fail to get menu items. " + e);
                }
            });
        }

        public interface ItemClickInterface {
            void onItemClick(int position);
        }
    }
}

