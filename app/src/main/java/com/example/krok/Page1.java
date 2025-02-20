package com.example.krok;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Page1 extends Fragment {
    private static final String OPEN_WEATHER_MAP_API = "https://api.openweathermap.org/data/2.5/weather?id=3083829&appid=ab705d098889a8e1258ef1073193aa5f";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context context;
    SampleSQLiteDBHelper db2helper;
    Handler h = new Handler();
    Handler h2 = new Handler();
    int delay = 3000; //1 second=1000 milisecond,
    String steps;
    String heightDiff;
    String maxHeight;
    String exceedance;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Page1() {
    }

    public static Page1 newInstance(String param1, String param2) {
        Page1 fragment = new Page1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        PackageManager manager = getActivity().getPackageManager();

        context = getContext();
        db2helper = new SampleSQLiteDBHelper(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page1_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String dbid = sharedPref.getString("selected_tripID", "empty");
        int tempStepMin = 0, tempStepMax = 0, tempHMin = 0, tempHMax = 0, exceedanceInt =0;
        int tempPrevious =0;
        int tempMaxH = 0;
        String Duration ="";
        try {
            Cursor cursor = db2helper.GetTrip(getContext(), dbid);
            cursor.moveToFirst();
            cursor.moveToNext();
            if (!cursor.isAfterLast()) {
                tempStepMin = cursor.getInt(3);
                Long firstT = cursor.getLong(4);
                Log.println(Log.ASSERT, "tempStempMin", String.valueOf(tempStepMin));
                cursor.moveToLast();


                Long lastT = cursor.getLong(4);

                Long dur =  lastT - firstT;
                long second = (dur / 1000) % 60;
                long minute = (dur / (1000 * 60)) % 60;
                long hour = (dur / (1000 * 60 * 60)) % 24;


                Log.println(Log.ASSERT, "dur", String.valueOf(dur));
            //   Date date = originalFormat.parse(dur.toString());
                Duration =  String.valueOf(hour + ":" + minute +":"+second);
                tempStepMax = cursor.getInt(3);
                Log.println(Log.ASSERT, "tempStepMax", String.valueOf(tempStepMax));
                cursor.moveToFirst();
                int[] table ={1,2,3};

                tempPrevious=cursor.getInt(2);
                while (cursor.moveToNext()) {
                    if (tempMaxH < cursor.getInt(2)) {
                        tempHMax = cursor.getInt(2);
                    }
                    if (tempPrevious==0)
                        tempPrevious = cursor.getInt(2);
                    else
                    if (tempPrevious < cursor.getInt(2)) {
                        exceedanceInt +=(cursor.getInt(2) - tempPrevious);
                    }
                    tempPrevious= cursor.getInt(2);

                }



            }
            cursor.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        steps = String.valueOf(tempStepMax - tempStepMin);
        maxHeight = String.valueOf(tempHMax);
        exceedance = String.valueOf(exceedanceInt);
        TextView textKroki = getView().findViewById(R.id.text_kroki);
        textKroki.setText("Ilość kroków: " + steps);
        TextView textNazwa = getView().findViewById(R.id.text_nazwa);
        textNazwa.setText("Nazwa wycieczki: " + dbid);
        TextView textWys = getView().findViewById(R.id.text_wys);
        textWys.setText("Najwyższy punkt: " + maxHeight + "m");

        TextView textPrz = getView().findViewById(R.id.text_prz);
        textPrz.setText("Pokanane przewyższenia: " + exceedance + "m");
        TextView textTime = getView().findViewById(R.id.text_time);
        textTime.setText("Czas trwania: " + Duration);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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




    }

    public void onPause() {
        super.onPause();

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
