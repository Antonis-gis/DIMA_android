package com.example.elena.ourandroidapp.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    ContactArrayAdapter contactsArrayAdapterforRefresh;
    //final List<Pair<Boolean, Contact>> contacts=new ArrayList<>(GlobalContainer.getContactsForAdapter());
    final List<BoolContactEntry> contacts=new ArrayList<>(GlobalContainer.getContactsForAdapter2());
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
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
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
                /*
                for (Pair<Boolean, Contact> ct : contacts){

                    if(ct.first){
                        receipients.add(ct.second.getPhoneNumber());
                    }
                }
                */
                for (BoolContactEntry ct : contacts){

                    if(ct.getChoosen()){
                        receipients.add(ct.getContact().getPhoneNumber());
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


                for (String receipient: receipients){
                    mPollService.writeNewPollToUser(receipient, p.getId());
                    p.addParticipant(receipient);
                }
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
                p.addParticipant(mPhoneNumber);
                mPollService.writeNewPoll(p);

                mPollService.writeNewPollToUser(mPhoneNumber, p.getId());

                Intent intent = new Intent(ChooseContactsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        contactsListView = findViewById(R.id.contactsList);


        final ContactArrayAdapter contactsArrayAdapter = new ContactArrayAdapter(this, contacts);
        contactsArrayAdapterforRefresh=contactsArrayAdapter;
        contactsListView.setAdapter(contactsArrayAdapter);

        contactsListView.
                setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int i, long l) {
                        /*
                        CheckBox cb = view.findViewById(R.id.checkBox);
                        if(cb.isChecked()){
                            cb.setChecked(false);
                            contacts.set(i, new Pair(false, contacts.get(i).second));
                        }else{
                            cb.setChecked(true);
                            contacts.set(i, new Pair(true, contacts.get(i).second));

                        }
                        */
/*
                            CheckBox cb = view.findViewById(R.id.checkBox);
                            if (cb.isChecked()) {
                                contacts.set(i, new Pair(true, contacts.get(i).second));
                            } else {
                                contacts.set(i, new Pair(false, contacts.get(i).second));
                            }
                        */


                        ImageView cb = view.findViewById(R.id.changedPic);
                            if(contacts.get(i).getChoosen()){
                                cb.setVisibility(View.GONE);
                                contacts.get(i).setChoosen(false);
                            }else{
                                contacts.get(i).setChoosen(true);
                                cb.setVisibility(View.VISIBLE);
                            }


                        //String phoneNumber = contacts.get(i).second.getPhoneNumber();
                        //Boolean newBool = !contacts.get(i).first;
                        //contacts.set(i, new Pair(newBool, contacts.get(i).second));
                    }
                });
//this was needed since i wanted it to be possible to check box by either clicking on box or clicking on name
/*
        contactsListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                for (int i=0; i<contactsListView.getChildCount(); i++) {
                    final int idx = i;
                    CheckBox cb = contactsListView.getChildAt(idx).findViewById(R.id.checkBox);
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {

                            if (isChecked) {
                                contacts.set(idx, new Pair(true, contacts.get(idx).second));
                            } else {
                                contacts.set(idx, new Pair(false, contacts.get(idx).second));
                            }
                        }
                    });

                }

                contactsListView.removeOnLayoutChangeListener(this);
            }
        });
*/



    }
    private void refresh() {
        DatabaseService mCheckService = DatabaseService.getInstance();
        HashMap<String, Contact> contactsToCheck=new HashMap<>();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Contact c = new Contact(name, phoneNumber);
            Contact value = contactsToCheck.get(name);
            if (value != null) {
                System.err.println(value.getName());
            }

            contactsToCheck.put(phoneNumber, c);

        }
        DatabaseService.ContactsCallback callback = new DatabaseService.ContactsCallback() {
            public void onLoad() {
                contacts.clear();
                contacts.addAll(new ArrayList<>(GlobalContainer.getContactsForAdapter2()));
                contactsArrayAdapterforRefresh.notifyDataSetChanged();
            }

            @Override
            public void onFailure() {

            }
        };
        mCheckService.getTheOnesInDatabase(contactsToCheck, callback);
        phones.close();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.refresh:
                refresh();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public static class BoolContactEntry{
        Boolean chosen;
        Contact contact;
        public BoolContactEntry(Pair<Boolean, Contact> bc){
            this.chosen=bc.first;
            this.contact=bc.second;
        }

        public BoolContactEntry(Boolean b , Contact c){
            this.chosen=b;
            this.contact=c;
        }
        public void  setChoosen(boolean b){
           this.chosen=b;
        }
        public boolean  getChoosen(){
            return chosen;
        }
        public Contact  getContact(){
            return contact;
        }
    }


}
