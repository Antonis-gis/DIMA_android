package com.example.elena.ourandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.model.Contact;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.model.PollNotAnonymous;
import com.example.elena.ourandroidapp.services.DatabaseService;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import com.example.elena.ourandroidapp.adapters.ContactArrayAdapter;

public class ChooseContactsActivity extends AppCompatActivity {
    ListView contactsListView;
    final List<Pair<Boolean, Contact>> contacts=new ArrayList<>(GlobalContainer.getContactsForAdapter());
    public static final Random RANDOM = new Random();
    public static final String BACKEND_ACTION_SUBSCRIBE = "SUBSCRIBE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("pollId");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* ********************************VERSION WITH SERVER *************************************
                String  SENDER_ID="656945745490";
                //String msgId = UUID.randomUUID().toString();;
                FirebaseMessaging fm = FirebaseMessaging.getInstance();

                Gson gson =new Gson();
                String serialized = gson.toJson(receipients);
                fm.send(new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId(Integer.toString(RANDOM.nextInt()))
                        .addData("action", BACKEND_ACTION_SUBSCRIBE)
                        .addData("poll_id", id)
                        .addData("recipient", serialized)
                        .build());

                        */
                ///also here we should save poll to repository and to globalContainer (maybe it is not enough)
                final ArrayList<String> receipients = new ArrayList<>();
                for (Pair<Boolean, Contact> ct : contacts){

                    if(ct.first){
                        receipients.add(ct.second.getPhoneNumber());
                    }
                }
                HashMap<String, Poll> polls2 = GlobalContainer.getPolls();
                Intent incomingIntent = getIntent();
                String what = incomingIntent.getStringExtra("type");
                Integer type = Integer.parseInt(incomingIntent.getStringExtra("type"));
                Poll p;
                if(type==1){
                    p = (Poll)incomingIntent.getSerializableExtra("poll");
                } else{
                    p = (PollNotAnonymous)incomingIntent.getSerializableExtra("poll");
                }
                DatabaseService mPollService = DatabaseService.getInstance();
                mPollService.writeNewPoll(p);

                for (String receipient: receipients){
                    mPollService.writeNewPollToUser(receipient, p.getId());
                }
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
                mPollService.writeNewPollToUser(mPhoneNumber, p.getId());

                Intent intent = new Intent(ChooseContactsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        contactsListView = findViewById(R.id.contactsList);


        final ContactArrayAdapter contactsArrayAdapter = new ContactArrayAdapter(this, contacts);
        contactsListView.setAdapter(contactsArrayAdapter);

        contactsListView.
                setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int i, long l) {
                        CheckBox cb = view.findViewById(R.id.checkBox);
                        if(cb.isChecked()){
                            cb.setChecked(false);
                            contacts.set(i, new Pair(false, contacts.get(i).second));
                        }else{
                            cb.setChecked(true);
                            contacts.set(i, new Pair(true, contacts.get(i).second));

                        }
                        //String phoneNumber = contacts.get(i).second.getPhoneNumber();
                        //Boolean newBool = !contacts.get(i).first;
                        //contacts.set(i, new Pair(newBool, contacts.get(i).second));
                    }
                });



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
