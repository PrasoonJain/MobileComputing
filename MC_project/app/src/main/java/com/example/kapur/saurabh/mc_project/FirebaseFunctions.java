package com.example.kapur.saurabh.mc_project;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by souravghai on 24/3/18.
 */

public class FirebaseFunctions {

    private String UserId;
    private Context myContext;
    private FirebaseDatabase mDatabase;
    private UploadImageStore uploadImageStore;

    public FirebaseFunctions(Context myContext, String UserId) {
        this.myContext = myContext;
        this.UserId = UserId;
        mDatabase = FirebaseDatabase.getInstance();
    }

    public User getUserProfile(DataSnapshot dataSnapshot, String userId) {
        User user = User.getUser();

        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref2 = mDatabase.getReference("user");
        user.setUser_id(UserId);

//        if (dataSnapshot.getKey().equals(userId))
//        {
//            user = (User) dataSnapshot.getValue();
//        }

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(myContext.getString(R.string.firebase_user_profile))) {
                DataSnapshot child = ds.child(UserId);
                for (DataSnapshot dc : child.getChildren()) {
//                    if (dc.getKey().equals(userId))
//                    {
//
//                    }
                    if (dc.getKey().equals(myContext.getString(R.string.firebase_user_name))) {
                        user.setUser_name(dc.getValue().toString());
                    }
                    if (dc.getKey().equals(myContext.getString(R.string.firebase_user_phone_number))) {
                        user.setPhone_number(dc.getValue().toString());
                    }
                    if (dc.getKey().equals(myContext.getString(R.string.firebase_user_email))) {
                        user.setEmail(dc.getValue().toString());
                    }
                    if (dc.getKey().equals(myContext.getString(R.string.firebase_user_club_ids))) {
                        user.setClubIds((HashMap<String, Boolean>) dc.getValue());
                    }
                    if (dc.getKey().equals(myContext.getString(R.string.firebase_user_event_ids))) {
                        user.setEventIds((HashMap<String, Boolean>) dc.getValue());
                    }
                    if (dc.getKey().equals(myContext.getString(R.string.firebase_user_image))) {
                        user.setUser_image(dc.getValue().toString());
                    }
                    if (dc.getKey().equals(myContext.getString(R.string.firebase_role))) {
                        user.setRole(dc.getValue().toString());
                    }
                    if (dc.getKey().equals(myContext.getString(R.string.firebase_user_clubname))) {
                        user.setClubname(dc.getValue().toString());
                    }
                    if (dc.getKey().equals("approvedEvents")) {
                        user.setApprovedEvents((HashMap<String, Boolean>) dc.getValue());
                    }
                    if (dc.getKey().equals("disapprovedEvents")) {
                        user.setDisapprovedEvents((HashMap<String, Boolean>) dc.getValue());
                    }
                    if (dc.getKey().equals("newEvents")) {
                        user.setNewEvents((HashMap<String, Boolean>) dc.getValue());
                    }

                }
            }
        }
        if (user.getRole().equals("Coordinator"))
        {
            FirebaseMessaging.getInstance().subscribeToTopic(user.getClubname()+"_Coordinator");
        }
        else if (user.getRole().equals("Admin"))
        {
            FirebaseMessaging.getInstance().subscribeToTopic("Admin");
        }
        else
        {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Admin");
        }

        return user;
    }

    public ArrayList<Event> getUpcommingEvents(DataSnapshot dataSnapshot) {
        ArrayList<Event> list = new ArrayList<Event>();
        final DatabaseReference ref2 = mDatabase.getReference("events");
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(myContext.getString(R.string.firebase_events))) {
                for (DataSnapshot dc : ds.getChildren()) {
                    Event event = new Event();
                    event.setEventId(dc.getKey());
                    for (DataSnapshot di : dc.getChildren()) {
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_club))) {
                            event.setEventClub(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_date))) {
                            event.setEventDate(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_start_time))) {
                            event.setEventStartTime(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_end_time))) {
                            event.setEventEndTime(di.getValue().toString());
                        }

                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_descripton))) {
                            event.setEventDescription(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_name))) {
                            event.setEventName(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_venue))) {
                            event.setEventVenue(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_image))) {
                            event.setEventImage(di.getValue().toString());
                        }

                    }
                    String date = event.getEventDate() + " " + event.getEventEndTime();
                    if (check_event_date(date))
                        list.add(event);
                    else
                    {
                        ref2.child(dc.getKey()).removeValue();
                    }
                }
            }
        }

        return list;
    }

    public ArrayList<Event> getRecommendedEvents(DataSnapshot dataSnapshot) {

        ArrayList<Event> list = new ArrayList<>();
        ArrayList<Clubs> club_list = getFollowClubList(dataSnapshot);
        for (int i = 0; i < club_list.size(); i++) {
            list.addAll(getClubEvents(dataSnapshot, club_list.get(i).getName()));
        }


        return list;
    }

    public ArrayList<Clubs> getClubList(String category, DataSnapshot dataSnapshot) {
        ArrayList<Clubs> arrayList = new ArrayList<Clubs>();
        DataSnapshot ds = dataSnapshot.child("clubs");
        int flag;
        String cat;
        for (DataSnapshot dd : ds.getChildren()) {
            flag = 1;
            Clubs clubs = new Clubs();
            clubs.setClubId(dd.getKey());
            for (DataSnapshot di : dd.getChildren()) {
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_category))) {
                    cat = di.getValue().toString();
                    clubs.setCategory(cat);
                    if (!cat.equals(category))
                        flag = 0;
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_name))) {
                    clubs.setName(di.getValue().toString());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_about))) {
                    clubs.setAbout(di.getValue().toString());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_image))) {
                    clubs.setClub_Image(di.getValue().toString());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_followerIds))) {
                    clubs.setFollowerIds((HashMap<String, Boolean>) di.getValue());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_eventIds))) {
                    clubs.setEventIds((HashMap<String, Boolean>) di.getValue());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_memberIds))) {
                    clubs.setMemberIds((HashMap<String, Boolean>) di.getValue());
                }
            }
            if (flag == 1)
                arrayList.add(clubs);
        }
        return arrayList;
    }

    public ArrayList<Event> getPendingEvents(DataSnapshot dataSnapshot) {
        ArrayList<Event> list = new ArrayList<Event>();
        final DatabaseReference ref2 = mDatabase.getReference("PendingEvents");
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals("PendingEvents")) {
                for (DataSnapshot dc : ds.getChildren()) {
                    Event event = new Event();
                    event.setEventId(dc.getKey());
                    for (DataSnapshot di : dc.getChildren()) {
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_club))) {
                            event.setEventClub(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_date))) {
                            event.setEventDate(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_descripton))) {
                            event.setEventDescription(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_name))) {
                            event.setEventName(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_venue))) {
                            event.setEventVenue(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_image))) {
                            event.setEventImage(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_start_time))) {
                            event.setEventStartTime(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_end_time))) {
                            event.setEventEndTime(di.getValue().toString());
                        }

                    }
                    String date = event.getEventDate() + " " + event.getEventEndTime();
                    if (check_event_date(date))
                        list.add(event);
                    else
                    {
                        ref2.child(dc.getKey()).removeValue();
                    }
                    Log.d("test", event.getEventName());
                }
            }
            break;
        }

        return list;
    }

    public void updateUser(User user) {
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref2 = mDatabase.getReference("user");
        Log.d("updateUser: ", user.getUser_name());
        ref2.child(user.getUser_id()).setValue(user);

    }

    public void updateClubs(Clubs club) {
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref2 = mDatabase.getReference("clubs");
        ref2.child(club.getClubId()).setValue(club);
    }


    public void AddUser(String user_name, String email, String phone_number, Uri user_image) {
        mDatabase = FirebaseDatabase.getInstance();


        final DatabaseReference ref2 = mDatabase.getReference("user");


        User new_user = new User(user_name, email, phone_number, user_image.toString());
        new_user.setUser_id(UserId);
        ref2.child(new_user.getUser_id()).setValue(new_user);
    }

    public void AddPendingEvent(String event_name, String event_date, String event_venue, String event_image, String clubName, String startTime, String endTime, String eventDesp) {
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDatabase.getReference("PendingEvents");


        Event new_event = new Event();
        new_event.setEventDate(event_date);
        new_event.setEventVenue(event_venue);
        new_event.setEventName(event_name);
        new_event.setEventImage(event_image);
        new_event.setEventClub(clubName);
        new_event.setEventStartTime(startTime);
        new_event.setEventEndTime(endTime);
        new_event.setEventDescription(eventDesp);
        new_event.setEventId(myRef.push().getKey());
        myRef.child(new_event.getEventId()).setValue(new_event);
    }

    public void AddEvent(String event_name, String event_date, String event_venue, String event_image, String clubName, String startTime, String endTime, String eventDesp,String eventId) {
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDatabase.getReference("events");


        Event new_event = new Event();
        new_event.setEventDate(event_date);
        new_event.setEventVenue(event_venue);
        new_event.setEventName(event_name);
        new_event.setEventImage(event_image);
        new_event.setEventClub(clubName);
        new_event.setEventDescription(eventDesp);
        new_event.setEventStartTime(startTime);
        //Log.d("event time",startTime);
        new_event.setEventEndTime(endTime);
        new_event.setEventId(eventId);
        myRef.child(new_event.getEventId()).setValue(new_event);
    }

    public void AddClubs(String about, String category, String name, String image) {
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDatabase.getReference("clubs");


        Clubs new_club = new Clubs(about, category, name, image);

        new_club.setClubId(myRef.push().getKey());
        myRef.child(new_club.getClubId()).setValue(new_club);
    }


    public ArrayList<Clubs> getFollowClubList(DataSnapshot dataSnapshot) {
        User user = User.getUser();
        HashMap<String, Boolean> hm = user.getClubIds();
        Set<String> ids = user.getClubIds().keySet();
        List<String> exist = new ArrayList<>();
        ArrayList<Clubs> arrayList = new ArrayList<Clubs>();
        String key;
        DataSnapshot ds = dataSnapshot.child("clubs");
        for (DataSnapshot dd : ds.getChildren()) {
            Clubs clubs = new Clubs();
            key = dd.getKey();
            if (!ids.contains(key))
                continue;
            clubs.setClubId(key);
            exist.add(key);
            for (DataSnapshot di : dd.getChildren()) {
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_category))) {
                    clubs.setCategory(di.getValue().toString());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_name))) {
                    clubs.setName(di.getValue().toString());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_about))) {
                    clubs.setAbout(di.getValue().toString());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_followerIds))) {
                    clubs.setFollowerIds((HashMap<String, Boolean>) di.getValue());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_eventIds))) {
                    clubs.setEventIds((HashMap<String, Boolean>) di.getValue());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_memberIds))) {
                    clubs.setMemberIds((HashMap<String, Boolean>) di.getValue());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_image))) {
                    clubs.setClub_Image( di.getValue().toString());
                }
            }
            arrayList.add(clubs);
        }
        Set<String> temp = new HashSet<>(hm.keySet());
        temp.removeAll(exist);
        hm.keySet().removeAll(temp);

        user.setClubIds(hm);
        updateUser(user);
        return arrayList;
    }


    public ArrayList<Event> getLikedEvents(DataSnapshot dataSnapshot) {
        ArrayList<Event> list = new ArrayList<Event>();
        User user = User.getUser();
        HashMap<String, Boolean> hm = user.getEventIds();
        int flag;
        List<String> exist_map = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(myContext.getString(R.string.firebase_events))) {
                for (DataSnapshot dc : ds.getChildren()) {
                    Event event = new Event();
                    event.setEventId(dc.getKey());

                    flag = 1;
                    if (!hm.containsKey(dc.getKey())) {
                        flag = 0;
                        continue;
                    }
                    exist_map.add(dc.getKey());
                    for (DataSnapshot di : dc.getChildren()) {
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_club))) {
                            String eventClub = di.getValue().toString();
                            event.setEventClub(eventClub);
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_date))) {
                            event.setEventDate(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_descripton))) {
                            event.setEventDescription(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_name))) {
                            event.setEventName(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_venue))) {
                            event.setEventVenue(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_image))) {
                            event.setEventImage(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_start_time))) {
                            event.setEventStartTime(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_end_time))) {
                            event.setEventEndTime(di.getValue().toString());
                        }
                    }
                    if (flag == 1)
                        list.add(event);
                }
            }
        }
        Set<String> temp = new HashSet<>(hm.keySet());
        temp.removeAll(exist_map);
        hm.keySet().removeAll(temp);
        user.setEventIds(hm);
        updateUser(user);
        return list;
    }

    public ArrayList<Event> getClubEvents(DataSnapshot dataSnapshot, String clubname) {
        ArrayList<Event> list = new ArrayList<Event>();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(myContext.getString(R.string.firebase_events))) {
                for (DataSnapshot dc : ds.getChildren()) {
                    Event event = new Event();
                    event.setEventId(dc.getKey());
                    int flag = 0;
                    for (DataSnapshot di : dc.getChildren()) {
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_club)) && di.getValue().toString().equals(clubname)) {
                            flag = 1;
                            event.setEventClub(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_date))) {
                            event.setEventDate(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_descripton))) {
                            event.setEventDescription(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_name))) {
                            event.setEventName(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_venue))) {
                            event.setEventVenue(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_image))) {
                            event.setEventImage(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_start_time))) {
                            event.setEventStartTime(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_end_time))) {
                            event.setEventEndTime(di.getValue().toString());
                        }

                    }
                    if (flag == 1)
                        list.add(event);
                }
            }
        }

        return list;
    }


    public ArrayList<String> getClubnames(DataSnapshot dataSnapshot) {
        User user = User.getUser();

        ArrayList<String> arrayList = new ArrayList<String>();
        String key;
        DataSnapshot ds = dataSnapshot.child("clubs");
        for (DataSnapshot dd : ds.getChildren()) {
            String clubs = new String();
            for (DataSnapshot di : dd.getChildren()) {
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_name))) {
                    clubs = (di.getValue().toString());
                }

            }
            arrayList.add(clubs);
        }
        return arrayList;
    }

    public boolean setCoordinator(DataSnapshot dataSnapshot, String email, String clubName) {

        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref2 = mDatabase.getReference("user");

        dataSnapshot = dataSnapshot.child("user");
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            int flag = 0;
            for (DataSnapshot dc : ds.getChildren()) {
                Log.d("setCoordinator: ", dc.getKey());
                if (dc.getKey().equals(myContext.getString(R.string.firebase_user_email)) && dc.getValue().toString().equals(email)) {
                    flag = 1;
                    Log.d("seese: ", String.valueOf(ds.child("role").getValue()));
                    if (ds.child("role").getValue() != null) {
                        flag = 0;
                    }
                    break;
                }


            }
            if (flag == 1) {
                DatabaseReference usersRef = ref2.child(ds.getKey());
                usersRef = usersRef.child(myContext.getString(R.string.firebase_role));
                usersRef.setValue("Coordinator");
                usersRef = ref2.child(ds.getKey());
                usersRef = usersRef.child(myContext.getString(R.string.firebase_user_clubname));
                usersRef.setValue(clubName);
                return true;
            }
//            }
        }


        return false;
    }

    public String getClubImage(DataSnapshot dataSnapshot, String clubname) {
        String key;
        DataSnapshot ds = dataSnapshot.child("clubs");
        for (DataSnapshot dd : ds.getChildren()) {
            Clubs clubs = new Clubs();
            key = dd.getKey();
            int flag = 0;
            clubs.setClubId(key);
            for (DataSnapshot di : dd.getChildren()) {
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_category))) {
                    clubs.setCategory(di.getValue().toString());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_name)) && di.getValue().equals(clubname)) {
                    clubs.setName(di.getValue().toString());
                    flag = 1;
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_about))) {
                    clubs.setAbout(di.getValue().toString());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_followerIds))) {
                    clubs.setFollowerIds((HashMap<String, Boolean>) di.getValue());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_eventIds))) {
                    clubs.setEventIds((HashMap<String, Boolean>) di.getValue());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_memberIds))) {
                    clubs.setMemberIds((HashMap<String, Boolean>) di.getValue());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_club_image))) {
                    clubs.setClub_Image(di.getValue().toString());
                }
            }
            if (flag == 1) {
                return clubs.getClub_Image();
            }
        }

        return null;
    }


    public ArrayList<Members> getMembers(DataSnapshot dataSnapshot, String clubname) {
        ArrayList<Members> members = new ArrayList<Members>();
        boolean b;
        DataSnapshot ds = dataSnapshot.child("members");
        for (DataSnapshot dd : ds.getChildren()) {
            Members mem = new Members();
            b = true;
            for (DataSnapshot di : dd.getChildren()) {
                if (di.getKey().equals(myContext.getString(R.string.firebase_member_id))) {
                    mem.setMember_id(di.getValue().toString());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_member_name))) {
                    mem.setMember_name(di.getValue().toString());
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_member_club_name))) {
                    String cn = di.getValue().toString();
                    if (!clubname.equals(cn)) {
                        b = false;
                        break;
                    }
                    mem.setMember_club_name(cn);
                }
                if (di.getKey().equals(myContext.getString(R.string.firebase_member_email))) {
                    mem.setMember_email(di.getValue().toString());
                }
            }
            if (b)
                members.add(mem);
        }

        return members;
    }

    public void uploadMembers(Members members) {
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDatabase.getReference("members");

        members.setMember_id(myRef.push().getKey());
        myRef.child(members.getMember_id()).setValue(members);

    }

    public void Delete_event_from_club(DataSnapshot dataSnapshot, String clubname) {
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDatabase.getReference("events");
        String key;
        DataSnapshot ds = dataSnapshot.child("events");
        for (DataSnapshot dd : ds.getChildren()) {
            key = dd.getKey();
            int flag = 0;
            for (DataSnapshot di : dd.getChildren()) {

                if (di.getKey().equals(myContext.getString(R.string.firebase_event_club)) && di.getValue().equals(clubname)) {
//                        clubs.setName(di.getValue().toString());
                    flag = 1;
                    ref.child(key).removeValue();
                    return;
                }

            }


        }
    }


    public void Delete_club(DataSnapshot dataSnapshot, String clubname) {
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDatabase.getReference("clubs");
        String key;
        DataSnapshot ds = dataSnapshot.child("clubs");
        for (DataSnapshot dd : ds.getChildren()) {
            Clubs clubs = new Clubs();
            key = dd.getKey();
            int flag = 0;
            clubs.setClubId(key);
            for (DataSnapshot di : dd.getChildren()) {

                if (di.getKey().equals(myContext.getString(R.string.firebase_club_name)) && di.getValue().equals(clubname)) {
                    clubs.setName(di.getValue().toString());
                    flag = 1;
//                    Delete_event_from_club(clubname);
                    ref.child(key).removeValue();
                    return;
                }

            }


        }
    }

    public boolean Remove_coordinator(DataSnapshot dataSnapshot, String email) {
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref2 = mDatabase.getReference("user");

        dataSnapshot = dataSnapshot.child("user");
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            int flag = 0;
            for (DataSnapshot dc : ds.getChildren()) {
                Log.d("setCoordinator: ", dc.getKey());
                if (dc.getKey().equals(myContext.getString(R.string.firebase_user_email)) && dc.getValue().toString().equals(email)) {
                    flag = 1;
                    Log.d("seese: ", String.valueOf(ds.child("role").getValue()));
                    if (ds.child("role").getValue() == null) {
                        flag = 0;
                    }
                    break;
                }
            }
            if (flag == 1) {
                DatabaseReference usersRef = ref2.child(ds.getKey());
                usersRef = usersRef.child(myContext.getString(R.string.firebase_role));
                usersRef.removeValue();
                usersRef = ref2.child(ds.getKey());
                usersRef = usersRef.child(myContext.getString(R.string.firebase_user_clubname));
                usersRef.removeValue();
                return true;
            }
//            }
        }
        return false;
    }

    public boolean Remove_member(DataSnapshot dataSnapshot, String email) {

        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref2 = mDatabase.getReference("members");

        dataSnapshot = dataSnapshot.child("members");
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            for (DataSnapshot dd : ds.getChildren()) {
                if (dd.getKey().equals(myContext.getString(R.string.firebase_member_email)) && dd.getValue().toString().equals(email)) {
                    DatabaseReference usersRef = ref2.child(ds.getKey());
                    usersRef.removeValue();
                    return true;
                }
            }
        }
        return false;
    }

    public void addToApprovedEvents(DataSnapshot dataSnapshot, Event event) {
        addToNewEvents(dataSnapshot,event);
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref2 = mDatabase.getReference("user");

        String clubName = event.getEventClub();

        dataSnapshot = dataSnapshot.child("user");
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            DataSnapshot dd = ds.child("role");
            DataSnapshot dc = ds.child("clubname");
            if (dd.getValue() != null && !dd.getValue().toString().equals("Admin") && dc.getValue().toString().equals(clubName)) {
                HashMap<String, Boolean> hm = (HashMap<String, Boolean>) ds.child("approvedEvents").getValue();
                if(hm==null)
                {
                    hm = new HashMap<String,Boolean>();
                }
                hm.put(event.getEventId(), true);
                ref2.child(ds.getKey()).child("approvedEvents").setValue(hm);
            }
        }


    }

    public void addToDisapprovedEvents(DataSnapshot dataSnapshot, Event event) {
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref2 = mDatabase.getReference("user");

        String clubName = event.getEventClub();

        dataSnapshot = dataSnapshot.child("user");
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            DataSnapshot dd = ds.child("role");
            DataSnapshot dc = ds.child("clubname");
            if (dd.getValue() != null && !dd.getValue().toString().equals("Admin") && dc.getValue().toString().equals(clubName)) {
                HashMap<String, Boolean> hm = (HashMap<String, Boolean>) ds.child("disapprovedEvents").getValue();
                if(hm==null)
                {
                    hm = new HashMap<String,Boolean>();
                }
                hm.put(event.getEventId(), true);
                ref2.child(ds.getKey()).child("disapprovedEvents").setValue(hm);
            }
        }

    }

    public void addToNewEvents(DataSnapshot dataSnapshot, Event event) {
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref2 = mDatabase.getReference("user");

        dataSnapshot = dataSnapshot.child("user");
        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            HashMap<String, Boolean> hm = (HashMap<String, Boolean>) ds.child("newEvents").getValue();
            if(hm==null)
            {
                hm = new HashMap<String,Boolean>();
            }
            hm.put(event.getEventId(), true);
            ref2.child(ds.getKey()).child("newEvents").setValue(hm);
        }
    }

    public Event getEventFromId(DataSnapshot dataSnapshot,String eventId) {
        ArrayList<Event> list = new ArrayList<Event>();
        User user = User.getUser();
        HashMap<String, Boolean> hm = user.getEventIds();
        List<String> exist_map = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(myContext.getString(R.string.firebase_events))) {
                for (DataSnapshot dc : ds.getChildren()) {
                    Event event = new Event();
                    event.setEventId(dc.getKey());
                    if (!dc.getKey().equals(eventId))
                    {
                        continue;
                    }
                    exist_map.add(dc.getKey());
                    for (DataSnapshot di : dc.getChildren()) {
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_club))) {
                            String eventClub = di.getValue().toString();
                            event.setEventClub(eventClub);
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_date))) {
                            event.setEventDate(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_descripton))) {
                            event.setEventDescription(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_name))) {
                            event.setEventName(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_venue))) {
                            event.setEventVenue(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_image))) {
                            event.setEventImage(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_start_time))) {
                            event.setEventStartTime(di.getValue().toString());
                        }
                        if (di.getKey().equals(myContext.getString(R.string.firebase_event_end_time))) {
                            event.setEventEndTime(di.getValue().toString());
                        }
                    }
                    return event;
                }
            }
        }
        return null;
    }

    public boolean check_event_date(String date)
    {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy hh:mm");
        Date d1 = null;
        try {
            d1 = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long curTime = System.currentTimeMillis();
        if (curTime > d1.getTime())
            return false;
        return true;
    }

}