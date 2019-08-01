package com.example.kapur.saurabh.mc_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.synnapps.carouselview.ImageListener;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfileActivity extends BaseActivity {

    FirebaseStorage storage;
    StorageReference storageRef,imageRef;
    ProgressDialog progressDialog;
    UploadTask uploadTask;
    Uri downloadUrl;
    private static final int ActivityNum = 3;
    private FirebaseAuth mAuth;
    private Button button;
    private DatabaseReference mDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private TextView email;
    private TextView ph;
    private TextView userName;
    private CircleImageView profile_image;
    private LinearLayout followClubList;
    private TextView liked_events;
    private TextView fol_clubs;
    private View events_liked_card;
    Uri selectedImage;
    private static final int SELECT_PHOTO = 100;
    File imagefile;
    File compressim;

    private int key=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
       // ConstraintLayout frame = findViewById(R.id.home_frame);

        //setting up nav bar
        settingNavBar();

        //set up firebase
        settingUpFirebase();
        liked_events = (TextView) findViewById(R.id.events_liked_count);
        fol_clubs = (TextView)findViewById(R.id.clubs_fol_count);
        button = (Button)findViewById(R.id.sign_out_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        profile_image=(CircleImageView)findViewById(R.id.profile_image);
        email = (TextView) findViewById(R.id.email);
        ph     = (TextView)findViewById(R.id.phone_number);
        userName = (TextView)findViewById(R.id.user_name);
        followClubList = (LinearLayout) findViewById(R.id.follow_club_list);

        followClubList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FollowingClubList.class);
                startActivity(intent);
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });

        events_liked_card = findViewById(R.id.liked_events_layout);


    }

    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void settingNavBar() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bott_nav_bar);
        BottomNavBar.disableShiftMode(bottomNavigationView);
        BottomNavBar.navBarnavigation(this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ActivityNum);
        menuItem.setChecked(true);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgressDialog();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(getApplicationContext(),mAuth.getCurrentUser().getUid());
                User user = User.getUser();
                if(user!=null) {
                    updateProfile(user);
                }
                else{
                    Toast.makeText(getApplicationContext(),"NULL", Toast.LENGTH_SHORT).show();
                }
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        hideProgressDialog();
    }

    private void updateProfile(User user) {
        email.setText(user.getEmail());
        userName.setText(user.getUser_name());
        liked_events.setText(Integer.toString(user.getEventIds().size()));
        final Intent intent = new Intent(this,InterestedEvents.class);
        events_liked_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        fol_clubs.setText(Integer.toString(user.getClubIds().size()));
        //Log.d("urlimage",user.getUser_image());
        if (user.getUser_image() != null)
            Picasso.with(ProfileActivity.this).load(user.getUser_image()).resize(200,200).into(profile_image);
    }
    @Override
    public void onBackPressed() {
        if(key == 1){
            super.onBackPressed();
            return;
        }
        key = 1;
        Toast.makeText(getApplicationContext(), "press back Button again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                key = 0;
            }
        },2000);
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
//                    Toast.makeText(this,"Image selected ", Toast.LENGTH_SHORT).show();
                    selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    imagefile = new File(picturePath);
                    if(selectedImage!=null){
                        try {

                            Log.d("imagefile",imagefile.toString());
                            compressim = new Compressor(getApplicationContext()).compressToFile(imagefile);
                            selectedImage=Uri.fromFile(compressim);
                            uploadImage(selectedImage);
                        }
                        catch (IOException e){
                            Log.d("Io error",e.toString());
                        }
                    }
                    //         actualImage = .from(this, imageReturnedIntent.getData());
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
        progressDialog.setMessage("Setting Image");
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
                Toast.makeText(ProfileActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                // finish();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("url",downloadUrl.toString());
                Toast.makeText(ProfileActivity.this, "Profile image added", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                //showing the uploaded image in ImageView using the download url
                Picasso.with(ProfileActivity.this).load(downloadUrl).into(profile_image);
                User user = User.getUser();
                user.setUser_image(downloadUrl.toString());
                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(ProfileActivity.this,user.getUser_id());
                firebaseFunctions.updateUser(user);
            }
        });


    }




}
