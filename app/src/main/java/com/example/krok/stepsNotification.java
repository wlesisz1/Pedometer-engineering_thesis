package com.example.krok;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class stepsNotification extends Application {


    public static final String CHANNEL_ID = "TripServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        CreateNotificationChannel();
    }

    private void CreateNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Zapisywanie kroków",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =  getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
