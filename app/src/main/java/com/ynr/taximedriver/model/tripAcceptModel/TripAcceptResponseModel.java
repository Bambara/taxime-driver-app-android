package com.ynr.taximedriver.model.tripAcceptModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TripAcceptResponseModel {
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("details")
    private String details;
    @Expose
    @SerializedName("content")
    private List<Content> content;


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

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public class Content {
        @Expose
        @SerializedName("_id")
        private String _id;
        @Expose
        @SerializedName("packageDeliveryKMPerDay")
        private String packageDeliveryKMPerDay;
        @Expose
        @SerializedName("packageDeliveryKMPerHour")
        private String packageDeliveryKMPerHour;
        @Expose
        @SerializedName("radius")
        private double radius;
        @Expose
        @SerializedName("normalWaitingChargePerMinute")
        private double normalWaitingChargePerMinute;
        @Expose
        @SerializedName("trafficWaitingChargePerMinute")
        private double trafficWaitingChargePerMinute;
        @Expose
        @SerializedName("belowAboveKMRange")
        private double belowAboveKMRange;
        @Expose
        @SerializedName("minimumKM")
        private double minimumKM;
        @Expose
        @SerializedName("minimumFare")
        private double minimumFare;
        @Expose
        @SerializedName("baseFare")
        private double baseFare;
        @Expose
        @SerializedName("tripCancelationFee")
        private double tripCancelationFee;
        @Expose
        @SerializedName("belowKMFare")
        private double belowKMFare;
        @Expose
        @SerializedName("aboveKMFare")
        private double aboveKMFare;
        @Expose
        @SerializedName("lowerBidLimit")
        private double lowerBidLimit;
        @Expose
        @SerializedName("upperBidLimit")
        private double upperBidLimit;
        @Expose
        @SerializedName("districtPrice")
        private double districtPrice;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getPackageDeliveryKMPerDay() {
            return packageDeliveryKMPerDay;
        }

        public void setPackageDeliveryKMPerDay(String packageDeliveryKMPerDay) {
            this.packageDeliveryKMPerDay = packageDeliveryKMPerDay;
        }

        public String getPackageDeliveryKMPerHour() {
            return packageDeliveryKMPerHour;
        }

        public void setPackageDeliveryKMPerHour(String packageDeliveryKMPerHour) {
            this.packageDeliveryKMPerHour = packageDeliveryKMPerHour;
        }

        public double getRadius() {
            return radius;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        public double getNormalWaitingChargePerMinute() {
            return normalWaitingChargePerMinute;
        }

        public void setNormalWaitingChargePerMinute(double normalWaitingChargePerMinute) {
            this.normalWaitingChargePerMinute = normalWaitingChargePerMinute;
        }

        public double getTrafficWaitingChargePerMinute() {
            return trafficWaitingChargePerMinute;
        }

        public void setTrafficWaitingChargePerMinute(double trafficWaitingChargePerMinute) {
            this.trafficWaitingChargePerMinute = trafficWaitingChargePerMinute;
        }

        public double getBelowAboveKMRange() {
            return belowAboveKMRange;
        }

        public void setBelowAboveKMRange(double belowAboveKMRange) {
            this.belowAboveKMRange = belowAboveKMRange;
        }

        public double getMinimumKM() {
            return minimumKM;
        }

        public void setMinimumKM(double minimumKM) {
            this.minimumKM = minimumKM;
        }

        public double getMinimumFare() {
            return minimumFare;
        }

        public void setMinimumFare(double minimumFare) {
            this.minimumFare = minimumFare;
        }

        public double getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(double baseFare) {
            this.baseFare = baseFare;
        }

        public double getBelowKMFare() {
            return belowKMFare;
        }

        public void setBelowKMFare(double belowKMFare) {
            this.belowKMFare = belowKMFare;
        }

        public double getAboveKMFare() {
            return aboveKMFare;
        }

        public void setAboveKMFare(double aboveKMFare) {
            this.aboveKMFare = aboveKMFare;
        }

        public double getLowerBidLimit() {
            return lowerBidLimit;
        }

        public void setLowerBidLimit(double lowerBidLimit) {
            this.lowerBidLimit = lowerBidLimit;
        }

        public double getUpperBidLimit() {
            return upperBidLimit;
        }

        public void setUpperBidLimit(double upperBidLimit) {
            this.upperBidLimit = upperBidLimit;
        }

        public double getDistrictPrice() {
            return districtPrice;
        }

        public void setDistrictPrice(double districtPrice) {
            this.districtPrice = districtPrice;
        }

        public double getTripCancelationFee() {
            return tripCancelationFee;
        }

        public void setTripCancelationFee(double tripCancelationFee) {
            this.tripCancelationFee = tripCancelationFee;
        }
    }
}
