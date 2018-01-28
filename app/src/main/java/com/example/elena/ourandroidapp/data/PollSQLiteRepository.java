package com.example.elena.ourandroidapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.elena.ourandroidapp.model.Contact;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.model.Poll.*;
import com.example.elena.ourandroidapp.model.PollNotAnonymous;
import com.example.elena.ourandroidapp.model.PollNotAnonymous.*;
import com.example.elena.ourandroidapp.services.GlobalContainer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.example.elena.ourandroidapp.data.PollDBContract.getWritableDatabase;
import static com.example.elena.ourandroidapp.data.PollDBContract.*;

/**
 * Created by elena on 08/12/17.
 */

public class PollSQLiteRepository {
    private SQLiteDatabase db;

    public PollSQLiteRepository(Context context){
        db = getWritableDatabase(context);
    }

    public void truncateAll4Tables(){
        db.delete(VoteEntry.TABLE_NAME, null, null);
        db.delete(OptionEntry.TABLE_NAME, null, null);
        db.delete(PollEntry.TABLE_NAME, null, null);
        db.delete(ParticipantEntry.TABLE_NAME, null, null);
        db.delete(ContactEntry.TABLE_NAME, null, null);
    }

    public void dropAll4Tables(){
        String sql = "DROP TABLE IF EXISTS " + VoteEntry.TABLE_NAME;
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS " + OptionEntry.TABLE_NAME;
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS " + PollEntry.TABLE_NAME;
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS " + ParticipantEntry.TABLE_NAME;
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME;
        db.execSQL(sql);
    }

    public void add(Poll poll){
        /*
        db.execSQL("INSERT OR REPLACE INTO "+ PollEntry.TABLE_NAME+" ("+
                        PollEntry._ID+", "+
                        PollEntry.POLL_NAME_CLMN+ ", "+ PollEntry.POLL_QUESTION_CLMN+ ")"+
                        "VALUES(?"+", "+"?"+", "+"?)",
                new Object[]{poll.getOptionId(), poll.getTitle(), poll.getQuestion()});
*/

        ContentValues poll_values = new ContentValues();
        poll_values.put(PollEntry._ID, poll.getId());
        poll_values.put(PollEntry.POLL_NAME_CLMN, poll.getTitle());
        poll_values.put(PollEntry.POLL_QUESTION_CLMN, poll.getQuestion());
        poll_values.put(PollEntry.POLL_CHANGED_CLMN, poll.getChanged());
        poll_values.put(PollEntry.POLL_ALREADY_VOTED_CLMN, poll.checkIfVoted());
        if (poll instanceof PollNotAnonymous) {
            poll_values.put(PollEntry.POLL_ANON_CLMN, 0);
        } else {poll_values.put(PollEntry.POLL_ANON_CLMN, 1);}
        db.insert(PollEntry.TABLE_NAME, null, poll_values);

        for (Option opt : poll.getOptions().values()) {
            /*db.execSQL("INSERT OR REPLACE INTO " + OptionEntry.TABLE_NAME + " ("  +
                            OptionEntry.OPTION_TEXT_CLMN + ", " +
                            OptionEntry.POLL_ID_CLMN + ", " +
                            OptionEntry.VOTES_COUNT_CLMN + ") " +
                            "VALUES(?" + "," + "?" + "," + "?)",
                    new Object[]{opt.getText(), poll.getOptionId(), opt.getVotesCount()});
                    */
            ContentValues option_values = new ContentValues();
            option_values.put(OptionEntry.OPTION_TEXT_CLMN, opt.getText());
            option_values.put(OptionEntry.POLL_ID_CLMN, poll.getId());
            option_values.put(OptionEntry.VOTES_COUNT_CLMN, opt.getVotesCount());
            db.insert(OptionEntry.TABLE_NAME, null, option_values);
            if(opt instanceof PollNotAnonymous.OptionNotAnonymous) {
                Cursor cursor=getOptionCursor(poll.getId(), opt.getText());
                int idx =cursor.getColumnIndex(OptionEntry._ID);
                cursor.moveToFirst();
                String option_id = cursor.getString( idx );
                ArrayList<String> voted = ((OptionNotAnonymous)opt).getVoted();
                for (String phoneofvoted : voted) {
                    db.execSQL("INSERT OR REPLACE INTO " + VoteEntry.TABLE_NAME + " (" +
                                    VoteEntry.OPTION_ID_CLMN + ", " +
                                    VoteEntry.PHONE_NUMBER_CLMN + ") " +
                                    "VALUES(?" + "," + "?)",
                            new Object[]{option_id, phoneofvoted});
                }
            }
        }

        for (String participant: poll.getParticipants()) {
            ContentValues participant_values = new ContentValues();
            participant_values.put(ParticipantEntry.PHONE_NUMBER_CLMN, participant);
            participant_values.put(ParticipantEntry.POLL_ID_CLMN, poll.getId());
            db.insert(ParticipantEntry.TABLE_NAME, null, participant_values);
        }
    }

