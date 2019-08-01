package com.example.kapur.saurabh.mc_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class UploadImageStore {
    FirebaseStorage storage;
    StorageReference storageRef,imageRef;
    ProgressDialog progressDialog;
    UploadTask uploadTask;
    Uri downloadUrl;
    public Uri uploadImage( Uri selectedImage) {
        storage = FirebaseStorage.getInstance();
        //creates a storage reference
        storageRef = storage.getReference();
        //create reference to images folder and assing a name to the file that will be uploaded
        imageRef = storageRef.child("images/" + selectedImage.getLastPathSegment());
        //creating and showing progress dialog

//        progressDialog = new ProgressDialog(context);
//        progressDialog.setMax(100);
//        progressDialog.setMessage("Uploading...");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.show();
//        progressDialog.setCancelable(false);
        //starting upload
        uploadTask = imageRef.putFile(selectedImage);
        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
            }
        });
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                //Toast.makeText(context, "Error in uploading!", Toast.LENGTH_SHORT).show();
               // progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                downloadUrl = taskSnapshot.getDownloadUrl();
              //  Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show();
               // progressDialog.dismiss();

                //showing the uploaded image in ImageView using the download url
                //Picasso.with(context).load(downloadUrl).into(imageView);
            }
        });
        return downloadUrl;

    }
}
