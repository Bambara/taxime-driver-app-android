package com.ynr.taximedriver.model.driverConnectModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverConnectedModel {
    @Expose
    @SerializedName("_id")
    private String _id;
    @Expose
    @SerializedName("socketId")
    private String socketId;
    @Expose
    @SerializedName("driverId")
    private String driverId;
    @SerializedName("vehicleId")
    @Expose
    private String vehicleId;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("vehicleCategory")
    @Expose
    private String vehicleCategory;
    @SerializedName("vehicleSubCategory")
    @Expose
    private String vehicleSubCategory;
    @SerializedName("operationRadius")
    @Expose
    private int operationRadius;
    @SerializedName("driverName")
    @Expose
    private String driverName;
    @SerializedName("driverContactNumber")
    @Expose
    private String driverContactNumber;
    @SerializedName("vehicleRegistrationNo")
    @Expose
    private String vehicleRegistrationNo;
    @SerializedName("vehicleLicenceNo")
    @Expose
    private String vehicleLicenceNo;
    @SerializedName("currentStatus")
    @Expose
    private String currentStatus;
    @Expose
    @SerializedName("bidValue")
    private int bidValue;
    @Expose
    @SerializedName("driverPic")
    private String driverPic;
    @Expose
    @SerializedName("mapIconOntripSVG")
    private String mapIconOntripSVG;
    @Expose
    @SerializedName("mapIconOfflineSVG")
    private String mapIconOfflineSVG;
    @Expose
    @SerializedName("mapIconSVG")
    private String mapIconSVG;
    @Expose
    @SerializedName("subCategoryIconSelectedSVG")
    private String subCategoryIconSelectedSVG;
    @Expose
    @SerializedName("subCategoryIconSVG")
    private String subCategoryIconSVG;
    @Expose
    @SerializedName("mapIconOntrip")
    private String mapIconOntrip;
    @Expose
    @SerializedName("mapIconOffline")
    private String mapIconOffline;
    @Expose
    @SerializedName("mapIcon")
    private String mapIcon;
    @Expose
    @SerializedName("subCategoryIconSelected")
    private String subCategoryIconSelected;
    @Expose
    @SerializedName("subCategoryIcon")
    private String subCategoryIcon;

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

    public int getOperationRadius() {
        return operationRadius;
    }

    public void setOperationRadius(int operationRadius) {
        this.operationRadius = operationRadius;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverContactNumber() {
        return driverContactNumber;
    }

    public void setDriverContactNumber(String driverContactNumber) {
        this.driverContactNumber = driverContactNumber;
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

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public int getBidValue() {
        return bidValue;
    }

    public void setBidValue(int bidValue) {
        this.bidValue = bidValue;
    }

    public String getDriverPic() {
        return driverPic;
    }

    public void setDriverPic(String driverPic) {
        this.driverPic = driverPic;
    }

    public String getMapIconOntripSVG() {
        return mapIconOntripSVG;
    }

    public void setMapIconOntripSVG(String mapIconOntripSVG) {
        this.mapIconOntripSVG = mapIconOntripSVG;
    }

    public String getMapIconOfflineSVG() {
        return mapIconOfflineSVG;
    }

    public void setMapIconOfflineSVG(String mapIconOfflineSVG) {
        this.mapIconOfflineSVG = mapIconOfflineSVG;
    }

    public String getMapIconSVG() {
        return mapIconSVG;
    }

    public void setMapIconSVG(String mapIconSVG) {
        this.mapIconSVG = mapIconSVG;
    }

    public String getSubCategoryIconSelectedSVG() {
        return subCategoryIconSelectedSVG;
    }

    public void setSubCategoryIconSelectedSVG(String subCategoryIconSelectedSVG) {
        this.subCategoryIconSelectedSVG = subCategoryIconSelectedSVG;
    }

    public String getSubCategoryIconSVG() {
        return subCategoryIconSVG;
    }

    public void setSubCategoryIconSVG(String subCategoryIconSVG) {
        this.subCategoryIconSVG = subCategoryIconSVG;
    }

    public String getMapIconOntrip() {
        return mapIconOntrip;
    }

    public void setMapIconOntrip(String mapIconOntrip) {
        this.mapIconOntrip = mapIconOntrip;
    }

    public String getMapIconOffline() {
        return mapIconOffline;
    }

    public void setMapIconOffline(String mapIconOffline) {
        this.mapIconOffline = mapIconOffline;
    }

    public String getMapIcon() {
        return mapIcon;
    }

    public void setMapIcon(String mapIcon) {
        this.mapIcon = mapIcon;
    }

    public String getSubCategoryIconSelected() {
        return subCategoryIconSelected;
    }

    public void setSubCategoryIconSelected(String subCategoryIconSelected) {
        this.subCategoryIconSelected = subCategoryIconSelected;
    }

    public String getSubCategoryIcon() {
        return subCategoryIcon;
    }

    public void setSubCategoryIcon(String subCategoryIcon) {
        this.subCategoryIcon = subCategoryIcon;
    }
}
