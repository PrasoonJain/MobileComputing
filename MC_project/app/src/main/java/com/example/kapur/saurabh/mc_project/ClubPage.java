package com.example.kapur.saurabh.mc_project;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClubPage extends AppCompatActivity implements
        ActionBar.TabListener{

    private ViewPager viewPager;
    TabLayout homeTabLayout;
    private ClubPageTabAdapter mAdapter;
    private ActionBar actionBar;
    private  String head;
    private String[] tabs = { "About", "Events", "Members" };

    // Tab titles
    private ImageView club_img;
    private Button delete_btn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String clubDescription;
    // Tab titles
    private Context context;


    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setClubDescription(String clubDescription) {
        this.clubDescription = clubDescription;
    }

    public String getClubDescription() {

        return clubDescription;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_page);
//        FloatingActionButton add_event = findViewById(R.id.Add_event);

        Intent in = getIntent();
        head = in.getStringExtra("club_name");
        clubDescription = in.getStringExtra("club_description");

        final Intent intent = new Intent(this,CreateEvent.class);
        intent.putExtra("clubName",head);
        club_img = findViewById(R.id.club_page_img);
        Picasso.with(context).load(in.getStringExtra("club_img")).into(club_img);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
//        User user = User.getUser();
//        if (user.getRole().equals("Admin"))
        delete_btn = findViewById(R.id.delete_btn);
//        toolbar.setTitle(head);
        settingUpFirebase();

        User user = User.getUser();
        delete_btn.setVisibility(View.GONE);
        if (user.getRole()!=null && user.getRole().equals("Admin"))
        {
            delete_btn.setVisibility(View.VISIBLE);
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(head);
                }
            });
        }



        homeTabLayout=(TabLayout)findViewById(R.id.tab);
        viewPager=(ViewPager)findViewById(R.id.pager);
        homeTabLayout.addTab(homeTabLayout.newTab().setText("About"));
        homeTabLayout.addTab(homeTabLayout.newTab().setText("Events"));
        homeTabLayout.addTab(homeTabLayout.newTab().setText("Members"));
        homeTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mAdapter=new ClubPageTabAdapter(getSupportFragmentManager(),homeTabLayout.getTabCount());
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(homeTabLayout));

        homeTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void showDialog(final String clubname)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Delete Club");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to Delete")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context,mAuth.getCurrentUser().getUid());
                                firebaseFunctions.Delete_event_from_club(dataSnapshot,clubname);
                                firebaseFunctions.Delete_club(dataSnapshot,clubname);
                                databaseReference.removeEventListener(this);
                                finish();

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
