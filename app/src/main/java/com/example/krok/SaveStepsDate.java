package com.example.krok;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

public class SaveStepsDate extends BroadcastReceiver {
    private SensorManager mSensorManager;
    private Sensor mStepCounter;
    String date;
    String todaysteps;
    SampleSQLiteDBHelper db2helper;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.println(Log.ASSERT,"", "Toast.makeText(context,.show()");
        db2helper = new SampleSQLiteDBHelper(context);
         this.context=context;
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dateWithoutTime = cal.getTime();
        date = dateWithoutTime.toString();

        mSensorManager.registerListener(mSensorEventListener, mStepCounter,
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, SaveStepsDate.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long millis = (System.currentTimeMillis() - c.getTimeInMillis());

        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000*60*60*24-millis,pi);
      //  am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, pi); //
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, SaveStepsDate.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }


    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        private float mStepOffset = 0;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (mStepOffset == 0) {
                mStepOffset = event.values[0];
            }
            int step_C = (int)event.values[0];
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date dateWithoutTime = cal.getTime();
            if (date == null || !date.equals(dateWithoutTime.toString())) {

                date = dateWithoutTime.toString();
                todaysteps = Integer.toString(step_C);
                try {
                    InputStream is = new FileInputStream(context.getFilesDir() + "data.json");
                    int length = is.available();
                    byte[] data = new byte[length];
                    is.read(data);
                    is.close();
                    String d = new String(data);
                    JSONObject jso = new JSONObject(d);
                    JSONObject sys = jso.getJSONObject("sys");
                    sys.put("date", date);
                    sys.put("todaysteps", todaysteps);
                    jso.put("sys", sys);
                    OutputStream os = new FileOutputStream(context.getFilesDir() + "data.json");
                    os.write(jso.toString().getBytes());
                    db2helper.AddStartSTEP(context, todaysteps);

                } catch (Exception e1) {

                    Log.getStackTraceString(e1);

                }
            }



            mSensorManager.unregisterListener(this);
        }
    };


}

