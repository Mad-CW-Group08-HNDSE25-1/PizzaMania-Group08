package com.androidapp.pizzamania;

public class BranchesDTO {

    private String key;
    private String branchName;
    private String branchAddress;
    private double latitude;
    private double longitude;

    public BranchesDTO() {

    }

    public BranchesDTO(String key, String branchName, String branchAddress, double latitude, double longitude) {
        this.key = key;
        this.branchName = branchName;
        this.branchAddress = branchAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
