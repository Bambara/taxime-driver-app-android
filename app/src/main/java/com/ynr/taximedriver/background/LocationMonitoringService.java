package com.ynr.taximedriver.background;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import android.location.LocationManager;

import com.ynr.taximedriver.R;
import com.ynr.taximedriver.config.KeyString;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.ynr.taximedriver.home.HomeActivity.CHANNEL_ID;


/**
 * Created by devdeeds.com on 27-09-2017.
 */

public class LocationMonitoringService extends Service {

    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    int geocoderMaxResults = 1;
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 500;
    private static final float LOCATION_DISTANCE = 5f;
    // flag for GPS Status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS Tracking is enabled
    boolean isGPSTrackingEnabled = false; // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private String provider_info;


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            sendLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

            //Create the intent that’ll fire when the user taps the notification//

//            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);


            mBuilder.setSmallIcon(R.drawable.notification_icon_two);
            mBuilder.setContentTitle("Welcome TaxiMe");
            mBuilder.setContentText(" ");
            mBuilder.setPriority(NotificationCompat.PRIORITY_MIN);
//            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_SECRET);
//            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(HomeActivity.this);
            mNotificationManager.notify(001, mBuilder.build());

            startForeground(001, mBuilder.build());
        }
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        //getting GPS status
        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.i("TAG GPS status", String.valueOf(isGPSEnabled));

        //getting network status
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
        if (provider_info != null) {
            if (provider_info.equals(LocationManager.GPS_PROVIDER)) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mLocationManager.requestLocationUpdates(provider_info, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[0]);
                } catch (Exception e) {

                }
            } else if (provider_info.equals(LocationManager.NETWORK_PROVIDER)) {
                try {
                    mLocationManager.requestLocationUpdates(provider_info, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[1]);
                } catch (Exception e) {

                }
            }
        }

//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[1]);
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//        }
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[0]);
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
//        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        super.onDestroy();
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void sendLocation(Location location) {
        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(KeyString.EXTRA_LATITUDE, location.getLatitude());
        intent.putExtra(KeyString.EXTRA_LONGITUDE, location.getLongitude());
        intent.putExtra(KeyString.EXTRA_ADDRESS, getAddressLine(getApplicationContext(), location));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    public String getAddressLine(Context context, Location location) {
        List<Address> addresses = getGeocoderAddress(context, location);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);

            return addressLine;
        } else {
            return null;
        }
    }

    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress(Context context, Location location) {
        if (location != null) {

            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), this.geocoderMaxResults);

                return addresses;
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }
}