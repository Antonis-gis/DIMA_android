package com.example.elena.ourandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.services.DatabaseService;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getActionBar().setDisplayHomeAsUpEnabled(true);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        Intent intent = getIntent();
        String id = intent.getStringExtra("pollId");
        DatabaseService mPollService = DatabaseService.getInstance();
        DatabaseService.Callback callback = new DatabaseService.Callback() {
            @Override
            public void onLoad(String poll_id) {

                //here the update of the view in case of new vote should happen
            }

            @Override
            public void onFailure() {

            }
        };

        HashMap<String, Poll> polls = GlobalContainer.getPolls();
        GlobalContainer.getPolls().get(id).setChanged(0);
        Poll p = GlobalContainer.getPolls().get(id);


        TextView pollTextView = findViewById(R.id.textView);
        pollTextView.setText(p.getTitle());

        DatabaseReference ref = mPollService.getRefWithListener(p, callback);
            }
        }





