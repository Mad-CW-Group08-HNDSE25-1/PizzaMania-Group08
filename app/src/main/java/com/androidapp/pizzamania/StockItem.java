package com.androidapp.pizzamania;

public class StockItem {
    private String itemId;
    private String name;
    private String branchName;
    private int quantity;

    public StockItem(String itemId, String name, String branchName, int quantity) {
        this.itemId = itemId;
        this.name = name;
        this.branchName = branchName;
        this.quantity = quantity;
    }

    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public String getBranchName() { return branchName; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}
