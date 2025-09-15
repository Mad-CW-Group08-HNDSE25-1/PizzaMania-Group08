package com.androidapp.pizzamania;

import java.util.List;

public class OrderItem {

    public static class Item {
        private String itemID;
        private int qty;
        private double price;

        public Item() { }

        public Item(String itemID, int qty, double price) {
            this.itemID = itemID;
            this.qty = qty;
            this.price = price;
        }

        public String getItemID() { return itemID; }
        public int getQty() { return qty; }
        public double getPrice() { return price; }
    }

    public static class Location {
        private double latitude;
        private double longitude;

        public Location() { }
        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
    }

    private String orderID;
    private String userID;
    private String branchID;
    private List<Item> itemList;
    private double totalAmount;
    private String orderStatus;
    private String createdAt;
    private Location location;

    public OrderItem() { }

    public OrderItem(String orderID, String userID, String branchID, List<Item> itemList,
                     double totalAmount, String orderStatus, String createdAt, Location location) {
        this.orderID = orderID;
        this.userID = userID;
        this.branchID = branchID;
        this.itemList = itemList;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.location = location;
    }

    public String getOrderID() { return orderID; }
    public String getUserID() { return userID; }
    public String getBranchID() { return branchID; }
    public List<Item> getItemList() { return itemList; }
    public double getTotalAmount() { return totalAmount; }
    public String getOrderStatus() { return orderStatus; }
    public String getCreatedAt() { return createdAt; }
    public Location getLocation() { return location;}
}
