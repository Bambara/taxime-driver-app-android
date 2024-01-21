package com.ynr.taximedriver.background;

import static com.ynr.taximedriver.home.HomeActivity.CHANNEL_ID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.gps.GPSTracker;
import com.ynr.taximedriver.model.SubmitLocationModel;
import com.ynr.taximedriver.model.driverConnectModel.DriverConnectResultModel;
import com.ynr.taximedriver.model.driverConnectModel.DriverConnectedModel;
import com.ynr.taximedriver.model.tripModel.TripModel;
import com.ynr.taximedriver.session.LoginSession;
import com.ynr.taximedriver.socket.MySocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyService extends Service {
    public MyService() {
    }

    public static final String ACTION_LOCATION_BROADCAST = MyService.class.getName() + "MyService";
    private com.github.nkzawa.socketio.client.Socket mSocket;
    private SubmitLocationModel locationModel;
    //    private GPSTracker location;
    private Handler timerHandler;
    private Runnable locationUpdateRunnable, tripListenerRunnable;
    private final List<TripModel> tripModelList = new ArrayList<>();
    Callbacks activity;
    private final IBinder mBinder = new LocalBinder();
    private PowerManager.WakeLock wl;
    private double latitude = 0.0, longitude = 0.0;
    private String address;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("TAG_ON_BIND", "call onBind method");
        return mBinder;
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        Log.i("TAG_SERVICE", "Service created");
        MySocket socket = (MySocket) getApplication();
        mSocket = socket.getmSocket();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
        wl.acquire();

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
            mBuilder.setVibrate(null);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(HomeActivity.this);
            mNotificationManager.notify(001, mBuilder.build());

            startForeground(001, mBuilder.build());
        }

        Intent i = new Intent(getApplicationContext(), MyService.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 3333, i, PendingIntent.FLAG_IMMUTABLE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 60);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, cal.getTimeInMillis(), pi);

        GPSTracker location = new GPSTracker(getApplicationContext());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        address = location.getAddressLine(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("TAG_SERVICE", "Service started");
        locationModel = new SubmitLocationModel();
        final LoginSession session = new LoginSession(getApplicationContext());

        timerHandler = new Handler();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        latitude = intent.getDoubleExtra(KeyString.EXTRA_LATITUDE, 0);
                        longitude = intent.getDoubleExtra(KeyString.EXTRA_LONGITUDE, 0);
                        address = intent.getStringExtra(KeyString.EXTRA_ADDRESS);
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );

        /**
         * updating driver location every 3 second
         */
        locationUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                //if (latitude > 0 && longitude > 0) {
