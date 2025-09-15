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

    public interface OnReorderListener {
        void onReorder(OrderItem order);
    }

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

        holder.tvOrderId.setText("Order: " + safeString(order.getOrderID()));
        holder.tvOrderTotal.setText("Total: Rs. " + order.getTotalAmount());
        holder.tvOrderStatus.setText("Status: " + safeString(order.getOrderStatus()));

        String dateText = "Unknown date";
        try {
            long timeStamp = Long.parseLong(order.getCreatedAt());
            if (timeStamp > 0) {
                dateText = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(new Date(timeStamp));
            }
        } catch (Exception e) {
            dateText = "Unknown date";
        }
        holder.tvOrderDate.setText("Date: " + dateText);

        holder.btnReorder.setOnClickListener(v -> listener.onReorder(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String safeString(String value) {
        return value != null ? value : "";
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