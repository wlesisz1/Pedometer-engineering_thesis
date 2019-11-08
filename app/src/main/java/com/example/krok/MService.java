package com.example.krok;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

public class MService extends Service {
    private SensorManager mSensorManager;
    private Sensor mStepCounter;
    String date;
    String todaysteps;
    static boolean isDone;
    SampleSQLiteDBHelper db2helper;
    static String  stepst;
    static String LastDateDB;

    SaveStepsDate saveStepsDate = new SaveStepsDate();
    @Nullable
    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Context context = getApplicationContext();
        Log.println(Log.ASSERT, "service", getApplicationContext().toString());

        saveStepsDate.setAlarm(this);


        isDone = false;
        db2helper = new SampleSQLiteDBHelper(context);

        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Cursor cursor = db2helper.GetPomiarSteps(context);
        cursor.moveToLast();
        String DateFromDB = cursor.getString(0);


        LastDateDB = DateFromDB;
        // Toast.makeText(context, date, Toast.LENGTH_SHORT).show();
        mSensorManager.registerListener(mSensorEventListener, mStepCounter,
                SensorManager.SENSOR_DELAY_FASTEST);


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dateWithoutTime = cal.getTime();
        if (LastDateDB == null || !LastDateDB.equals(dateWithoutTime.toString())) {

            dateWithoutTime = cal.getTime();
            date = dateWithoutTime.toString();


            try {
                InputStream is = new FileInputStream(context.getFilesDir() + "data.json");
                int length = is.available();
                byte[] data = new byte[length];
                is.read(data);
                is.close();
                String d = new String(data);
                while (!isDone) {
                    SystemClock.sleep(1000);
                }

                JSONObject jso = new JSONObject(d);
                JSONObject sys = jso.getJSONObject("sys");
                sys.put("date", date);
                sys.put("todaysteps", stepst);
                jso.put("sys", sys);
                OutputStream os = new FileOutputStream(context.getFilesDir() + "data.json");
                os.write(jso.toString().getBytes());

                db2helper.AddStartSTEP(context, stepst);

            } catch (Exception e1) {

                Log.getStackTraceString(e1);

            }
        }


        return START_STICKY;
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
                stepst = String.valueOf(step_C);
                isDone=true;
                mSensorManager.unregisterListener(this);
            }
        };


}
