package com.ynr.taximedriver.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.ynr.taximedriver.model.roadPickupModel.EndRoadPickupTripModel;
import com.ynr.taximedriver.model.tripAcceptModel.TripAcceptResponseModel;
import com.ynr.taximedriver.model.tripModel.TripModel;

public class TripSession {
    static SharedPreferences pref; // Shared Preferences
    SharedPreferences.Editor editor;  // Editor for Shared preferences
    Context _context;   // Context
    int PRIVATE_MODE = 0;   // Shared pref mode
    private static final String PREF_NAME = "Trip";   // Sharedpref file name
    private static final String TRIP_MODEL = "tripModel";
    private static final String TRIP_PRICE_MODEL = "tripPriceModel";
    private static final String LAST_LONGITUDE = "lastLongitude";
    private static final String LAST_LATITUDE = "lastLatitude";
    private static final String WAIT_TIME_MILLIS = "waitTimeMillis";
    private static final String TOTAL_TIME_MILLIS = "totalTimeMillis";
    private static final String TOTAL_DISTANCE = "totalDistance";
    private static final String PREV_DISTANCE = "prevDistance";
    private static final String DRIVER_SATATE = "driverState";
    private static final String IS_ON_TRIP = "isTrip";
    private static final String LAST_ACTIVITY = "lastActivity";
    private static final String ONGOING_ROAD_PICKUP = "categoryImage";

    // Constructor
    public TripSession(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLastActivity(String activity) {
        editor.putString(LAST_ACTIVITY, activity);
        editor.commit();
    }
    public String getLastActivity() {
        return pref.getString(LAST_ACTIVITY, null);
    }

    public void setTripModel(TripModel model) {
        Gson gson = new Gson();
        String json = gson.toJson(model);
        editor.putString(TRIP_MODEL, json);
        editor.putBoolean(IS_ON_TRIP, true);
        editor.commit();
    }
    public TripModel getTripModel() {
        Gson gson = new Gson();
        String json = pref.getString(TRIP_MODEL, null);
        TripModel object = gson.fromJson(json, TripModel.class);
        return object;
    }

    public void setTripPriceModel(TripAcceptResponseModel model) {
        Gson gson = new Gson();
        String json = gson.toJson(model);
        editor.putString(TRIP_PRICE_MODEL, json);
        editor.commit();
    }
    public TripAcceptResponseModel getTripPriceModel() {
        Gson gson = new Gson();
        String json = pref.getString(TRIP_PRICE_MODEL, null);
        TripAcceptResponseModel object = gson.fromJson(json, TripAcceptResponseModel.class);
        return object;
    }

    public void setLastLatitude(double lastLatitude) {
        editor.putFloat(LAST_LATITUDE, (float)lastLatitude);
        editor.commit();
    }
    public double getLastLatitude() {
        return (double)pref.getFloat(LAST_LATITUDE, 0.0f);
    }

    public void setLastLongitude(double lastLongitude) {
        editor.putFloat(LAST_LONGITUDE, (float)lastLongitude);
        editor.commit();
    }
    public double getLastLongitude() {
        return (double)pref.getFloat(LAST_LONGITUDE, 0.0f);
    }

    public void setWaitTimeMillis(double waitTimeMillis) {
        editor.putFloat(WAIT_TIME_MILLIS, (float)waitTimeMillis);
        editor.commit();
    }
    public double getWaitTimeMillis() {
        return (double)pref.getFloat(WAIT_TIME_MILLIS, 0.0f);
    }

    public void setTotalTimeMillis(double totalTimeMillis) {
        editor.putFloat(TOTAL_TIME_MILLIS, (float)totalTimeMillis);
        editor.commit();
    }
    public double getTotalTimeMillis() {
        return (double)pref.getFloat(TOTAL_TIME_MILLIS, 0.0f);
    }

    public void setTotalDistance(double totalDistance) {
        editor.putFloat(TOTAL_DISTANCE, (float)totalDistance);
        editor.commit();
    }
    public double getTotalDistance() {
        return (double)pref.getFloat(TOTAL_DISTANCE, 0.0f);
    }

    public void setPrevDistance(double prevDistance) {
        editor.putFloat(PREV_DISTANCE, (float)prevDistance);
        editor.commit();
    }
    public double getPrevDistance() {
        return (double)pref.getFloat(PREV_DISTANCE, 0.0f);
    }

    public void setDriverState(String driverState) {
        editor.putString(DRIVER_SATATE, driverState);
        editor.commit();
    }
    public String getDriverState() {
        return pref.getString(DRIVER_SATATE, null);
    }

    public boolean isOnTrip() {
        return pref.getBoolean(IS_ON_TRIP, false);
    }
    /**
     * set and get ongoing roadPickup
     */
    public void setRoadPickup(EndRoadPickupTripModel model) {
        String json =  new Gson().toJson(model);
        editor.putString(ONGOING_ROAD_PICKUP, json);
        editor.commit();
        Log.i("setRoadPickup", json);
    }
    public EndRoadPickupTripModel getRoadPickup() {
        String json = pref.getString(ONGOING_ROAD_PICKUP, "");
        Gson gson = new Gson();
        return gson.fromJson(json, EndRoadPickupTripModel.class);
    }
    public boolean isOnPickup() {
        return !pref.getString(ONGOING_ROAD_PICKUP, "").isEmpty();
    }
    public void clearRoadPickup(){
        editor.remove(ONGOING_ROAD_PICKUP);
        editor.commit();
    }
    public void clearTripSession() {
        editor.clear();
        editor.commit();
    }
}
