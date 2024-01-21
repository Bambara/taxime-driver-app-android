package com.ynr.taximedriver.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.background.AlarmReceiver;
import com.ynr.taximedriver.background.LocationMonitoringService;
import com.ynr.taximedriver.background.MyService;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.config.Permission;
import com.ynr.taximedriver.home.dispatcher.DispatcherFragment;
import com.ynr.taximedriver.home.driver.BookingsFragment;
import com.ynr.taximedriver.home.driver.DriverFragment;
import com.ynr.taximedriver.home.driver.OnTripActivity;
import com.ynr.taximedriver.home.road_pickup.RoadPickupPriceCalculatorActivity;
import com.ynr.taximedriver.model.DriverState;
import com.ynr.taximedriver.model.driverCheckInformationModel.DriverCheckRequestModel;
import com.ynr.taximedriver.model.driverCheckInformationModel.DriverCheckResponseModel;
import com.ynr.taximedriver.model.driverConnectModel.DriverDisconnectModel;
import com.ynr.taximedriver.model.tripModel.TripModel;
import com.ynr.taximedriver.myHires.MyHiresFragment;
import com.ynr.taximedriver.notification.NotificationFragment;
import com.ynr.taximedriver.other.SocketReconnect;
import com.ynr.taximedriver.other.VehicleSelecterPopup;
import com.ynr.taximedriver.profile.ProfileFragment;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.session.LoginSession;
import com.ynr.taximedriver.session.TripListSession;
import com.ynr.taximedriver.session.TripSession;
import com.ynr.taximedriver.socket.MySocket;
import com.ynr.taximedriver.wallet.WalletFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MyService.Callbacks {
    private ImageView profileImage;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    private LinearLayout home, myHires, wallet, notification, profile;
    private TextView homeText, myHireText, walletText, notificationText, profileText;
    private ImageView homeIcon, myHireIcon, walletIcon, notificationIcon, profileIcon;
    private Socket mSocket;
    MyService myService;
    Intent serviceIntent;
    public List<TripModel> tripModelList = new ArrayList<>();
    private int tripModelSize = tripModelList.size();
    BookingsFragment fragment;
    HomeFragment homeFragment;
    protected PowerManager.WakeLock mWakeLock;
    public static final String CHANNEL_ID = "Trip Notification Channel";
    private boolean onBackPress = false;
    private boolean isMyServiceStared = false;
    private Handler connectionStateHandler;
    private Runnable connectionStateRunnable;
    AudioManager mobileMode;
    int previousNotificationVolume;

    private final String BookingFragmentTag = "BookingsTag";

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            //Toast.makeText(MainActivity.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            Log.i("TAG_SERVICE_CONNECTION", "onServiceConnected called");
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getServiceInstance(); //Get instance of your service!
            myService.registerClient(HomeActivity.this); //Activity register in the service as client for callabcks!
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            //Toast.makeText(MainActivity.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();
            Log.i("TAG_SERVICE_CONNECTION", "nServiceDisconnected called");
        }
    };
    private MediaPlayer mp;

    @SuppressLint({"InvalidWakeLockTag", "WakelockTimeout"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        createNotificationChannel();
        onBackPress = false;
        //startActivity(new Intent(getApplicationContext(), GPSTracker.class));
        //startService(new Intent(this, GPSTracker.class));
        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        TypeToken<List<TripModel>> token = new TypeToken<List<TripModel>>() {
                        };
                        List<TripModel> data = new Gson().fromJson(intent.getStringExtra(KeyString.TRIP_LIST), token.getType());
                        updateDriverTrips(data);
                    }
                }, new IntentFilter(MyService.ACTION_LOCATION_BROADCAST)
        );
        MySocket socket = (MySocket) getApplication();
        mSocket = socket.getmSocket();
        mSocket.connect();
        setConnectionState();
//        if (!isLocationServiceStared) {
//            startService(new Intent(this, LocationMonitoringService.class));
//            isLocationServiceStared = true;
//        }
        if (!isMyServiceStared) {
            //startService(new Intent(this, MyService.class));
            Permission permission = new Permission(getApplicationContext(), HomeActivity.this);
            if (!permission.isLocationPermissionGranted()) {
                permission.checkPermissions();
//                return;
            } else {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
                if (Build.VERSION.SDK_INT >= 23) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 30000, pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, 30000, pendingIntent);
                }

                isMyServiceStared = true;
            }
        }
//        else if (!isLocationServiceStared || !isMyServiceStared){
//            AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
//            alertDialog.setTitle("Something Went Wrong");
//            alertDialog.setMessage("please restart your app");
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            finish();
//                        }
//                    });
//            alertDialog.show();
//        }


        //when tap on trip notification start the driver fragment