//                    location = new GPSTracker(getApplicationContext());
                try {
                    locationModel.set_id(new LoginSession(getApplicationContext()).get_Id());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                locationModel.setSocketId(mSocket.id());
//                    locationModel.setLatitude(location.getLatitude());
//                    locationModel.setLongitude(location.getLongitude());
//                    locationModel.setAddress(location.getAddressLine(getApplicationContext()));
                locationModel.setLatitude(latitude);
                locationModel.setLongitude(longitude);
                locationModel.setAddress(address);

                try {
                    locationModel.setVehicleId(session.getVehicle().getId());
                    locationModel.setVehicleCategory(session.getVehicle().getVehicleCategory());
                    locationModel.setVehicleSubCategory(session.getVehicle().getVehicleSubCategory());
                    locationModel.setVehicleRegistrationNo(session.getVehicle().getVehicleRegistrationNo());
                    locationModel.setVehicleLicenceNo(session.getVehicle().getVehicleLicenceNo());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                locationModel.setDriverId(session.getUserDetails().getContent().getId());
                locationModel.setOperationRadius(1);
                locationModel.setDriverName(session.getUserDetails().getContent().getFirstName() + " " + session.getUserDetails().getContent().getLastName());
                locationModel.setDriverContactNumber(session.getUserDetails().getContent().getContactNo());
                locationModel.setCurrentStatus(session.getDriverState());
                try {
                    locationModel.setDriverPic(session.getUserDetails().getContent().getProfileImage());
                    locationModel.setMapIconOntripSVG(session.getCategoryImage().getMapIconOntripSVG());
                    locationModel.setMapIconOfflineSVG(session.getCategoryImage().getMapIconOfflineSVG());
                    locationModel.setMapIconSVG(session.getCategoryImage().getMapIconSVG());
                    locationModel.setSubCategoryIconSelectedSVG(session.getCategoryImage().getSubCategoryIconSelectedSVG());
                    locationModel.setSubCategoryIconSVG(session.getCategoryImage().getSubCategoryIconSVG());
                    locationModel.setMapIconOntrip(session.getCategoryImage().getMapIconOntrip());
                    locationModel.setMapIconOffline(session.getCategoryImage().getMapIconOffline());
                    locationModel.setMapIcon(session.getCategoryImage().getMapIcon());
                    locationModel.setSubCategoryIconSelected(session.getCategoryImage().getSubCategoryIconSelected());
                    locationModel.setSubCategoryIcon(session.getCategoryImage().getSubCategoryIcon());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
//                        Log.i("TAG_ADDRESS_CHECK_XXXXX", locationModel.getAddress());
                } catch (Exception e) {

                }
                //locationModel.setAddress(null);
//                    if (mSocket.connected()) {
//                        Log.i("TAG_SERVICE_SOCKET", "true");
//                    } else {
//                        Log.i("TAG_SERVICE_SOCKET", "false");
//                    }
                try {
                    mSocket.emit(KeyString.SUBMIT_LOCATION_SOCKET, new JSONObject(new Gson().toJson(locationModel, SubmitLocationModel.class)));
                } catch (JSONException e) {
                    Log.i("TAG_SUBMIT_LOCATION", e.getMessage());
                }
//                    Log.i("TAG_SOCKET_STATE", String.valueOf(mSocket.connected()));
//                    Log.i("TAG_TIMER", String.valueOf(locationModel.getAddress()));
//                    Log.i("TAG_URL", KeyString.BASE_URL + ":" + KeyString.DRIVER_SOCKET);
                //}
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.postDelayed(locationUpdateRunnable, 0);

        /**
         * listener for the trip
         */
        mSocket.on(KeyString.DISPATCH_SOCKET, args -> {
            JSONObject data = (JSONObject) args[0];
            TripModel tripModel = new Gson().fromJson(data.toString(), TripModel.class);
            tripModel.setExpireTime(((int) System.currentTimeMillis() / 1000) + tripModel.getValidTime());
            tripModelList.add(tripModel);
            try {
                activity.updateClient(tripModelList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        /**
         * listener for trip expired
         */
        mSocket.on(KeyString.REMOVE_DISPATCH_SOCKET, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                TripModel tripModel = new Gson().fromJson(data.toString(), TripModel.class);
                for (int i = 0; i < tripModelList.size(); i++) {
                    if (tripModelList.get(i).getId().equals(tripModel.getId())) {
                        tripModelList.remove(i);
                    }
                }
                try {
                    activity.updateClient(tripModelList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tripListenerRunnable = new Runnable() {
            @Override
            public void run() {
                int currentTime = (int) System.currentTimeMillis() / 1000;
                for (int i = 0; i < tripModelList.size(); i++) {
                    if (tripModelList.get(i).getExpireTime() < currentTime) {
                        tripModelList.remove(i);
                        try {
                            activity.updateClient(tripModelList);
                            sendTripList(tripModelList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                timerHandler.postDelayed(this, 500);
            }
        };
        timerHandler.postDelayed(tripListenerRunnable, 0);

        /**
         * listener for reconnect event
         */
        mSocket.on("reconnect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                DriverConnectedModel dataModel = new DriverConnectedModel();
                final LoginSession session = new LoginSession(getApplicationContext());
//                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());

                if (session.getVehicle() == null) return;

                dataModel.set_id(session.get_Id());
                dataModel.setSocketId(session.getSocketId());
                dataModel.setVehicleId(session.getVehicle().getId());
                dataModel.setDriverId(session.getUserDetails().getContent().getId());
//                dataModel.setAddress(gpsTracker.getAddressLine(getApplicationContext()));
//                dataModel.setLatitude(gpsTracker.getLatitude());
//                dataModel.setLongitude(gpsTracker.getLongitude());
                dataModel.setAddress(address);
                dataModel.setLatitude(latitude);
                dataModel.setLongitude(longitude);
                dataModel.setVehicleCategory(session.getVehicle().getVehicleCategory());
                dataModel.setVehicleSubCategory(session.getVehicle().getVehicleSubCategory());
                dataModel.setOperationRadius(1);
                dataModel.setDriverName(session.getUserDetails().getContent().getFirstName() + " " + session.getUserDetails().getContent().getLastName());
                dataModel.setDriverContactNumber(session.getUserDetails().getContent().getContactNo());
                dataModel.setVehicleRegistrationNo(session.getVehicle().getVehicleRegistrationNo());
                dataModel.setVehicleLicenceNo(session.getVehicle().getVehicleLicenceNo());
                dataModel.setCurrentStatus(session.getDriverState());

                try {
                    dataModel.setDriverPic(session.getUserDetails().getContent().getProfileImage());
                    dataModel.setMapIconOntripSVG(session.getCategoryImage().getMapIconOntripSVG());
                    dataModel.setMapIconOfflineSVG(session.getCategoryImage().getMapIconOfflineSVG());
                    dataModel.setMapIconSVG(session.getCategoryImage().getMapIconSVG());
                    dataModel.setSubCategoryIconSelectedSVG(session.getCategoryImage().getSubCategoryIconSelectedSVG());
                    dataModel.setSubCategoryIconSVG(session.getCategoryImage().getSubCategoryIconSVG());
                    dataModel.setMapIconOntrip(session.getCategoryImage().getMapIconOntrip());
                    dataModel.setMapIconOffline(session.getCategoryImage().getMapIconOffline());
                    dataModel.setMapIcon(session.getCategoryImage().getMapIcon());
                    dataModel.setSubCategoryIconSelected(session.getCategoryImage().getSubCategoryIconSelected());
                    dataModel.setSubCategoryIcon(session.getCategoryImage().getSubCategoryIcon());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    mSocket.emit(KeyString.RECON, new JSONObject(new Gson().toJson(dataModel, DriverConnectedModel.class)));
                } catch (Exception e) {
                    Log.i("TAG DRIVER RECONNECT", "catch");
                }
            }
        });

        mSocket.on(KeyString.RECONNECT_RESULT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    DriverConnectResultModel model = new Gson().fromJson(data.toString(), DriverConnectResultModel.class);
                    LoginSession session1 = new LoginSession(getApplicationContext());
                    session1.set_Id(model.get_id());
                    session1.setSocketId(model.getSocketId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return START_STICKY;
    }

    private void sendTripList(List<TripModel> tripModelList) {
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(KeyString.TRIP_LIST, new Gson().toJson(tripModelList));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        Log.i("TAG_SERVICE", "Service stopped ");
        wl.release();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        //timerHandler.removeCallbacks(locationUpdateRunnable);
        //timerHandler.removeCallbacks(tripListenerRunnable);

    }

    //Here Activity register to the service as Callbacks client
    public void registerClient(Activity activity) {
        this.activity = (Callbacks) activity;
    }

    //returns the instance of the service
    public class LocalBinder extends Binder {
        public MyService getServiceInstance() {
            return MyService.this;
        }
    }

    //callbacks interface for communication with service clients!
    public interface Callbacks {
        void updateClient(List<TripModel> data);
    }

}
