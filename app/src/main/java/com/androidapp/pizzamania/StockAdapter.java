package com.androidapp.pizzamania;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private List<StockModel> stockList;
    private OnStockChangeListener listener;

    public interface OnStockChangeListener {
        void onStockChanged(StockModel item, int newQty);
    }

    public StockAdapter(List<StockModel> stockList, OnStockChangeListener listener) {
        this.stockList = stockList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StockModel item = stockList.get(position);

        holder.tvItemName.setText(item.getName());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Highlight low stock
        if (item.getQuantity() < 5) {
            holder.tvQuantity.setTextColor(Color.RED);
        } else {
            holder.tvQuantity.setTextColor(Color.BLACK);
        }

        holder.btnIncrease.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            item.setQuantity(newQty);
            holder.tvQuantity.setText(String.valueOf(newQty));
            listener.onStockChanged(item, newQty);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int newQty = Math.max(0, item.getQuantity() - 1);
            item.setQuantity(newQty);
            holder.tvQuantity.setText(String.valueOf(newQty));
            listener.onStockChanged(item, newQty);
        });
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvQuantity;
        ImageButton btnIncrease, btnDecrease;
        public ViewHolder(View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }
}
