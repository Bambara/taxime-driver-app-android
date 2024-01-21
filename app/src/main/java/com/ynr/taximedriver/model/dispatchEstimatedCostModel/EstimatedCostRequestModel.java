package com.ynr.taximedriver.model.dispatchEstimatedCostModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EstimatedCostRequestModel {
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
    @SerializedName("pickupTime")
    private String pickupTime;
    @Expose
    @SerializedName("distance")
    private double distance;
    @Expose
    @SerializedName("biddingPrice")
    private int biddingPrice;
    @Expose
    @SerializedName("categoty")
    private String categoty;
    @Expose
    @SerializedName("subCategory")
    private String subCategory;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getBiddingPrice() {
        return biddingPrice;
    }

    public void setBiddingPrice(int biddingPrice) {
        this.biddingPrice = biddingPrice;
    }

    public String getCategoty() {
        return categoty;
    }

    public void setCategoty(String categoty) {
        this.categoty = categoty;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
}
