package com.androidapp.pizzamania;

public class AdminModel {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;
    private String role;
    private String branch;
    private String lastLogin;

    public AdminModel() {
    }

    public AdminModel(String userId, String name, String email, String phone,
                      String profileImageUrl, String role, String branch, String lastLogin) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.branch = branch;
        this.lastLogin = lastLogin;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin;}
}
