package com.androidapp.pizzamania;

public class AdminModel {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String branch;
    private String profileUrl;

    public AdminModel() {} // Required for Firebase

    public AdminModel(String id, String name, String email, String phone, String role, String branch, String profileUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.branch = branch;
        this.profileUrl = profileUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getBranch() { return branch; }
    public String getProfileUrl() { return profileUrl;}
}
