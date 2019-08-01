package com.example.kapur.saurabh.mc_project;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class NotificationService extends IntentService {

    public static final String EVENT_NAME = "event_name";
    public static final String EVENT_CLUB_NAME = "event_club_name";
    public static final String EVENT_DATE = "event_date";
    public static final String EVENT_START_TIME = "event_start_date";

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent1) {
        if (intent1 != null) {

            String name = intent1.getStringExtra(EVENT_NAME);
            String clubName = intent1.getStringExtra(EVENT_CLUB_NAME);
            String eventDate = intent1.getStringExtra(EVENT_DATE);
            String eventStartTime = intent1.getStringExtra(EVENT_START_TIME);

            String content = name + ", " + clubName;

            Calendar calendar = Calendar.getInstance();
//            Toast.makeText(this,"check it",Toast.LENGTH_SHORT).show();
            Notification eventNotification = getNotification(content);

            Random rand = new Random();
            int randInt = rand.nextInt(1000000);
            Intent intent = new Intent(this,ShowNotification.class);
            intent.putExtra(ShowNotification.NOTIFICATION_ID, randInt);
            intent.putExtra(ShowNotification.NOTIFICATION, eventNotification);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,randInt,intent,PendingIntent.FLAG_UPDATE_CURRENT);

//            Toast.makeText(this,"check it",Toast.LENGTH_SHORT).show();
            long startTime = 0;
            try {
                startTime = getDateTimeInMil(eventDate,eventStartTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            long time = startTime - 30*60*1000;
            long time = startTime + 10*1000;

            AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time,AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("You have an upcoming Event");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.notification);
        return builder.build();
    }

    private long getDateTimeInMil(String eventDate, String eventStartTime) throws ParseException {
        String date = eventDate + " " + eventStartTime;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy hh:mm");
        Date d = format.parse(date);
        long time = d.getTime();
        //Log.d("notify time",Long.toString(time));


//        String[] dateArray = eventDate.split("/");
//        String[] timeArray = eventStartTime.split(":");
//        String yr = "20" + dateArray[2];
//
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(timeArray[0]));
//        cal.set(Calendar.MINUTE,Integer.parseInt(timeArray[1]));
//        cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(dateArray[1]));
//        cal.set(Calendar.MONTH, Integer.parseInt(dateArray[0])-1);
//        cal.set(Calendar.YEAR,Integer.parseInt(yr));
//        long time = cal.getTimeInMillis();

        return time;
    }

}
