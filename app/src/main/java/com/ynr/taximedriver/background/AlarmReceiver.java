package com.ynr.taximedriver.background;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import androidx.core.content.ContextCompat;

public class AlarmReceiver extends BroadcastReceiver {
    PowerManager.WakeLock screenWakeLock;
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (screenWakeLock == null)
        {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            screenWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,"ScreenLock tag from AlarmListener");
            screenWakeLock.acquire();
        }
//        Intent service = new Intent(context, MyService.class);
//        startWakefulService(context, service);
//        Intent locationService = new Intent(context, LocationMonitoringService.class);
//        startWakefulService(context, locationService);
        ContextCompat.startForegroundService(context, new Intent(context, LocationMonitoringService.class));
        ContextCompat.startForegroundService(context, new Intent(context, MyService.class));
        if (screenWakeLock != null)
            screenWakeLock.release();
    }
}
