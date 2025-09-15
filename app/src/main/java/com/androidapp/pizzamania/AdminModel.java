package com.androidapp.pizzamania;

public class AdminModel {
    private String name, email, role, profileImageUrl;

    public AdminModel() {
        // Firebase needs empty constructor
    }

    public AdminModel(String name, String email, String role, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getProfileImageUrl() { return profileImageUrl;}
}