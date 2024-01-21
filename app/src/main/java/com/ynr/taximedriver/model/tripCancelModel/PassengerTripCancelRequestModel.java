package com.ynr.taximedriver.model.tripCancelModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ynr.taximedriver.model.Location;

import java.util.List;

public class PassengerTripCancelRequestModel {
    @Expose
    @SerializedName("cancelCost")
    private double cancelCost;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("cancelReason")
    private String cancelReason;
    @Expose
    @SerializedName("driverId")
    private String driverId;
    @Expose
    @SerializedName("tripId")
    private String tripId;
    @Expose
    @SerializedName("pickupLocation")
    private Location pickupLocation;
    @Expose
    @SerializedName("dropLocations")
    private List<Location> dropLocations;
    @Expose
    @SerializedName("passengerId")
    private String passengerId;
    @Expose
    @SerializedName("estimatedCost")
    private double estimatedCost;

    public double getCancelCost() {
        return cancelCost;
    }

    public void setCancelCost(double cancelCost) {
        this.cancelCost = cancelCost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public List<Location> getDropLocations() {
        return dropLocations;
    }

    public void setDropLocations(List<Location> dropLocations) {
        this.dropLocations = dropLocations;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
}
