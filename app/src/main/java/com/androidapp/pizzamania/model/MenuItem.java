package com.androidapp.pizzamania.model;


import java.util.Map;

public class MenuItem {
    private String id, name, description, price, image, branchId, categoryId;
    private Map<String, Boolean> sizes, toppings;

    public MenuItem() {
    }

    public MenuItem(String id, String name, String description, String price, String branchId, String categoryId, Map<String, Boolean> sizes, Map<String, Boolean> toppings, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.branchId = branchId;
        this.categoryId = categoryId;
        this.sizes = sizes;
        this.toppings = toppings;
    }

    public MenuItem(String id, String name, String categoryId) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Map<String, Boolean> getSizes() {
        return sizes;
    }

    public void setSizes(Map<String, Boolean> sizes) {
        this.sizes = sizes;
    }

    public Map<String, Boolean> getToppings() {
        return toppings;
    }

    public void setToppings(Map<String, Boolean> toppings) {
        this.toppings = toppings;
    }
}

