package com.ynr.taximedriver.model.tripAcceptModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ynr.taximedriver.model.Location;

public class PassengerTripAcceptRequestModel {
    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("driverId")
    private String driverId;
    @Expose
    @SerializedName("vehicleId")
    private String vehicleId;
    @Expose
    @SerializedName("passengerId")
    private String passengerId;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("vehicleSubCategory")
    private String vehicleSubCategory;
    @Expose
    @SerializedName("vehicleCategory")
    private String vehicleCategory;
    @Expose
    @SerializedName("pickupLocation")
    private Location pickupLocation;
    @Expose
    @SerializedName("customerTelephoneNo")
    private String customerTelephoneNo;
    @Expose
    @SerializedName("currentLocationLatitude")
    private double currentLocationLatitude;
    @Expose
    @SerializedName("currentLocationLongitude")
    private double currentLocationLongitude;

    public String getCustomerTelephoneNo() {
        return customerTelephoneNo;
    }

    public void setCustomerTelephoneNo(String customerTelephoneNo) {
        this.customerTelephoneNo = customerTelephoneNo;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public String getVehicleSubCategory() {
        return vehicleSubCategory;
    }

    public void setVehicleSubCategory(String vehicleSubCategory) {
        this.vehicleSubCategory = vehicleSubCategory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getCurrentLocationLongitude() {
        return currentLocationLongitude;
    }

    public void setCurrentLocationLongitude(double currentLocationLongitude) {
        this.currentLocationLongitude = currentLocationLongitude;
    }

    public double getCurrentLocationLatitude() {
        return currentLocationLatitude;
    }

    public void setCurrentLocationLatitude(double currentLocationLatitude) {
        this.currentLocationLatitude = currentLocationLatitude;
    }
}
