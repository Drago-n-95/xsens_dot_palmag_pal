package com.xsens.dot.android.example.adapters;

public class MfmData {
    private int progressPercentage;
    private String Address;

    public MfmData(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}

