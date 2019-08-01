package com.example.kapur.saurabh.mc_project;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddCoordinator extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String clubname;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<String> clubnamesList;
    private Context context;
    private EditText email;
    private Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coordinator);

        final Spinner spinner = (Spinner) findViewById(R.id.club_name);
        email = findViewById(R.id.coor_email);
        create = findViewById(R.id.Create_coordinator);
        spinner.setOnItemSelectedListener(this);
        settingUpFirebase();
        FirebaseFunctions firebaseFunctions = new FirebaseFunctions(this, mAuth.getCurrentUser().getUid());
        clubnamesList = new ArrayList<>();
        context = this;
        clubnamesList.add("Select Club Name");
        update_event_list();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, clubnamesList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context, mAuth.getCurrentUser().getUid());
                        boolean added = false;
                        Log.d("added: ", String.valueOf(clubnamesList.get(spinner.getSelectedItemPosition()).equals("Select Club Name")));
                        Log.d("added: ", String.valueOf(email.getText()));
                        if (!clubnamesList.get(spinner.getSelectedItemPosition()).equals("Select Club Name")) {
                            added = firebaseFunctions.setCoordinator(dataSnapshot, String.valueOf(email.getText()), clubnamesList.get(spinner.getSelectedItemPosition()));

                        }
                        if (added) {
                            Toast.makeText(context, "Coordinator Added", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                        else {
                            Toast.makeText(context, "Coordinator Not Added Check Details", Toast.LENGTH_SHORT).show();
                        }
                        databaseReference.removeEventListener(this);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void update_event_list() {
        //mShimmerViewContainer.startShimmerAnimation();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context, mAuth.getCurrentUser().getUid());
                ArrayList<String> names = firebaseFunctions.getClubnames(dataSnapshot);
                clubnamesList.clear();
                clubnamesList.addAll(names);

//                eventListAdapter.notifyDataSetChanged();
                databaseReference.removeEventListener(this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //mShimmerViewContainer.stopShimmerAnimation();
    }
}
