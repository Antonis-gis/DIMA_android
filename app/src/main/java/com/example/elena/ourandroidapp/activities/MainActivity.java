package com.example.elena.ourandroidapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.adapters.PollArrayAdapter;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.services.DatabaseService;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    public static final Random RANDOM = new Random();
    public static final String FCM_PROJECT_SENDER_ID = "656945745490";
    public static final String FCM_SERVER_CONNECTION = "@gcm.googleapis.com";
    public static final String BACKEND_ACTION_MESSAGE = "MESSAGE";
    public static final String BACKEND_ACTION_ECHO = "ECHO";
    public static final String BACKEND_ACTION_SUBSCRIBE = "SUBSCRIBE";

    ListView pollsListView;
    final List<Poll> polls=new ArrayList<>(GlobalContainer.getPolls().values());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Polls");
        setSupportActionBar(toolbar);
        final ArrayList<String> receipients = new ArrayList<>();
        receipients.add("9876543210");
        receipients.add("1234567890");
        FloatingActionButton fab = (FloatingActionButton)
                findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

/*
                String  SENDER_ID="656945745490";
                //String msgId = UUID.randomUUID().toString();;
                FirebaseMessaging fm = FirebaseMessaging.getPolls();
                ArrayList<String> receipients = new ArrayList<>();
                receipients.add("9876543210");
                receipients.add("1234567890");
                Gson gson =new Gson();
                String serialized = gson.toJson(receipients);
                fm.send(new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId(Integer.toString(RANDOM.nextInt()))
                        .addData("action", BACKEND_ACTION_SUBSCRIBE)
                        .addData("message","ping")
                        .addData("recipient", serialized)
                        .build());
*/
                //PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
                //repository.deletePoll("6");

                Intent intent = new Intent(MainActivity.this, NewPollActivity.class);
                startActivity(intent);


            }
        });


//basically this activity should react to:
        //1. onChange from firebase database when new vote is added
        //2. message from server that new poll is added (see my firebase messaging service)
        DatabaseService mPollService = DatabaseService.getInstance();
        final PollArrayAdapter pollsArrayAdapter = new PollArrayAdapter(this, polls);
        DatabaseService.Callback callback = new DatabaseService.Callback() {//we need somehow find right poll and update its view
            @Override
            public void onLoad(String poll_id) {

                for(Poll p:polls){
                    if(p.getId().equals(poll_id)){
                        int idx = polls.indexOf(p);
                        p=GlobalContainer.getPolls().get(poll_id);
                        polls.set(idx, p);

                    }
                }
                //here the notifications on poll items should be set
                pollsArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure() {

            }
        };


        //ArrayList<DatabaseReference> dRefs = mPollService.getRefsWithSharedCallbackOnAllLoaded(callback); //this is just to save references with attached eventListeners
        ArrayList<DatabaseReference> refs =new ArrayList<>();
        for (Poll p : GlobalContainer.getPolls().values()){
        DatabaseReference ref = mPollService.getRefWithListener(p, callback);
        refs.add(ref);
        }

        //mPollService.linkAllRefsForGlobalContainer(callback);//this is for globalContainer updates


        pollsListView = findViewById(R.id.pollsList);



        pollsListView.setAdapter(pollsArrayAdapter);

        pollsListView.
            setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView,
                View view, int i, long l) {

            String pollId = polls.get(i).getId();
            if(polls.get(i).getChanged()==1){
                polls.get(i).setChanged(0);
                pollsArrayAdapter.notifyDataSetChanged();

            }
            Intent intent = new Intent(MainActivity.this, ItemActivity.class);
            intent.putExtra("pollId", pollId);
            startActivity(intent);

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("NewPollReceived")
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
            //here we should update the ListView
            //TODO
            //see if it works
            String poll_id = intent.getExtras().getString("poll_id");
            GlobalContainer.getPolls().get(poll_id).setChanged(1);

                    polls.add(GlobalContainer.getPolls().get(poll_id));



            ((BaseAdapter) pollsListView.getAdapter()).notifyDataSetChanged();
            //EditText edit =(EditText) findViewById(R.id.editText4);
            //edit.setText(intent.getExtras().getString("message"));
        }
    };



}

