package com.ynr.taximedriver.model.mapDistanceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Leg {

    @SerializedName("distance")
    @Expose
    private Distance distance;
    @SerializedName("duration")
    @Expose
    private Duration duration;
    @SerializedName("end_address")
    @Expose
    private String dropAddress;
    @SerializedName("end_location")
    @Expose
    private Location dropLocation;
    @SerializedName("start_address")
    @Expose
    private String pickupAddress;
    @SerializedName("start_location")
    @Expose
    private Location pickupLocation;

    /**
     *
     * @return
     * The distance
     */
    public Distance getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     * The distance
     */
    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     * The duration
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     * The duration
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }


    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(Location dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }
}
