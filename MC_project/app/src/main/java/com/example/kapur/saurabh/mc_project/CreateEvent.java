package com.example.kapur.saurabh.mc_project;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import id.zelory.compressor.Compressor;

public class CreateEvent extends AppCompatActivity {
    FirebaseStorage storage;
    StorageReference storageRef, imageRef;
    ProgressDialog progressDialog;
    UploadTask uploadTask;
    Uri downloadUrl;
    Button create;
    EditText date_edit;
    EditText time_edit;
    EditText end_time;
    EditText name;
    EditText venue;
    EditText eventDisp;
    File imagefile;
    File compressim;
    Button addimage;
    private String clubName;
    private FirebaseFunctions firebaseFunctions;
    private UploadImageStore uploadImageStore;
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;
    private static final int SELECT_PHOTO = 100;
    private Calendar datetime = null;
    Uri selectedImage;
    String clubImage;
    Uri url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        mDatabase = FirebaseDatabase.getInstance();
        User user = User.getUser();
        Intent in = getIntent();
        clubName = user.getClubname();
        uploadImageStore = new UploadImageStore();
        firebaseFunctions = new FirebaseFunctions(this, FirebaseAuth.getInstance().getCurrentUser().getUid());
        create = findViewById(R.id.create_button);
        date_edit = findViewById(R.id.event_date);
        time_edit = findViewById(R.id.event_time);
        end_time = findViewById(R.id.end_time_edit);
        name = findViewById(R.id.event_name);
        venue = findViewById(R.id.event_venue);
        addimage = findViewById(R.id.add_event_image);
        eventDisp = findViewById(R.id.event_description);

        databaseReference = mDatabase.getReference();
        club_image();
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
                new DatePickerDialog(CreateEvent.this, date, myCalendar
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
                        DecimalFormat formater = new DecimalFormat("00");
                        String newMinute = formater.format(minute);
                        String newHour = formater.format(hourOfDay);

                        datetime = Calendar.getInstance();
                        Calendar c = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);

                        time_edit.setText(newHour + ":" + newMinute);


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
                        if (datetime == null) {
                            Toast.makeText(getApplicationContext(), "Enter start time first", Toast.LENGTH_LONG).show();
                            return;
                        }

                        DecimalFormat formater = new DecimalFormat("00");
                        String newMinute = formater.format(minute);
                        String newHour = formater.format(hourOfDay);

                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        if (datetime.getTimeInMillis() < c.getTimeInMillis()) {
                            end_time.setText(newHour + ":" + newMinute);
                        } else {
                            Toast.makeText(getApplicationContext(), "Sorry we can't go back in time!!", Toast.LENGTH_LONG).show();
                        }


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

//                Timestamp d = new Timestamp(System.currentTimeMillis());
                if (selectedImage != null) {
                    try {
                        Log.d("imagefile", imagefile.toString());
                        compressim = new Compressor(getApplicationContext()).compressToFile(imagefile);
                        selectedImage = Uri.fromFile(compressim);
                        uploadImage(selectedImage);
                    } catch (IOException e) {
                        Log.d("Io error", e.toString());
                    }
                } else {
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
                    Toast.makeText(this, "Image selected ", Toast.LENGTH_SHORT).show();
                    selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    imagefile = new File(picturePath);
                    //         actualImage = .from(this, imageReturnedIntent.getData());
                }
        }
    }

    public void uploadImage(Uri selectedImage) {
        storage = FirebaseStorage.getInstance();
        //creates a storage reference
        storageRef = storage.getReference();
        //create reference to images folder and assing a name to the file that will be uploaded
        imageRef = storageRef.child("images/" + selectedImage.getLastPathSegment());
        //creating and showing progress dialog

        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Creating event");
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
                Toast.makeText(CreateEvent.this, "Error in creating!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                // finish();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("url", downloadUrl.toString());
                Toast.makeText(CreateEvent.this, "Event Successfully Created", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                push();

                //showing the uploaded image in ImageView using the download url
                //Picasso.with(context).load(downloadUrl).into(imageView);
            }
        });


    }

    public void club_image() {
        //mShimmerViewContainer.startShimmerAnimation();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context,mAuth.getCurrentUser().getUid());
                clubImage = firebaseFunctions.getClubImage(dataSnapshot, clubName);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //mShimmerViewContainer.stopShimmerAnimation();
    }

    public void push() {
        final DatabaseReference ref2 = mDatabase.getReference("events");
        Log.d("push: ", clubImage.toString());
        String naam = name.getText().toString();
        String date = date_edit.getText().toString();
        String ven = venue.getText().toString();
        String startTime = time_edit.getText().toString();
        String endTime = end_time.getText().toString();
        String dis = eventDisp.getText().toString();

        if (naam.equals("") || date.equals("") || ven.equals("") || clubImage.equals("") || clubName.equals("") || startTime.equals("") || endTime.equals("") || dis.equals("")) {
            Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (downloadUrl == null)
            firebaseFunctions.AddPendingEvent(naam, date, ven, clubImage, clubName, startTime, endTime, dis);
        else
            firebaseFunctions.AddPendingEvent(naam, date, ven, downloadUrl.toString(), clubName, startTime, endTime, dis);

        Toast.makeText(this, "Event Created Successfully", Toast.LENGTH_SHORT).show();
        NotificationRequest.Request("Admin","New Pending Request",naam);

        finish();

    }

}
