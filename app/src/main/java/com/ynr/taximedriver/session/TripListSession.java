package com.ynr.taximedriver.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ynr.taximedriver.model.tripModel.TripModel;

import java.util.List;

public class TripListSession {
    static SharedPreferences pref; // Shared Preferences
    SharedPreferences.Editor editor;  // Editor for Shared preferences
    Context _context;   // Context
    int PRIVATE_MODE = 0;   // Shared pref mode
    private static final String PREF_NAME = "TripList";   // Sharedpref file name
    private static final String TRIP_MODEL = "tripModel";

    // Constructor
    public TripListSession(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setTripListSession(List<TripModel> model) {
        Gson gson = new Gson();
        String json = gson.toJson(model);
        editor.putString(TRIP_MODEL, json);
        editor.commit();
    }
    public List<TripModel> getTripList() {
        String data = pref.getString(TRIP_MODEL, null);
        TypeToken<List<TripModel>> token = new TypeToken<List<TripModel>>() {};
        return new Gson().fromJson(data, token.getType());
    }

}
