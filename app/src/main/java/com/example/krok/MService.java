package com.example.krok;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MService extends Service {


    SaveStepsDate saveStepsDate = new SaveStepsDate();
    @Nullable
    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.println(Log.ASSERT,"service",getApplicationContext().toString()) ;
        saveStepsDate.onReceive(getApplicationContext(), intent);
        saveStepsDate.setAlarm(this);

        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        saveStepsDate.setAlarm(this);
    }

}