    public void addContact(String name, String phoneNumber){
        ContentValues contact_values = new ContentValues();
        contact_values.put(ContactEntry.PHONE_NUMBER_CLMN, phoneNumber);
        contact_values.put(ContactEntry.CONTACT_NAME_CLMN, name);
        db.insert(ContactEntry.TABLE_NAME, null, contact_values);
    }
    public boolean deleteContact(String name) {
        return db.delete(ContactEntry.TABLE_NAME, ContactEntry.CONTACT_NAME_CLMN + "='" + name + "'", null) > 0;
    }
    public boolean deletePoll(String poll_id) {
        return db.delete(PollEntry.TABLE_NAME, PollEntry._ID + "='" + poll_id + "'", null) > 0;
    }
    private Cursor getOptionCursor(String poll_id, String option_text){
        /*String select = "Select _id from " + OptionEntry.TABLE_NAME + " Where " + OptionEntry.POLL_ID_CLMN + "=" + poll.getOptionId()
                + " AND " + OptionEntry.OPTION_TEXT_CLMN +  "=" + opt.getText()+"";*/
        Cursor cursor =
                db.query(OptionEntry.TABLE_NAME, // a. table
                        null, // b. column names
                        "poll_id =? and option_text=?", // c. selections
                        new String[] { poll_id, option_text }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        return cursor;
    }

    public void incrementVotedCount(String poll_id, String option_text){
        Cursor cursor=getOptionCursor(poll_id, option_text);
        if (cursor !=null) { cursor.moveToFirst(); }
        int votedCount = cursor.getInt(cursor.getColumnIndex(OptionEntry.VOTES_COUNT_CLMN));
        int votedCountInc = ++ votedCount;
        ContentValues values = new ContentValues();
        values.put(OptionEntry.VOTES_COUNT_CLMN, votedCountInc);

        db.update(OptionEntry.TABLE_NAME, values, "poll_id =? and option_text=?", new String[] { poll_id, option_text } );
    }

    public void addVoted(String poll_id, String option_text, String phoneofvoted){
        Cursor cursor =
                db.query(OptionEntry.TABLE_NAME, // a. table
                        new String[] { OptionEntry._ID }, // b. column names
                        "poll_id =? and option_text=?", // c. selections
                        new String[] { poll_id, option_text }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        String option_id = cursor.getString( cursor.getColumnIndex(OptionEntry._ID) );
        ContentValues values = new ContentValues();
        values.put(VoteEntry.OPTION_ID_CLMN, option_id);
        values.put(VoteEntry.PHONE_NUMBER_CLMN, phoneofvoted);
        db.insert(VoteEntry.TABLE_NAME, null, values);


    }
/*
    public PollCursor findById(String id){
        String query = "SELECT * FROM "+ PollEntry.TABLE_NAME
                + " WHERE "+ PollEntry._ID +" = ?";

        Cursor cursor = db.rawQuery(query, new String[]{id});

        return new PollCursor(cursor);
    }
*/
    public PollCursor findAll(){

       Cursor cursor = db.rawQuery("SELECT poll._id AS poll_id, poll_name AS poll_name, option.option_text AS option_text, option.votes_count AS votes_count, " +
                       "phone_number AS phone_number FROM " + PollEntry.TABLE_NAME + " LEFT JOIN " +  OptionEntry.TABLE_NAME+ " ON poll._id=option.poll_id LEFT JOIN  " +
                       VoteEntry.TABLE_NAME + " ON option._id=vote.option_id ORDER BY  poll._id ASC, option_text",
                null);

        return new PollCursor(cursor);
    }
    public PollCursor findPolls(){

        Cursor cursor = db.rawQuery("SELECT * FROM " + PollEntry.TABLE_NAME,
                null);

        return new PollCursor(cursor);
    }

    public OptionCursor findOptions(String pollid){

        Cursor cursor = db.rawQuery("SELECT * FROM " +  OptionEntry.TABLE_NAME+ " WHERE poll_id=" + pollid,
                null);
        return new OptionCursor(cursor);
    }

    public ParticipantCursor findParticipants(String pollid){

        Cursor cursor = db.rawQuery("SELECT * FROM " +  ParticipantEntry.TABLE_NAME+ " WHERE poll_id=" + pollid,
                null);
        return new ParticipantCursor(cursor);
    }

    public VoteCursor findVotes(String pollid, String optionid){
        Cursor cursor = db.rawQuery("SELECT phone_number AS phone_number FROM " + OptionEntry.TABLE_NAME+ " LEFT JOIN  " +
                        VoteEntry.TABLE_NAME + " ON option._id=vote.option_id WHERE option.poll_id=" + pollid +" AND vote.option_id=" + optionid,
                null);

        return new VoteCursor(cursor);
    }

    public ContactCursor findContacts() {

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContactEntry.TABLE_NAME,
                null);

        return new ContactCursor(cursor);
    }

    public HashMap<String, Contact> returnContacts() {
        HashMap<String, Contact> contacts = new HashMap<>();
        ContactCursor cc = findContacts();
        while (cc.moveToNext()) {
            Contact c = new Contact(cc.getName(), cc.getNumber());
            String name = cc.getName();
            contacts.put(name, c);
        }
        return contacts;
    }

    public HashMap<String, Poll> returnPolls() {
        HashMap<String, Poll> polls =new HashMap<>();

        PollCursor pc = findPolls();
        while (pc.moveToNext()) {
            int anon = pc.getIfAnon();
            Poll p;
            if(anon==1){
            p = new Poll(pc.getName(), pc.getQuestion(), pc.getId());
            } else {p = new PollNotAnonymous(pc.getName(), pc.getQuestion(), pc.getId());}
            OptionCursor oc = findOptions(pc.getId());
            p.setChanged(pc.getIfChanged());
            if(pc.getIfAlreadyVoted()==1){
                p.setVoted();
            }
            while (oc.moveToNext()) {
                Option o;
                if(anon==1){
                    o = new Option(oc.getText(), oc.getVotesCount());
                } else {
                    o = new OptionNotAnonymous(oc.getText(), 0);
                    VoteCursor vc = findVotes(pc.getId(), oc.getId());
                    while (vc.moveToNext()) {
                        ((OptionNotAnonymous)o).addVoted(vc.getNumber());
                        //String text = vc.getOptionText();
                                //String pol = vc.getPollId();
                    }
                    }
                   p.addOption(o);
                }
                ParticipantCursor partc = findParticipants(pc.getId());
            while (partc.moveToNext()) {
            p.addParticipant(partc.getNumber());
            }
                polls.put(p.getId(), p);
            }

return polls;
        }

    }




