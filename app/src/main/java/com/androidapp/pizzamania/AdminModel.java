package com.androidapp.pizzamania;

public class AdminModel {
    private String name, email, phone, branch, role, userId, profileImageUrl;

    public AdminModel() { }

    public AdminModel(String name, String email, String phone, String branch, String role, String userId, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.branch = branch;
        this.role = role;
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl;}
}
