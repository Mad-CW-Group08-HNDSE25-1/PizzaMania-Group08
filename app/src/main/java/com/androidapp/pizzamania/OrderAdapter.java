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

    private List<OrderItem> orderList;
    private OnReorderListener listener;

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
        holder.tvOrderId.setText("Order: " + order.getOrderId());
        holder.tvTotal.setText("Total: Rs. " + order.getTotalPrice());
        holder.tvStatus.setText("Status: " + order.getStatus());
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(order.getCreatedAt()));
        holder.tvDate.setText("Date: " + date);

        holder.btnReorder.setOnClickListener(v -> listener.onReorder(order));
    }

    @Override
    public int getItemCount() { return orderList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvTotal, tvStatus, tvDate;
        Button btnReorder;

        public ViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            btnReorder = itemView.findViewById(R.id.btnReorder);
        }
    }
}
