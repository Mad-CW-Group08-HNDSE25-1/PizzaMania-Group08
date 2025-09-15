package com.androidapp.pizzamania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<OrderItem> orderList;
    private final OnReorderListener listener;

    // Listener interface for reorder button
    public interface OnReorderListener { void onReorder(OrderItem order); }

    public OrderAdapter(List<OrderItem> orderList, OnReorderListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem order = orderList.get(position);

        holder.tvOrderId.setText("Order: " + order.getOrderID());
        holder.tvOrderTotal.setText("Total: Rs. " + order.getTotalAmount());
        holder.tvOrderStatus.setText("Status: " + order.getOrderStatus());

        // Format timestamp
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(order.getCreatedAt()));
        holder.tvOrderDate.setText("Date: " + date);

        // Reorder button
        holder.btnReorder.setOnClickListener(v -> listener.onReorder(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderTotal, tvOrderStatus, tvOrderDate;
        Button btnReorder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            btnReorder = itemView.findViewById(R.id.btnReorder);
        }
    }
}
