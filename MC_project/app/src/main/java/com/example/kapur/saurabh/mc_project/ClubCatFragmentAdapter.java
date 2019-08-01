package com.example.kapur.saurabh.mc_project;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

/**
 * Created by souravghai on 5/4/18.
 */

public class ClubCatFragmentAdapter extends FragmentPagerAdapter {
    int NumTabs;
    public ClubCatFragmentAdapter(FragmentManager fragmentManager, int NumTabs){
        super(fragmentManager);
        this.NumTabs=NumTabs;
    }
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TechnicalFragment technicalFragment = new TechnicalFragment();
                return technicalFragment;

            case 1:
                CulturalFragment culturalFragment = new CulturalFragment();
                return culturalFragment;

            case 2:
                SportsFragment sportsFragment = new SportsFragment();
                return sportsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return NumTabs;
    }
}
