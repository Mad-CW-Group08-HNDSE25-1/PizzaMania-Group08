package com.androidapp.pizzamania.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.EditMenuItemActivity;
import com.androidapp.pizzamania.MenuItemDetailFragment;
import com.androidapp.pizzamania.R;
import com.androidapp.pizzamania.controller.MenuItemController;
import com.androidapp.pizzamania.model.MenuItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class  OffersRvAdapter extends RecyclerView.Adapter<OffersRvAdapter.OfferViewHolder> {

    private ArrayList<MenuItem> itemArrayList;
    private Context context;
    private MenuItemController menuItemController = new MenuItemController();

    public OffersRvAdapter(ArrayList<MenuItem> itemArrayList, Context context) {
        this.itemArrayList = itemArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
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

    public class OfferViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView nameTxt, priceTxt;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.offerImage);
            nameTxt = itemView.findViewById(R.id.offerTitle);
            priceTxt = itemView.findViewById(R.id.offerPrice);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    MenuItem menuItem = itemArrayList.get(getAdapterPosition());
                    Intent intent = new Intent(context, MenuItemDetailFragment.class);
                    intent.putExtra("itemId", menuItem.getId());
                    context.startActivity(intent);
                }

            });
        }
    }
}
