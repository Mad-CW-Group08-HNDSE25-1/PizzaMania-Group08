package com.androidapp.pizzamania.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.R;
import com.androidapp.pizzamania.UpdateMenuItemActivity;
import com.androidapp.pizzamania.controller.MenuItemController;
import com.androidapp.pizzamania.model.MenuItem;

import java.util.ArrayList;

public class MenuItemRvAdapter extends RecyclerView.Adapter<MenuItemRvAdapter.ViewHolder>{
    private ArrayList<MenuItem> menuItemsArrayList;
    private Context context;
    private MenuItemController menuItemController = new MenuItemController();

    public MenuItemRvAdapter(ArrayList<MenuItem> menuItemsArrayList, Context context) {
        this.menuItemsArrayList = menuItemsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuItemRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemRvAdapter.ViewHolder holder, int position) {
        MenuItem menuItem = menuItemsArrayList.get(position);
        holder.itemNameTv.setText(menuItem.getName());
        holder.itemPriceTv.setText(menuItem.getPrice());
        holder.itemDescriptionTv.setText(menuItem.getDescription());
    }

    @Override
    public int getItemCount(){
        return menuItemsArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView itemNameTv;
        private final TextView itemDescriptionTv;
        private final TextView itemPriceTv;
        private Button deleteBtn;

        public ViewHolder(View itemView){
            super(itemView);

            itemNameTv = itemView.findViewById(R.id.itemNametxt);
            itemPriceTv = itemView.findViewById(R.id.itemPricetxt);
            itemDescriptionTv = itemView.findViewById(R.id.itemDescriptiontxt);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

            deleteBtn.setOnClickListener(view -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION){
                    MenuItem menuItem = menuItemsArrayList.get(getAdapterPosition());

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Warning!!");
                    builder.setMessage("Are you sure you want to delete?");
                    builder.setCancelable(false);

                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener)(dialog, which) -> {
                        menuItemController.deleteMenuItem(menuItem.getId())
                                .addOnSuccessListener(task -> {
                                    menuItemsArrayList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Item has been deleted from Database.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Fail to delete the course. ", Toast.LENGTH_SHORT).show();
                                    Log.d("Error", "Fail to delete the course. "+e);
                                });
                    });

                    builder.setNegativeButton("No", (DialogInterface.OnClickListener)(dialog, which) -> {
                        dialog.cancel();
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION){
                    MenuItem menuItem = menuItemsArrayList.get(getAdapterPosition());
                    Intent intent = new Intent(context, UpdateMenuItemActivity.class);
                    intent.putExtra("menuItemId", menuItem.getId());
                    context.startActivity(intent);
                }

            });
        }
    }


}


