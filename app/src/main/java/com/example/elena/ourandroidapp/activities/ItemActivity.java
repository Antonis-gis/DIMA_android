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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.elena.ourandroidapp.ApplicationContextProvider;
import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.adapters.NewOptionAdapter;
import com.example.elena.ourandroidapp.adapters.Option_Adapter;
import com.example.elena.ourandroidapp.data.PollSQLiteRepository;
import com.example.elena.ourandroidapp.model.Binding;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.model.PollNotAnonymous;
import com.example.elena.ourandroidapp.services.DatabaseService;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemActivity extends AppCompatActivity {
    Binding b;
    ListView optionsListView;

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
        final Poll poll = GlobalContainer.getPolls().get(id);
        if(poll.checkIfVoted()==1){
            TextView alreadyVotedText = findViewById(R.id.already_voted_string);
            alreadyVotedText.setVisibility(TextView.VISIBLE);

        }
        final ArrayList<Poll.Option> options = new ArrayList<>(poll.getOptions().values());
        int numberOfParticipants = poll.getParticipants().size();
        final Option_Adapter optionsArrayAdapter = new Option_Adapter(this, options, numberOfParticipants);
        optionsListView = findViewById(R.id.options_list);
        optionsListView.setAdapter(optionsArrayAdapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPollService.sendVote(id, GlobalContainer.getPolls().get(id).getOptions().get("try1"));
                System.err.println(GlobalContainer.getPolls().get(id).checkIfVoted());

            }
        });
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
                                HashMap<String, Poll> polls3 = GlobalContainer.getPolls();
                                optionsArrayAdapter.notifyDataSetChanged();
                            }
                        });
                        }
                }
                optionsListView.removeOnLayoutChangeListener(this);
            }
        });



        DatabaseService.Callback callback = new DatabaseService.Callback() {
            @Override
            public void onLoad(String poll_id) {

                //here the update of the view in case of new vote should happen
            }

            @Override
            public void onFailure() {

            }
        };


        TextView titleView = findViewById(R.id.title);
        titleView.setText(poll.getTitle());
        TextView questionView = findViewById(R.id.question);
        questionView.setText(poll.getQuestion());

        b = mPollService.getRefWithListener(GlobalContainer.getPolls().get(id), callback);
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


        }







