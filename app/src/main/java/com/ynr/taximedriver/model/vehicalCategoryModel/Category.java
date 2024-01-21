package com.ynr.taximedriver.model.vehicalCategoryModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("categoryTag")
    @Expose
    private String categoryTag;
    @SerializedName("subCategoryName")
    @Expose
    private String subCategoryName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("mapIcon")
    @Expose
    private String mapIcon;
    @SerializedName("subCategoryIconSelected")
    @Expose
    private String subCategoryIconSelected;
    @SerializedName("subCategoryIcon")
    @Expose
    private String subCategoryIcon;
    @SerializedName("isEnable")
    @Expose
    private boolean isEnable;
    @SerializedName("lowerBidLimit")
    @Expose
    private int lowerBidLimit;
    @SerializedName("upperBidLimit")
    @Expose
    private int upperBidLimit;

    public String getCategoryTag() {
        return categoryTag;
    }

    public void setCategoryTag(String categoryTag) {
        this.categoryTag = categoryTag;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public int getLowerBidLimit() {
        return lowerBidLimit;
    }

    public void setLowerBidLimit(int lowerBidLimit) {
        this.lowerBidLimit = lowerBidLimit;
    }

    public int getUpperBidLimit() {
        return upperBidLimit;
    }

    public void setUpperBidLimit(int upperBidLimit) {
        this.upperBidLimit = upperBidLimit;
    }

}
