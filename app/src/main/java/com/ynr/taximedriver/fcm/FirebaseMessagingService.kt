package com.ynr.taximedriver.fcm;

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class FirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("Nortification", "recived notification")

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        remoteMessage.data.isNotEmpty().let {
            Log.d("Nortification", "Message data payload: " + remoteMessage.data)
            // Handle message within 10 seconds

            var data : Map<String, String>? = null

            try {
                data = remoteMessage.data
            } catch (e: Exception){
                e.printStackTrace()
            }
            if (data != null)
                NotificationBuildManager.getInstance(applicationContext).displayNotification(title, body, remoteMessage.data)
            else
                NotificationBuildManager.getInstance(applicationContext).displayNotification(title, body)

        }


    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(s: String) {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        super.onNewToken(s)
        runBlocking {
            launch {
                updateToken(s)
            }
        }

    }

    init {
        //FCM Configuration
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }

    private fun updateToken(token: String){

//        val json = JSONObject()
//        json.put("token", token)
//        json.put("contactNumber ", LoginSession(this).userDetails.user.contactNumber)
//
//        val body = RequestBody.create(
//                okhttp3.MediaType.parse(
//                        "application/json; charset=utf-8"
//                ), json.toString()
//        )
//
//        val apiInterface = JsonApiClient.getApiClient().create(ApiInterface::class.java)
//        val call = apiInterface.updateNotificationToken(body)
//        call.enqueue(object : Callback<VehicleCategoryResponseModel?> {
//            override fun onResponse(call: Call<VehicleCategoryResponseModel?>, response: Response<VehicleCategoryResponseModel?>) {
////                progressDialog.dismiss();
//                Log.i("FirebaseMessaging", response.code().toString())
////                if (response.code() == 200) {
////                    setVehicleCategoryBitmap(response.body())
////                } else {
//
////                }
//            }
//
//            override fun onFailure(call: Call<VehicleCategoryResponseModel?>, t: Throwable) {
//                Log.d("FirebaseMessaging", t.message)
//
//            }
//        })
    }



}