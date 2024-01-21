package com.ynr.taximedriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentStatus {
    @SerializedName("trip_type")
    @Expose
    private String tripType;
    @SerializedName("driver_socket_id")
    @Expose
    private String driverSocketId;
    @SerializedName("passenger_id")
    @Expose
    private String passengerId;
    @SerializedName("pay_status")
    @Expose
    private String payStatus;
    @SerializedName("pay_method")
    @Expose
    private String payMethod;

    private PassengerTripEndRequestModel passengerTripEndRequestModel;
    private DispatchTripEndRequestModel dispatchTripEndRequestModel;

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getDriverSocketId() {
        return driverSocketId;
    }

    public void setDriverSocketId(String driverSocketId) {
        this.driverSocketId = driverSocketId;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public PassengerTripEndRequestModel getPassengerTripEndRequestModel() {
        return passengerTripEndRequestModel;
    }

    public void setPassengerTripEndRequestModel(PassengerTripEndRequestModel passengerTripEndRequestModel) {
        this.passengerTripEndRequestModel = passengerTripEndRequestModel;
    }

    public DispatchTripEndRequestModel getDispatchTripEndRequestModel() {
        return dispatchTripEndRequestModel;
    }

    public void setDispatchTripEndRequestModel(DispatchTripEndRequestModel dispatchTripEndRequestModel) {
        this.dispatchTripEndRequestModel = dispatchTripEndRequestModel;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }
}