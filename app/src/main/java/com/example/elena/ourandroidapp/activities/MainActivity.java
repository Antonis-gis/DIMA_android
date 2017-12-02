package com.example.elena.ourandroidapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.adapters.PollArrayAdapter;
import com.example.elena.ourandroidapp.model.Poll;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final Random RANDOM = new Random();
    public static final String FCM_PROJECT_SENDER_ID = "656945745490";
    public static final String FCM_SERVER_CONNECTION = "@gcm.googleapis.com";
    public static final String BACKEND_ACTION_MESSAGE = "MESSAGE";
    public static final String BACKEND_ACTION_ECHO = "ECHO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_coordinator_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton)
                findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  SENDER_ID="656945745490";
                //String msgId = UUID.randomUUID().toString();;
                FirebaseMessaging fm = FirebaseMessaging.getInstance();
                fm.send(new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId(Integer.toString(RANDOM.nextInt()))
                        .addData("action", BACKEND_ACTION_MESSAGE)
                        .addData("message","ping")
                        .addData("recipient","9876543210")
                        .build());

            }
        });

        final List<Poll> polls = Poll.getPolls();

        ListView pollsListView = findViewById(R.id.pollsList);


        PollArrayAdapter moviesArrayAdapter = new PollArrayAdapter(this, polls);
        pollsListView.setAdapter(moviesArrayAdapter);

        pollsListView.
            setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView,
                View view, int i, long l) {

            String pollTitle = polls.get(i).getTitle();
            Intent intent = new Intent(MainActivity.this, ItemActivity.class);
            intent.putExtra("title", pollTitle);
            startActivity(intent);

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("MyData")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EditText edit =(EditText) findViewById(R.id.editText4);
            edit.setText(intent.getExtras().getString("message"));
        }
    };

}

