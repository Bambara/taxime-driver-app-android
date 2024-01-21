package com.ynr.taximedriver.model.vehicleModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Content {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("isApproved")
    @Expose
    private boolean isApproved;
    @SerializedName("vehicleBrandName")
    @Expose
    private String vehicleBrandName;
    @SerializedName("manufactureYear")
    @Expose
    private String manufactureYear;
    @SerializedName("vehicleColor")
    @Expose
    private String vehicleColor;
    @SerializedName("vehicleRegistrationNo")
    @Expose
    private String vehicleRegistrationNo;
    @SerializedName("vehicleLicenceNo")
    @Expose
    private String vehicleLicenceNo;
    @SerializedName("vehicleRevenueExpiryDate")
    @Expose
    private String vehicleRevenueExpiryDate;
    @SerializedName("vehicleRevenueNo")
    @Expose
    private String vehicleRevenueNo;
    @SerializedName("vehicleSubCategory")
    @Expose
    private String vehicleSubCategory;
    @SerializedName("vehicleCategory")
    @Expose
    private String vehicleCategory;
    @SerializedName("driverInfo")
    @Expose
    private List<DriverInfo> driverInfo;
    @SerializedName("ownerInfo")
    @Expose
    private OwnerInfo ownerInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getVehicleBrandName() {
        return vehicleBrandName;
    }

    public void setVehicleBrandName(String vehicleBrandName) {
        this.vehicleBrandName = vehicleBrandName;
    }

    public String getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(String manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getVehicleRegistrationNo() {
        return vehicleRegistrationNo;
    }

    public void setVehicleRegistrationNo(String vehicleRegistrationNo) {
        this.vehicleRegistrationNo = vehicleRegistrationNo;
    }

    public String getVehicleLicenceNo() {
        return vehicleLicenceNo;
    }

    public void setVehicleLicenceNo(String vehicleLicenceNo) {
        this.vehicleLicenceNo = vehicleLicenceNo;
    }

    public String getVehicleRevenueExpiryDate() {
        return vehicleRevenueExpiryDate;
    }

    public void setVehicleRevenueExpiryDate(String vehicleRevenueExpiryDate) {
        this.vehicleRevenueExpiryDate = vehicleRevenueExpiryDate;
    }

    public String getVehicleRevenueNo() {
        return vehicleRevenueNo;
    }

    public void setVehicleRevenueNo(String vehicleRevenueNo) {
        this.vehicleRevenueNo = vehicleRevenueNo;
    }

    public String getVehicleSubCategory() {
        return vehicleSubCategory;
    }

    public void setVehicleSubCategory(String vehicleSubCategory) {
        this.vehicleSubCategory = vehicleSubCategory;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public List<DriverInfo> getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(List<DriverInfo> driverInfo) {
        this.driverInfo = driverInfo;
    }

    public OwnerInfo getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(OwnerInfo ownerInfo) {
        this.ownerInfo = ownerInfo;
    }
}
