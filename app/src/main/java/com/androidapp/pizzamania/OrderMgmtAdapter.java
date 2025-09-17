package com.androidapp.pizzamania;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.model.Order;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OrderMgmtAdapter extends RecyclerView.Adapter<OrderMgmtAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;
    private String branchId;

    public OrderMgmtAdapter(Context context, List<Order> orderList, String branchId) {
        this.context = context;
        this.orderList = orderList;
        this.branchId = branchId;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.order_mgmt_items, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderIdTxt.setText("Order ID: " + order.getOrderID());
        holder.totalPriceTxt.setText("Total: Rs. " + order.getTotalAmount());
        holder.statusTxt.setText("Status: " + order.getOrderStatus());

        // Cancel order
        holder.cancelBtn.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("orders")
                    .child(order.getOrderID());
            ref.removeValue();
            orderList.remove(position);
            notifyItemRemoved(position);
        });

        // Update Status
        holder.updateStatusBtn.setOnClickListener(v -> {
            String newStatus = getNextStatus(order.getOrderStatus());
            order.setOrderStatus(newStatus);

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("orders")
                    .child(order.getOrderID());
            ref.child("orderStatus").setValue(newStatus);

            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String getNextStatus(String currentStatus) {
        switch (currentStatus.toLowerCase()) {
            case "pending": return "Confirmed";
            case "confirmed": return "On the way";
            case "on the way": return "Completed";
            default: return "Completed";
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTxt, totalPriceTxt, statusTxt;
        Button updateStatusBtn, cancelBtn;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTxt = itemView.findViewById(R.id.orderIdTxt);
            totalPriceTxt = itemView.findViewById(R.id.totalPriceTxt);
            statusTxt = itemView.findViewById(R.id.statusTxt);
            updateStatusBtn = itemView.findViewById(R.id.updateStatusBtn);
            cancelBtn = itemView.findViewById(R.id.cancelBtn);
        }
    }
}
