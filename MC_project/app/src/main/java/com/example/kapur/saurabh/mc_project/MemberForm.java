package com.example.kapur.saurabh.mc_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MemberForm extends AppCompatActivity {


    private EditText name;
    private EditText email;
    private TextView nameWarning;
    private TextView emailWarning;
    private TextView iiitdEmailWarning;
    private Button create;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String memberName;
    private String memberEmail;
    private String memberClub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_form);

        //setting up firebase
        settingUpFirebase();

        memberClub = getIntent().getStringExtra("club_of_coordinator");

        name = findViewById(R.id.member_name);
        email = findViewById(R.id.member_email);
        nameWarning = findViewById(R.id.name_warning_message);
        emailWarning = findViewById(R.id.email_warning_message);
        iiitdEmailWarning = findViewById(R.id.email_iiitd_warning_message);
        create = findViewById(R.id.create_button);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberName = name.getText().toString();
                memberEmail = email.getText().toString();
                Boolean b = true;
                if(memberName.equals(""))
                {
                    nameWarning.setVisibility(View.VISIBLE);
                    b = false;
                }
                if(memberEmail.equals(""))
                {
                    emailWarning.setVisibility(View.VISIBLE);
                    b = false;
                }
                if(!memberEmail.contains("@iiitd.ac.in"))
                {
                    iiitdEmailWarning.setVisibility(View.VISIBLE);
                    b = false;
                }

                if(b)
                {
                    Members members = new Members();
                    members.setMember_name(memberName);
                    members.setMember_email(memberEmail);
                    members.setMember_club_name(memberClub);
                    uploadMember(members);

                }

            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameWarning.setVisibility(View.GONE);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailWarning.setVisibility(View.GONE);
                iiitdEmailWarning.setVisibility(View.GONE);
            }
        });

    }

    private void settingUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void uploadMember(Members members) {
        FirebaseFunctions firebaseFunctions = new FirebaseFunctions(this,mAuth.getCurrentUser().getUid());
        firebaseFunctions.uploadMembers(members);

        Toast.makeText(this,"Member is added",Toast.LENGTH_SHORT).show();

        finish();
    }
}
