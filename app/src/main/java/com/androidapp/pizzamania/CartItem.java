package com.androidapp.pizzamania;

public class CartItem {
    private String itemId;
    private String name;
    private double price;
    private int quantity;

    // Required for Firebase
    public CartItem() {}

    public CartItem(String itemId, String name, double price, int quantity) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() {
        return price * quantity;
    }
}