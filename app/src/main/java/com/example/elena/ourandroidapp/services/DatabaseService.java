package com.example.elena.ourandroidapp.services;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by elena on 02/12/17.
 */

public class DatabaseService {
    private static DatabaseService instance;
    private DatabaseReference mDatabase;

    private DatabaseService(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized DatabaseService getInstance(){
        if (instance == null)
            instance = new DatabaseService();

        return instance;
    }

    public void writeTokenData(String userId, String token) {
        mDatabase.child("users").child(userId).setValue(token);

    }
}
