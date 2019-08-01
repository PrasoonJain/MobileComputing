package com.example.kapur.saurabh.mc_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Created by souravghai on 14/2/18.
 */

public class ClubsCatView {

    private String name;
    private Integer image;

    public ClubsCatView() {

    }

    public ClubsCatView(String name,Integer image) {
        this.name = name;
        this.image = image;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public Integer getImage() {
        return image;
    }
}
