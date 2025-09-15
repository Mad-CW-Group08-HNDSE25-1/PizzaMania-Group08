package com.androidapp.pizzamania.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Order implements Serializable {
    @Exclude
    private String id;
    private String total, branch, status;

    public Order() {
    }

    public Order(String total, String branch, String status) {
        this.total = total;
        this.branch = branch;
        this.status = status;
    }

    public Order(String total, String branch) {
        this.total = total;
        this.branch = branch;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
