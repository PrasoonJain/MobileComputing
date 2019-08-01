package com.example.kapur.saurabh.mc_project;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MembersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClubPage clubPage;
    private UsercardViewAdapter adapter;
    private ArrayList<Members> listItems = new ArrayList<Members>();
    private RecyclerView.LayoutManager mLayoutManager;
    private View view;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String clubname;

    public MembersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_members,container,false);
        return  view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        clubPage = (ClubPage) getActivity();
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //setting up firebase
        settingUpFirebase();

        recyclerView=(RecyclerView)view.findViewById(R.id.user_card_recyclerView);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        clubname = clubPage.getHead();


        adapter = new UsercardViewAdapter(listItems);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, GridLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter);

        prepareData();
    }

    private void prepareData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context,mAuth.getCurrentUser().getUid());
                ArrayList<Members> clubList = firebaseFunctions.getMembers(dataSnapshot,clubname);
                listItems.clear();
                listItems.addAll(clubList);

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

}
