package com.ynr.taximedriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DispatchAcceptModel {
    @SerializedName("dispatchId")
    @Expose
    private String dispatchId;
    @SerializedName("dispatcherId")
    @Expose
    private String dispatcherId;
    @SerializedName("driverId")
    @Expose
    private String driverId;
    @SerializedName("vehicleId")
    @Expose
    private String vehicleId;
    @SerializedName("type")
    @Expose
    private String type;

    public String getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(String dispatchId) {
        this.dispatchId = dispatchId;
    }

    public String getDispatcherId() {
        return dispatcherId;
    }

    public void setDispatcherId(String dispatcherId) {
        this.dispatcherId = dispatcherId;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
