package com.example.elena.ourandroidapp.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.elena.ourandroidapp.ApplicationContextProvider;
import com.example.elena.ourandroidapp.data.PollSQLiteRepository;
import com.example.elena.ourandroidapp.model.Binding;
import com.example.elena.ourandroidapp.model.Contact;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.model.PollNotAnonymous;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by elena on 02/12/17.
 */

public class DatabaseService {
    private static DatabaseService instance;
    private DatabaseReference mDatabase;

    private DatabaseService() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public static synchronized DatabaseService getInstance() {
        if (instance == null)
            instance = new DatabaseService();

        return instance;
    }

    public void writeTokenData(String userId, String token) {
        mDatabase.child("users").child(userId).child("token").setValue(token);

    }

    public void writeNewPollToUser(String userId, String pollId) {
        mDatabase.child("users").child(userId).child("poll_ids").child(pollId).setValue(0);

    }



    public void addPollIdToUsersPollsIdList(String poll_id) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
        DatabaseReference newRequestRef = mDatabase.child("users").child(mPhoneNumber).child("poll_ids").child(poll_id);
        newRequestRef.setValue(0);
    }



    public void writeNewPoll(Poll poll) {


        mDatabase.child("polls").child(poll.getId()).setValue(poll);
        addPollIdToUsersPollsIdList(poll.getId());
        //com.google.firebase.database.DatabaseException: Found a conflicting setters with name: setWallpaper (conflicts with setWallpaper defined on android.content.ContextWrapper)
        //means somehow not coherent structure of that you write and what firebase database expects.
        //If incountered remove items like the oes you want to load from firebase database
        //also connected to pollRef field of Poll
        //maybe I should do additional HashMap in GlobalContainer for links
        //other cause can be lack of getters and setters
    }
/*
    public void linkRef(Poll poll, final Callback callback) {
        String query = "polls/" + poll.getId();
        HashMap<String, Poll> pollsLocal = GlobalContainer.getPolls();
        DatabaseReference pollRef = FirebaseDatabase.getInstance().getReference(query);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String id = dataSnapshot.child("id").getValue(String.class);
                if(id==null){
                    //what will we do if pol is deleted from database - actually it shouldnt happen
                } else {
                    HashMap<String, Poll> polls = GlobalContainer.getPolls();
                    Poll poll = polls.get(id);

                    if (poll instanceof PollNotAnonymous) {
                        poll = dataSnapshot.getValue(PollNotAnonymous.class);
                    } else {
                        poll = dataSnapshot.getValue(Poll.class);
                    }
                    poll.setRef(FirebaseDatabase.getInstance().getReference("polls/" + poll.getId()));
                    polls.put(poll.getId(), poll);
                    PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
                    repository.deletePoll(poll.getId());
                    repository.add(poll);

                    callback.onLoad();
                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure();
            }

        };

        pollRef.addValueEventListener(postListener);
        GlobalContainer.getPolls().get(poll.getId()).setRef(pollRef);

    }

*/

    public Binding getRefWithListener(Poll poll, final Callback callback, final boolean notifyOnOwnVote) {
        String query = "polls/" + poll.getId();
        DatabaseReference pollRef = FirebaseDatabase.getInstance().getReference(query);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String id = dataSnapshot.child("id").getValue(String.class);
                if(id==null){
                    //what will we do if pol is deleted from database - actually it shouldnt happen
                } else {
                    HashMap<String, Poll> polls = GlobalContainer.getPolls();
                    Poll newPoll= new Poll();
                    Poll poll = polls.get(id);
                    if (poll instanceof PollNotAnonymous) {
                        newPoll = dataSnapshot.getValue(PollNotAnonymous.class);
                        //this cycle is not approptiate solution but it is here because I couldnt find other way to have optionsnotAnanonous in not anonumous polls
                        for(Poll.Option opt: newPoll.getOptions().values()){
                            PollNotAnonymous.OptionNotAnonymous optna = dataSnapshot.child("options").child(opt.getText()).getValue(PollNotAnonymous.OptionNotAnonymous.class);
                            newPoll.getOptions().put(opt.getText(), optna);
                        }
                    } else {
                        newPoll = dataSnapshot.getValue(Poll.class);
                    }
                    //since this method is used when we want to keep refs in activity(to call activity specific callbacks),
                    // poll's ref field is not set
                    boolean newVote=false;
                    //here we assume that options and their number cant change
                    //we check if it is new vote what caused onChange

                                        for (Poll.Option o: poll.getOptions().values()){
                    Poll.Option no = newPoll.getOptions().get(o.getText());
                    if(no==null){break;} //actually it shouldnt happen, but while testing it is better to work it out. It means option was deleted
                    if(!(no.getVotesCount()==o.getVotesCount())){
                        newVote=true;
                        break;
                    }
                    }


                    if(GlobalContainer.getPolls().containsKey(newPoll.getId())&&(GlobalContainer.getPolls().get(newPoll.getId()).getChanged()==1)){
                        newPoll.setChanged(1);
                    }
                    if(poll.checkIfVoted()==1){
                        newPoll.setVoted();
                    }
                    polls.put(poll.getId(), newPoll);
                    PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
                    repository.deletePoll(poll.getId());
                    repository.add(newPoll);
                    GlobalContainer.getPolls().put(poll.getId(), newPoll);//this is so we want call onDataChange twice

                    if(newVote) {
                        newPoll.setChanged(1);//what if we are in itemactivity?
                        if(newVote||notifyOnOwnVote) {
                            callback.onLoad(newPoll.getId());
                        }
                    }////how to appropriatily call callback??


                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure();
            }

        };

        pollRef.addValueEventListener(postListener);
        return new Binding(pollRef, postListener);

    }

