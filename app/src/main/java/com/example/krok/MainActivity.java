package com.example.krok;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;

public class MainActivity extends AppCompatActivity  implements Page1.OnFragmentInteractionListener, Page2.OnFragmentInteractionListener, Page3.OnFragmentInteractionListener, StepsHistory.OnFragmentInteractionListener, MapActual.OnFragmentInteractionListener, Settings_Activity.OnFragmentInteractionListener,Status_Activity.OnFragmentInteractionListener{
 private long backPressedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent().putExtra("cel_krok", "0");
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_bar);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_status);

        startService(new Intent(this, MService.class));

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment SelectedFragment = null;
            switch (menuItem.getItemId()){
                case R.id.nav_status:
                    SelectedFragment = new Status_Activity();
                    getSupportFragmentManager().popBackStack();
                    break;
                case R.id.nav_wyc:
                    SelectedFragment = new TripActivity();
                    getSupportFragmentManager().popBackStack();
                    break;
                case R.id.nav_sett:
                    SelectedFragment = new Settings_Activity();
                    getSupportFragmentManager().popBackStack();
                    break;
                    case R.id.default_activity_button:
                        SelectedFragment = new Status_Activity();
                        break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    SelectedFragment).commit();

            return true;
        }
    };

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis() || 0<getSupportFragmentManager().getBackStackEntryCount()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Dotknij ponownie żeby wyjść", Toast.LENGTH_LONG).show();
        }
        backPressedTime = System.currentTimeMillis();

    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}