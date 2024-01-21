package com.ynr.taximedriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RefundModel {
    @Expose
    @SerializedName("partnerAccount")
    private String partnerAccount;

    @Expose
    @SerializedName("amount")
    private double amount;

    @Expose
    @SerializedName("mobileNo")
    private String mobileNo;

    @Expose
    @SerializedName("email")
    private String email;

    @Expose
    @SerializedName("globalId")
    private String globalId;


    public String getPartnerAccount() {
        return partnerAccount;
    }

    public void setPartnerAccount(String partnerAccount) {
        this.partnerAccount = partnerAccount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }
}
