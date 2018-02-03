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
import android.widget.Button;
import android.widget.ListView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.adapters.NewOptionAdapter;
import com.example.elena.ourandroidapp.adapters.PollArrayAdapter;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewPollActivity extends AppCompatActivity {
    ListView optionsListView;
    public static final Random RANDOM = new Random();
    public static final String BACKEND_ACTION_SUBSCRIBE = "SUBSCRIBE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ArrayList<String> receipients = new ArrayList<>();
        //receipients.add("9876543210");
        //receipients.add("1234567890");
        Poll newPoll = new Poll();
        final String newPollId = newPoll.getId();
        final List<String> options=new ArrayList<>();
        final NewOptionAdapter optionsArrayAdapter = new NewOptionAdapter(this, options);
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
        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
             String str = "";
             options.add(str);
             optionsArrayAdapter.notifyDataSetChanged();

            }
        });

        String str ="whatever";
        options.add(str);
        optionsListView = findViewById(R.id.new_options_list);
        optionsListView.setAdapter(optionsArrayAdapter);

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