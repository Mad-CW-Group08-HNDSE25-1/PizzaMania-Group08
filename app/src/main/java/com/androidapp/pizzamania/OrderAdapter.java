package com.androidapp.pizzamania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<OrderModel> orderList;
    private OnReorderClickListener listener;

    public interface OnReorderClickListener {
        void onReorderClick(OrderModel order);
    }

    public OrderAdapter(List<OrderModel> orderList, OnReorderClickListener listener) {
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
        OrderModel order = orderList.get(position);
        holder.tvOrderId.setText("Order #" + order.getOrderId());
        holder.tvStatus.setText("Status: " + order.getStatus());
        holder.tvPrice.setText("Total: Rs. " + order.getTotalPrice());
        holder.tvDate.setText("Date: " + order.getCreatedAt());

        // Color code status
        switch (order.getStatus().toLowerCase()) {
            case "pending":
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_orange_dark));
                break;
            case "preparing":
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_blue_dark));
                break;
            case "delivered":
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_green_dark));
                break;
            default:
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.black));
                break;
        }

        holder.btnReorder.setOnClickListener(v -> listener.onReorderClick(order));
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvPrice, tvDate;
        Button btnReorder;
        public ViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnReorder = itemView.findViewById(R.id.btnReorder);
        }
    }
}
