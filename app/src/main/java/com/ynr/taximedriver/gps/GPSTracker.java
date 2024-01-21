package com.ynr.taximedriver.gps;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Create this Class from tutorial :
 * http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial
 *
 * For Geocoder read this : http://stackoverflow.com/questions/472313/android-reverse-geocoding-getfromlocation
 *
 */

public class GPSTracker extends Service implements LocationListener {

    // Get Class Name
    private static String TAG = GPSTracker.class.getName();

    private final Context mContext;

    // flag for GPS Status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS Tracking is enabled
    boolean isGPSTrackingEnabled = false;

    Location location;
    double latitude;
    double longitude;

    // How many Geocoder should return our GPSTracker
    int geocoderMaxResults = 1;

    // The minimum distance to change updates in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 2;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private String provider_info;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    /**
     * Try to get my current location by GPS or Network Provider
     */
    @SuppressLint("MissingPermission")
    public void getLocation() {

        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.i("TAG GPS status", String.valueOf(isGPSEnabled));

            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.i("TAG Network status", String.valueOf(isNetworkEnabled));

            // Try to get location if you GPS Service is enabled
            if (isGPSEnabled) {
                this.isGPSTrackingEnabled = true;

                Log.i("TAG", "Application use GPS Service");

                /*
                 * This provider determines location using
                 * satellites. Depending on conditions, this provider may take a while to return
                 * a location fix.
                 */

                provider_info = LocationManager.GPS_PROVIDER;
                Log.i("TAG Provider", provider_info);
                Log.i("TAG", LocationManager.NETWORK_PROVIDER);

            } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
                this.isGPSTrackingEnabled = true;

                Log.i("TAG", "Application use Network State to get GPS coordinates");

                /*
                 * This provider determines location based on
                 * availability of cell tower and WiFi access points. Results are retrieved
                 * by means of a network lookup.
                 */
                provider_info = LocationManager.NETWORK_PROVIDER;
                Log.i("TAG Provider", provider_info);

            }

            // Application can use GPS or Network Provider
            if (!provider_info.isEmpty()) {
                try {
                    locationManager.requestLocationUpdates(provider_info, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                } catch (Exception e) {

                }
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(provider_info);
                    updateGPSCoordinates();
                }
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            Log.e(TAG, "Impossible to connect to LocationManager", e);
        }
    }

    /**
     * Update GPSTracker latitude and longitude
     */
    public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * GPSTracker latitude getter and setter
     * @return latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * GPSTracker longitude getter and setter
     * @return
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public String getProvider_info(){
        return provider_info;
    }

    /**
     * GPSTracker isGPSTrackingEnabled getter.
     * Check GPS/wifi is enabled
     */
    public boolean getIsGPSTrackingEnabled() {

        return this.isGPSTrackingEnabled;
    }

    /**
     * Stop using GPS listener
     * Calling this method will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }


    /**
     * Function to show settings alert dialog
     */
