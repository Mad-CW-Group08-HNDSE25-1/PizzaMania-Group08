package com.androidapp.pizzamania.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.R;
import com.androidapp.pizzamania.controller.MenuItemController;
import com.androidapp.pizzamania.model.MenuItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MenuItemCardRvAdapter extends RecyclerView.Adapter<MenuItemCardRvAdapter.ViewHolder>{
    private ArrayList<MenuItem> itemArrayList;
    private Context context;
    private MenuItemController menuItemController = new MenuItemController();

    public MenuItemCardRvAdapter(ArrayList<MenuItem> itemArrayList, Context context) {
        this.itemArrayList = itemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuItemCardRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuItemCardRvAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_menu_item_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemCardRvAdapter.ViewHolder holder, int position) {

        MenuItem menuItem = itemArrayList.get(position);
        holder.nameTxt.setText(menuItem.getName());
        holder.totalTxt.setText(menuItem.getPrice());
        holder.qtyTxt.setText(String.valueOf(holder.qty));
        holder.qty = 0;
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
        private TextView nameTxt, totalTxt, qtyTxt;
        private int qty = 0;
        private Button plusBtn, minusBtn, addToCartBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            totalTxt = itemView.findViewById(R.id.totalTxt);
            qtyTxt = itemView.findViewById(R.id.qtyTxt);
            plusBtn = itemView.findViewById(R.id.plusBtn);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION){
                    MenuItem menuItem = itemArrayList.get(getAdapterPosition());
                    Intent intent = new Intent(context, MenuItemDetailActivity.class);
                    intent.putExtra("itemId", menuItem.getId());
                    context.startActivity(intent);
                }

            });

            plusBtn.setOnClickListener(view -> {
                qty ++;
                qtyTxt.setText(String.valueOf(qty));
            });

            minusBtn.setOnClickListener(view -> {
                if(qty > 0){
                    qty --;
                    qtyTxt.setText(String.valueOf(qty));
                }
                else {
                    qty = 0;
                    qtyTxt.setText(String.valueOf(qty));
                }

            });

            addToCartBtn.setOnClickListener(view -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    MenuItem menuItem = itemArrayList.get(getAdapterPosition());
                    Intent intent = new Intent(context, MenuItemDetailActivity.class);
                    intent.putExtra("itemId", menuItem.getId());
                    intent.putExtra("itemPrice", menuItem.getPrice());
                    intent.putExtra("itemQty", qty);
                    context.startActivity(intent);
                }
            });
        }
    }

    public interface ItemClickInterface {
        void onItemClick(int position);
    }
}

