package com.example.krok;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Status_Activity extends Fragment implements StepsHistory.OnFragmentInteractionListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        

        return inflater.inflate(R.layout.activity_status_, container, false);
    }

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
        hhistory_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HHistory fragment = new HHistory();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment); // fragment container id in first parameter is the  container(Main layout id) of Activity
                transaction.addToBackStack("just_back");  // this will manage backstack
                transaction.commit();

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
