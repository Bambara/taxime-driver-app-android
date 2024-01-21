package com.ynr.taximedriver.model.vehicleModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverInfo {
    @SerializedName("isEnableDriver")
    @Expose
    private boolean isEnableDriver;
    @SerializedName("driverId")
    @Expose
    private String driverId;
    @SerializedName("_id")
    @Expose
    private String _id;

    public boolean isEnableDriver() {
        return isEnableDriver;
    }

    public void setEnableDriver(boolean enableDriver) {
        isEnableDriver = enableDriver;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
