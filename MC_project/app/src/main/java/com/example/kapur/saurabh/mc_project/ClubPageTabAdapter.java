package com.example.kapur.saurabh.mc_project;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ClubPageTabAdapter extends FragmentPagerAdapter {

    int NumTabs;

    public ClubPageTabAdapter(FragmentManager fm,int NumTabs) {
        super(fm);
        this.NumTabs=NumTabs;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new AboutFragment();
            case 1:
                // Games fragment activity
                return new EventFragment();
            case 2:
                // Movies fragment activity
                return new MembersFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return NumTabs;
    }
}
