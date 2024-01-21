package com.ynr.taximedriver.model.tripCancelModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DispatchTripCancelRequestModel {
    @Expose
    @SerializedName("diverId")
    private String diverId;
    @Expose
    @SerializedName("vehicleId")
    private String vehicleId;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("dispatchId")
    private String dispatchId;

    public String getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(String dispatchId) {
        this.dispatchId = dispatchId;
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

    public String getDiverId() {
        return diverId;
    }

    public void setDiverId(String diverId) {
        this.diverId = diverId;
    }
}
