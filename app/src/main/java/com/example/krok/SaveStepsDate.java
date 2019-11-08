package com.example.krok;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.SENSOR_SERVICE;
import static android.util.Log.e;

public class SaveStepsDate extends BroadcastReceiver  {

    Context context;



    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, SaveStepsDate.class);
        context.startService(i);



    }
    public void setAlarm(Context context)
    {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 1); // For 1 PM or 2 PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        PendingIntent pi = PendingIntent.getService(context, 0,
                new Intent(context, SaveStepsDate.class),PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
      //  am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, pi); //
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, SaveStepsDate.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }



}

