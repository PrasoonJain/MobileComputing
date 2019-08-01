package com.example.kapur.saurabh.mc_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.view.View.INVISIBLE;

public class EventPage extends AppCompatActivity {


    private TextView event_clubname,event_name,event_detail,event_date,event_starttime,event_endtime;
    private ImageView event_image;
    private Button event_edit;
    private Event event;
    String Event_club,Event_name,Event_detail,Event_date,Event_starttime,Event_endtime,Image_url;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        event = (Event) extras.get("event_obj");
        event_clubname=(TextView)findViewById(R.id.event_club);
        event_name=(TextView)findViewById(R.id.event_name);
        event_detail=(TextView)findViewById(R.id.event_description);
        event_date=(TextView)findViewById(R.id.event_date);
        event_starttime=(TextView)findViewById(R.id.event_starttime);
        event_endtime=(TextView)findViewById(R.id.event_endtime);
        event_image=(ImageView)findViewById(R.id.event_image);
        event_edit=(Button)findViewById(R.id.eventEdit);
        User user =User.getUser();
        event_edit.setVisibility(View.GONE);
        if(user.getRole()!=null && user.getRole().equals("Coordinator")&& user.getClubname().equals(event.getEventClub())){
            event_edit.setVisibility(View.VISIBLE);
        }
        final Intent intent1=new Intent(this,EditEvent.class);
        Bundle b = new Bundle();
        b.putSerializable("event_obj",  event);
        intent1.putExtras(b);
        setValues(event.getEventClub(),event.getEventName(),event.getEventDescription(),event.getEventStartTime(),event.getEventEndTime(),event.getEventImage(),event.getEventDate());
        event_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(intent1);
                finish();

            }
        });
    }

    private void setValues(String club,String name,String detail,String start_time,String end_time,String imageUrl,String date)
    {
        event_clubname.setText(club);
        event_name.setText(name);
        event_detail.setText(detail);
        event_starttime.setText(start_time);
        event_endtime.setText(end_time);
        event_date.setText(date);
        Picasso.with(this).load(imageUrl).into(event_image);



    }

}
