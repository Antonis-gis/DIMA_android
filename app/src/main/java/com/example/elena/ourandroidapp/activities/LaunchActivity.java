package com.example.elena.ourandroidapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.elena.ourandroidapp.services.DatabaseService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LaunchActivity extends AppCompatActivity {
    private static final String TAG = "LaunchActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.empty);
        //this part should be changed later
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseService mTokenService = DatabaseService.getInstance();
        mTokenService.writeTokenData("1234567890",refreshedToken);
        //DatabaseReference myRef = database.getReference("message");

        //myRef.setValue("Hello, World!");

        // determine where to go
        if(true) {
            Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        } else {
            Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }
    }

