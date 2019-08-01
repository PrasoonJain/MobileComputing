package com.example.kapur.saurabh.mc_project;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBuffer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class DataChangeNotificationService extends IntentService {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    public static ReentrantLock lock = new ReentrantLock();

    public DataChangeNotificationService() {
        super("DataChangeNotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d("service","started 2");

        //setting up firebase
        settingUpFirebase();

        final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser cur_user = mAuth.getCurrentUser();
        if (cur_user == null)
        {
            stopSelf();
            return;
        }
        String userID = cur_user.getUid();
        final DatabaseReference ref2 = mDatabase.getReference("user").child(userID);
        final DatabaseReference ref1 = mDatabase.getReference("user").child(userID).child("approvedEvents");

        ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String userId = mAuth.getCurrentUser().getUid();
                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(getApplicationContext(),userId);

//                User user = User.getUser();
//                Log.d("user id",user.getUser_id());

                HashMap<String,Boolean> approvedEvents;

                Log.d("user1",ref2.getKey());
                Log.d("user2",mAuth.getCurrentUser().getUid());

//                approvedEvents = user.getApprovedEvents();
//                disApprovedEvents = user.getDisapprovedEvents();
//                newEvents = user.getNewEvents();

                String x =  dataSnapshot.getKey();
//
                Log.d("called","yes");

                if(x==null)
                    return;
                Notification notification = createApproveNotification();
                sendNotification(notification);

                ref2.child("approvedEvents").removeValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String userId = mAuth.getCurrentUser().getUid();
//                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(getApplicationContext(),userId);
//
//                User user = firebaseFunctions.getUserProfile(dataSnapshot,userId);
//                HashMap<String,Boolean> approvedEvents;
//                HashMap<String,Boolean> disApprovedEvents;
//                HashMap<String,Boolean> newEvents;
//
//                approvedEvents = user.getApprovedEvents();
//                disApprovedEvents = user.getDisapprovedEvents();
//                newEvents = user.getNewEvents();
//                Log.d("called","yes");
//
//                for (String eventId: approvedEvents.keySet())
//                {
//                    DataSnapshot ds = dataSnapshot.child("events");
//                    Event e = firebaseFunctions.getEventFromId(dataSnapshot,eventId);
//                    if(e==null)
//                        Log.d("event null",eventId);
//
//                    Notification notification = createApproveNotification(e);
//
//                    approvedEvents.remove(e.getEventId());
//
//                    user.setApprovedEvents(approvedEvents);
//
//                    sendNotification(notification);
//
//                    firebaseFunctions.updateUser(user);
//
//                    Log.d("approved",e.getEventName());
//                }
//
//                for (String eventId: disApprovedEvents.keySet())
//                {
//
//                }
//
//                for (String eventId: newEvents.keySet())
//                {
//
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//        });
    }

    private void sendNotification(Notification notification) {

        Random rand = new Random();
        int randInt = rand.nextInt(1000000);
        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(randInt, notification);
    }

    private Notification createApproveNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Your event has been approved");
        builder.setSmallIcon(R.drawable.notification);
        return builder.build();
    }

    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
