package com.example.kapur.saurabh.mc_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PendingEventListAdapter extends RecyclerView.Adapter<PendingEventListAdapter.MyViewHolder>{
    private List<Event> eventList;
    private Context context;
    private FirebaseDatabase mDatabase;
    private FirebaseFunctions firebaseFunctions;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView event_Disp;

        public TextView event_Description;
        public TextView  event_Venue;
        public TextView  event_Date;
        public TextView  event_Name;
        public Button approve;
        public Button  disapprove;
        public ImageView event_image;

        public MyViewHolder(View view) {
            super(view);
            event_Venue = view.findViewById(R.id.Pending_event_Venue);
            event_Name = view.findViewById(R.id.pending_event_title);
            event_Date = view.findViewById(R.id.pending_event_date);
            approve = view.findViewById(R.id.App_btn);
            disapprove = view.findViewById(R.id.Disapp_btn);
            event_image=view.findViewById(R.id.Pending_event_img);


        }


    }


    public PendingEventListAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @Override
    public PendingEventListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_request_card, parent, false);

        final RecyclerView recyclerView=(RecyclerView) parent.findViewById(R.id.pending_request_recycler_view);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                Intent intent = new Intent(context,EventPage.class);
                Bundle b = new Bundle();
                b.putSerializable("event_obj",  eventList.get(itemPosition));
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });

        return new PendingEventListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PendingEventListAdapter.MyViewHolder holder, int position) {

        final Event event=eventList.get(position);
        Log.d("ezmovers","setting new scan"+event);
        holder.event_Name.setText(event.getEventName());
        holder.event_Venue.setText(event.getEventVenue());
        holder.event_Date.setText(event.getEventDate());
        Picasso.with(context).load(event.getEventImage()).into(holder.event_image);
        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Approve(event);
            }
        });
        holder.disapprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disapprove(event);
            }
        });
        
        

    }

    private void Disapprove(final Event event) {
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref1 = mDatabase.getReference("PendingEvents");
        ref1.child(event.getEventId()).removeValue();
        NotificationRequest.Request(event.getEventClub()+"_Coordinator","Event Disapproved",event.getEventName());
        final DatabaseReference databaseReference = mDatabase.getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseFunctions.addToDisapprovedEvents(dataSnapshot,event);
                databaseReference.removeEventListener(this);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void Approve(final Event event) {
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref1 = mDatabase.getReference("PendingEvents");
        final DatabaseReference ref2 = mDatabase.getReference("events");

        firebaseFunctions=new FirebaseFunctions(context, FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Log.d("event time",event.getEventEndTime());
        NotificationRequest.Request(event.getEventClub()+"_Coordinator","Event Approved",event.getEventName());
        NotificationRequest.Request(event.getEventClub(),"New Event",event.getEventName() + " by " + event.getEventClub() + " club");

        final DatabaseReference databaseReference = mDatabase.getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("data changed","done");
                firebaseFunctions.AddEvent(event.getEventName(),event.getEventDate(),event.getEventVenue(),event.getEventImage(),event.getEventClub(),event.getEventStartTime(),event.getEventEndTime(),event.getEventDescription(),event.getEventId());
                firebaseFunctions.addToApprovedEvents(dataSnapshot,event);
                ref1.child(event.getEventId()).removeValue();
                firebaseFunctions.addToNewEvents(dataSnapshot,event);
                databaseReference.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount()
    {
        return eventList.size();
    }
}
