package com.ynr.taximedriver.fcm;

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
import androidx.core.content.ContextCompat
import com.ynr.taximedriver.R
import com.ynr.taximedriver.home.HomeActivity

class NotificationBuildManager private constructor(private val context: Context) {

    val mTag = "NotificationManager"
    var notificationId = 0

    fun displayNotification(title: String?, body: String?) {

        Log.d("notification_test", body ?: "Notification Display")

        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon_two)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)

        if(notificationId > 9000000){
            notificationId = 0
        }

        mNotificationManager.notify(notificationId++, mBuilder?.build())
    }

    fun displayNotification(title: String?, body: String?, data: Map<String, String>?) {

        Log.d("notification_test", body  ?: "Notification Display")

        if (data != null) {

            buildNormalNotification(title, body,data)
        } else {
            buildNormalNotification(title, body)
        }

    }


    fun buildNormalNotification(title: String?, body: String?) {

        Thread {

            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            //intent.putExtra("dfsd", "Afadf")

            var soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)


            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon_two)
                .setColor(ContextCompat.getColor(context, R.color.white))
                .setContentTitle(title)
                //.setLargeIcon(logo)
                .setAutoCancel(true)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)


            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

            if(notificationId > 9000000){
                notificationId = 0
            }

            mNotificationManager.notify(notificationId++, mBuilder?.build())


        }.start()
    }

    private fun buildNormalNotification(title: String?, body: String?, data: Map<String, String>?) {

        val intent = Intent(context, HomeActivity::class.java)
        intent.putExtra("data", data?.get("value[0]"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)


        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon_two)
            .setColor(ContextCompat.getColor(context, R.color.white))
            .setContentTitle(title)
            //.setLargeIcon(logo)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)


        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        if(notificationId > 9000000){
            notificationId = 0
        }

        mNotificationManager.notify(notificationId++, mBuilder?.build())

    }

    companion object {


        val CHANNEL_ID = "TaxiMeDriverNotification"
        @SuppressLint("StaticFieldLeak")
        private var mInstance: NotificationBuildManager? = null

        @Synchronized
        fun getInstance(context: Context): NotificationBuildManager {

            if (mInstance == null) {
                mInstance = NotificationBuildManager(context)

            }
            return mInstance as NotificationBuildManager
        }

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel
                val name = context.getString(R.string.channel_name)
                val descriptionText = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
                mChannel.description = descriptionText
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)
            }
        }
    }


}
