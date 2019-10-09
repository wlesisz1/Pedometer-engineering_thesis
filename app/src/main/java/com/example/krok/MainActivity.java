package com.example.krok;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements Page1.OnFragmentInteractionListener, Page2.OnFragmentInteractionListener, Page3.OnFragmentInteractionListener {
    ViewPager viewPager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getIntent().putExtra("cel_krok", "0");
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        SwipeAdapter swipeAdapter = new SwipeAdapter(getSupportFragmentManager());
        viewPager.setAdapter(swipeAdapter);

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