//        try {
//            if (getIntent().getStringExtra(KeyString.TRIP_NOTIFICATION).equals(KeyString.TRIP_NOTIFICATION)) {
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, DriverFragment.newInstance()).commit();
//            }
//        } catch (Exception e) {
//
//        }

        //background service connection
        serviceIntent = new Intent(getApplicationContext(), MyService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment, HomeFragment.newInstance()).commit();

        setBottomToolbar();

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        if (new LoginSession(getApplicationContext()).getVehicle() == null) {
            new VehicleSelecterPopup(getApplicationContext(), HomeActivity.this);
        } else if (new LoginSession(getApplicationContext()).getSocketId() == null) {
//            boolean state = true;
            SocketReconnect.driverConnect(getApplicationContext(), mSocket);
//            while (state) {
//                if (mSocket.connected()) {
//                    SocketReconnect.driverConnect(getApplicationContext(), mSocket);
//                    state = false;
//                    Log.i("TAG_DRIVER_CONNECT", "true");
//                } else {
//                    Log.i("TAG_DRIVER_CONNECT", "false");
//                }
//            }
            checkDriver(getApplicationContext());
        }
        TripSession tripSession = new TripSession(getApplicationContext());
        if (tripSession.isOnTrip()) {
            Intent intent = new Intent(getApplicationContext(), OnTripActivity.class);
            startActivity(intent);
        }  else if (tripSession.isOnPickup()) {
            Intent intent = new Intent(getApplicationContext(), RoadPickupPriceCalculatorActivity.class);
            startActivity(intent);
        }
    }

    public void stopTone() {
        if (mp == null) return;
        mp.stop();
        mp.release();
        mp = null;
    }

    public void playTone() {
        if (mp != null) {
            stopTone();
        }
        mp = MediaPlayer.create(HomeActivity.this, R.raw.beep);
        mp.setVolume(1, 1);
        mp.start();
    }

    /**
     * display current socket connection state on home top right conner
     */
    private void setConnectionState() {
        connectionStateHandler = new Handler();
        connectionStateRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (currentFragment instanceof HomeFragment && new LoginSession(getApplicationContext()).getSocketId() != null) {
                    final HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                    if (mSocket.connected() && !homeFragment.getConenctionState().equals(R.string.connected)) {
                        runOnUiThread(() -> homeFragment.setConnectionState(getString(R.string.connected)));
                    } else if (!homeFragment.getConenctionState().equals(R.string.connecting)) {
                        runOnUiThread(() -> homeFragment.setConnectionState(getString(R.string.reconnecting)));
                    }
                }
                connectionStateHandler.postDelayed(this, 500);
            }
        };
        connectionStateHandler.postDelayed(connectionStateRunnable, 500);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (currentFragment instanceof DispatcherFragment || currentFragment instanceof DriverFragment ||
                currentFragment instanceof MyHiresFragment || currentFragment instanceof WalletFragment ||
                currentFragment instanceof NotificationFragment || currentFragment instanceof ProfileFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, HomeFragment.newInstance()).commit();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                homeText.setTextColor(getColor(R.color.redOne));
                myHireText.setTextColor(getColor(R.color.black));
                walletText.setTextColor(getColor(R.color.black));
                notificationText.setTextColor(getColor(R.color.black));
                profileText.setTextColor(getColor(R.color.black));
            }
        } else {
            onBackPress = true;
            super.onBackPressed();
        }
    }

    /**
     * set bottom tool bar icon color when tap the icon
     */
    private void setBottomToolbar() {
        home = findViewById(R.id.home);
        myHires = findViewById(R.id.my_hires);
        wallet = findViewById(R.id.wallet);
        notification = findViewById(R.id.notification);
        profile = findViewById(R.id.profile);
        homeText = findViewById(R.id.home_text);
        myHireText = findViewById(R.id.my_hire_text);
        walletText = findViewById(R.id.wallet_text);
        notificationText = findViewById(R.id.notification_text);
        profileText = findViewById(R.id.profile_text);
        homeIcon = findViewById(R.id.home_image);
        myHireIcon = findViewById(R.id.my_hire_image);
        walletIcon = findViewById(R.id.wallet_image);
        notificationIcon = findViewById(R.id.notification_image);
        profileIcon = findViewById(R.id.profileImage);
        home.setOnClickListener(view -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (!(currentFragment instanceof HomeFragment)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, HomeFragment.newInstance()).commit();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    homeText.setTextColor(getColor(R.color.redOne));
                    homeText.setTypeface(null, Typeface.BOLD);
                    myHireText.setTextColor(getColor(R.color.black));
                    walletText.setTextColor(getColor(R.color.black));
                    notificationText.setTextColor(getColor(R.color.black));
                    profileText.setTextColor(getColor(R.color.black));
                }
            }
        });
        myHires.setOnClickListener(view -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (!(currentFragment instanceof MyHiresFragment)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, MyHiresFragment.newInstance()).commit();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    homeText.setTextColor(getColor(R.color.black));
                    myHireText.setTextColor(getColor(R.color.redOne));
                    myHireText.setTypeface(null, Typeface.BOLD);
                    walletText.setTextColor(getColor(R.color.black));
                    notificationText.setTextColor(getColor(R.color.black));
                    profileText.setTextColor(getColor(R.color.black));
                }
            }
        });
        wallet.setOnClickListener(v -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (!(currentFragment instanceof WalletFragment)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, WalletFragment.newInstance()).commit();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    homeText.setTextColor(getColor(R.color.black));
                    myHireText.setTextColor(getColor(R.color.black));
                    walletText.setTextColor(getColor(R.color.redOne));
                    walletText.setTypeface(null, Typeface.BOLD);
                    notificationText.setTextColor(getColor(R.color.black));
                    profileText.setTextColor(getColor(R.color.black));
                }
            }
        });
        notification.setOnClickListener(view -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (!(currentFragment instanceof NotificationFragment)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, NotificationFragment.newInstance()).commit();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    homeText.setTextColor(getColor(R.color.black));
                    myHireText.setTextColor(getColor(R.color.black));
                    walletText.setTextColor(getColor(R.color.black));
                    notificationText.setTextColor(getColor(R.color.redOne));
                    notificationText.setTypeface(null, Typeface.BOLD);
                    profileText.setTextColor(getColor(R.color.black));
                }
            }
        });
        profile.setOnClickListener(view -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (!(currentFragment instanceof ProfileFragment)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, ProfileFragment.newInstance()).commit();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    homeText.setTextColor(getColor(R.color.black));
                    myHireText.setTextColor(getColor(R.color.black));
                    walletText.setTextColor(getColor(R.color.black));
                    notificationText.setTextColor(getColor(R.color.black));
                    profileText.setTextColor(getColor(R.color.redOne));
                    profileText.setTypeface(null, Typeface.BOLD);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        if (!onBackPress) {
            Intent myAlarm = new Intent(getApplicationContext(), AlarmReceiver.class);
            //myAlarm.putExtra("project_id",project_id); //put the SAME extras
            PendingIntent recurringAlarm = PendingIntent.getBroadcast(getApplicationContext(), 0, myAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarms.cancel(recurringAlarm);

            try {
                mobileMode.setStreamMute(AudioManager.STREAM_MUSIC, true);
                mobileMode.setStreamVolume(AudioManager.STREAM_NOTIFICATION, previousNotificationVolume, 0);
            } catch (Exception ignored) {
            }
            new TripListSession(getApplicationContext()).setTripListSession(null);
            stopService(new Intent(this, MyService.class));
            stopService(new Intent(this, LocationMonitoringService.class));
            isMyServiceStared = false;
            //isLocationServiceStared = false;
            //stopService(new Intent(this, GPSTracker.class));
            unbindService(mConnection);
            LoginSession session = new LoginSession(getApplicationContext());
            if (!session.getDriverState().equals(KeyString.GOING_TO_PICKUP) && !session.getDriverState().equals(KeyString.ON_TRIP)) {
                session.setDriverState(KeyString.OFFLINE);
            }
            //new LoginSession(getApplicationContext()).setDriverState(KeyString.OFFLINE);
            DriverState state = new DriverState(mSocket.id(), session.getDriverState(), new LoginSession(getApplicationContext()).get_Id());
            //if (!mSocket.connected()) new SocketReconnect(getApplicationContext(), mSocket);
            DriverDisconnectModel disconnectModel = new DriverDisconnectModel();
            disconnectModel.set_id(session.get_Id());
            disconnectModel.setDriverId(session.getUserDetails().getContent().getId());
            disconnectModel.setSocketId(session.getSocketId());
            try {
                mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                mSocket.emit(KeyString.SOCKET_DISCONNECT, new JSONObject(new Gson().toJson(disconnectModel, DriverDisconnectModel.class)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.disconnect();

            session.setSocketId(null);

            connectionStateHandler.removeCallbacks(connectionStateRunnable);
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        this.mWakeLock.release();
        super.onPause();
    }

    @SuppressLint("WakelockTimeout")
    @Override
    public void onResume() {
        super.onResume();
        this.mWakeLock.acquire();
        if (tripModelList.size() > 0) {
            try {
                homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                homeFragment.navigateDriver();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(result -> {
            final Status status = result.getStatus();
            //final LocationSettingsStates state = result.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    //...
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(HomeActivity.this, REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                    //...
                    break;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult()", Integer.toString(resultCode));
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == REQUEST_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    // All required changes were successfully made
                    Toast.makeText(HomeActivity.this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(HomeActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NotNull ConnectionResult connectionResult) {

    }

    private void updateDriverTrips(final List<TripModel> data) {
        this.tripModelList = data;
        new TripListSession(getApplicationContext()).setTripListSession(data);
        runOnUiThread(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (tripModelSize < data.size()) {
                sendNotification(data.get(tripModelSize));
                playTone();
            }
            try {
                if (currentFragment instanceof HomeFragment) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, DriverFragment.newInstance()).commit();
                }else if (currentFragment instanceof DriverFragment) {
                    Fragment currentNestedFragment = getSupportFragmentManager().findFragmentById(R.id.driver_fragment);
                    if (currentNestedFragment instanceof BookingsFragment) {
                        ((BookingsFragment) currentNestedFragment).updateList();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, BookingsFragment.newInstance(), BookingFragmentTag).commit();
                    }
                }else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, DriverFragment.newInstance()).commit();
                }
            } catch (Exception ignored) {
            }
            tripModelSize = data.size();
        });
    }

    @Override
    public void updateClient(final List<TripModel> data) {
        this.tripModelList = data;
        new TripListSession(getApplicationContext()).setTripListSession(data);
        mobileMode = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        previousNotificationVolume = mobileMode.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        mobileMode.setStreamMute(AudioManager.STREAM_MUSIC, false);
        mobileMode.setStreamVolume(AudioManager.STREAM_MUSIC, mobileMode.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        runOnUiThread(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (tripModelSize < data.size()) {
                playTone();
            }
            try {
                if (currentFragment instanceof HomeFragment) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, DriverFragment.newInstance()).commit();
                } else if (currentFragment instanceof DriverFragment) {
                    Fragment currentNestedFragment = getSupportFragmentManager().findFragmentById(R.id.driver_fragment);
                    if (currentNestedFragment instanceof BookingsFragment) {
                        ((BookingsFragment) currentNestedFragment).updateList();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, BookingsFragment.newInstance(), BookingFragmentTag).commit();
                    }
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, DriverFragment.newInstance()).commit();
                }
            } catch (Exception ignored) {
            }
            tripModelSize = data.size();
        });
    }

    public void sendNotification(TripModel model) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(HomeActivity.this, CHANNEL_ID);
        //Create the intent that’ll fire when the user taps the notification//
//        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(HomeActivity.class);
//        stackBuilder.addNextIntent(intent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        intent.putExtra(KeyString.TRIP_NOTIFICATION, KeyString.TRIP_NOTIFICATION);
//        final Intent intent = getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
        final Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(HomeActivity.this, 0, intent, 0);
        mBuilder.setSmallIcon(R.drawable.notification_icon_two);
        mBuilder.setContentTitle("New Trip");
        mBuilder.setContentText(model.getDropLocations().get(0).getAddress());
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(HomeActivity.this);
        mNotificationManager.notify(2, mBuilder.build());
    }

    /**
     * create notification chanel for upper API level 24
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void checkDriver(final Context context) {
        DriverCheckRequestModel model = new DriverCheckRequestModel();
        final LoginSession session = new LoginSession(context);
        model.setDriverId(session.getUserDetails().getContent().getId());
        model.setVehicleId(session.getVehicle().getId());
        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<DriverCheckResponseModel> call = apiInterface.driverCheck(model);
        call.enqueue(new Callback<DriverCheckResponseModel>() {
            @Override
            public void onResponse(@NotNull Call<DriverCheckResponseModel> call, @NotNull Response<DriverCheckResponseModel> response) {
                //progressDialog.dismiss();
                Log.i("TAG CHECK DRIVER CODE", String.valueOf(response.code()));
                LoginSession session1 = new LoginSession(context);
                if (response.code() == 200) {
                    session1.setDriverEnable(response.body().getDriverDispatch().get(0).isEnable());
                    session1.setDispatcherEnable(response.body().getDriverDispatch().get(0).isDispatchEnable());
                    session1.setVehicleEnable(response.body().getVehicle().isEnable());
                    session1.createSubCategoryImageSession(response.body().getSubCategoryImage());
                    if (response.body().getDriverDispatch().get(0).isDispatchEnable()) {
                        session1.createDispatcherSession(response.body().getDriverDispatch().get(0).getDispatcher().get(0));
                    } else {
                        session1.createDispatcherSession(null);
                    }
                } else if (response.code() == 203) {
                    session1.setDriverEnable(response.body().getDriverDispatch().get(0).isEnable());
                    session1.setDispatcherEnable(response.body().getDriverDispatch().get(0).isDispatchEnable());
                    session1.setVehicleEnable(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<DriverCheckResponseModel> call, @NotNull Throwable t) {
                //progressDialog.dismiss();
                Log.i("TAG CHECK DRIVER FAIL", t.getMessage());
            }
        });
    }
}
