package com.androidapp.pizzamania;

public class OrderModel {
    private String orderId;
    private String status;
    private double totalPrice;
    private String createdAt;

    public OrderModel(String orderId, String status, double totalPrice, String createdAt) {
        this.orderId = orderId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public double getTotalPrice() { return totalPrice; }
    public String getCreatedAt() { return createdAt; }
}
