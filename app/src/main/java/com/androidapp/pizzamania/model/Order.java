package com.androidapp.pizzamania.model;

public class Order {
    private String orderID;      // matches Firebase key
    private String branchID;     // matches Firebase branch field
    private String userID;
    private double totalAmount;  // matches Firebase field
    private String orderStatus;  // matches Firebase field

    public Order() {} // Required empty constructor for Firebase

    public Order(String orderID, String branchID, String userID, double totalAmount, String orderStatus) {
        this.orderID = orderID;
        this.branchID = branchID;
        this.userID = userID;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
    }

    // Getters and Setters
    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }

    public String getBranchID() { return branchID; }
    public void setBranchID(String branchID) { this.branchID = branchID; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
}
