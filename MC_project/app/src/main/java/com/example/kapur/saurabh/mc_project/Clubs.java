package com.example.kapur.saurabh.mc_project;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by souravghai on 1/3/18.
 */

public class Clubs {

    //private ImageView imageView;

    private String about;
    private String category;
    private String clubId;
    private String name;
    private String club_Image;
    private HashMap<String,Boolean> followerIds = new HashMap<String,Boolean>();
    private HashMap<String,Boolean> eventIds = new HashMap<String,Boolean>();
    private HashMap<String,Boolean> memberIds = new HashMap<String,Boolean>();

    public String getClub_Image() {
        return club_Image;
    }

    public void setClub_Image(String club_Image) {
        this.club_Image = club_Image;
    }


    public Clubs() {

    }

    public Clubs(String about,String category,String name,String image) {
        this.name = name;
        this.category = category;
        this.about = about;
        this.club_Image=image;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCategory() {

        return category;
    }

    public String getClubId() {
        return clubId;
    }

    public String getAbout() {
        return about;
    }

    public void setFollowerIds(HashMap<String, Boolean> followerIds) {
        this.followerIds = followerIds;
    }

    public void setEventIds(HashMap<String, Boolean> eventIds) {
        this.eventIds = eventIds;
    }

    public void setMemberIds(HashMap<String, Boolean> memberIds) {
        this.memberIds = memberIds;
    }

    public HashMap<String, Boolean> getFollowerIds() {

        return followerIds;
    }

    public HashMap<String, Boolean> getEventIds() {
        return eventIds;
    }

    public HashMap<String, Boolean> getMemberIds() {
        return memberIds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

//    public void setImageView(ImageView imageView) {
//        this.imageView = imageView;
//    }
//
//    public ImageView getImageView() {
//
//        return imageView;
//    }
}
