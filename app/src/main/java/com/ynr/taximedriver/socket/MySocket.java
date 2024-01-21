package com.ynr.taximedriver.socket;

import android.app.Application;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.ynr.taximedriver.config.KeyString;

import java.net.URISyntaxException;

public class MySocket extends Application {
    private Socket mSocket;
    private static final String URL = KeyString.BASE_URL + ":" + KeyString.DRIVER_SOCKET;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mSocket = IO.socket(URL);
            Log.i("TAG_MY_SOCKET", String.valueOf(mSocket.connected()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getmSocket() {
        return mSocket;
    }
}
