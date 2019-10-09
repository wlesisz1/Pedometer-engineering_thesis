package com.example.krok;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SwipeAdapter extends FragmentStatePagerAdapter {
    public SwipeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return Page1.newInstance("0", "Page # 1");
            case 1:
                return Page2.newInstance("1", "Page # 2");
            case 2:
                return Page3.newInstance("2", "Page # 3");
            default:
                return null;
        }

    }

    public CharSequence getPageTitle(int Position) {
        switch (Position) {
            case 0:
                return "Krokomierz";
            case 1:
                return "Historia";

            case 2:
                return "Ustawienia";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
