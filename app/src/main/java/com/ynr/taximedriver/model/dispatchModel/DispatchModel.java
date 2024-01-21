package com.ynr.taximedriver.model.dispatchModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DispatchModel {
    @SerializedName("dispatcherId")
    @Expose
    private String dispatcherId;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("customerTelephoneNo")
    @Expose
    private String mobileNumber;
    @SerializedName("noOfPassengers")
    @Expose
    private int nomberOfPassengers;
    @SerializedName("pickupDate")
    @Expose
    private String pickupDate;
    @SerializedName("pickupTime")
    @Expose
    private String pickupTime;
    @SerializedName("pickupLocation")
    @Expose
    private Location pickupLocation;
    @SerializedName("dropLocations")
    @Expose
    private List<Location> dropLocations;
    @SerializedName("distance")
    @Expose
    private double distance;
    @SerializedName("vehicleCategory")
    @Expose
    private String vehicleCategory;
    @SerializedName("vehicleSubCategory")
    @Expose
    private String vehicleSubCategory;
    @SerializedName("hireCost")
    @Expose
    private int unitCost;
    @SerializedName("totalPrice")
    @Expose
    private double totalCost;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("validTime")
    @Expose
    private int validTime;
    @SerializedName("operationRadius")
    @Expose
    private double operationRadius;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getNomberOfPassengers() {
        return nomberOfPassengers;
    }

    public void setNumberOfPassengers(int nomberOfPassengers) {
        this.nomberOfPassengers = nomberOfPassengers;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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


    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(int unitCost) {
        this.unitCost = unitCost;
    }

    public String getDispatcherId() {
        return dispatcherId;
    }

    public void setDispatcherId(String dispatcherId) {
        this.dispatcherId = dispatcherId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public List<Location> getDropLocations() {
        return dropLocations;
    }

    public void setDropLocations(List<Location> dropLocations) {
        this.dropLocations = dropLocations;
    }

    public int getValidTime() {
        return validTime;
    }

    public void setValidTime(int validTime) {
        this.validTime = validTime;
    }

    public double getOperationRadius() {
        return operationRadius;
    }

    public void setOperationRadius(double operationRadius) {
        this.operationRadius = operationRadius;
    }
}
