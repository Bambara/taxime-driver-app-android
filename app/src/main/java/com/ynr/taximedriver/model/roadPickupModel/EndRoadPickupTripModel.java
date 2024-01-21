package com.ynr.taximedriver.model.roadPickupModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EndRoadPickupTripModel {
    @SerializedName("distance")
    @Expose
    private double distance;
    @SerializedName("adminCommission")
    @Expose
    private double adminCommission;
    @SerializedName("driverId")
    @Expose
    private String driverId;
    @SerializedName("paymentMethod")
    @Expose
    private String paymentMethod;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("totalCost")
    @Expose
    private double totalCost;
    @SerializedName("waitTime")
    @Expose
    private double waitTime;
    @SerializedName("waitingCost")
    @Expose
    private double waitingCost;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("dropLocation")
    @Expose
    private Location dropLocation;
    @SerializedName("pickupLocation")
    @Expose
    private Location pickupLocation;
    @SerializedName("tripTime")
    @Expose
    private double tripTime;

    //Additional properties for local use
    @SerializedName("pickupData")
    @Expose
    private StartRoadPickupTripResponceModel pickupData;

    public StartRoadPickupTripResponceModel getPickupData() {
        return pickupData;
    }

    public void setPickupData(StartRoadPickupTripResponceModel pickupData) {
        this.pickupData = pickupData;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(double waitTime) {
        this.waitTime = waitTime;
    }

    public double getWaitingCost() {
        return waitingCost;
    }

    public void setWaitingCost(double waitingCost) {
        this.waitingCost = waitingCost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(Location dropLocation) {
        this.dropLocation = dropLocation;
    }

    public double getAdminCommission() {
        return adminCommission;
    }

    public void setAdminCommission(double adminCommission) {
        this.adminCommission = adminCommission;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTripTime() {
        return tripTime;
    }

    public void setTripTime(double tripTime) {
        this.tripTime = tripTime;
    }

    public static class Location {
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("latitude")
        @Expose
        private double latitude;
        @SerializedName("longitude")
        @Expose
        private double longitude;

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
}
