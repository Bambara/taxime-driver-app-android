package com.ynr.taximedriver.model.driverConnectModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverConnectResultModel {
    @Expose
    @SerializedName("_id")
    private String _id;
    @Expose
    @SerializedName("socketId")
    private String socketId;

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
