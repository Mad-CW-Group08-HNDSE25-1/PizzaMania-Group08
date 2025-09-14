package com.androidapp.pizzamania;

public class CartItem {
    private int id;
    private String itemId;
    private String name;
    private double price;
    private int quantity;

    public CartItem(int id, String itemId, String name, double price, int quantity) {
        this.id = id;
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters
    public int getId() { return id; }
    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

