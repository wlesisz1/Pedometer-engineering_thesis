package com.example.krok;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class TripActivity extends Fragment implements Page1.OnFragmentInteractionListener, Page2.OnFragmentInteractionListener, Page3.OnFragmentInteractionListener {
    ViewPager viewPager;
    Button RecordButton;
    ExpandableListView TripsListView;
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
    private Boolean RecordStart=false;
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
        celkrok.putString("cel_krok","0");
        setArguments(celkrok);

        SwipeAdapter swipeAdapter = new SwipeAdapter(getFragmentManager());
        viewPager.setAdapter(swipeAdapter);

        RecordButton = getView().findViewById(R.id.recordbutton);
RecordButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(RecordStart ==false)
        {
            RecordButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.record_on));
            RecordStart=true;


        }else

        {
            RecordButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.record_off));
RecordStart=false;
        }
    }
});



        TripsListView = getView().findViewById(R.id.listoftrips);


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


}