/*
            public DatabaseReference getRefWithSingleEventListener(Poll poll, final Callback callback) {
        //***********************this is to retrieve poll so if it is already here - skip***********
                String query = "polls/" + poll.getId();
                DatabaseReference pollRef = FirebaseDatabase.getInstance().getReference(query);
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        String id = dataSnapshot.child("id").getValue(String.class);
                        if(id==null){
                            //what will we do if pol is deleted from database - actually it shouldnt happen
                        } else {
                            HashMap<String, Poll> polls = GlobalContainer.getPolls();
                            Poll newPoll= new Poll();
                            Poll poll = polls.get(id);
                            if (poll instanceof PollNotAnonymous) {
                                newPoll = dataSnapshot.getValue(PollNotAnonymous.class);
                                //this cycle is not approptiate solution but it is here because I couldnt find other way to have optionsnotAnanonous in not anonumous polls
                                for(Poll.Option opt: newPoll.getOptions().values()){
                                    PollNotAnonymous.OptionNotAnonymous optna = dataSnapshot.child("options").child(opt.getText()).getValue(PollNotAnonymous.OptionNotAnonymous.class);
                                    newPoll.getOptions().put(opt.getText(), optna);
                                }
                            } else {
                                newPoll = dataSnapshot.getValue(Poll.class);
                            }
                            //since this method is used when we want to keep refs in activity(to call activity specific callbacks),
                            // poll's ref field is not set
                            boolean newVote=false;
                            //here we assume that options and their number cant change
                            //we check if it is new vote what caused onChange

                            for (Poll.Option o: poll.getOptions().values()){
                                Poll.Option no = newPoll.getOptions().get(o.getText());
                                if(no==null){break;} //actually it shouldnt happen, but while testing it is better to work it out. It means option was deleted
                                if(!(no.getVotesCount()==o.getVotesCount())){
                                    newVote=true;
                                    break;
                                }
                            }

                            polls.put(poll.getId(), newPoll);

                            PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
                            repository.deletePoll(poll.getId());
                            repository.add(newPoll);
                            GlobalContainer.getPolls().put(poll.getId(), newPoll);//this is so we want call onDataChange twice

                            if(newVote){
                                newPoll.setChanged(1);//what if we are in itemactivity?
                                callback.onLoad(newPoll.getId());
                            }

                        }

                    }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure();
            }

        };

        pollRef.addListenerForSingleValueEvent(postListener);
        return pollRef;

    }
*/
public DatabaseReference getRefWithSingleEventListener(Poll poll, final Callback callback) {
    //***********************this is to retrieve poll so if it is already here - skip***********
    String query = "polls/" + poll.getId();
    DatabaseReference pollRef = FirebaseDatabase.getInstance().getReference(query);
    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            String id = dataSnapshot.child("id").getValue(String.class);
            if(id==null){
                //what will we do if pol is deleted from database - actually it shouldnt happen
            } else {
                HashMap<String, Poll> polls = GlobalContainer.getPolls();
                Poll newPoll= new Poll();
                Poll poll = polls.get(id);
                if (poll instanceof PollNotAnonymous) {
                    newPoll = dataSnapshot.getValue(PollNotAnonymous.class);
                    //this cycle is not approptiate solution but it is here because I couldnt find other way to have optionsnotAnanonous in not anonumous polls
                    for(Poll.Option opt: newPoll.getOptions().values()){
                        PollNotAnonymous.OptionNotAnonymous optna = dataSnapshot.child("options").child(opt.getText()).getValue(PollNotAnonymous.OptionNotAnonymous.class);
                        newPoll.getOptions().put(opt.getText(), optna);
                    }
                } else {
                    newPoll = dataSnapshot.getValue(Poll.class);
                }

                newPoll.setChanged(1);
                polls.put(poll.getId(), newPoll);

                PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
                repository.deletePoll(poll.getId());
                repository.add(newPoll);
                GlobalContainer.getPolls().put(poll.getId(), newPoll);//this is so we want call onDataChange twice
            }

        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.onFailure();
        }

    };

    pollRef.addListenerForSingleValueEvent(postListener);
    return pollRef;

}
    public ArrayList<Binding> getRefsWithSharedCallbackOnAllLoaded(final Callback callback){
        final ArrayList<Integer> counter = new ArrayList<>();
        final int count= GlobalContainer.getPolls().size();
        final ArrayList<Binding> dRefs = new ArrayList<>();

        for(Poll poll : GlobalContainer.getPolls().values()){
            Binding b =getRefWithListener(poll, new Callback(){
                public void onLoad(String poll_id){
                    counter.add(1);
                    if(counter.size()==count){
                        callback.onLoad(poll_id);
                    }
                }
                public void onFailure(){
                    callback.onFailure();
                }
            }, false);
            dRefs.add(b);
            }
            return dRefs;
        }
