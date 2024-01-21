package com.ynr.taximedriver.model.vehicleModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OwnerInfo {
    @SerializedName("ownerContactEmail")
    @Expose
    private String ownerContactEmail;
    @SerializedName("ownerContactNumber")
    @Expose
    private String ownerContactNumber;
    @SerializedName("ownerContactName")
    @Expose
    private String ownerContactName;
    @SerializedName("isVerify")
    @Expose
    private boolean isVerify;
    @SerializedName("address")
    @Expose
    private Address address;

    public String getOwnerContactEmail() {
        return ownerContactEmail;
    }

    public void setOwnerContactEmail(String ownerContactEmail) {
        this.ownerContactEmail = ownerContactEmail;
    }

    public String getOwnerContactNumber() {
        return ownerContactNumber;
    }

    public void setOwnerContactNumber(String ownerContactNumber) {
        this.ownerContactNumber = ownerContactNumber;
    }

    public String getOwnerContactName() {
        return ownerContactName;
    }

    public void setOwnerContactName(String ownerContactName) {
        this.ownerContactName = ownerContactName;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
