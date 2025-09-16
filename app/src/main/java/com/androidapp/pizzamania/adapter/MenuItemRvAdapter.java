package com.androidapp.pizzamania.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.EditMenuItemActivity;
import com.androidapp.pizzamania.R;
import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.controller.MenuItemController;
import com.androidapp.pizzamania.model.MenuItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MenuItemRvAdapter extends RecyclerView.Adapter<MenuItemRvAdapter.ViewHolder> {
    private ArrayList<MenuItem> itemArrayList;
    private Context context;
    private MenuItemController menuItemController = new MenuItemController();

    public MenuItemRvAdapter(ArrayList<MenuItem> itemArrayList, Context context) {
        this.itemArrayList = itemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuItemRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuItemRvAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_item_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemRvAdapter.ViewHolder holder, int position) {

        MenuItem menuItem = itemArrayList.get(position);
        holder.nameTxt.setText(menuItem.getName());
        holder.priceTxt.setText(menuItem.getPrice());
        Glide.with(holder.itemView.getContext())
                .load(menuItem.getImage())
                .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public void updateData(List<MenuItem> newList) {
        itemArrayList.clear();
        itemArrayList.addAll(newList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView nameTxt, priceTxt;
        private Button deleteItemBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            deleteItemBtn = itemView.findViewById(R.id.deleteItemBtn);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    MenuItem menuItem = itemArrayList.get(getAdapterPosition());
                    Intent intent = new Intent(context, EditMenuItemActivity.class);
                    intent.putExtra("itemId", menuItem.getId());
                    context.startActivity(intent);
                }

            });

            deleteItemBtn.setOnClickListener(view -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    MenuItem menuItem = itemArrayList.get(getAdapterPosition());

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Warning!!");
                    builder.setMessage("Are you sure you want to delete?");
                    builder.setCancelable(false);

                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        menuItemController.deleteItem(menuItem.getId(), new OnResultListener<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                itemArrayList.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "item has been deleted from Database.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(context, "Fail to delete the item. ", Toast.LENGTH_SHORT).show();
                                Log.d("Error", "Fail to delete the item. " + e);
                            }
                        });
                    });

                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
    }

    public interface ItemClickInterface {
        void onItemClick(int position);
    }
}





