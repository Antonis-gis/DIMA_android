package com.example.elena.ourandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final DatabaseService mPollService = DatabaseService.getInstance();
        Intent intent = getIntent();
        final String id = intent.getStringExtra("pollId");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPollService.sendVote(id, GlobalContainer.getPolls().get(id).getOptions().get("try1"));
                System.err.println(GlobalContainer.getPolls().get(id).checkIfVoted());

            }
        });


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
        //p = GlobalContainer.getPolls().get(id);


        TextView pollTextView = findViewById(R.id.textView);
        pollTextView.setText(GlobalContainer.getPolls().get(id).getTitle());

        DatabaseReference ref = mPollService.getRefWithListener(GlobalContainer.getPolls().get(id), callback);
            }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


        }







