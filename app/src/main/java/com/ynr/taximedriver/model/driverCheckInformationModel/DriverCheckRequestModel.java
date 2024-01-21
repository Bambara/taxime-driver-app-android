package com.ynr.taximedriver.model.driverCheckInformationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverCheckRequestModel {
    @Expose
    @SerializedName("driverId")
    private String driverId;
    @Expose
    @SerializedName("vehicleId")
    private String vehicleId;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
}
