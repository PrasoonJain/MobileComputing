package com.example.kapur.saurabh.mc_project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditEvent extends AppCompatActivity {

    FirebaseStorage storage;
    StorageReference storageRef,imageRef;
    ProgressDialog progressDialog;
    UploadTask uploadTask;
    Uri downloadUrl;
    Button create;
    Button delete;
    EditText date_edit;
    EditText time_edit;
    EditText end_time;
    EditText name;
    EditText venue;
    EditText eventDisp;
    ImageView eventimage;
    Button addimage;
    private String clubName;
    private FirebaseFunctions firebaseFunctions;
    private  UploadImageStore uploadImageStore;
    private FirebaseDatabase mDatabase;
    private static final int SELECT_PHOTO = 100;
    Uri selectedImage;
    Uri url;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        mDatabase = FirebaseDatabase.getInstance();
        User user = User.getUser();
        Intent in=getIntent();
        clubName=user.getClubname();
        uploadImageStore=new UploadImageStore();
        firebaseFunctions=new FirebaseFunctions(this, FirebaseAuth.getInstance().getCurrentUser().getUid());
        create = findViewById(R.id.create_button);
        date_edit = findViewById(R.id.event_date);
        time_edit = findViewById(R.id.event_time);
        end_time=findViewById(R.id.end_time_edit);
        name = findViewById(R.id.event_name);
        venue = findViewById(R.id.event_venue);
        addimage=findViewById(R.id.add_event_image);
        eventDisp=findViewById(R.id.event_description);
        eventimage=(ImageView) findViewById(R.id.logo);
        delete=(Button)findViewById(R.id.delete_button);
        Bundle extras = getIntent().getExtras();
        event = (Event) extras.get("event_obj");
       // Log.d("editev",event.getEventName());
        name.setText(event.getEventName());
        time_edit.setText(event.getEventStartTime());
        end_time.setText(event.getEventEndTime());
        date_edit.setText(event.getEventDate());
        venue.setText(event.getEventVenue());
        eventDisp.setText(event.getEventDescription());
        Picasso.with(this).load(event.getEventImage()).resize(300,200).into(eventimage);

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                date_edit.setText(sdf.format(myCalendar.getTime()));

            }

        };

        date_edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditEvent.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final Calendar c = Calendar.getInstance();
        final int mHour = c.get(Calendar.HOUR_OF_DAY);
        final int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        time_edit.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);

        time_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();

            }
        });
        final TimePickerDialog timePickerDialogendtime = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        end_time.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);

        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialogendtime.show();

            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NotificationRequest.Request("all","Event updated",event.getEventName());
//                Timestamp d = new Timestamp(System.currentTimeMillis());
                if(selectedImage!=null){
                    uploadImage(selectedImage);
                    Log.d("editevac",selectedImage.toString());

                }
                else{
                    //selectedImage=Uri.parse(event.getEventImage());
                    push();
                }
                //  Log.d("url",downloadUrl.toString());

//                Log.d("imageurl",url.toString());

//                else{
//                   //put the image of club here
//                }


            }
        });
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();


            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationRequest.Request("all","Event Deleted",event.getEventName());
                showDialog();
            }
        });
    }

    public void selectImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this,"Image selected ", Toast.LENGTH_SHORT).show();
                    selectedImage = imageReturnedIntent.getData();
                }
        }
    }
    public void uploadImage( Uri selectedImage) {
        storage = FirebaseStorage.getInstance();
        //creates a storage reference
        storageRef = storage.getReference();
        //create reference to images folder and assing a name to the file that will be uploaded
        imageRef = storageRef.child("images/" + selectedImage.getLastPathSegment());
        //creating and showing progress dialog

        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Saving Changes");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);
        //starting upload
        uploadTask = imageRef.putFile(selectedImage);
        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //sets and increments value of progressbar
                progressDialog.incrementProgressBy((int) progress);
            }
        });
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(EditEvent.this, "Error in saving!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                // finish();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("url",downloadUrl.toString());
                Toast.makeText(EditEvent.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                push();
//                final DatabaseReference ref2 = mDatabase.getReference("events");
//                ref2.child(event.getEventId()).removeValue();
//                firebaseFunctions.AddEvent(name.getText().toString(),date_edit.getText().toString(),venue.getText().toString(),downloadUrl.toString(),clubName,time_edit.getText().toString(),end_time.getText().toString(),eventDisp.getText().toString());
//                finish();

                //showing the uploaded image in ImageView using the download url
                //Picasso.with(context).load(downloadUrl).into(imageView);
            }
        });



    }
    public void push(){
        final DatabaseReference ref2 = mDatabase.getReference("events");

        if(downloadUrl==null)
        firebaseFunctions.AddEvent(name.getText().toString(),date_edit.getText().toString(),venue.getText().toString(),event.getEventImage(),clubName,time_edit.getText().toString(),end_time.getText().toString(),eventDisp.getText().toString(),event.getEventId());
        else
            firebaseFunctions.AddEvent(name.getText().toString(),date_edit.getText().toString(),venue.getText().toString(),downloadUrl.toString(),clubName,time_edit.getText().toString(),end_time.getText().toString(),eventDisp.getText().toString(),event.getEventId());
        ref2.child(event.getEventId()).removeValue();
        Toast.makeText(this,"Changes Saved",Toast.LENGTH_SHORT).show();
        finish();
    }
    public void deleteEvent(){
        final DatabaseReference ref2 = mDatabase.getReference("events");
        ref2.child(event.getEventId()).removeValue();
        Toast.makeText(this,"Event Deleted",Toast.LENGTH_SHORT).show();
        finish();
    }

    public void showDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Delete Event");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to Delete")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        deleteEvent();


                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

}
