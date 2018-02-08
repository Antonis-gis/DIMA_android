package com.example.elena.ourandroidapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.elena.ourandroidapp.ApplicationContextProvider;
import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.adapters.PollArrayAdapter;
import com.example.elena.ourandroidapp.data.PollSQLiteRepository;
import com.example.elena.ourandroidapp.model.Binding;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.services.DatabaseService;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
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
    HashMap<String, Binding> bindings=new HashMap<>(); //since we need to find the poll it regerences when we remove it
    final List<Poll> polls=new ArrayList<>(GlobalContainer.getPolls().values());
    Boolean init = true;
    PollArrayAdapter pollsArrayAdapterForCallback;
    final DatabaseService.Callback callback = new DatabaseService.Callback() {//we need somehow find right poll and update its view
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
            pollsArrayAdapterForCallback.notifyDataSetChanged();
        }

        @Override
        public void onFailure() {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Polls");
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

// Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        final PollArrayAdapter pollsArrayAdapter = new PollArrayAdapter(this, polls);
        pollsArrayAdapterForCallback=pollsArrayAdapter;

        if (extras != null) {
            if (extras.containsKey("isNewLogin")) {
                boolean isNew = extras.getBoolean("isNewLogin", false);
                DatabaseService mIdsService = DatabaseService.getInstance();
                DatabaseService.Callback idsCallback = new DatabaseService.Callback() {
                    public void onLoad(String poll_id) {
/*
                        for (Poll p : GlobalContainer.getPolls().values()) //if we are here then before polls were empty, initialized woth empty gobalContainer
                            polls.add(p);
*/
                        DatabaseService mInitService = DatabaseService.getInstance();
                        for (Poll p : GlobalContainer.getPolls().values()){
                            Binding b = mInitService.getRefWithListener(p, callback, false);
                            bindings.put(p.getId(),b);
                            //bindings.add(b);
                        }
                        GlobalContainer.emptyRefs();

                        ((BaseAdapter) pollsListView.getAdapter()).notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure() {

                    }
                };
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
                //mIdsService.retrieveListOfUserPolls(mPhoneNumber, idsCallback);

                // TODO: Do something with the value of isNew.
            }
        } else{
            clearBindings();
            DatabaseService mInitService = DatabaseService.getInstance();
            HashMap<String, Poll> polls3 = GlobalContainer.getPolls();
            for (Poll p : GlobalContainer.getPolls().values()){
                Binding b = mInitService.getRefWithListener(p, callback, false);
                //bindings.add(b);
                bindings.put(p.getId(), b);
            }
        }


        final ArrayList<String> receipients = new ArrayList<>();
        receipients.add("9876543210");
        receipients.add("1234567890");
        FloatingActionButton fab = (FloatingActionButton)
                findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearBindings();
                HashMap<String, Poll> polls = GlobalContainer.getPolls();
                Intent intent = new Intent(MainActivity.this, NewPollActivity.class);
                startActivity(intent);


            }
        });


//basically this activity should react to:
        //1. onChange from firebase database when new vote is added
        //2. message from server that new poll is added (see my firebase messaging service)

        pollsListView = findViewById(R.id.pollsList);

        pollsListView.setAdapter(pollsArrayAdapter);
        pollsListView.setLongClickable(true);
        pollsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                //Do your tasks here
                final String pollId = polls.get(position).getId();

                AlertDialog.Builder alert = new AlertDialog.Builder(
                        MainActivity.this);
                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure to delete poll");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do your work here
                        GlobalContainer.getPolls().remove(pollId);
                        PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
                        repository.deletePoll(pollId);
                        DatabaseService mPollService = DatabaseService.getInstance();
                        mPollService.deletePollForUser(pollId);
                        Binding b =bindings.get(pollId);
                        b.getRef().removeEventListener(b.getListener());
                        polls.clear();
                        polls.addAll(GlobalContainer.getPolls().values());
                        pollsArrayAdapter.notifyDataSetChanged();
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();

                return true;
            }
        });
        pollsListView.
            setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView,
                View view, int i, long l) {

            String pollId = polls.get(i).getId();
            if(polls.get(i).getChanged()==1){
                HashMap<String, Poll> polls3 = GlobalContainer.getPolls();
                polls.get(i).setChanged(0);

                GlobalContainer.getPolls().get(polls.get(i).getId()).setChanged(0);
                //PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
                //repository.deletePoll(polls.get(i).getId());
                //repository.add(GlobalContainer.getPolls().get(polls.get(i).getId()));
                pollsArrayAdapter.notifyDataSetChanged();

            }
            clearBindings();
            Intent intent = new Intent(MainActivity.this, ItemActivity.class);
            intent.putExtra("pollId", pollId);
            startActivity(intent);

            }
        });


    }

    protected void clearBindings(){
        for(Binding b : bindings.values()){
            b.getRef().removeEventListener(b.getListener());
        }
        bindings.clear();
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
                HashMap<String, Poll> polls2 = GlobalContainer.getPolls();

                //polls.add(GlobalContainer.getPolls().get(poll_id));
                polls.clear();
                polls.addAll(GlobalContainer.getPolls().values());
            DatabaseService mInitService = DatabaseService.getInstance();
            Binding b = mInitService.getRefWithListener(GlobalContainer.getPolls().get(poll_id), callback, false);
            bindings.put(poll_id,b);
                ((BaseAdapter) pollsListView.getAdapter()).notifyDataSetChanged();
                //EditText edit =(EditText) findViewById(R.id.editText4);
                //edit.setText(intent.getExtras().getString("message"));
                HashMap<String, Poll> polls3 = GlobalContainer.getPolls();
            }

    };



}

