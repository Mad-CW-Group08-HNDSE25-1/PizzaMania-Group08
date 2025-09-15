package com.androidapp.pizzamania;

public class AdminModel {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String branch;
    private String profileUrl;

    public Admin() {} // Needed for Firebase

    public Admin(String id, String name, String email, String phone, String role, String branch, String profileUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.branch = branch;
        this.profileUrl = profileUrl;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl =profileUrl;}
}
