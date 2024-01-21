package com.ynr.taximedriver.other;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

    public static boolean isConnected(Context context) {
        ConnectivityManager CM = (ConnectivityManager)context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(CM != null){
            NetworkInfo info = CM.getActiveNetworkInfo();
            if(info != null){
                if(info.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
//        try {
//            InetAddress ipAddr = InetAddress.getByName("google.com");
//            //You can replace it with your name
//            return !ipAddr.equals("");
//
//        } catch (Exception e) {
//            return false;
//        }
    }
}