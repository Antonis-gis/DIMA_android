package com.example.elena.ourandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.adapters.NewOptionAdapter;
import com.example.elena.ourandroidapp.adapters.PollArrayAdapter;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.model.PollNotAnonymous;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NewPollActivity extends AppCompatActivity {
    ListView optionsListView;
    public static final Random RANDOM = new Random();
    public static final String BACKEND_ACTION_SUBSCRIBE = "SUBSCRIBE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poll);
        HashMap<String, Poll> polls3 = GlobalContainer.getPolls();
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
                String pollId = UUID.randomUUID().toString().replaceAll("-", "");
                int idx = options.size()-1;
                final EditText editView = (EditText) optionsListView.getChildAt(idx).findViewById(R.id.new_option_string);
                options.remove(idx);
                options.add(idx, editView.getText().toString());
                EditText titleET = findViewById(R.id.new_title);
                EditText questionET = findViewById(R.id.new_question);
                Poll newPoll = new Poll(titleET.getText().toString(), questionET.getText().toString(), pollId);
                for(String option: options){
                    newPoll.addOption(option);
                }
                HashMap<String, Poll> polls = GlobalContainer.getPolls();
                Intent intent = new Intent(NewPollActivity.this,ChooseContactsActivity.class);
                intent.putExtra("pollId", newPoll.getId());
                Bundle b = new Bundle();
                b.putSerializable("poll", newPoll);
                intent.putExtras(b);
                intent.putExtra("type", Integer.toString(newPoll.getType()));
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

                        //optionsListView.invalidateViews();
                        optionsArrayAdapter.notifyDataSetChanged();
                        final int idx = options.size()-2;
                        final EditText editView = (EditText) optionsListView.getChildAt(idx).findViewById(R.id.new_option_string);
                        editView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean hasFocus) {
                                if (hasFocus) {

                                } else {

                                    options.remove(idx);
                                    options.add(idx, editView.getText().toString());
                                    optionsArrayAdapter.notifyDataSetChanged();

                                }
                            }
                        });

                        Button delBtn = (Button) optionsListView.getChildAt(idx).findViewById(R.id.delete_btn);
                        delBtn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                //do something
                                int idxlast = options.size()-1;
                                final EditText editViewLast = (EditText) optionsListView.getChildAt(idxlast).findViewById(R.id.new_option_string);

                                options.add(idxlast, editViewLast.getText().toString());
                                options.remove(options.size()-1); //or some other task
                                options.remove(idx);
                                optionsArrayAdapter.notifyDataSetChanged();


                            }
                        });

                    }
                });





        String str ="whatever";
        options.add(str);
        optionsListView = findViewById(R.id.new_options_list);
        optionsListView.setAdapter(optionsArrayAdapter);
        for (int i=0; i<optionsListView.getChildCount(); i++) {
            final int idx = i;
            final EditText editView = (EditText) optionsListView.getChildAt(i).findViewById(R.id.new_option_string);
            editView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {

                    } else {
                        options.remove(idx);
                        options.add(idx, editView.getText().toString());
                        optionsArrayAdapter.notifyDataSetChanged();

                    }
                }
            });
            Button delBtn = (Button) optionsListView.getChildAt(i).findViewById(R.id.delete_btn);
            delBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    options.remove(idx); //or some other task
                    optionsArrayAdapter.notifyDataSetChanged();
                }
            });

        }




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