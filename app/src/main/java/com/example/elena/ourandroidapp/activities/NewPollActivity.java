package com.example.elena.ourandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.model.Poll;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

public class NewPollActivity extends AppCompatActivity {
    public static final Random RANDOM = new Random();
    public static final String BACKEND_ACTION_SUBSCRIBE = "SUBSCRIBE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ArrayList<String> receipients = new ArrayList<>();
        //receipients.add("9876543210");
        //receipients.add("1234567890");
        Poll newPoll = new Poll();
        final String newPollId = newPoll.getId();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                String  SENDER_ID="656945745490";
                //String msgId = UUID.randomUUID().toString();;
                FirebaseMessaging fm = FirebaseMessaging.getInstance();

                Gson gson =new Gson();
                String serialized = gson.toJson(receipients);
                fm.send(new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId(Integer.toString(RANDOM.nextInt()))
                        .addData("action", BACKEND_ACTION_SUBSCRIBE)
                        .addData("poll_id", newPollId)
                        .addData("recipient", serialized)
                        .build());
                        */
                Intent intent = new Intent(NewPollActivity.this,ChooseContactsActivity.class);
                intent.putExtra("pollId", "3");
                startActivity(intent);
            }
        });

    }

}
