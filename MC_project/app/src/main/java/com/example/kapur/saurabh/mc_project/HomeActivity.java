package com.example.kapur.saurabh.mc_project;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.Manifest;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.synnapps.carouselview.CarouselView;

import com.example.kapur.saurabh.mc_project.FragmentAdapterClassHome;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeActivity extends AppCompatActivity{

    TabLayout homeTabLayout;
    ViewPager homeViewPager;
    FragmentAdapterClassHome fragmentAdapterClassHome;
    int[] sampleImages = {R.drawable.home1, R.drawable.home2, R.drawable.home3};
    CarouselView carouselView;
    private static final int ActivityNum = 2;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Context context;
    private int key = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        //start a service
//        startAService();


        int check = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (ContextCompat.checkSelfPermission(this,  Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);
            }
        } else {
//            Toast.makeText(this, "" + Manifest.permission.WRITE_EXTERNAL_STORAGE + " is already granted.", Toast.LENGTH_SHORT).show();
        }


        context = this;

        //setting up nav bar
        settingNavBar();

        //setting up firebase
        settingUpFirebase();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser==null)
                    return;
                Log.d( "onDataChange: ","ok");
                String userId = mAuth.getCurrentUser().getUid();
                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context,userId);
                firebaseFunctions.getUserProfile(dataSnapshot,userId);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        Carousel View code
        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(imageListener);


        homeTabLayout=findViewById(R.id.home_tab);
        homeTabLayout.setupWithViewPager(homeViewPager);

        homeTabLayout.addTab(homeTabLayout.newTab().setText("Upcoming"));
        homeTabLayout.addTab(homeTabLayout.newTab().setText("Recommended"));


        fragmentAdapterClassHome=new FragmentAdapterClassHome(getSupportFragmentManager(),homeTabLayout.getTabCount());
//        pending_btn =  findViewById(R.id.PendingRequests);




//        Upcoming and Home Tab code
          homeViewPager=(ViewPager)findViewById(R.id.home_pager);
          homeViewPager.setAdapter(fragmentAdapterClassHome);

        homeTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        homeViewPager.setAdapter(fragmentAdapterClassHome);
        homeViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(homeTabLayout));

        homeTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                homeViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }

    private void startAService() {

        if(!isServiceRunning())
        {
            //Toast.makeText(this,"service started",LENGTH_SHORT).show();
            Intent intent = new Intent(this,DataChangeNotificationService.class);
            startService(intent);
        }
    }

    private boolean isServiceRunning() {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
                if("com.example.MyNeatoIntentService".equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;

    }

    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //    ImageListener for carousel view
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    private void settingNavBar() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bott_nav_bar);
        BottomNavBar.disableShiftMode(bottomNavigationView);
        BottomNavBar.navBarnavigation(this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ActivityNum);
        menuItem.setChecked(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

        } else {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(key == 1){
            super.onBackPressed();
            return;
        }
        key = 1;
        Toast.makeText(getApplicationContext(), "press back Button again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                key = 0;
            }
        },2000);
    }


}
