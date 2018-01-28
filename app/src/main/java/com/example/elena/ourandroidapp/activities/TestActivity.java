package com.example.elena.ourandroidapp.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.data.PollCursor;
import com.example.elena.ourandroidapp.data.PollDBContract;
import com.example.elena.ourandroidapp.data.PollSQLiteRepository;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.model.PollNotAnonymous;
import com.example.elena.ourandroidapp.services.DatabaseService;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PollSQLiteRepository repository = new PollSQLiteRepository(this);
        GlobalContainer.updatePolls(repository.returnPolls());
        HashMap<String, Poll> polls = GlobalContainer.getPolls();
        DatabaseService mPollService = DatabaseService.getInstance();
        DatabaseService.Callback callback = new DatabaseService.Callback() {
            @Override
            public void onLoad(String poll_id) {

                TextView titleTextView = findViewById(R.id.textView2);
                String before = titleTextView.getText().toString();
                titleTextView.setText(before + " loaded");
            }

            @Override
            public void onFailure() {

            }
        };
        //ArrayList<DatabaseReference> dRefs = mPollService.getRefsWithSharedCallbackOnAllLoaded(callback);





        String a = "aaa";
        mPollService.sendVote("124", new PollNotAnonymous.OptionNotAnonymous("try3"));
        String aa = "aaa";



        PollNotAnonymous trypoll= new PollNotAnonymous("TitleTry","Question.try", "124");
        trypoll.addOption("try1");
        trypoll.addOption("try3");
        trypoll.addVoted("try3", "12345678");
        trypoll.addParticipant("part1");
        trypoll.addParticipant("part2");


        //repository.truncateAll3Tables();////////////////////////////////////////////////REMOVE LATER
        //repository.dropAll3Tables();
        repository.add(trypoll);


        Poll trypoll2= new Poll("TitleTry2","Question.try2", "464787");
        trypoll.addOption("what");
        trypoll.addOption("the");
        trypoll.addOption("fuck?");
        trypoll2.incrementVotesCount("fuck?");
        trypoll2.incrementVotesCount("fuck?");
        trypoll2.incrementVotesCount("what");
        trypoll2.addParticipant("part21");
        trypoll2.addParticipant("part22");
        repository.add(trypoll2);


        trypoll.incrementVotesCount("try1");
        repository.incrementVotedCount(trypoll.getId(), "try1");
        List<Poll> polls2 =Poll.getPolls();

        for (Poll poll : polls2){
            repository.add(poll);
        }
        repository.deletePoll("2");

        PollCursor pc = repository.findAll();
        String title="";
        String options="";
        String result="";

        while (pc.moveToNext()) {
            String str =pc.getName();
            //String fuckingid =pc.getOptionId();
            title=str;
            String option = pc.getString(pc.getColumnIndex(PollDBContract.OptionEntry.OPTION_TEXT_CLMN));
            String vc = pc.getString(pc.getColumnIndex(PollDBContract.OptionEntry.VOTES_COUNT_CLMN));
            if(option!=null) {
                String voted = pc.getString(pc.getColumnIndex(PollDBContract.VoteEntry.PHONE_NUMBER_CLMN));
                title = title.concat(" option: ").concat(option).concat(" votes count: ").concat(vc);
                if (voted != null) {
                    title = title.concat(" voted: ").concat(voted);
                }
            }


result = result.concat(System.lineSeparator()).concat(title);
        }
        result= trypoll.serializeAndReturn();
        int weight = result.getBytes().length;

        TextView titleTextView = findViewById(R.id.textView2);
        final HashMap<String, Poll> pollsRecovered = repository.returnPolls();
        GlobalContainer.updatePolls(repository.returnPolls());
        titleTextView.setText(weight +"");


/*
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.put(name, phoneNumber);
            //Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();



        }
        phones.close();
        */
        HashMap<String, String> contacts=new HashMap<>();
String number = "+393278287327";
        contacts.put("Abdel","+393278287327");
        contacts.put("Somebody","642965321345");

        //mPollService.getTheOnesInDatabase(contacts, new DatabaseService.DefaultContactsCallback());

        contacts.put("Anna","123457543");
        contacts.put("Ashley","873696542");
        contacts.put("Beatriz","136898743");
        String aqqq = "aaa";
        //mPollService.linkRef(polls.get("6"), callback);
//Structure of Firebase message: action - based on server actions (should be at least newPoll and vote,
// if newPoll : message = Object, receipients - list of numbers for server to subscribe
// if vote : poll= poll_id (in firebase, this first message unique id), option =
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                String a = "aaa";
                final HashMap<String, Poll> polls2 = GlobalContainer.getPolls();
                HashMap<String, String> res = GlobalContainer.getContacts();
                String a2 = "aaa";
HashMap<String, DatabaseReference> refs = GlobalContainer.getRefs();
                DatabaseService mPollService = DatabaseService.getInstance();
                Poll poll = polls2.get("124");
                mPollService.writeNewPoll(polls2.get("124"));
                mPollService.writeNewPoll(polls2.get("3"));
                mPollService.writeNewPoll(polls2.get("464787"));
                mPollService.writeNewPoll(polls2.get("6"));

*/


            }
        });
    }




}
