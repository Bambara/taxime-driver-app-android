package com.ynr.taximedriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverState {
    @SerializedName("_id")
    @Expose
    private String _id;
    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("currentStatus")
    @Expose
    private String currentStatus;

    public DriverState(String to, String currentStatus, String _id) {
        this.to = to;
        this.currentStatus = currentStatus;
        this._id = _id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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
}
