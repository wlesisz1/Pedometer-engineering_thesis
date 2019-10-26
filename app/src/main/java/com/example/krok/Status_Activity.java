package com.example.krok;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

import static android.content.Context.SENSOR_SERVICE;

public class Status_Activity extends Fragment implements StepsHistory.OnFragmentInteractionListener {



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        LinearLayout krokipage =  getView().findViewById(R.id.krokipage);
        LinearLayout hhistory_card =  getView().findViewById(R.id.hhistory_card);
        LinearLayout map_card =  getView().findViewById(R.id.map_card);
        krokipage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StepsHistory fragment = new StepsHistory();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment); // fragment container id in first parameter is the  container(Main layout id) of Activity
                transaction.addToBackStack("just_back");  // this will manage backstack
                transaction.commit();

            }

        });
        krokipage.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    v.getBackground().setColorFilter(R.drawable.step_gradientdark, PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    break;
                }
            }
            return false;
        });
        hhistory_card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    v.getBackground().setColorFilter(R.drawable.step_gradientdark, PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    break;
                }
            }
            return false;
        });
        map_card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    v.getBackground().setColorFilter(R.drawable.step_gradientdark, PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    break;
                }
            }
            return false;
        });


        hhistory_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                HHistory fragment = new HHistory();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment); // fragment container id in first parameter is the  container(Main layout id) of Activity
                transaction.addToBackStack("just_back");  // this will manage backstack
                transaction.commit();
*/
            }
        });

        map_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MapActual fragment = new MapActual();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment); // fragment container id in first parameter is the  container(Main layout id) of Activity
                transaction.addToBackStack("just_back");  // this will manage backstack
                transaction.commit();

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private static final String OPEN_WEATHER_MAP_API = "https://api.openweathermap.org/data/2.5/weather?id=3083829&appid=ab705d098889a8e1258ef1073193aa5f";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context context;
    SampleSQLiteDBHelper db2helper;
    Handler h = new Handler();
    Handler h2 = new Handler();
    int delay = 1000; //1 second=1000 milisecond,
    Runnable runnable;
    Runnable runnable2;
    private float presure;
    private float presure_0;
    private int step_C;
    private String max_s;
    private float height;
    private String date = "0";
    private String todaysteps = "0";
    private String mParam1;
    private String mParam2;
    private SensorManager mSensorManager;
    private Sensor mStepCounter;
    private Sensor mStepSensor2;
    private Sensor mPressureSensor;
    private Status_Activity.OnFragmentInteractionListener mListener;
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
            step_C = (int)event.values[0];
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date dateWithoutTime = cal.getTime();
            if (date == null || !date.equals(dateWithoutTime.toString())) {
                date = dateWithoutTime.toString();
                todaysteps = Integer.toString(step_C);
             save();
            }
        }
    };
    private SensorEventListener mSensorEventListener2 = new SensorEventListener() { //cisnienie

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override

        public void onSensorChanged(SensorEvent event) {
            presure = event.values[0];


        }
    };


    public static Status_Activity newInstance(String param1, String param2) {
        Status_Activity fragment = new Status_Activity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static boolean copyAsset(Context context) {
        OutputStream out = null;
        try {
            File file = new File(context.getFilesDir(), "data.json");
            if (file.exists()) {
            } else {
                AssetManager am = context.getAssets();
                InputStream is = am.open("data.json");
                int length = is.available();
                byte[] data = new byte[length];
                int read;
                new File(context.getFilesDir() + "data.json").createNewFile();
                out = new FileOutputStream(context.getFilesDir() + "data.json");
                while ((read = is.read(data)) != -1) {
                    out.write(data, 0, read);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        PackageManager manager = getActivity().getPackageManager();
        boolean hasSTEPCOUNTER = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
        boolean hasBARO = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
        if (!hasSTEPCOUNTER || !hasBARO) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setMessage("Niestety nie posiadasz wszystkich czujników wymaganych do poprawnego działania tego programu");
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder1.setNegativeButton(
                    "Zamknij aplikację",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        }
        context = getContext();
        db2helper = new SampleSQLiteDBHelper(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mPressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepSensor2 = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        File F = new File(getActivity().getFilesDir() + "data.json");
        if (!F.exists())
            copyAsset(getActivity());
        try {
            InputStream is = new FileInputStream(getActivity().getFilesDir() + "data.json");
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            String d = new String(data);
            JSONObject jso = new JSONObject(d);
            JSONObject main = jso.getJSONObject("sys");
            Toast.makeText(getActivity(), main.getString("steps"), Toast.LENGTH_LONG);
            max_s = main.getString("steps");
            date = main.getString("date");
            todaysteps = main.getString("todaysteps");
            step_C = Integer.valueOf(todaysteps + 0);
        } catch (Exception e1) {
            Log.println(Log.ASSERT,"onCreateView", e1.getMessage());
            e1.printStackTrace();
        }
        View view = inflater.inflate(R.layout.activity_status_, container, false);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        updateData();
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Status_Activity.OnFragmentInteractionListener) {
            mListener = (Status_Activity.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorEventListener, mStepCounter,
                SensorManager.SENSOR_DELAY_FASTEST);

        mSensorManager.registerListener(mSensorEventListener2, mPressureSensor,
                SensorManager.SENSOR_DELAY_FASTEST);


        UstawWszystko();
        h.postDelayed(runnable = new Runnable() {
            public void run() {
                UstawWszystko();
                h.postDelayed(runnable, delay);
            }
        }, delay);
        h2.postDelayed(runnable2 = new Runnable() {
            public void run() {
                updateData();
                h2.postDelayed(runnable2, 1200000);
            }
        }, 1200000);
    }

    public void UstawWszystko() {

        TextView textView7 = getView().findViewById(R.id.steps_text);
        textView7.setText("Zrobiłeś dzisiaj już " + String.format("%.0f", step_C - Float.valueOf(todaysteps)) + " kroków. Twój cel to: " + max_s);
        TextView textView9 = getView().findViewById(R.id.map_text);
        TextView textView10 = getView().findViewById(R.id.height_text);
        height = Math.round(SensorManager.getAltitude(presure_0, presure));
        textView10.setText("Aktualne ciśnienie: " + String.format("%.2f", presure) + "hPa.      " + "Estymowana wysokość: " + String.format("%.0f", height) + "m");

     //   textView10.setText("Estymowana wysokość: " + String.format("%.0f", height) + "m");
        if (!String.format("%.0f", height).equals("44330") || height == Math.min(height, 0f))
            db2helper.AddPomiar(getContext(), String.format("%.0f", height));
    }
    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener);

        mSensorManager.unregisterListener(mSensorEventListener2);

        h.removeCallbacks(runnable);
    }

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
        JSONObject json = getJSON(getContext());
        if (json == null) {
            Toast.makeText(this.getContext(), "Nie ma takiego miasta", Toast.LENGTH_LONG).show();
        } else {
            SetPressure(json);
        }
    }

    public void save() {
        try {
            InputStream is = new FileInputStream(getActivity().getFilesDir() + "data.json");
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
            OutputStream os = new FileOutputStream(getActivity().getFilesDir() + "data.json");
            os.write(jso.toString().getBytes());
            db2helper.AddStartSTEP(context, todaysteps);

        } catch (Exception e1) {
            Log.println(Log.ASSERT,"save", e1.getMessage());
            e1.printStackTrace();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
