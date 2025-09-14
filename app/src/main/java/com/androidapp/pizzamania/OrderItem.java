package com.androidapp.pizzamania;

import java.util.Map;

public class OrderItem {
    private String orderId;
    private String userId;
    private Map<String, Integer> items; // itemId -> quantity
    private double totalPrice;
    private String status;
    private long createdAt;

    public OrderItem() { } // Required for Firebase

    public OrderItem(String orderId, String userId, Map<String, Integer> items,
                     double totalPrice, String status, long createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public Map<String, Integer> getItems() { return items; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public long getCreatedAt() { return createdAt; }
}
