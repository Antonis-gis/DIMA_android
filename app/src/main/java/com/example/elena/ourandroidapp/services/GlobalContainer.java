package com.example.elena.ourandroidapp.services;

import android.util.Pair;

import com.example.elena.ourandroidapp.activities.ChooseContactsActivity;
import com.example.elena.ourandroidapp.data.PollSQLiteRepository;
import com.example.elena.ourandroidapp.model.Contact;
import com.example.elena.ourandroidapp.model.Poll;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by elena on 23/01/18.
 */

public class GlobalContainer {
    static PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
    static HashMap<String, Poll> polls;
    static HashMap<String, DatabaseReference> pollRefs;
    static DatabaseReference selfRef;
    static HashMap<String, Contact> contacts;
    public static synchronized HashMap<String, Poll> getPolls(){
        if (polls == null)
            polls = repository.returnPolls();

        return polls;
    }

    public static synchronized void emptyPolls(){

        polls=new HashMap<>();
    }

    public static synchronized void emptyContacts(){

        contacts=new HashMap<>();
    }
    public static synchronized void emptyRefs(){
        pollRefs=new HashMap<>();
    }

    public static synchronized HashMap<String, Contact> getContacts(){
        if (contacts == null)
            contacts = repository.returnContacts();

        return contacts;
    }

    public static synchronized ArrayList<Pair<Boolean, Contact>> getContactsForAdapter(){
        ArrayList<Pair<Boolean, Contact>> pairs = new ArrayList<>();
        if (contacts == null)
            contacts = repository.returnContacts();
        for (Contact c : contacts.values()){
            Pair<Boolean, Contact> pair= new Pair<>(false, c);
            pairs.add(pair);
        }

        return pairs;
    }

    public static synchronized ArrayList<ChooseContactsActivity.BoolContactEntry> getContactsForAdapter2(){
        ArrayList<ChooseContactsActivity.BoolContactEntry> pairs = new ArrayList<>();
        if (contacts == null)
            contacts = repository.returnContacts();
        for (Contact c : contacts.values()){
            ChooseContactsActivity.BoolContactEntry bc= new ChooseContactsActivity.BoolContactEntry(false, c);
            pairs.add(bc);
        }

        return pairs;
    }

    public static synchronized HashMap<String, DatabaseReference> getRefs(){
        if (pollRefs == null)
            pollRefs = new HashMap<>();

        return pollRefs;
    }

    public static DatabaseReference getSelfRef(){
        if (selfRef == null){
            DatabaseService mDatabase = DatabaseService.getInstance();
            selfRef = mDatabase.getSelfRef();
        }

        return selfRef;
    }

    private GlobalContainer() {
        polls = repository.returnPolls();
        contacts = repository.returnContacts();
        pollRefs = new HashMap<>();
        DatabaseService mDatabase = DatabaseService.getInstance();
        selfRef = mDatabase.getSelfRef();
    }
    public static synchronized void updatePolls(HashMap<String, Poll> newpolls){
            polls = newpolls;

    }




}


