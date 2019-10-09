package com.example.krok;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;


public class Page2 extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button button3;
    Button button;
    SampleSQLiteDBHelper db2helper;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private LineChart mChart;

    public Page2() {
        // Required empty public constructor
    }

    public static Page2 newInstance(String param1, String param2) {
        Page2 fragment = new Page2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db2helper = new SampleSQLiteDBHelper(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page2_layout, container, false);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            Setchart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Setchart();
    }

    @Override
    public void onPause() {
        super.onPause();
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
        mChart = (LineChart) getView().findViewById(R.id.linechart);
        ArrayList<Entry> yValues = new ArrayList<>();
        Cursor cursor = db2helper.GetPomiar(getActivity());
        int bigg = 0;
        while (cursor.moveToNext()) {
            yValues.add(new Entry(cursor.getInt(0), cursor.getInt(2)));
            if (bigg < cursor.getInt(2))
                bigg = cursor.getInt(2);
        }
        LineDataSet set1 = new LineDataSet(yValues, "Wysokość");
        set1.setColor(Color.RED);
        set1.setCircleColors(Color.RED);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        mChart.getAxisLeft().setAxisMinimum(0f);
        mChart.getAxisLeft().setAxisMaximum(2 * bigg);
        mChart.getAxisRight().setAxisMinimum(0f);
        mChart.getAxisRight().setAxisMaximum(2 * bigg);
        mChart.setData(data);

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
