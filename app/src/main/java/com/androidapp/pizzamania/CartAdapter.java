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

        holder.tvName.setText(item.getName());
        holder.tvQty.setText(String.valueOf(item.getQuantity()));
        holder.tvPrice.setText("Rs. " + item.getTotalPrice());

        //  Increase quantity (+)
        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            updateItemInDB(item); //  Update SQLite
            notifyItemChanged(position);
            listener.onCartChanged();
        });

        // Decrease quantity (–)
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                updateItemInDB(item); // Update SQLite
                notifyItemChanged(position);
                listener.onCartChanged();
            }
        });

        //  Delete item
        holder.btnDelete.setOnClickListener(v -> {
            removeItemFromDB(item); //  Remove from SQLite
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

    //  Update existing item in SQLite
    private void updateItemInDB(CartItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", item.getQuantity());
        db.update("Cart", values, "itemId=?", new String[]{item.getItemId()});
        db.close();
    }

    //  Remove item from SQLite
    private void removeItemFromDB(CartItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Cart", "itemId=?", new String[]{item.getItemId()});
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
