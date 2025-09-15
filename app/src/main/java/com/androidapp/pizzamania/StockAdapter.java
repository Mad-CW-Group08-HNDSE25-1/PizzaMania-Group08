package com.androidapp.pizzamania;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private Context context;
    private List<StockItem> stockList;
    private Map<String, BranchesDTO> branchMap;
    private int lowStockThreshold = 5;

    public StockAdapter(Context context, List<StockItem> stockList, Map<String, BranchesDTO> branchMap) {
        this.context = context;
        this.stockList = stockList;
        this.branchMap = branchMap;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stock, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        StockItem stock = stockList.get(position);
        holder.tvItemName.setText(stock.getItemName());
        holder.tvQuantity.setText("Qty: " + stock.getQuantity());

        // Low stock highlighting
        if (stock.getQuantity() <= lowStockThreshold) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFCDD2"));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        // Display nearest branch
        BranchesDTO branch = branchMap.get(stock.getBranchId());
        if (branch != null) {
            holder.tvNearestBranch.setText("Available at: " + branch.getBranchName());
        }

        // Increase stock
        holder.btnIncrease.setOnClickListener(v -> updateStock(stock, stock.getQuantity() + 1));

        // Decrease stock
        holder.btnDecrease.setOnClickListener(v -> {
            int newQty = stock.getQuantity() - 1;
            if (newQty >= 0) updateStock(stock, newQty);
        });
    }

    private void updateStock(StockItem stock, int newQty) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("BranchStock")
                .child(stock.getStockId());
        ref.child("quantity").setValue(newQty)
                .addOnSuccessListener(aVoid -> {
                    stock.setQuantity(newQty);
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to update stock", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    static class StockViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvQuantity, tvNearestBranch;
        Button btnIncrease, btnDecrease;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvNearestBranch = itemView.findViewById(R.id.tvNearestBranch);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
 }
}
}
