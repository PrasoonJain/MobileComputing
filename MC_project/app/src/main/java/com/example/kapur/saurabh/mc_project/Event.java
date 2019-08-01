package com.example.kapur.saurabh.mc_project;

/**
 * Created by saurabhkapur on 27/02/18.
 */

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.sql.Timestamp;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Create    private String eventEndTime;
    private String eventClub;
    private String eventId;
    private String eventImage;
    private String eventStartTime;d by prasoon on 27/2/18.
 */

public class Event implements Serializable {
    private String eventName;
    private String eventDate;
    private String eventDescription;
    private String eventVenue;
    private String eventEndTime;
    private String eventClub;
    private String eventId;
    private String eventImage;
    private String eventStartTime;

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }



    public String getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }



    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public void setEventClub(String eventClub) {
        this.eventClub = eventClub;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {

        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public String getEventClub() {
        return eventClub;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }


}