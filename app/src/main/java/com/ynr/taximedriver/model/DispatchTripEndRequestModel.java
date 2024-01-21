package com.ynr.taximedriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DispatchTripEndRequestModel {
    @Expose
    @SerializedName("dispatchId")
    private String dispatchId;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("distance")
    private double distance;
    @Expose
    @SerializedName("waitingCost")
    private double waitingCost;
    @Expose
    @SerializedName("waitTime")
    private int waitTime;
    @Expose
    @SerializedName("estimatedCost")
    private double estimatedCost;
    @Expose
    @SerializedName("totalCost")
    private double totalCost;
    @Expose
    @SerializedName("dispatcherId")
    private String dispatcherId;
    @Expose
    @SerializedName("adminCommission")
    private double adminCommission;
    @Expose
    @SerializedName("driverId")
    private String driverId;
    @Expose
    @SerializedName("paymentMethod")
    private String paymentMethod;
    @Expose
    @SerializedName("realPickupLocation")
    private Location realPickupLocation ;
    @Expose
    @SerializedName("realDropLocation")
    private Location realDropLocation;
    @Expose
    @SerializedName("destinations")
    private List<Location> destinations;
    @Expose
    @SerializedName("tripTime")
    private String tripTime;

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public double getWaitingCost() {
        return waitingCost;
    }

    public void setWaitingCost(double waitingCost) {
        this.waitingCost = waitingCost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(String dispatchId) {
        this.dispatchId = dispatchId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDispatcherId() {
        return dispatcherId;
    }

    public void setDispatcherId(String dispatcherId) {
        this.dispatcherId = dispatcherId;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<Location> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Location> destinations) {
        this.destinations = destinations;
    }

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public Location getRealPickupLocation() {
        return realPickupLocation;
    }

    public void setRealPickupLocation(Location realPickupLocation) {
        this.realPickupLocation = realPickupLocation;
    }

    public Location getRealDropLocation() {
        return realDropLocation;
    }

    public void setRealDropLocation(Location realDropLocation) {
        this.realDropLocation = realDropLocation;
    }
}
