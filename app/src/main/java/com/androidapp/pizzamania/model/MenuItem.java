package com.androidapp.pizzamania.model;


import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class MenuItem implements Serializable {
    @Exclude
    private String id;
    private String name, description, price, branch, imageURL;


    public MenuItem() {
    }

    public MenuItem(String id, String name, String description, String price, String branch, String imageURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.branch = branch;
        this.imageURL = imageURL;
    }

    public MenuItem(String name, String description, String price, String branch) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.branch = branch;
    }

    public MenuItem(String name, String description, String price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
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

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
