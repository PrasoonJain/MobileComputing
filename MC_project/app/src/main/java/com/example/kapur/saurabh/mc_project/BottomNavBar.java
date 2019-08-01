package com.example.kapur.saurabh.mc_project;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.lang.reflect.Field;


/**
 * Created by souravghai on 19/3/18.
 */

public class BottomNavBar {
    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());

            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public static void navBarnavigation(final Context context, BottomNavigationView bottomNavigationView)
    {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId())
                {
                    case R.id.navigation_interests:


                        intent = new Intent(context,InterestedEvents.class);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                        ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.navigation_clubs:
                        intent = new Intent(context,NewClubCat.class);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                        ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.navigation_home:
                        intent = new Intent(context,HomeActivity.class);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                        ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.navigation_profile:
                        intent = new Intent(context,ProfileActivity.class);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                        ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        break;
                }
                return false;
            }
        });
    }
}
