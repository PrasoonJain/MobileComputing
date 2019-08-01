package com.example.kapur.saurabh.mc_project;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by souravghai on 1/3/18.
 */

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.clubViewHolder> {

    ArrayList<Clubs> list;
    RecyclerViewActionListener listener;
    Context context;

    public ClubAdapter(ArrayList<Clubs> list,RecyclerViewActionListener listener,Context context) {
        this.list = list;
        this.listener = listener;
        this.context = context;
    }

    public class clubViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView imageView;
        Button button;

        public clubViewHolder(final View itemView,final RecyclerViewActionListener recyclerViewActionListener) {
            super(itemView);
            name = itemView.findViewById(R.id.club_name);
            button = itemView.findViewById(R.id.follow_button);
            imageView = itemView.findViewById(R.id.club_card_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewActionListener.onCatClick(itemView,getAdapterPosition(),list);
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewActionListener.toggleFollowButton(button,list,getAdapterPosition());
                }
            });
        }
    }

    @Override
    public ClubAdapter.clubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_clubs, parent, false);

        ClubAdapter.clubViewHolder vh = new ClubAdapter.clubViewHolder(view,listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ClubAdapter.clubViewHolder holder, int position) {
        Clubs club = list.get(position);

//        holder.imageView.setImageDrawable(club.getImageView());
//        Picasso.with(context).load(club.getClub_Image()).resize(200,200).into(holder.imageView);

        holder.name.setText(club.getName());
        Picasso.with(context).load(club.getClub_Image()).into(holder.imageView);

        listener.checkClubList(list.get(position).getClubId(),holder.button);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
