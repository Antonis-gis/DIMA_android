package com.example.elena.ourandroidapp;

/**
 * Created by elena on 23/01/18.
 */

import android.app.Application;

import com.example.elena.ourandroidapp.model.Poll;

import java.util.ArrayList;

public class MyCustomApplication extends Application {
    public ArrayList<Poll> globalArray = null;

    public MyCustomApplication() {
        super();
        globalArray = new ArrayList<>();

    }

        }