/*
    public void linkAllRefsForGlobalContainer(final Callback callback){
        final ArrayList<Integer> counter = new ArrayList<>();
        final int count= GlobalContainer.getPolls().size();

        for(Poll poll : GlobalContainer.getPolls().values()){
            Binding b =getRefWithListener(poll, callback);
            GlobalContainer.getRefs().put(poll.getId(), b);
                }
    }
    */
/*
    public void updatePollsWithFirebaseChanges(final Callback callback){
        final ArrayList<Integer> counter = new ArrayList<>();
        final int count= GlobalContainer.getPolls().size();
        for(Poll poll : GlobalContainer.getPolls().values()){
            DatabaseReference ref =getRefWithListener(poll, new Callback(){
                public void onLoad(){
                    counter.add(1);
                    if(counter.size()==count){
                        callback.onLoad();
                    }
                }
                public void onFailure(){
                    callback.onFailure();
                }
            });
            GlobalContainer.getRefs().put(poll.getId(), ref);}

    }
*/


    public void sendVote(String pollId, Poll.Option option) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
        mDatabase.child("users").child(mPhoneNumber).child("poll_ids").child(pollId).setValue(1);
        GlobalContainer.getPolls().get(pollId).setVoted();
        final Poll.Option fOption = option;
        String refPath = "polls/" + pollId + "/options/" + option.getText();
        Query query = mDatabase.child("polls").child(pollId).child("options").child(option.getText());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(refPath);
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Poll.Option p;
                if (fOption instanceof PollNotAnonymous.OptionNotAnonymous) {
                    p = mutableData.getValue(PollNotAnonymous.OptionNotAnonymous.class);
                } else {
                    p = mutableData.getValue(Poll.Option.class);
                }

                if (p == null) {
                    return Transaction.success(mutableData);
                }
                if (p instanceof PollNotAnonymous.OptionNotAnonymous) {
                    Context mAppContext = ApplicationContextProvider.getContext();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
                    ((PollNotAnonymous.OptionNotAnonymous) p).addVoted(mPhoneNumber);
                } else{
                    p.incrementVotesCount();
                }


                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {

            }
        });

    }

    public void retrieveListOfUserPolls(String mPhoneNumber, final Callback callback){
        //final ArrayList<String> ids=new ArrayList<>();
        GlobalContainer.emptyPolls();
        final GenericTypeIndicator<HashMap<String, Integer>> t = new GenericTypeIndicator<HashMap<String, Integer>>(){};
        DatabaseReference ref = mDatabase.child("users").child(mPhoneNumber).child("poll_ids");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            ///This onDataChange will be run only once (coz ListenerForSingleValueEvent)
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Integer> temp_ids = dataSnapshot.getValue(t);
                if(temp_ids!=null) {
                    final Integer size = temp_ids.size();
                    final ArrayList<Integer> counter = new ArrayList<>();
                    DatabaseService.Callback accCallback = new DatabaseService.Callback() {
                        public void onLoad(String poll_id) {
                            counter.add(1);
                            if (counter.size() == size) {
                                callback.onLoad(""); //this we call after we retrieve to globalContainer all polls
                            }
                        }

                        @Override
                        public void onFailure() {

                        }
                    };

                    for (Map.Entry<String, Integer> entry : temp_ids.entrySet())

                    {
                        //ids.add(str);
                        retrievePollToGlobalContainer(entry.getKey(), accCallback);
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    public void retrievePollToGlobalContainer(String poll_id, final Callback callback){ ///this is to get new Poll from Database (when we receive message that we became participant of new poll)
        //addPollIdToUsersPollsIdList(poll_id);
        DatabaseReference ref = mDatabase.child("polls");
        DatabaseReference ref2 = mDatabase.child("polls").child(poll_id);
            ref.child(poll_id).addListenerForSingleValueEvent(new ValueEventListener() {
                Poll poll;
                @Override
                ///This onDataChange will be run only once (coz ListenerForSingleValueEvent)
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int type = dataSnapshot.child("type").getValue(Integer.class);
                    if(type==1){
                        poll= dataSnapshot.getValue(Poll.class);
                    }else{
                        poll= dataSnapshot.getValue(PollNotAnonymous.class);
                    }
                    //HashMap<String, Poll> polls = GlobalContainer.getPolls();
                    poll.setChanged(1);
                    GlobalContainer.getPolls().put(poll.getId(), poll);

                    DatabaseReference ref =getRefWithSingleEventListener(poll, callback);
                    GlobalContainer.getRefs().put(poll.getId(), ref); //this callback should update poll in GlobalContainer on change
                    callback.onLoad(poll.getId());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

                });

    }




    public DatabaseReference getReference(String query){
        DatabaseReference rootRef = mDatabase.child("users");
        return rootRef;
    }

    public void getTheOnesInDatabase(HashMap<String, Contact> contactsToCheck, final ContactsCallback callback) {
        final PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
        repository.truncateContactsTable();
        int badCount =0;
        for(Contact c : contactsToCheck.values()) {
            /*
            if(c.getName().equals("Elena Mobile Apps")){
                String str = c.getPhoneNumber();
                System.out.println(c.getName());
            }*/
            c.setPhoneNumber(c.getPhoneNumber().replaceAll("\\s+",""));
            String phone = c.getPhoneNumber();
            if (phone.contains(".") || phone.contains("$") || phone.contains("[") || phone.contains("]") || phone.contains("#") || phone.contains("\\")) {
                badCount++;
            }

        }
        GlobalContainer.emptyContacts();
        DatabaseReference rootRef = mDatabase.child("users");
        final ArrayList<Integer> counter = new ArrayList<>();
        final int count = contactsToCheck.size() - badCount;
        Set<Map.Entry<String,Contact>> set = contactsToCheck.entrySet();
        for (Map.Entry<String, Contact> entry : contactsToCheck.entrySet()) {
            final Map.Entry<String, Contact> finalEntry = entry;
            String phone = finalEntry.getValue().getPhoneNumber();
            if (!(phone.contains(".")||phone.contains("$")||phone.contains("[")||phone.contains("]")||phone.contains("#")||phone.contains("\\"))) {
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        counter.add(1);
                        /*
                        if(finalEntry.getValue().getName().equals("Elena Mobile Apps")){
                            String str = finalEntry.getValue().getPhoneNumber();
                            System.out.println(finalEntry.getValue().getName());
                        }
                        */
                        if (snapshot.hasChild(finalEntry.getValue().getPhoneNumber())) {
                            // run some code
                            HashMap<String, Contact> contacts = GlobalContainer.getContacts();
                            contacts.put(finalEntry.getKey(), finalEntry.getValue());
                            //PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
                            repository.deleteContact(finalEntry.getKey());
                            repository.addContact(finalEntry.getValue().getName(), finalEntry.getValue().getPhoneNumber());


                        }
                        if (counter.size() == count) {
                            callback.onLoad();
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onFailure();
                    }
                });
            }
        }

    }



public interface Callback {

        void onLoad(String poll_id);
        void onFailure();

    }
    public static class DefaultCallback implements Callback{
        public void onLoad(String poll_id){

        }
        public void onFailure(){

        }
    }

    public interface ContactsCallback {

        void onLoad();
        void onFailure();

    }
    public static class DefaultContactsCallback implements ContactsCallback {
        public void onLoad(){

        }
        public void onFailure(){

        }
    }

    public void setNewPollListener(final Callback callback){
        GlobalContainer.getSelfRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DatabaseService mPollService = DatabaseService.getInstance();
                final String id = dataSnapshot.getKey();
            if(!(GlobalContainer.getPolls().containsKey(id))) {
                mPollService.retrievePollToGlobalContainer(id, callback);
            }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public DatabaseReference getSelfRef(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
        DatabaseReference rootRef = mDatabase.child("users").child(mPhoneNumber).child("poll_ids");
        return rootRef;
    }

    public void deletePollForUser(String pollId){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
        DatabaseReference ref = mDatabase.child("users").child(mPhoneNumber).child("poll_ids");
        Query usersQuery = ref.orderByKey().equalTo(pollId);

        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DatabaseReference pollRef = mDatabase.child("polls").child(pollId).child("participants");
        Query pollsQuery = pollRef.orderByValue().equalTo(mPhoneNumber);

        pollsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
