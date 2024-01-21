package com.ynr.taximedriver.other;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.fcm.NotificationBuildManager;
import com.ynr.taximedriver.login.LoginActivity;
import com.ynr.taximedriver.model.AndroidAppVersionModel;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.session.LoginSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 300;
    ProgressDialog progressDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);
        NotificationBuildManager.Companion.createNotificationChannel(this);

        if (ConnectionDetector.isConnected(getApplicationContext())) {
            appVersionCheck();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Check your internet connection and try again");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    private void appVersionCheck() {
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<AndroidAppVersionModel> call = apiInterface.appVersionCheck();
        call.enqueue(new Callback<AndroidAppVersionModel>() {
            @Override
            public void onResponse(Call<AndroidAppVersionModel> call, Response<AndroidAppVersionModel> response) {
//                progressDialog.dismiss();
                if (response.code() == 200) {
                    if (response.body().getAndroidAppVersion() == KeyString.APP_VERSION) {
                        Intent mainIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
                        alertDialog.setTitle("Update Available");
                        alertDialog.setMessage("please update your app and restart");
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
//                                        new LoginSession(getApplicationContext()).clearSession();
                                        finish();
                                    }
                                });
                        alertDialog.show();
                    }
                } else {
                    Intent mainIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<AndroidAppVersionModel> call, Throwable t) {
//                progressDialog.dismiss();
                Intent mainIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}
