package com.example.kapur.saurabh.mc_project;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

public class NewClubCat extends AppCompatActivity implements RecyclerViewActionListener {


    TabLayout clubTabLayout;
    ViewPager clubCatPager;
    ClubCatFragmentAdapter clubCatFragmentAdapter;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private static final int ActivityNum = 1;
    private User user = null;
    private int key=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_club_cat);

        //setting up nav bar
        settingNavBar();

        //setting up firebase
        settingUpFirebase();

        clubCatPager = findViewById(R.id.club_cat_pager);
        clubTabLayout = findViewById(R.id.club_cat_tab);

        clubTabLayout.addTab(clubTabLayout.newTab().setText("Technical"));
        clubTabLayout.addTab(clubTabLayout.newTab().setText("Cultural"));
        clubTabLayout.addTab(clubTabLayout.newTab().setText("Sports"));

        clubCatFragmentAdapter = new ClubCatFragmentAdapter(getSupportFragmentManager(), clubTabLayout.getTabCount());

        clubTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        clubCatPager.setAdapter(clubCatFragmentAdapter);
        clubCatPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(clubTabLayout));

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
    public void onCatClick(View view, int position, ArrayList<Clubs> Clublist) {
        Intent in = new Intent(this, ClubPage.class);
        in.putExtra("club_name", Clublist.get(position).getName());
        in.putExtra("club_description",Clublist.get(position).getAbout());
        in.putExtra("club_img",Clublist.get(position).getClub_Image());
        startActivity(in);
    }

    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void toggleFollowButton(Button button, final ArrayList<Clubs> list, final int position) {

        if (button.getText().equals("Follow")) {
            button.setText("UnFollow");
            button.setBackgroundColor(button.getContext().getResources().getColor(R.color.buttonpressed));
            FirebaseMessaging.getInstance().subscribeToTopic(list.get(position).getName());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FirebaseFunctions firebaseFunctions = new FirebaseFunctions(getApplicationContext(), mAuth.getCurrentUser().getUid());
                    // if(user==null)
                    user = User.getUser();
                    Clubs club = list.get(position);

                    HashMap<String, Boolean> map = user.getClubIds();
                    map.put(club.getClubId(), true);
                    user.setClubIds(map);

                    HashMap<String, Boolean> map1 = club.getFollowerIds();
                    map1.put(user.getUser_id(), true);
                    club.setFollowerIds(map1);


                    firebaseFunctions.updateUser(user);
                    firebaseFunctions.updateClubs(club);
                    databaseReference.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            button.setText("Follow");
            button.setBackgroundColor(button.getContext().getResources().getColor(R.color.buttonunpressed));
            FirebaseMessaging.getInstance().unsubscribeFromTopic(list.get(position).getName());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FirebaseFunctions firebaseFunctions = new FirebaseFunctions(getApplicationContext(), mAuth.getCurrentUser().getUid());
                    //if(user==null)
                    user = User.getUser();
                    Clubs club = list.get(position);
                    Log.d("onDataChange: ", String.valueOf(user.getClubIds()));
                    HashMap<String, Boolean> map = user.getClubIds();
                    map.remove(club.getClubId());
                    user.setClubIds(map);

                    HashMap<String, Boolean> map1 = club.getFollowerIds();
                    map1.remove(user.getUser_id());
                    club.setFollowerIds(map1);

                    firebaseFunctions.updateUser(user);
                    firebaseFunctions.updateClubs(club);
                    databaseReference.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void checkClubList(final String clubId,final Button button) {
        user = User.getUser();
        HashMap<String, Boolean> map = user.getClubIds();
        // Toast.makeText(getApplicationContext(), clubId, Toast.LENGTH_SHORT).show();
        //Log.d("club", map.toString());
        if (map.containsKey(clubId)) {
            button.setText("UnFollow");
            button.setBackgroundColor(button.getContext().getResources().getColor(R.color.buttonpressed));
        } else {
            button.setText("Follow");
            button.setBackgroundColor(button.getContext().getResources().getColor(R.color.buttonunpressed));
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
