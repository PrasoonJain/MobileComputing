package com.example.kapur.saurabh.mc_project;

        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentPagerAdapter;
        import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by prasoon on 27/2/18.
 */

public class FragmentAdapterClassHome extends FragmentPagerAdapter {
    int NumTabs;
    public FragmentAdapterClassHome(FragmentManager fragmentManager,int NumTabs){
        super(fragmentManager);
        this.NumTabs=NumTabs;
    }
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                HomeUpcomingActivity homeUpcoming = new HomeUpcomingActivity();
                return homeUpcoming;

            case 1:
                HomeRecommendedActivity homeRecommended = new HomeRecommendedActivity();
                return homeRecommended;

            default:
                return null;
        }
    }
    @Override
    public int getCount(){
        return NumTabs;
    }

}

