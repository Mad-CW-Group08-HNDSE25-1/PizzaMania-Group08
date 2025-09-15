package com.androidapp.pizzamania;

public class StockItem {
    private String stockId;
    private String itemName;
    private String branchId;
    private int quantity;

    public StockItem() { }

    public StockItem(String stockId, String itemName, String branchId, int quantity) {
        this.stockId = stockId;
        this.itemName = itemName;
        this.branchId = branchId;
        this.quantity = quantity;
    }

    public String getStockId() { return stockId; }
    public void setStockId(String stockId) { this.stockId = stockId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity=quantity;}
}
