package com.example.kapur.saurabh.mc_project;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by saurabhkapur on 28/02/18.
 */

public class User {

    private static User user;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private HashMap<String,Boolean> approvedEvents = new HashMap<String,Boolean>();

    public HashMap<String, Boolean> getApprovedEvents() {
        return approvedEvents;
    }

    public HashMap<String, Boolean> getDisapprovedEvents() {
        return disapprovedEvents;
    }

    public static void setUser(User user) {
        User.user = user;
    }

    public void setApprovedEvents(HashMap<String, Boolean> approvedEvents) {
        this.approvedEvents = approvedEvents;
    }

    public void setDisapprovedEvents(HashMap<String, Boolean> disapprovedEvents) {
        this.disapprovedEvents = disapprovedEvents;
    }

    public void setNewEvents(HashMap<String, Boolean> newEvents) {
        this.newEvents = newEvents;
    }

    public HashMap<String, Boolean> getNewEvents() {

        return newEvents;
    }

    private HashMap<String,Boolean> disapprovedEvents = new HashMap<String,Boolean>();
    private HashMap<String,Boolean> newEvents = new HashMap<String,Boolean>();

    private User() {
    }  //private constructor.

    public static User getUser() {
        if (user == null) { //if there is no instance available... create new one


            user = new User();
            Log.d("getUser: ", "yes");


        }

        return user;
    }

    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void setClubIds(HashMap<String, Boolean> clubIds) {
        this.clubIds = clubIds;
    }

    public void setEventIds(HashMap<String, Boolean> eventIds) {
        this.eventIds = eventIds;
    }

    public HashMap<String, Boolean> getClubIds() {

        return clubIds;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private String role;

    public String getClubname() {
        return clubname;
    }

    public void setClubname(String clubname) {
        this.clubname = clubname;
    }

    private String clubname;

    public HashMap<String, Boolean> getEventIds() {
        return eventIds;
    }

    private String email;
    private String phone_number;
    private String user_name;
    private String user_id;

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    private String user_image;
    private HashMap<String, Boolean> clubIds = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> eventIds = new HashMap<String, Boolean>();

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

//    public User() {
//        // DO NOT DELETE THIS. Default Constructor is necessary.
//    }

    public User(String user_name, String email, String phone_number, String user_image) {
        this.user_name = user_name;
        this.email = email;
        this.phone_number = phone_number;
        this.user_image = user_image;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPhone_number() {

        return phone_number;
    }

    public String getUser_name() {

        return user_name;
    }

    public String getEmail() {
        return email;
    }

}
