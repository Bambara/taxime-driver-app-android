package com.ynr.taximedriver.model.vehicleModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VehicleModel {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("content")
    @Expose
    private List<Content> content;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }
}
