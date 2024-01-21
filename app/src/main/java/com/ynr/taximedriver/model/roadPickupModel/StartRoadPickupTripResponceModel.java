package com.ynr.taximedriver.model.roadPickupModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StartRoadPickupTripResponceModel {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("content")
    @Expose
    private List<Data> content;
    @SerializedName("tripId")
    @Expose
    private String tripId;

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

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public List<Data> getContent() {
        return content;
    }

    public void setContent(List<Data> content) {
        this.content = content;
    }


    public class Data {
        @SerializedName("districtPrice")
        @Expose
        private int districtPrice;
        @SerializedName("baseFare")
        @Expose
        private int baseFare;
        @SerializedName("minimumFare")
        @Expose
        private int minimumFare;
        @SerializedName("minimumKM")
        @Expose
        private int minimumKM;
        @SerializedName("belowAboveKMRange")
        @Expose
        private int belowAboveKMRange;
        @SerializedName("aboveKMFare")
        @Expose
        private int aboveKMFare;
        @SerializedName("belowKMFare")
        @Expose
        private int belowKMFare;
        @SerializedName("trafficWaitingChargePerMinute")
        @Expose
        private int trafficWaitingChargePerMinute;
        @SerializedName("normalWaitingChargePerMinute")
        @Expose
        private int normalWaitingChargePerMinute;

        public int getDistrictPrice() {
            return districtPrice;
        }

        public void setDistrictPrice(int districtPrice) {
            this.districtPrice = districtPrice;
        }

        public int getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(int baseFare) {
            this.baseFare = baseFare;
        }

        public int getMinimumFare() {
            return minimumFare;
        }

        public void setMinimumFare(int minimumFare) {
            this.minimumFare = minimumFare;
        }

        public int getMinimumKM() {
            return minimumKM;
        }

        public void setMinimumKM(int minimumKM) {
            this.minimumKM = minimumKM;
        }

        public int getBelowAboveKMRange() {
            return belowAboveKMRange;
        }

        public void setBelowAboveKMRange(int belowAboveKMRange) {
            this.belowAboveKMRange = belowAboveKMRange;
        }

        public int getAboveKMFare() {
            return aboveKMFare;
        }

        public void setAboveKMFare(int aboveKMFare) {
            this.aboveKMFare = aboveKMFare;
        }

        public int getBelowKMFare() {
            return belowKMFare;
        }

        public void setBelowKMFare(int belowKMFare) {
            this.belowKMFare = belowKMFare;
        }

        public int getTrafficWaitingChargePerMinute() {
            return trafficWaitingChargePerMinute;
        }

        public void setTrafficWaitingChargePerMinute(int trafficWaitingChargePerMinute) {
            this.trafficWaitingChargePerMinute = trafficWaitingChargePerMinute;
        }

        public int getNormalWaitingChargePerMinute() {
            return normalWaitingChargePerMinute;
        }

        public void setNormalWaitingChargePerMinute(int normalWaitingChargePerMinute) {
            this.normalWaitingChargePerMinute = normalWaitingChargePerMinute;
        }
    }
}