//    public void showSettingsAlert() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//
//        //Setting Dialog Title
//        alertDialog.setTitle(R.string.GPSAlertDialogTitle);
//
//        //Setting Dialog Message
//        alertDialog.setMessage(R.string.GPSAlertDialogMessage);
//
//        //On Pressing Setting button
//        alertDialog.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                mContext.startActivity(intent);
//            }
//        });
//
//        //On pressing cancel button
//        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                dialog.cancel();
//            }
//        });
//
//        alertDialog.show();
//    }

    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress(Context context) {
        if (location != null) {

            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, this.geocoderMaxResults);

                return addresses;
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    public String getAddressLine(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);

            return addressLine;
        } else {
            return null;
        }
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    public String getLocality(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getLocality();

            return locality;
        }
        else {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
    public String getPostalCode(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();

            return postalCode;
        } else {
            return null;
        }
    }

    /**
     * Try to get CountryName
     * @return null or postalCode
     */
    public String getCountryName(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String countryName = address.getCountryName();

            return countryName;
        } else {
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
//import android.Manifest;
//import android.app.Activity;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.PendingResult;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.ynr.snap.R;
//
//import java.io.IOException;
//import java.text.DateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//public class GPSTracker extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
//
//    private static final String TAG = "LocationActivity";
//    private static final long INTERVAL = 1000;
//    private static final long FASTEST_INTERVAL =  500;
//    Button btnFusedLocation;
//    TextView tvLocation;
//    LocationRequest mLocationRequest;
//    GoogleApiClient mGoogleApiClient;
//    public static Location mCurrentLocation;
//    String mLastUpdateTime;
//    static double latitude;
//    static double longitude;
//    static int geocoderMaxResults = 1;
//
//    public GPSTracker() {
////        this.mContext = context;
////        getLocation();
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    public GPSTracker(Context context) {
////        this.mContext = context;
////        getLocation();
//    }
//
//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//
//    @Override
//    public void onCreate() {
//        Log.d(TAG, "onCreate ...............................");
//        //show error dialog if GoolglePlayServices not available
//        if (!isGooglePlayServicesAvailable()) {
//           // finish();
//            Log.d(TAG, "onCreate ...............................");
//        }
//        createLocationRequest();
//        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onStart(Intent intent, int startid) {
//        if (mGoogleApiClient.isConnected()) {
//            startLocationUpdates();
//            Log.d(TAG, "Location update resumed .....................");
//        }
//    }
//
////    @Override
////    public void onStop() {
////        super.onStop();
////        Log.d(TAG, "onStop fired ..............");
////        mGoogleApiClient.disconnect();
////        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
////    }
//
//    private boolean isGooglePlayServicesAvailable() {
//        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (ConnectionResult.SUCCESS == status) {
//            return true;
//        } else {
//           // GooglePlayServicesUtil.getErrorDialog(status, , 0).show();
//            return false;
//        }
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
//        startLocationUpdates();
//    }
//
//    protected void startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        Log.d(TAG, "Location update started ..............: ");
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.d(TAG, "Connection failed: " + connectionResult.toString());
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        Log.d(TAG, "Firing onLocationChanged..............................................");
//        mCurrentLocation = location;
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//    }
//
//    /**
//      * GPSTracker latitude getter and setter
//      * @return latitude
//      */
//    public static double getLatitude() {
//        if (mCurrentLocation != null) {
//            latitude = mCurrentLocation.getLatitude();
//        }
//        return latitude;
//    }
//
//    /**
//     * GPSTracker longitude getter and setter
//     * @return
//     */
//    public static double getLongitude() {
//        if (mCurrentLocation != null) {
//            longitude = mCurrentLocation.getLongitude();
//        }
//        return longitude;
//    }
//        /**
//     * Try to get AddressLine
//     * @return null or addressLine
//     */
//    public static String getAddressLine(Context context) {
//        List<Address> addresses = getGeocoderAddress(context);
//
//        if (addresses != null && addresses.size() > 0) {
//            Address address = addresses.get(0);
//            String addressLine = address.getAddressLine(0);
//
//            return addressLine;
//        } else {
//            return null;
//        }
//    }
//    /**
//     * Get list of address by latitude and longitude
//     * @return null or List<Address>
//     */
//    public static List<Address> getGeocoderAddress(Context context) {
//        if (mCurrentLocation != null) {
//
//            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
//
//            try {
//                /**
//                 * Geocoder.getFromLocation - Returns an array of Addresses
//                 * that are known to describe the area immediately surrounding the given latitude and longitude.
//                 */
//                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, geocoderMaxResults);
//
//                return addresses;
//            } catch (IOException e) {
//                //e.printStackTrace();
//                Log.e(TAG, "Impossible to connect to Geocoder", e);
//            }
//        }
//
//        return null;
//    }
//
//    protected void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        Log.d(TAG, "Location update stopped .......................");
//    }
//}