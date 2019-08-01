package com.example.kapur.saurabh.mc_project;

/**
 * Created by saurabhkapur on 27/02/18.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prasoon on 27/2/18.
 */

public class HomeRecommendedActivity extends Fragment {

    private EventListAdapter eventListAdapter;
    private List<Event> eventList = new ArrayList<>();
    private ShimmerRecyclerView recyclerView;
    private View view;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Context context;

    com.github.clans.fab.FloatingActionButton add_event;
    com.github.clans.fab.FloatingActionButton add_club;
    com.github.clans.fab.FloatingActionButton add_cord;
    com.github.clans.fab.FloatingActionButton pendingreq;
    com.github.clans.fab.FloatingActionButton addMember;
    com.github.clans.fab.FloatingActionButton deleteCoordinator;
    com.github.clans.fab.FloatingActionButton deleteMember;


    private FloatingActionMenu options;

    @Override
    public void onStart() {

        super.onStart();
        update_event_list();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_upcoming_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();
        options = (FloatingActionMenu) view.findViewById(R.id.Admin_options);
        add_event = view.findViewById(R.id.Add_event);
        add_club = view.findViewById(R.id.Add_club);
        add_cord = view.findViewById(R.id.Add_coordinator);
        pendingreq = view.findViewById(R.id.Pending_req);
        addMember = view.findViewById(R.id.Add_member);
        deleteCoordinator = view.findViewById(R.id.Delete_coordinator);
        deleteMember = view.findViewById(R.id.Delete_member);

        //setting up firebase
        settingUpFirebase();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null)
                    return;
                String userId = mAuth.getCurrentUser().getUid();
                FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context, userId);
                firebaseFunctions.getUserProfile(dataSnapshot, userId);
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        settingUpFirebase();

        recyclerView = (ShimmerRecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.showShimmerAdapter();

        eventList = new ArrayList<Event>();
        eventListAdapter = new EventListAdapter(eventList, getContext());
        recyclerView.setAdapter(eventListAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        update_event_list();

    }

    private void updateUI() {

        final User cur_user = User.getUser();
        Log.d("username ", cur_user.getUser_name());
        if (cur_user.getRole() != null && cur_user.getRole().equals("Admin")) {


            options.setVisibility(View.VISIBLE);
            addMember.setVisibility(View.GONE);
            deleteMember.setVisibility(View.GONE);
//            add_event.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), CreateEvent.class);
//                    startActivity(intent);
//
//                    //TODO something when floating action menu first item clicked
//
//                }
//            });
            add_event.setVisibility(View.GONE);
//            add_event.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), CreateEvent.class);
//                    startActivity(intent);
//
//                    //TODO something when floating action menu first item clicked
//
//                }
//            });

            add_club.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //TODO something when floating action menu second item clicked
                    Intent intent = new Intent(getActivity(), ClubForm.class);
                    startActivity(intent);

                }
            });
            add_cord.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //TODO something when floating action menu third item clicked
                    Intent intent = new Intent(getActivity(), AddCoordinator.class);
                    startActivity(intent);

                }
            });
            pendingreq.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //TODO something when floating action menu third item clicked
                    Intent intent = new Intent(getActivity(), PendingRequestActivity.class);
                    startActivity(intent);

                }
            });
            deleteCoordinator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteCoordinatorDialog();
                }
            });
        } else if (cur_user.getRole() != null && cur_user.getRole().equals("Coordinator")) {

            options.setVisibility(View.VISIBLE);
            add_club.setVisibility(View.GONE);
            add_cord.setVisibility(View.GONE);
            pendingreq.setVisibility(View.GONE);
            deleteCoordinator.setVisibility(View.GONE);
            add_event.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CreateEvent.class);
                    startActivity(intent);

                    //TODO something when floating action menu first item clicked

                }
            });
            addMember.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MemberForm.class);
                    intent.putExtra("club_of_coordinator", cur_user.getClubname());
                    startActivity(intent);

                }
            });
            deleteMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteMemberDialog();
                }
            });

        }
    }

    private void showDeleteMemberDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_delete_coordinator, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.delete_coordinator_name);

        dialogBuilder.setTitle("Delete Member");
        dialogBuilder.setMessage("Enter Email Id below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String emailId = edt.getText().toString();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context, mAuth.getCurrentUser().getUid());
                        boolean bb = firebaseFunctions.Remove_member(dataSnapshot, emailId);
                        if (bb) {
                            Toast.makeText(context, "Member Removed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "There is no such member", Toast.LENGTH_SHORT).show();
                        }
                        databaseReference.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void showDeleteCoordinatorDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_delete_coordinator, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.delete_coordinator_name);

        dialogBuilder.setTitle("Delete Coordinator");
        dialogBuilder.setMessage("Enter Email Id below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String emailId = edt.getText().toString();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FirebaseFunctions firebaseFunctions = new FirebaseFunctions(context, mAuth.getCurrentUser().getUid());
                        boolean bb = firebaseFunctions.Remove_coordinator(dataSnapshot, emailId);
                        if (bb) {
                            Toast.makeText(context, "Coordinator Removed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "There is no such coordinator", Toast.LENGTH_SHORT).show();
                        }
                        databaseReference.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
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
                ArrayList<Event> recommendedEvents = firebaseFunctions.getRecommendedEvents(dataSnapshot);
                eventList.clear();
                eventList.addAll(recommendedEvents);

                eventListAdapter.notifyDataSetChanged();
                databaseReference.removeEventListener(this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

