package com.example.kapur.saurabh.mc_project;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UsercardViewAdapter extends  RecyclerView.Adapter<UsercardViewAdapter.ViewHolder>
{
    private ArrayList<Members> listItems;


    public UsercardViewAdapter(ArrayList<Members> listItem)
    {
        this.listItems=listItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_card,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final Members listItem=listItems.get(position);
        holder.name_of_member.setText(listItem.getMember_name());
        holder.email_of_member.setText(listItem.getMember_email());
    }

    @Override
    public int getItemCount()
    {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name_of_member;
        public TextView email_of_member;
        public ViewHolder(View itemView) {
            super(itemView);
            name_of_member=(TextView)itemView.findViewById(R.id.member_name);
            email_of_member = (TextView)itemView.findViewById(R.id.member_email);
        }
    }


}
