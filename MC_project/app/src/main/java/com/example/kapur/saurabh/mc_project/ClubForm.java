package com.example.kapur.saurabh.mc_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

public class ClubForm extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseStorage storage;
    StorageReference storageRef,imageRef;
    ProgressDialog progressDialog;
    UploadTask uploadTask;
    Uri downloadUrl;
    int width;
    int height;
    EditText editText;
    EditText about;
    File imagefile;
    File compressim;
    Spinner cat_spin;
    android.widget.Button button;
    String catname;
    Button club_image;
    private FirebaseFunctions firebaseFunctions;
    private  UploadImageStore uploadImageStore;
    private FirebaseDatabase mDatabase;
    private static final int SELECT_PHOTO = 100;
    Uri selectedImage;
    Uri url;
    private String category;
    private ArrayList<String> cat;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_form);

       // DisplayMetrics displayMetrics = new DisplayMetrics();
       // getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        firebaseFunctions=new FirebaseFunctions(this, FirebaseAuth.getInstance().getCurrentUser().getUid());

       // width = (int) (displayMetrics.widthPixels * 0.8);
        //height = (int) (displayMetrics.heightPixels * 0.8);

        //getWindow().setLayout(width, height);
        Intent in=getIntent();
        category=in.getStringExtra("category");
        editText = (EditText) findViewById(R.id.club_name_edit_text);
        about = (EditText) findViewById(R.id.club_about_edit_text);
        cat_spin = findViewById(R.id.cat_spinner);
        button = (Button) findViewById(R.id.club_create_button);
        club_image=(Button) findViewById(R.id.club_image_add_button);
        final TextView text = (TextView) findViewById(R.id.empty_warning_message);
        club_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        context = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clubName = editText.getText().toString();
                String aboutClub = about.getText().toString();
                if (cat_spin.getSelectedItemPosition() == 0) {
                    Toast.makeText(context, "Please Select a Category", Toast.LENGTH_SHORT).show();
                    return;
                }
                catname = cat.get(cat_spin.getSelectedItemPosition());
                if (clubName.matches("")) {
                    text.setVisibility(View.VISIBLE);
                } else {
                    if (selectedImage != null) {
                        try {
                            Log.d("imagefile", imagefile.toString());
                            compressim = new Compressor(getApplicationContext()).compressToFile(imagefile);
                            selectedImage = Uri.fromFile(compressim);
                            uploadImage(selectedImage);
                        } catch (IOException e) {
                            Log.d("Io error", e.toString());
                        }
                    }

                    else
                        Toast.makeText(context, "Please Select an image", Toast.LENGTH_SHORT).show();

                }
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setVisibility(View.INVISIBLE);
            }
        });

        cat_spin.setOnItemSelectedListener(this);
        cat = new ArrayList<>();
        cat.add("Select a Category");
        cat.add("cultural");
        cat.add("technical");
        cat.add("sports");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cat);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cat_spin.setAdapter(dataAdapter);
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
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    imagefile = new File(picturePath);
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
        progressDialog.setMessage("Creating Club");
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
                Toast.makeText(ClubForm.this, "Error in creating!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                // finish();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("url",downloadUrl.toString());
                Toast.makeText(ClubForm.this, "Club Successfully Created", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                firebaseFunctions.AddClubs(about.getText().toString(),catname,editText.getText().toString(),downloadUrl.toString());
                finish();
                //showing the uploaded image in ImageView using the download url
                //Picasso.with(context).load(downloadUrl).into(imageView);
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
