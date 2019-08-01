package com.example.kapur.saurabh.mc_project;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {


    private ArrayList<Event> eventList;
    private EventListAdapter eventListAdapter;
    private FirebaseDatabase mDatabase;
    private View view;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Context context;
    public String clubname;
    private ClubPage clubPage;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        ShimmerRecyclerView recyclerView = (ShimmerRecyclerView)view.findViewById(R.id.recyclerview);
        if (recyclerView == null)
            Log.d("test","null");
        eventList = new ArrayList<Event>();
        eventListAdapter = new EventListAdapter(eventList,getContext());
        recyclerView.setAdapter(eventListAdapter);
        context = getActivity();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        settingUpFirebase();
        update_event_list();
        clubname = clubPage.getHead();
        return view;
    }


    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        clubPage = (ClubPage) getActivity();
        this.context = context;
    }

    public void update_event_list ()
    {
        //mShimmerViewContainer.startShimmerAnimation();
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context,mAuth.getCurrentUser().getUid());
                ArrayList<Event> upcommingEvents = firebaseFunctions.getClubEvents(dataSnapshot,clubname);
                eventList.clear();
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

}
