package com.example.kapur.saurabh.mc_project;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InterestedEvents extends AppCompatActivity {


    private EventListAdapter eventListAdapter;
    private ArrayList<Event> eventList;
    private ShimmerRecyclerView recyclerView;
    private FirebaseDatabase mDatabase;
    private View view;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private static final int ActivityNum = 0;
    private Context context;
    private int key = 0;
    private TextView emptyInterestedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested_events);
        settingNavBar();


        settingUpFirebase();

        context = this;
        recyclerView = (ShimmerRecyclerView) findViewById(R.id.recyclerview);


        eventList = new ArrayList<Event>();
        eventListAdapter = new EventListAdapter(eventList, this);
        recyclerView.setAdapter(eventListAdapter);

        emptyInterestedList = (TextView) findViewById(R.id.empty_interested_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        update_event_list();
    }

    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void update_event_list() {
        //mShimmerViewContainer.startShimmerAnimation();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context, mAuth.getCurrentUser().getUid());
                ArrayList<Event> upcommingEvents = firebaseFunctions.getLikedEvents(dataSnapshot);
                eventList.clear();
                if (upcommingEvents != null && upcommingEvents.isEmpty()) {
                    emptyInterestedList.setVisibility(View.VISIBLE);
                } else {
                    emptyInterestedList.setVisibility(View.GONE);
                }
                eventList.addAll(upcommingEvents);

                eventListAdapter.notifyDataSetChanged();
                databaseReference.removeEventListener(this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //mShimmerViewContainer.stopShimmerAnimation();
    }

    private void settingNavBar() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bott_nav_bar);
        BottomNavBar.disableShiftMode(bottomNavigationView);
        BottomNavBar.navBarnavigation(this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ActivityNum);
        menuItem.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if (key == 1) {
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
        }, 2000);
    }
}
