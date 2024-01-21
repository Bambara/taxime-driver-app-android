package com.ynr.taximedriver.config;

import com.ynr.taximedriver.BuildConfig;

public class KeyString {

    public static final int APP_VERSION = BuildConfig.VERSION_CODE;


    public static final String BASE_URL = "http://192.168.0.100";

    //    public static final String BASE_URL = "http://13.229.230.179";
    public static final String BASE_SOCKET = "8095";
    public static final String DRIVER_SOCKET = "8099";

    public static final String PASSENGER_SOCKET = "8101";

    public static final String PROFILE_IMAGE = "profile image";
    public static final String DRIVING_LICENCE_FRONT = "licence front";
    public static final String DRIVING_LICENCE_BACK = "licence back";
    public static final String NIC_FRONT = "nic_front";
    public static final String NIC_BACK = "nic back";
    public static final String MALE = "male";
    public static final String FEMALE = "female";
    public static final String MOBILE_NUMBER = "mobile number";
    public static final String FIRST_NAME = "first name";
    public static final String EMAIL = "email";
    public static final String NIC = "nic";
    public static final String ON_TRIP = "onTrip";
    public static final String UPON_COMPLETION = "uponCompletion";
    public static final String DRIVER_ARRIVED = "arrived";

    public static final String DROP_LOCATION_ARRIVED = "arrivedToDropLocation";

    public static final String UPDATE_PAY_STATUS = "updatePayStatus";

    public static final String PAYMENT_PENDING = "paymentPending";

    public static final String PAYMENT_DONE = "paymentDone";

    public static final String NOT_PAYED = "notPayed";
    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";

    public static final String BLOCKED = "blocked";
    public static final String GOING_TO_PICKUP = "goingToPickup";

    public static final String DISCONNECTED = "disconnect";
    public static final String UPDATE_DRIVER_STATE_SOCKET = "updateCurrentStatus";
    public static final String SUBMIT_LOCATION_SOCKET = "submitLocation";
    public static final String DRIVER_CONNECTED_SOCKET = "driverConnected";
    public static final String PASSENGER_CANCEL_TRIP_SOCKET = "passengercanceltrip";
    public static final String DISPATCH_SOCKET = "dispatch";
    public static final String REMOVE_DISPATCH_SOCKET = "removeDispatch";
    public static final String SOCKET_DISCONNECT = "toDisconnect";
    public static final String TYPE = "driverDispatch";
    public static final String TRIP_MODEL = "tripModel";
    public static final String TRIP_PRICE_MODEL = "tripPriceModel";
    public static final String ROAD_PICUP_MODEL = "roadPickupModel";
    public static final String DISPATCH_HISTORY_MODEL = "dispatchHistoryModel";
    public static final String VEHICLE_BOOK = "vehicle book";
    public static final String VEHICLE_REVENUE_LICENCE = "vehicle revenue insurance";
    public static final String VEHICLE_INSURANCE = "vehicle insurance";
    public static final String VEHICLE_FRONT = "vehicle front";
    public static final String VEHICLE_BACK = "vehicle back";
    public static final String VEHICLE_SIDE_VIEW = "vehicle side view";
    public static final String TRIP_NOTIFICATION = "trip notification";
    public static final String DRIVER_CONNECT_RESULT = "driverConnectResult";
    public static final String RECON = "recon";
    public static final String RECONNECT_RESULT = "reConnectResult";
    public static final String TOTAL_TIME = "totalTime";
    public static final String TRIP_LIST = "tripList";
    public static final String WAIT_TIME = "waitTime";
    public static final String DISTANCE = "distance";
    public static final String DRIVER_DISPATCH = "driverDispatch";
    public static final String USER_DISPATCH = "userDispatch";
    public static final String ADMIN_DISPATCH = "adminDispatch";
    public static final String PASSENGER_TRIP = "passengerTrip";

    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String EXTRA_ADDRESS = "extra_address";

    public static final String DEFAULT = "default";
    public static final String ACCEPTED = "accepted";
    public static final String CANCELED = "canceled";
    public static final String DONE = "done";
}