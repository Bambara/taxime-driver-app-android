package com.ynr.taximedriver.model.tripAcceptModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DispatchTripAcceptRequestModel {
    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("dispatcherId")
    private String dispatcherId;
    @Expose
    @SerializedName("driverId")
    private String driverId;
    @Expose
    @SerializedName("vehicleId")
    private String vehicleId;
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

    public String getDispatcherId() {
        return dispatcherId;
    }

    public void setDispatcherId(String dispatcherId) {
        this.dispatcherId = dispatcherId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class Location {
        @Expose
        @SerializedName("longitude")
        private double longitude;
        @Expose
        @SerializedName("latitude")
        private double latitude;
        @Expose
        @SerializedName("address")
        private String address;

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
    }
}
