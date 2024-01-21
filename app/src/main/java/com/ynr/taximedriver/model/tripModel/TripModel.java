package com.ynr.taximedriver.model.tripModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ynr.taximedriver.model.Location;

import java.util.List;

public class TripModel {
    private int expireTime;
    @SerializedName("bidValue")
    @Expose
    private int bidValue;
    @SerializedName("validTime")
    @Expose
    private int validTime;
    @SerializedName("assignedDriverId")
    @Expose
    private String assignedDriverId;
    @SerializedName("assignedVehicleId")
    @Expose
    private String assignedVehicleId;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("customerTelephoneNo")
    @Expose
    private String mobileNumber;
    @SerializedName("distance")
    @Expose
    private double distance;
    @SerializedName("dropLocation")
    @Expose
    private com.ynr.taximedriver.model.Location dropLocation;
    @Expose
    @SerializedName("dropLocations")
    private List<com.ynr.taximedriver.model.Location> dropLocations;
    @SerializedName("hireCost")
    @Expose
    private double hireCost;
    @SerializedName("noOfPassengers")
    @Expose
    private int noOfPassengers;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("pickupDateTime")
    @Expose
    private String pickupDateTime;
    @SerializedName("pickupLocation")
    @Expose
    private com.ynr.taximedriver.model.Location pickupLocation;
    @SerializedName("totalPrice")
    @Expose
    private double totalPrice;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("vehicleCategory")
    @Expose
    private String vehicleCategory;
    @SerializedName("vehicleSubCategory")
    @Expose
    private String vehicleSubCategory;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("dispatcherId")
    @Expose
    private String dispatcherId;
    @Expose
    @SerializedName("passengerDetails")
    private PassengerDetails passengerDetails;

    public int getValidTime() {
        return validTime;
    }

    public void setValidTime(int validTime) {
        this.validTime = validTime;
    }

    public String getAssignedDriverId() {
        return assignedDriverId;
    }

    public void setAssignedDriverId(String assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }

    public String getAssignedVehicleId() {
        return assignedVehicleId;
    }

    public void setAssignedVehicleId(String assignedVehicleId) {
        this.assignedVehicleId = assignedVehicleId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getHireCost() {
        return hireCost;
    }

    public void setHireCost(double hireCost) {
        this.hireCost = hireCost;
    }

    public int getNoOfPassengers() {
        return noOfPassengers;
    }

    public void setNoOfPassengers(int noOfPassengers) {
        this.noOfPassengers = noOfPassengers;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPickupDateTime() {
        return pickupDateTime;
    }

    public void setPickupDateTime(String pickupDateTime) {
        this.pickupDateTime = pickupDateTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public String getVehicleSubCategory() {
        return vehicleSubCategory;
    }

    public void setVehicleSubCategory(String vehicleSubCategory) {
        this.vehicleSubCategory = vehicleSubCategory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public String getDispatcherId() {
        return dispatcherId;
    }

    public void setDispatcherId(String dispatcherId) {
        this.dispatcherId = dispatcherId;
    }

    public int getBidValue() {
        return bidValue;
    }

    public void setBidValue(int bidValue) {
        this.bidValue = bidValue;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(Location dropLocation) {
        this.dropLocation = dropLocation;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public List<Location> getDropLocations() {
        return dropLocations;
    }

    public void setDropLocations(List<Location> dropLocations) {
        this.dropLocations = dropLocations;
    }

    public PassengerDetails getPassengerDetails() {
        return passengerDetails;
    }

    public void setPassengerDetails(PassengerDetails passengerDetails) {
        this.passengerDetails = passengerDetails;
    }

    public class PassengerDetails {
        @Expose
        @SerializedName("userProfilePic")
        private String userProfilePic;
        @Expose
        @SerializedName("contactNumber")
        private String contactNumber;
        @Expose
        @SerializedName("gender")
        private String gender;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
            @SerializedName("id")
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }

        public String getUserProfilePic() {
            return userProfilePic;
        }

        public void setUserProfilePic(String userProfilePic) {
            this.userProfilePic = userProfilePic;
        }
    }
}
