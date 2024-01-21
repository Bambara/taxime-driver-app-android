package com.ynr.taximedriver.other;

import android.content.Context;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.gps.GPSTracker;
import com.ynr.taximedriver.model.driverConnectModel.DriverConnectResultModel;
import com.ynr.taximedriver.model.driverConnectModel.DriverConnectedModel;
import com.ynr.taximedriver.session.LoginSession;

import org.json.JSONException;
import org.json.JSONObject;

public class SocketReconnect {

    private static double latitude, longitude;
    private static String address;

    public SocketReconnect(Context context, Socket mSocket) {
        if (new LoginSession(context).getVehicle() != null) {
            mSocket.connect();
            driverConnect(context, mSocket);
            //checkDriver(context);
        }
    }

    public static void driverConnect(final Context context, Socket mSocket) {
//            boolean state = true;
//            while (state) {
//                if (latitude > 0 && longitude > 0) {
//                    state = false;
//                    Log.i("TAG_DRIVER_CONNECT", "true");
//                } else {
//                    Log.i("TAG_DRIVER_CONNECT", "false");
//                }
//                try {
//                    TimeUnit.MILLISECONDS.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        LocalBroadcastManager.getInstance(context).registerReceiver(
//                new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        latitude = intent.getDoubleExtra(KeyString.EXTRA_LATITUDE, 0);
//                        longitude = intent.getDoubleExtra(KeyString.EXTRA_LONGITUDE, 0);
//                        address = intent.getStringExtra(KeyString.EXTRA_ADDRESS);
//                    }
//                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
//        );
        //checkDriver(context);
        DriverConnectedModel dataModel = new DriverConnectedModel();
        final LoginSession session = new LoginSession(context);
        GPSTracker gpsTracker = new GPSTracker(context);

        if (session.getVehicle() == null) return;

        dataModel.setVehicleId(session.getVehicle().getId());
        dataModel.setDriverId(session.getUserDetails().getContent().getId() );
        dataModel.setAddress(gpsTracker.getAddressLine(context));
        dataModel.setLatitude(gpsTracker.getLatitude());
        dataModel.setLongitude(gpsTracker.getLongitude());
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

        }

        //MySocket socket = (MySocket)activity.getApplication();
        //mSocket = socket.getmSocket();
        //if (!mSocket.connected()) mSocket.connect();
        try {
            mSocket.emit(KeyString.DRIVER_CONNECTED_SOCKET, new JSONObject(new Gson().toJson(dataModel, DriverConnectedModel.class)));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("TAG_DRIVER_SOCKET", e.getMessage());
        }

            mSocket.on(KeyString.DRIVER_CONNECT_RESULT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject)args[0];
                    try {
                        DriverConnectResultModel model = new Gson().fromJson(data.toString(), DriverConnectResultModel.class);
                        LoginSession session1 = new LoginSession(context);
                        session1.set_Id(model.get_id());
                        session1.setSocketId(model.getSocketId());
                    } catch (Exception e) {

                    }

                }
            });
    }
}
