package com.example.kapur.saurabh.mc_project;

import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by souravghai on 4/3/18.
 */

public interface RecyclerViewActionListener {
    public void onCatClick(View view, int position, ArrayList<Clubs> list);

    void toggleFollowButton(Button button,ArrayList<Clubs> list,int position);

    void checkClubList(String clubId,Button button);
}
