package com.example.elena.ourandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.adapters.Option_Adapter;
import com.example.elena.ourandroidapp.model.Binding;
import com.example.elena.ourandroidapp.model.Contact;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.services.DatabaseService;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemActivity extends AppCompatActivity {
    Binding b;
    ListView optionsListView;
    String poll_id;
    final static ArrayList<Poll.Option> options = new ArrayList<>();
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
        poll_id=id;
        final Poll poll = GlobalContainer.getPolls().get(id);
        getSupportActionBar().setTitle(poll.getTitle());
        if(poll.checkIfVoted()==1){
            TextView alreadyVotedText = findViewById(R.id.already_voted_string);
            alreadyVotedText.setVisibility(TextView.VISIBLE);

        }
        //final ArrayList<Poll.Option> options = new ArrayList<>(poll.getOptions().values());
        options.addAll(poll.getOptions().values());
        int numberOfParticipants = poll.getParticipants().size();
        final Option_Adapter optionsArrayAdapter = new Option_Adapter(this, options, poll.getId());
        optionsListView = findViewById(R.id.options_list);
        optionsListView.setAdapter(optionsArrayAdapter);
        /*
        optionsListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                for (int i=0; i<optionsListView.getChildCount(); i++) {
                    final int idx = i;
                    Button voteBtn = (Button) optionsListView.getChildAt(i).findViewById(R.id.vote_btn);
                    if(poll.checkIfVoted()==1){
                        voteBtn.setVisibility(View.GONE);
                    } else {

                        voteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //do something
                                for (int y = 0; y < optionsListView.getChildCount(); y++) {
                                    optionsListView.getChildAt(y).findViewById(R.id.vote_btn).setVisibility(View.GONE);
                                }
                                String str = ((TextView) optionsListView.getChildAt(idx).findViewById(R.id.option_string)).getText().toString();

                                mPollService.sendVote(id, poll.getOptions().get(str));
                                TextView alreadyVotedText = findViewById(R.id.already_voted_string);
                                alreadyVotedText.setVisibility(TextView.VISIBLE);
                                poll.setVoted();
                                if(poll.getType()==1){
                                poll.incrementVotesCount(str);
                                } else {
                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                    String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
                                    ((PollNotAnonymous)poll).addVoted(str, mPhoneNumber);
                                }

                                GlobalContainer.getPolls().put(poll.getId(), poll);
                                PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
                                repository.deletePoll(poll.getId());
                                repository.add(poll);
                                optionsArrayAdapter.notifyDataSetChanged();
                            }
                        });
                        }
                    if(poll instanceof  PollNotAnonymous){
                        final Button showBtn = (Button) optionsListView.getChildAt(i).findViewById(R.id.show_vote_participants);
                        showBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                  TextView votedText = optionsListView.getChildAt(idx).findViewById(R.id.list_of_voted);
                                  if(votedText.getVisibility() == View.VISIBLE){
                                      votedText.setVisibility(View.GONE);
                                      showBtn.setText("Show voted");
                                  } else{
                                      votedText.setVisibility(View.VISIBLE);
                                      showBtn.setText("Hide voted");
                                  }
                            }
                        });
                                                       }
                    }

                //optionsListView.removeOnLayoutChangeListener(this);
            }
        });
        */



        DatabaseService.Callback callback = new DatabaseService.Callback() {
            @Override
            public void onLoad(String poll_id) {
                GlobalContainer.getPolls().get(poll_id).setChanged(0);
                options.clear();
                options.addAll(GlobalContainer.getPolls().get(poll_id).getOptions().values());
optionsArrayAdapter.notifyDataSetChanged();
                //here the update of the view in case of new vote should happen
            }

            @Override
            public void onFailure() {

            }
        };


        TextView questionView = findViewById(R.id.question);
        questionView.setText(poll.getQuestion());
        final Button showBtn = findViewById(R.id.hide_participants);
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView participantsViewh = findViewById(R.id.participants);
                if(participantsViewh.getVisibility()==View.VISIBLE) {
                    participantsViewh.setVisibility(View.INVISIBLE);
                    showBtn.setText("+ Participants");
                } else{
                    participantsViewh.setVisibility(View.VISIBLE);
                    showBtn.setText("- Participants");
                }
            }
        });

        TextView participantsView = findViewById(R.id.participants);
        String str ="";
        for (String participant : poll.getParticipants()){

                Contact c = GlobalContainer.getContacts().get(participant);
                HashMap<String, Contact> cc = GlobalContainer.getContacts();
                if(c!=null) {

                    str += GlobalContainer.getContacts().get(participant).getName() + ", ";

                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
                    mPhoneNumber=mPhoneNumber.replaceAll("\\s+","");
                    if(participant.equals(mPhoneNumber)){
                        str += "you, ";
                    }
                    else {
                        str += participant + ", ";
                    }
                }
            }
        if(str.length()>2) {
            str = str.substring(0, str.length() - 2);
        }
        participantsView.setText(str);

        b = mPollService.getRefWithListener(GlobalContainer.getPolls().get(id), callback, true);


            }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                b.getRef().removeEventListener(b.getListener());
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class optionsProvider{
        public ArrayList<Poll.Option> getOptions(){
            return options;
        }
    }


        }







