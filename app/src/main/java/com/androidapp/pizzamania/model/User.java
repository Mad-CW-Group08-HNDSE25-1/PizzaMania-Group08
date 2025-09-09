package com.androidapp.pizzamania.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class User implements Serializable {
    @Exclude
    private String id;
    private String name, email, phone, role, branch, imageURL;

    public User() {
    }

    public User(String name, String email, String phone, String role, String imageURL) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.imageURL = imageURL;
    }

    public User(String name, String email, String phone, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
