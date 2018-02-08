package com.example.elena.ourandroidapp.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    static ListView optionsListView;
    public static final Random RANDOM = new Random();
    public static final String BACKEND_ACTION_SUBSCRIBE = "SUBSCRIBE";
    static int generator=0;
    final static List<OptionHolder> options=new ArrayList<>();
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

        final NewOptionAdapter optionsArrayAdapter = new NewOptionAdapter(this, options);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String pollId = UUID.randomUUID().toString().replaceAll("-", "");
                /*
                int idx = options.size()-1;
                final EditText editView = (EditText) optionsListView.getChildAt(idx).findViewById(R.id.new_option_string);
                options.remove(idx);
                options.add(idx, editView.getText().toString());
                */

                EditText titleET = findViewById(R.id.new_title);
                //String title = titleET.getText();

                EditText questionET = findViewById(R.id.new_question);
                questionET.requestFocus(); //nessesary so last onFocusChangeListener for newoptionView is called
                if(titleET.getText().toString().equals("")||questionET.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "No empty fields allowed",
                            Toast.LENGTH_SHORT).show();
                }else {
                    CheckBox cb = findViewById(R.id.poll_not_anonymous);
                    Poll newPoll;
                    if (cb.isChecked()) {
                        newPoll = new PollNotAnonymous(titleET.getText().toString(), questionET.getText().toString(), pollId);
                    } else {
                        newPoll = new Poll(titleET.getText().toString(), questionET.getText().toString(), pollId);
                    }
boolean optionsCorrect = true;
                    for (OptionHolder option : options) {

                           newPoll.addOption(option.getText());
                           if(option.getText().equals("")){
                               optionsCorrect=false;
                           }
                    }
                    if(optionsCorrect){
                        HashMap<String, Poll> polls = GlobalContainer.getPolls();
                        Intent intent = new Intent(NewPollActivity.this, ChooseContactsActivity.class);
                        intent.putExtra("pollId", newPoll.getId());
                        Bundle b = new Bundle();
                        b.putSerializable("poll", newPoll);
                        intent.putExtras(b);
                        intent.putExtra("type", Integer.toString(newPoll.getType()));
                        startActivity(intent);
                    } else{
                        Toast.makeText(getApplicationContext(), "No empty fields allowed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();

             String str = "";
                 options.add(new OptionHolder(""));
                optionsArrayAdapter.notifyDataSetChanged();

                            }
                        });


        optionsListView = findViewById(R.id.new_options_list);
        optionsListView.setAdapter(optionsArrayAdapter);

        options.clear();
        options.add(new OptionHolder(""));



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                options.clear();
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class OptionHolder{
        String text;
        int id;
        public OptionHolder(String text){
            this.text=text;
            id=generator;
            generator++;

        }
        public String getText(){

            return text;
        }

        public void setText(String new_text){
            text=new_text;
        }
        public int getId(){
            return id;
        }

        public List<OptionHolder> returnOptions(){
            return options;
        }

    }

}