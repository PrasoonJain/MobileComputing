package com.example.kapur.saurabh.mc_project;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicalFragment extends Fragment {

    private ArrayList<Clubs> list = new ArrayList<Clubs>();
    private View view;
    private ClubAdapter adapter;
    private Context context;
    private NewClubCat newClubCat;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private static final String category = "technical";

    public TechnicalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {

        super.onStart();
        prepareData();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sports, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        newClubCat = (NewClubCat)getActivity();
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        settingUpFirebase();


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.club_recycler);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new ClubAdapter(list,(RecyclerViewActionListener)getActivity(),context);

        recyclerView.addItemDecoration(new DividerItemDecoration(context, GridLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter);

        prepareData();

    }

    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void prepareData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context,mAuth.getCurrentUser().getUid());
                ArrayList<Clubs> clubList = firebaseFunctions.getClubList(category,dataSnapshot);
                list.clear();
                list.addAll(clubList);

                adapter.notifyDataSetChanged();
                databaseReference.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




}
