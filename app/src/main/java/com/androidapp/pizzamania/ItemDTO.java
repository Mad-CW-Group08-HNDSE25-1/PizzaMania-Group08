package com.androidapp.pizzamania;

public class ItemDTO {

    private int orderID;
    private String itemID;
    private int qty;
    private double price;
    private double totalPerItem;

    public ItemDTO() {

    }

    public ItemDTO(int orderID, String itemID, int qty, double price, double totalPerItem) {
        this.orderID = orderID;
        this.itemID = itemID;
        this.qty = qty;
        this.price = price;
        this.totalPerItem = totalPerItem;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPerItem() {
        return totalPerItem;
    }

    public void setTotalPerItem(double totalPerItem) {
        this.totalPerItem = totalPerItem;
    }
}
