package com.ynr.taximedriver.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DispatchHistoryModel {
    @Expose
    @SerializedName("dispatchhistory")
    private List<DispatchHistory> dispatchHistories;
    @Expose
    @SerializedName("totalDispatchesDone")
    private int totalDispatchesDone;
    @Expose
    @SerializedName("totalDispatchEarnings")
    private double totalDispatchEarnings;

    public List<DispatchHistory> getDispatchHistories() {
        return dispatchHistories;
    }

    public void setDispatchHistories(List<DispatchHistory> dispatchHistories) {
        this.dispatchHistories = dispatchHistories;
    }

    public int getTotalDispatchesDone() {
        return totalDispatchesDone;
    }

    public void setTotalDispatchesDone(int totalDispatchesDone) {
        this.totalDispatchesDone = totalDispatchesDone;
    }

    public double getTotalDispatchEarnings() {
        return totalDispatchEarnings;
    }

    public void setTotalDispatchEarnings(double totalDispatchEarnings) {
        this.totalDispatchEarnings = totalDispatchEarnings;
    }

    public class DispatchHistory implements Parcelable {
        @Expose
        @SerializedName("_id")
        private String _id;
        @Expose
        @SerializedName("status")
        private String status;
        @Expose
        @SerializedName("type")
        private String type;
        @Expose
        @SerializedName("hireCost")
        private double hireCost;
        @Expose
        @SerializedName("vehicleSubCategory")
        private String vehicleSubCategory;
        @Expose
        @SerializedName("vehicleCategory")
        private String vehicleCategory;
        @Expose
        @SerializedName("bidValue")
        private int bidValue;
        @Expose
        @SerializedName("distance")
        private double distance;
        @Expose
        @SerializedName("noOfPassengers")
        private int noOfPassengers;
        @Expose
        @SerializedName("customerTelephoneNo")
        private String customerTelephoneNo;
        @Expose
        @SerializedName("customerName")
        private String customerName;
        @Expose
        @SerializedName("dispatcherId")
        private String dispatcherId;
        @Expose
        @SerializedName("recordedTime")
        private String recordedTime;
        @Expose
        @SerializedName("waitTime")
        private double waitTime;
        @Expose
        @SerializedName("waitingCost")
        private double waitingCost;
        @Expose
        @SerializedName("totalPrice")
        private double totalPrice;
        @Expose
        @SerializedName("dropLocations")
        private List<Location> dropLocations;
        @Expose
        @SerializedName("pickupLocation")
        private Location pickupLocation;
        @Expose
        @SerializedName("pickupDateTime")
        private String pickupDateTime;
        @Expose
        @SerializedName("assignedVehicleId")
        private String assignedVehicleId;
        @Expose
        @SerializedName("assignedDriverId")
        private String assignedDriverId;
        @Expose
        @SerializedName("dropDateTime")
        private String dropDateTime;
        @Expose
        @SerializedName("realDropLocation")
        private Location realDropLocation;
        @Expose
        @SerializedName("realPickupLocation")
        private Location realPickupLocation;
        @Expose
        @SerializedName("tripTime")
        private String tripTime;

        protected DispatchHistory(Parcel in) {
            _id = in.readString();
            status = in.readString();
            type = in.readString();
            hireCost = in.readDouble();
            vehicleSubCategory = in.readString();
            vehicleCategory = in.readString();
            bidValue = in.readInt();
            distance = in.readDouble();
            noOfPassengers = in.readInt();
            customerTelephoneNo = in.readString();
            customerName = in.readString();
            dispatcherId = in.readString();
            recordedTime = in.readString();
            waitTime = in.readDouble();
            waitingCost = in.readDouble();
            totalPrice = in.readDouble();
            pickupDateTime = in.readString();
            assignedVehicleId = in.readString();
            assignedDriverId = in.readString();
            dropDateTime = in.readString();
            tripTime = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(_id);
            dest.writeString(status);
            dest.writeString(type);
            dest.writeDouble(hireCost);
            dest.writeString(vehicleSubCategory);
            dest.writeString(vehicleCategory);
            dest.writeInt(bidValue);
            dest.writeDouble(distance);
            dest.writeInt(noOfPassengers);
            dest.writeString(customerTelephoneNo);
            dest.writeString(customerName);
            dest.writeString(dispatcherId);
            dest.writeString(recordedTime);
            dest.writeDouble(waitTime);
            dest.writeDouble(waitingCost);
            dest.writeDouble(totalPrice);
            dest.writeString(pickupDateTime);
            dest.writeString(assignedVehicleId);
            dest.writeString(assignedDriverId);
            dest.writeString(dropDateTime);
            dest.writeString(tripTime);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public final Creator<DispatchHistory> CREATOR = new Creator<DispatchHistory>() {
            @Override
            public DispatchHistory createFromParcel(Parcel in) {
                return new DispatchHistory(in);
            }

            @Override
            public DispatchHistory[] newArray(int size) {
                return new DispatchHistory[size];
            }
        };

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getHireCost() {
            return hireCost;
        }

        public void setHireCost(double hireCost) {
            this.hireCost = hireCost;
        }

        public String getVehicleSubCategory() {
            return vehicleSubCategory;
        }

        public void setVehicleSubCategory(String vehicleSubCategory) {
            this.vehicleSubCategory = vehicleSubCategory;
        }

        public String getVehicleCategory() {
            return vehicleCategory;
        }

        public void setVehicleCategory(String vehicleCategory) {
            this.vehicleCategory = vehicleCategory;
        }

        public int getBidValue() {
            return bidValue;
        }

        public void setBidValue(int bidValue) {
            this.bidValue = bidValue;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getNoOfPassengers() {
            return noOfPassengers;
        }

        public void setNoOfPassengers(int noOfPassengers) {
            this.noOfPassengers = noOfPassengers;
        }

        public String getCustomerTelephoneNo() {
            return customerTelephoneNo;
        }

        public void setCustomerTelephoneNo(String customerTelephoneNo) {
            this.customerTelephoneNo = customerTelephoneNo;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getDispatcherId() {
            return dispatcherId;
        }

        public void setDispatcherId(String dispatcherId) {
            this.dispatcherId = dispatcherId;
        }

        public String getRecordedTime() {
            return recordedTime;
        }

        public void setRecordedTime(String recordedTime) {
            this.recordedTime = recordedTime;
        }

        public double getWaitTime() {
            return waitTime;
        }

        public void setWaitTime(double waitTime) {
            this.waitTime = waitTime;
        }

        public double getWaitingCost() {
            return waitingCost;
        }

        public void setWaitingCost(double waitingCost) {
            this.waitingCost = waitingCost;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public List<Location> getDropLocations() {
            return dropLocations;
        }

        public void setDropLocations(List<Location> dropLocations) {
            this.dropLocations = dropLocations;
        }

        public Location getPickupLocation() {
            return pickupLocation;
        }

        public void setPickupLocation(Location pickupLocation) {
            this.pickupLocation = pickupLocation;
        }

        public String getPickupDateTime() {
            return pickupDateTime;
        }

        public void setPickupDateTime(String pickupDateTime) {
            this.pickupDateTime = pickupDateTime;
        }

        public String getAssignedVehicleId() {
            return assignedVehicleId;
        }

        public void setAssignedVehicleId(String assignedVehicleId) {
            this.assignedVehicleId = assignedVehicleId;
        }

        public String getAssignedDriverId() {
            return assignedDriverId;
        }

        public void setAssignedDriverId(String assignedDriverId) {
            this.assignedDriverId = assignedDriverId;
        }

        public String getDropDateTime() {
            return dropDateTime;
        }

        public void setDropDateTime(String dropDateTime) {
            this.dropDateTime = dropDateTime;
        }

        public Location getRealDropLocation() {
            return realDropLocation;
        }

        public void setRealDropLocation(Location realDropLocation) {
            this.realDropLocation = realDropLocation;
        }

        public Location getRealPickupLocation() {
            return realPickupLocation;
        }

        public void setRealPickupLocation(Location realPickupLocation) {
            this.realPickupLocation = realPickupLocation;
        }

        public String getTripTime() {
            return tripTime;
        }

        public void setTripTime(String tripTime) {
            this.tripTime = tripTime;
        }
    }
}
