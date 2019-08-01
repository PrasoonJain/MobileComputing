package com.example.kapur.saurabh.mc_project;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PendingRequestActivity extends AppCompatActivity {

    private PendingEventListAdapter PendingeventListAdapter;
    private ArrayList<Event> PendingeventList;
    private RecyclerView recyclerView;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Context context;
    private TextView  emptyPendingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_request);
        settingUpFirebase();
        context = this;

        recyclerView=findViewById(R.id.pending_request_recycler_view);
        emptyPendingList = findViewById(R.id.empty_pendingEvent_list);
//        recyclerView.showShimmerAdapter();

        PendingeventList = new ArrayList<Event>();
        PendingeventListAdapter = new PendingEventListAdapter(PendingeventList,this);
        recyclerView.setAdapter(PendingeventListAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        update_event_list();
//        finish();
    }

    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void update_event_list ()
    {
        //mShimmerViewContainer.startShimmerAnimation();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context,mAuth.getCurrentUser().getUid());
                ArrayList<Event> upcommingEvents = firebaseFunctions.getPendingEvents(dataSnapshot);
                PendingeventList.clear();
                if (upcommingEvents != null && upcommingEvents.isEmpty()) {
                    emptyPendingList.setVisibility(View.VISIBLE);
                } else {
                    emptyPendingList.setVisibility(View.GONE);
                }
                PendingeventList.addAll(upcommingEvents);

                PendingeventListAdapter.notifyDataSetChanged();
               // databaseReference.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //mShimmerViewContainer.stopShimmerAnimation();
    }
}
