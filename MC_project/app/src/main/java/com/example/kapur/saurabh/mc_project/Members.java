package com.example.kapur.saurabh.mc_project;

/**
 * Created by souravghai on 28/4/18.
 */

public class Members {

    private String member_club_name;
    private String member_name;
    private String member_email;
    private String member_id;

    public Members()
    {

    }


    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public void setMember_email(String member_email) {
        this.member_email = member_email;
    }

    public void setMember_club_name(String member_club_name) {
        this.member_club_name = member_club_name;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_club_name() {

        return member_club_name;
    }

    public String getMember_id() {
        return member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public String getMember_email() {
        return member_email;
    }
}
