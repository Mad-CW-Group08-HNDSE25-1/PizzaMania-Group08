package com.androidapp.pizzamania;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartList;
    private OnCartChangeListener listener;
    private DatabaseHelper dbHelper;

    // Listener interface for CartActivity to update total
    public interface OnCartChangeListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<CartItem> cartList, OnCartChangeListener listener) {
        this.cartList = cartList;
        this.listener = listener;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        // Set item data
        holder.tvName.setText(item.getName());
        holder.tvQty.setText(String.valueOf(item.getQuantity()));
        holder.tvPrice.setText(String.format("Rs. %.2f", item.getTotalPrice()));


        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            updateItemInDB(item);
            notifyItemChanged(position);
            listener.onCartChanged();
        });


        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                updateItemInDB(item);
                notifyItemChanged(position);
                listener.onCartChanged();
            }
        });


        holder.btnDelete.setOnClickListener(v -> {
            removeItemFromDB(item);
            cartList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartList.size());
            listener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }


    private void updateItemInDB(CartItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", item.getQuantity());
        db.update(DatabaseHelper.TABLE_CART, values, "itemId=?", new String[]{item.getItemId()});
        db.close();
    }


    private void removeItemFromDB(CartItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_CART, "itemId=?", new String[]{item.getItemId()});
        db.close();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvPrice;
        ImageButton btnPlus, btnMinus, btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvQty = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}