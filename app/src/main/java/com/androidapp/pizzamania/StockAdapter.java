package com.androidapp.pizzamania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    private List<StockItem> stockList;
    private OnStockChangeListener listener;

    public interface OnStockChangeListener {
        void onStockChanged(StockItem item, int newQuantity);
    }

    public StockAdapter(List<StockItem> stockList, OnStockChangeListener listener) {
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
        StockItem item = stockList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvBranch.setText(item.getBranchName());
        holder.tvQty.setText(String.valueOf(item.getQuantity()));

        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            listener.onStockChanged(item, item.getQuantity());
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                listener.onStockChanged(item, item.getQuantity());
            }
        });
    }

    @Override
    public int getItemCount() { return stockList.size(); }

    public void updateList(List<StockItem> newList) {
        stockList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBranch, tvQty;
        ImageButton btnPlus, btnMinus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvBranch = itemView.findViewById(R.id.tvBranchName);
            tvQty = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}
