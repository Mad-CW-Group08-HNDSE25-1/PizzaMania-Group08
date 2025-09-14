package com.androidapp.pizzamania.model;

public class MenuItem {
    private String branchId, category, description, name, price;

    public MenuItem() {
    }

    public MenuItem(String branchId, String category, String description, String name, String price) {
        this.branchId = branchId;
        this.category = category;
        this.description = description;
        this.name = name;
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
