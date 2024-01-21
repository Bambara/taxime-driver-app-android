package com.ynr.taximedriver.model.vehicalCategoryModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VehicleCategoryResponseModel {

    private int unitCost;
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

    public int getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(int unitCost) {
        this.unitCost = unitCost;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public class Content {
        @Expose
        @SerializedName("categoryTag")
        private String categoryTag;
        @Expose
        @SerializedName("packageDelivery")
        private String packageDelivery;
        @Expose
        @SerializedName("subDescription")
        private String subDescription;
        @Expose
        @SerializedName("subCategoryName")
        private String subCategoryName;
        @Expose
        @SerializedName("_id")
        private String _id;
        @Expose
        @SerializedName("mapIcon")
        private String mapIcon;
        @Expose
        @SerializedName("subCategoryIconSelected")
        private String subCategoryIconSelected;
        @Expose
        @SerializedName("subCategoryIcon")
        private String subCategoryIcon;
        @Expose
        @SerializedName("isEnable")
        private boolean isEnable;
        @Expose
        @SerializedName("priceSelection")
        private List<PriceSelection> priceSelection;
        @Expose
        @SerializedName("lowerBidLimit")
        private int lowerBidLimit;
        @Expose
        @SerializedName("upperBidLimit")
        private int upperBidLimit;
        @Expose
        @SerializedName("subCategoryNo")
        private int subCategoryNo;
        @Expose
        @SerializedName("driverTripTimerSecoends")
        private int validTime;
        @Expose
        @SerializedName("passengerCount")
        private int seatCount;

        public int getSeatCount() {
            return seatCount;
        }

        public boolean isEnable() {
            return isEnable;
        }

        public void setEnable(boolean enable) {
            isEnable = enable;
        }

        public String getSubCategoryIcon() {
            return subCategoryIcon;
        }

        public void setSubCategoryIcon(String subCategoryIcon) {
            this.subCategoryIcon = subCategoryIcon;
        }

        public String getSubCategoryIconSelected() {
            return subCategoryIconSelected;
        }

        public void setSubCategoryIconSelected(String subCategoryIconSelected) {
            this.subCategoryIconSelected = subCategoryIconSelected;
        }

        public String getMapIcon() {
            return mapIcon;
        }

        public void setMapIcon(String mapIcon) {
            this.mapIcon = mapIcon;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getSubCategoryName() {
            return subCategoryName;
        }

        public void setSubCategoryName(String subCategoryName) {
            this.subCategoryName = subCategoryName;
        }

        public String getSubDescription() {
            return subDescription;
        }

        public void setSubDescription(String subDescription) {
            this.subDescription = subDescription;
        }

        public String getPackageDelivery() {
            return packageDelivery;
        }

        public void setPackageDelivery(String packageDelivery) {
            this.packageDelivery = packageDelivery;
        }

        public String getCategoryTag() {
            return categoryTag;
        }

        public void setCategoryTag(String categoryTag) {
            this.categoryTag = categoryTag;
        }

        public int getSubCategoryNo() {
            return subCategoryNo;
        }

        public void setSubCategoryNo(int subCategoryNo) {
            this.subCategoryNo = subCategoryNo;
        }

        public int getUpperBidLimit() {
            return upperBidLimit;
        }

        public void setUpperBidLimit(int upperBidLimit) {
            this.upperBidLimit = upperBidLimit;
        }

        public int getLowerBidLimit() {
            return lowerBidLimit;
        }

        public void setLowerBidLimit(int lowerBidLimit) {
            this.lowerBidLimit = lowerBidLimit;
        }

        public List<PriceSelection> getPriceSelection() {
            return priceSelection;
        }

        public void setPriceSelection(List<PriceSelection> priceSelection) {
            this.priceSelection = priceSelection;
        }

        public int getValidTime() {
            return validTime;
        }

        public void setValidTime(int validTime) {
            this.validTime = validTime;
        }
    }

    public class PriceSelection {
        @Expose
        @SerializedName("districtName")
        private String districtName;
        @Expose
        @SerializedName("_id")
        private String _id;
        @Expose
        @SerializedName("timeBase")
        private List<TimeBase> timeBase;


        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getDistrictName() {
            return districtName;
        }

        public void setDistrictName(String districtName) {
            this.districtName = districtName;
        }

        public List<TimeBase> getTimeBase() {
            return timeBase;
        }

        public void setTimeBase(List<TimeBase> timeBase) {
            this.timeBase = timeBase;
        }
    }

    public class TimeBase {
        @Expose
        @SerializedName("packageDeliveryKMPerDay")
        private double packageDeliveryKMPerDay;
        @Expose
        @SerializedName("packageDeliveryKMPerHour")
        private double packageDeliveryKMPerHour;
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
        @SerializedName("_id")
        private String _id;
        @Expose
        @SerializedName("belowKMFare")
        private double belowKMFare;
        @Expose
        @SerializedName("aboveKMFare")
        private double aboveKMFare;
        @Expose
        @SerializedName("lowerBidLimit")
        private int lowerBidLimit;
        @Expose
        @SerializedName("upperBidLimit")
        private int upperBidLimit;
        @Expose
        @SerializedName("districtPrice")
        private double districtPrice;

        public double getDistrictPrice() {
            return districtPrice;
        }

        public void setDistrictPrice(double districtPrice) {
            this.districtPrice = districtPrice;
        }

        public int getUpperBidLimit() {
            return upperBidLimit;
        }

        public void setUpperBidLimit(int upperBidLimit) {
            this.upperBidLimit = upperBidLimit;
        }

        public int getLowerBidLimit() {
            return lowerBidLimit;
        }

        public void setLowerBidLimit(int lowerBidLimit) {
            this.lowerBidLimit = lowerBidLimit;
        }

        public double getAboveKMFare() {
            return aboveKMFare;
        }

        public void setAboveKMFare(double aboveKMFare) {
            this.aboveKMFare = aboveKMFare;
        }

        public double getBelowKMFare() {
            return belowKMFare;
        }

        public void setBelowKMFare(double belowKMFare) {
            this.belowKMFare = belowKMFare;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public double getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(double baseFare) {
            this.baseFare = baseFare;
        }

        public double getMinimumFare() {
            return minimumFare;
        }

        public void setMinimumFare(double minimumFare) {
            this.minimumFare = minimumFare;
        }

        public double getMinimumKM() {
            return minimumKM;
        }

        public void setMinimumKM(double minimumKM) {
            this.minimumKM = minimumKM;
        }

        public double getBelowAboveKMRange() {
            return belowAboveKMRange;
        }

        public void setBelowAboveKMRange(double belowAboveKMRange) {
            this.belowAboveKMRange = belowAboveKMRange;
        }

        public double getTrafficWaitingChargePerMinute() {
            return trafficWaitingChargePerMinute;
        }

        public void setTrafficWaitingChargePerMinute(double trafficWaitingChargePerMinute) {
            this.trafficWaitingChargePerMinute = trafficWaitingChargePerMinute;
        }

        public double getNormalWaitingChargePerMinute() {
            return normalWaitingChargePerMinute;
        }

        public void setNormalWaitingChargePerMinute(double normalWaitingChargePerMinute) {
            this.normalWaitingChargePerMinute = normalWaitingChargePerMinute;
        }

        public double getRadius() {
            return radius;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        public double getPackageDeliveryKMPerHour() {
            return packageDeliveryKMPerHour;
        }

        public void setPackageDeliveryKMPerHour(double packageDeliveryKMPerHour) {
            this.packageDeliveryKMPerHour = packageDeliveryKMPerHour;
        }

        public double getPackageDeliveryKMPerDay() {
            return packageDeliveryKMPerDay;
        }

        public void setPackageDeliveryKMPerDay(double packageDeliveryKMPerDay) {
            this.packageDeliveryKMPerDay = packageDeliveryKMPerDay;
        }
    }
}
