package com.example.krok;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static android.os.Looper.getMainLooper;
import static com.example.krok.Status_Activity.OPEN_WEATHER_MAP_API;
import static com.example.krok.tripNotification.CHANNEL_ID;

public class TripService extends Service implements OnMapReadyCallback {

    private MapboxMap mapboxMap;
    private TripServiceLocationCallback callback =
            new TripServiceLocationCallback(this);
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private SensorManager mSensorManager;
    private Sensor mStepCounter;
    private Sensor mPressureSensor;
    private LocationEngine locationEngine;
    public static String X;
    public static String Y;
    public  static int step_C;
    public static float pressure;
    float presure_0;
    public static float pressureatzero;
    public  static int height;
    String ID;
    SampleSQLiteDBHelper db2helper;
    private Timer timer = new Timer();
    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mPressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);


    }

    private void startMyOwnForeground() {



    }
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        private float mStepOffset = 0;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            step_C = (int)event.values[0];

        }
    };
    private SensorEventListener mSensorEventListener2 = new SensorEventListener() { //cisnienie

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override

        public void onSensorChanged(SensorEvent event) {
            pressure = event.values[0];
            Float test = SensorManager.getAltitude(
                    presure_0, pressure);

            height = Math.round(test);

        }
    };

    public JSONObject getJSON(Context context2) {

        try {
            URL url = new URL(OPEN_WEATHER_MAP_API);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader ir = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String text = ir.lines().collect(Collectors.joining("\n"));
            JSONObject json = new JSONObject(text);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void SetPressure(JSONObject json) {
        try {
            JSONObject main = json.getJSONObject("main");
            presure_0 = Float.parseFloat(main.getString("pressure"));
        } catch (Exception e) {
            Log.e("Weather", "Field not found");
        }
    }

    public void updateData() {
        JSONObject json = getJSON(getApplicationContext());
        if (json == null) {
            Toast.makeText(getApplicationContext(), "Nie ma takiego miasta", Toast.LENGTH_LONG).show();
        } else {
            SetPressure(json);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager.registerListener(mSensorEventListener, mStepCounter,
                SensorManager.SENSOR_DELAY_FASTEST);

        mSensorManager.registerListener(mSensorEventListener2, mPressureSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        db2helper = new SampleSQLiteDBHelper(getApplication());
        String input = intent.getStringExtra("inputExtra");
        ID=input;
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        Log.println(Log.ASSERT, "test", "testuje");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.wycieczki_24dp)
                .setContentTitle("The trip is being recorded... " + input)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);
        initLocationEngine();
        timer.scheduleAtFixedRate(new SaveTripDataToDB(), 0, 600000);
        timer.scheduleAtFixedRate(new SaveTripDataToDB(), 0, 10000);
        timer.scheduleAtFixedRate(new updatePressureOW(), 0, 900000);
        return START_NOT_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(getBaseContext());

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null){
            timer.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

// Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            initLocationEngine();
        }
    }



    private static class TripServiceLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<TripService> activityWeakReference;

        TripServiceLocationCallback(TripService activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            TripService activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }
               X= String.valueOf(result.getLastLocation().getLatitude());
               Y= String.valueOf(result.getLastLocation().getLongitude());
// Create a Toast which displays the new location's coordinates


// Pass the new location to the Maps SDK's LocationComponent

            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            TripService activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class SaveTripDataToDB extends TimerTask {
        @Override
        public void run() {
            Float x=Float.valueOf(X), y=Float.valueOf(Y);
            int Steps=0, Height=0;

            Log.println(Log.ASSERT, "test", X);
            /* TODO:
            *   1. GPS Data +
            *   2. Steps Data +
            *   3. Height Data */


Log.println(Log.ASSERT, "Zapisalem takie dane: ", "X = " + x.toString() + "; Y = " + y.toString() + "; step_C = " + String.valueOf(step_C)  + "; height = " +String.valueOf(height));
        db2helper.AddTripMeasurement(getApplication(), x,y,ID,height,step_C);
        }

    }

    private class LoadPressure extends TimerTask {
        @Override
        public void run() {
            pressureatzero = 1024;
        }

    }

    private class updatePressureOW extends TimerTask {
        @Override
        public void run() {
            updateData();
        }

    }
}
