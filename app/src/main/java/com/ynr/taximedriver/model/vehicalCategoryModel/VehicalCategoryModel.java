package com.ynr.taximedriver.model.vehicalCategoryModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VehicalCategoryModel {
    private int unitCost;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("content")
    @Expose
    private List<Category> category;

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(int unitCost) {
        this.unitCost = unitCost;
    }
}
