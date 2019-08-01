package com.example.kapur.saurabh.mc_project;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

public class FollowingClubList extends AppCompatActivity implements  RecyclerViewActionListener{

    private ArrayList<Clubs> list = new ArrayList<Clubs>();
    private ClubAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Context context;
    private User user;
    private ActionBar actionBar;
    private TextView emptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_club_list);

         actionBar = getSupportActionBar();
         actionBar.setTitle("Your Clubs");
         actionBar.setDisplayHomeAsUpEnabled(true);

        //set up firebase
        settingUpFirebase();
        context = this;
        emptyList = findViewById(R.id.empty_club_list);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.club_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new ClubAdapter(list,(RecyclerViewActionListener) this,context);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter);

        prepareData();
    }

    private void prepareData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context,mAuth.getCurrentUser().getUid());
                ArrayList<Clubs> followClubList = firebaseFunctions.getFollowClubList(dataSnapshot);
                list.clear();
                if (followClubList != null && followClubList.isEmpty()) {
                    emptyList.setVisibility(View.VISIBLE);
                } else {
                    emptyList.setVisibility(View.GONE);
                }
                list.addAll(followClubList);

                adapter.notifyDataSetChanged();
                databaseReference.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void onCatClick(View view, int position, ArrayList<Clubs> Clublist) {
        Intent in = new Intent(this, ClubPage.class);
        in.putExtra("club_name", Clublist.get(position).getName());
        in.putExtra("club_description",Clublist.get(position).getAbout());
        in.putExtra("club_img",Clublist.get(position).getClub_Image());
        startActivity(in);
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
    protected void onDestroy() {
        super.onDestroy();

    }
}
