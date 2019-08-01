package com.example.kapur.saurabh.mc_project;

/**
 * Created by saurabhkapur on 27/02/18.
 */

/**
 * Created by prasoon on 26/2/18.
 */

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.ALARM_SERVICE;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.MyViewHolder> {

    private List<Event> eventList;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    User cur_user ;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView event_Disp;

        public TextView  event_Description;
        public TextView  event_Venue;
        public TextView  event_Date;
        public TextView  event_Name;
        public TextView event_Start_Time;
        public TextView event_End_Time;
        public ImageView event_image;
        public com.like.LikeButton likeButton;
        public ImageView calendar_image;

        public MyViewHolder(View view) {
            super(view);
            event_Venue = view.findViewById(R.id.event_Venue);
            event_Name = view.findViewById(R.id.event_title);
            event_Date = view.findViewById(R.id.event_date);
            event_image=view.findViewById(R.id.event_img);
            likeButton = view.findViewById(R.id.like_btn);
            calendar_image = view.findViewById(R.id.calendar_btn);
            event_Start_Time = view.findViewById(R.id.event_starttime);
            event_End_Time = view.findViewById(R.id.event_endtime);
            settingUpFirebase();
        }
    }

    public EventListAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_events, parent, false);
        final ShimmerRecyclerView recyclerView=(ShimmerRecyclerView) parent.findViewById(R.id.recyclerview);
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
        return new MyViewHolder(itemView);
    }

    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Event event=eventList.get(position);
        Log.d("ezmovers","setting new scan"+event);
        Picasso.with(context).load(event.getEventImage()).resize(100,150).into(holder.event_image);
        Log.d("ezmovers","setting new scan"+event.getEventImage()+event.getEventName());
        holder.event_Name.setText(event.getEventName());
        holder.event_Venue.setText(event.getEventVenue());
        holder.event_Date.setText(event.getEventDate());
//        holder.event_End_Time.setText(event.getEventStartTime());
//        holder.event_Start_Time.setT
        holder.calendar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                String string = "January 2, 2010";
//                DateFormat format = new SimpleDateFormat("MM/d/yy", Locale.ENGLISH);
//
//                Date date = null;
//                try {
//                    date = format.parse(String.valueOf(holder.event_Date));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(date);
//                date.setTime();
                event.getEventStartTime().split(":");
                String date[] = event.getEventDate().split("/");
                String min= event.getEventStartTime().split(":")[1];
                String hr = event.getEventStartTime().split(":")[0];
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(hr), Integer.parseInt(min));
                Calendar endTime = Calendar.getInstance();

                min= event.getEventEndTime().split(":")[1];
                hr = event.getEventEndTime().split(":")[0];

                endTime.set(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(hr), Integer.parseInt(min));
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, event.getEventName())
                        .putExtra(CalendarContract.Events.DESCRIPTION, event.getEventDescription())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getEventVenue())
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                context.startActivity(intent);
            }
        });



        final FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context,mAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cur_user = User.getUser();
                databaseReference.removeEventListener(this);
                if (cur_user.getEventIds().containsKey(event.getEventId()))
                {
                    holder.likeButton.setLiked(true);
                }
                else {
                    holder.likeButton.setLiked(false);
                }
                databaseReference.removeEventListener(this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


//        User user = firebaseFunctions.getUserProfile(dataSnapshot,mAuth.getCurrentUser().getUid());

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            PendingIntent pendingIntent;
            AlarmManager alarmManager;
            Intent intent;

            @Override
            public void liked(LikeButton likeButton) {
                likeButton.setLiked(true);
                HashMap<String, Boolean> map = cur_user.getEventIds();
                map.put(event.getEventId(), true);
                cur_user.setEventIds(map);

                firebaseFunctions.updateUser(cur_user);
//                firebaseFunctions.updateClubs(club);

                // set notifications
                intent = new Intent(context,NotificationService.class);
                intent.putExtra(NotificationService.EVENT_NAME,event.getEventName());
                intent.putExtra(NotificationService.EVENT_CLUB_NAME,event.getEventClub());
                intent.putExtra(NotificationService.EVENT_DATE,event.getEventDate());
                intent.putExtra(NotificationService.EVENT_START_TIME,event.getEventStartTime());
                context.startService(intent);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                likeButton.setLiked(false);
                HashMap<String, Boolean> map = cur_user.getEventIds();

                map.remove(event.getEventId());
                cur_user.setEventIds(map);

                firebaseFunctions.updateUser(cur_user);
                Log.d("unLiked: ",context.getClass().getSimpleName());
                if (context.getClass().getSimpleName().equals("InterestedEvents"))
                {
                    eventList.remove(position);
                    notifyDataSetChanged();
                }
                //cancel notification
                if(intent!=null)
                    context.stopService(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return eventList.size();
    }

}
