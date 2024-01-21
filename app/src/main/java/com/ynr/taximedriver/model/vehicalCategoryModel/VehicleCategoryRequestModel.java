package com.ynr.taximedriver.model.vehicalCategoryModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehicleCategoryRequestModel {
    @Expose
    @SerializedName("address")
    private String address;
    @Expose
    @SerializedName("latitude")
    private double latitude;
    @Expose
    @SerializedName("longitude")
    private double longitude;
    @Expose
    @SerializedName("date")
    private String date;
    @Expose
    @SerializedName("time")
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
