package com.example.krok;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

public class StepsHistory extends Fragment {

    private OnFragmentInteractionListener mListener;

    SampleSQLiteDBHelper db2helper;


    private BarChart mChart;
    public StepsHistory() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_steps_history, container, false);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        db2helper = new SampleSQLiteDBHelper(getActivity());


        Button step_history_back_button =  getView().findViewById(R.id.step_history_back_button);
        step_history_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                Status_Activity fragment = new Status_Activity();
                fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });
        if (mChart!=null)
        mChart.clear();
        Setchart();

    }

    @Override
    public void onResume() {

        super.onResume();
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    public void Setchart() {
        db2helper = new SampleSQLiteDBHelper(getActivity());
        BarChart mChart = getView().findViewById(R.id.barchart);
        ArrayList<Entry> yValues = new ArrayList<>();
        Cursor cursor = db2helper.GetPomiarSteps(getActivity());

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        cursor.moveToLast();

        int bigg = 0;
        int times = 0;

        do  {
            if (cursor.moveToPrevious()) {
                int LessSteps = cursor.getInt(1);
                String ValuetoChange = cursor.getString(0);
                cursor.moveToNext();
                int MoreSteps = cursor.getInt(1);
                if(MoreSteps-LessSteps>0)
                    entries.add(new BarEntry(times, MoreSteps-LessSteps));
                else
                {
                    entries.add(new BarEntry(times, 0));
                }




                String VtoLabel = "";
                VtoLabel += ValuetoChange.substring(8, 10);
                VtoLabel += "/";
                VtoLabel += ValuetoChange.substring(4, 7);


                labels.add(VtoLabel);

                times++;
            }

        }while (cursor.moveToPrevious() && times < 7);

        while (entries.size() < 7) {
            entries.add(new BarEntry(times, 0));
            labels.add("N/A");
            times++;
        }

        for (BarEntry bar : entries) {
            Log.println(Log.DEBUG, "bbbar", String.valueOf(bar.getY()));
        }
        for (String bar : labels) {
            Log.println(Log.DEBUG, "bbbar", bar);
        }

        //     BarData dat = new BarData(labels,entries);
        BarDataSet set1 = new BarDataSet(entries, "Kroki");
        set1.setColor(Color.GRAY);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        String celKrok = sharedPref.getString("cel_krok", "empty");

        set1.setDrawValues(true);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();

        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setBarWidth(0.3f);
        data.setValueTextColor(Color.RED);
        mChart.getAxisLeft().setAxisMinimum(0f);
        mChart.getAxisLeft().setAxisMaximum(Integer.valueOf(celKrok)*1.5f);
        mChart.setFitBars(true);
        mChart.setDrawValueAboveBar(true);
        mChart.getAxisRight().setAxisMinimum(0f);
        mChart.getAxisRight().setAxisMaximum(Integer.valueOf(celKrok)*1.5f);

        mChart.animateY(1000);

        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setDescription(null);

        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter()
        {
            @Override
            public String getFormattedValue(float value) {
                return labels.get((int)value);

            }

            // we don't draw numbers, so no decimal digits needed

            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f);
        LimitLine limitLine = new LimitLine(Integer.valueOf(celKrok) , "Cel kroków");
        limitLine.setLineWidth(2f);
        limitLine.enableDashedLine(10f,15f,2f);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLine.setLineColor(Color.BLACK);

        limitLine.setTextSize(10f);
        YAxis yAxis = mChart.getAxisLeft();
       yAxis.addLimitLine(limitLine);
        xAxis.setValueFormatter(formatter);

        mChart.setData(data);
    }
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
