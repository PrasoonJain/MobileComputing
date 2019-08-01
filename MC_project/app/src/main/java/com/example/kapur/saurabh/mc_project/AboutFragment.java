package com.example.kapur.saurabh.mc_project;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */

public class AboutFragment extends Fragment {

    private TextView Description;
    private Context context;
    private ClubPage clubPage;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        clubPage = (ClubPage) getActivity();
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        Description = view.findViewById(R.id.club_description);
        Description.setText(clubPage.getClubDescription());

        return view;

    }

}
