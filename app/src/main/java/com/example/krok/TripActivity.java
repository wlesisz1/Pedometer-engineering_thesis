package com.example.krok;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class TripActivity extends Fragment implements PermissionsListener, Page1.OnFragmentInteractionListener, Page2.OnFragmentInteractionListener, Page3.OnFragmentInteractionListener {
    ViewPager viewPager;
    Button RecordButton;
    Spinner TripsListView;
    SampleSQLiteDBHelper helpee;
    FragmentPagerAdapter adapterViewPager;
    private TextView mTextMessage;
    private EditText Text;
    private TextView mTextView;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;
    private float presure;
    private float presure_0;
    private float step_C;
    private float height;
    private Boolean RecordStart = false;
    private PermissionsManager permissionsManager;
    SampleSQLiteDBHelper db2helper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.activity_trip, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = getView().findViewById(R.id.view_pager);

        Bundle celkrok = new Bundle();
        celkrok.putString("cel_krok", "0");
        setArguments(celkrok);

        SwipeAdapter swipeAdapter = new SwipeAdapter(getFragmentManager());
        viewPager.setAdapter(swipeAdapter);

        RecordButton = getView().findViewById(R.id.recordbutton);

        db2helper = new SampleSQLiteDBHelper(getActivity());

        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
        if (isServiceRunning("com.example.krok.TripService")) {
            RecordButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.record_on));
            RecordStart = true;
        }
        RecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RecordStart == false) {

                    final View view = getLayoutInflater().inflate(R.layout.askfornamealert, null);
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Wpisz nazwe wycieczki:");

                    alertDialog.setCancelable(false);


                    final EditText etComments = (EditText) view.findViewById(R.id.etComments);
                    etComments.setText("Trip");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            RecordButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.record_on));
                            RecordStart = true;
                            Cursor cursor = db2helper.GetTripNames(getActivity());
                            cursor.moveToFirst();
                            String ID;
                            boolean isUsed = false;
                            while (cursor.moveToNext()) {
                                if (cursor.getString(0) == etComments.getText().toString()) {
                                    isUsed = true;
                                }
                            }
//                    Log.println(Log.ASSERT, "ass", cursor.getString(1));
                            if (isUsed)
                                Toast.makeText(getContext(), "Ta nazwa jest zajeta!", Toast.LENGTH_SHORT).show();
                            else {
                                //   Toast.makeText(getContext(), String.valueOf(cursor.isAfterLast()), Toast.LENGTH_SHORT).show();
                                if (cursor.getCount() == 0) {
                                    ID = etComments.getText().toString();
                                    Toast.makeText(getContext(), "NoDB", Toast.LENGTH_SHORT).show();
                                    db2helper.AddTripIDAndCreateTable(getActivity(), ID);

                                } else {

                                    cursor.moveToLast();
                                    ID = etComments.getText().toString();
                                    db2helper.AddTripIDAndCreateTable(getActivity(), ID);
                                }
                                startService(getView(), ID);
                                SpinnerDataChange();

                            }
                            cursor.close();
                        }
                    });


                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });


                    alertDialog.setView(view);
                    alertDialog.show();


                } else {
                    RecordButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.record_off));
                    RecordStart = false;
                    stopService(getView());
                }
            }
        });

        SpinnerDataChange();

    }



    private boolean isServiceRunning(String serviceName){
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = i
                    .next();

            Log.println(Log.ASSERT, "0", runningServiceInfo.service.getClassName());
            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;

                if(runningServiceInfo.foreground)
                {
                    //service run in foreground
                }
            }
        }
        return serviceRunning;
    }
    public void startService(View v, String ID) {


        Intent serviceIntent = new Intent(getActivity(), TripService.class);
        serviceIntent.putExtra("inputExtra", ID);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }
    public void stopService(View v) {
        Intent serviceIntent = new Intent(getActivity(), TripService.class);
        getActivity().stopService(serviceIntent);
    }
    public void SpinnerDataChange() {
        Cursor cursor = db2helper.GetTripNames(getActivity());
        cursor.moveToFirst();

        TripsListView = getView().findViewById(R.id.listoftrips);


        ArrayList<String> Items = new ArrayList<>();
        Log.println(Log.ASSERT, "ass", String.valueOf(cursor.getCount()));
        while (!cursor.isAfterLast()) {

            Log.println(Log.ASSERT, "ass", cursor.getString(0));
            Items.add(cursor.getString(0));
            cursor.moveToNext();
        }
        Object[] temp = Items.toArray();
        String[] items = Arrays.copyOf(temp, temp.length, String[].class);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        TripsListView.setAdapter(adapter);
        cursor.close();
        TripsListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString( "selected_tripID", items[position] );

                editor.commit();
           SwipeAdapter swipeAdapter = new SwipeAdapter(getFragmentManager());
                ViewPager t =  getView().findViewById(R.id.view_pager);
               t.setAdapter(swipeAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public float getHeight() {
        return height;
    }

    public float getStep_C() {
        return step_C;
    }

    public float getPresure() {
        return presure;
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getContext(),"R.string.user_location_permission_explanation",
                Toast.LENGTH_LONG).show();
    }
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {

        } else {
            Toast.makeText(getContext(),"user_location_permission_not_granted", Toast.LENGTH_LONG).show();

        }
    }
}

