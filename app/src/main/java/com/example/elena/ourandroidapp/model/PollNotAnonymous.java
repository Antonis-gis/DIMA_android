package com.example.elena.ourandroidapp.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by elena on 15/12/17.
 */

public class PollNotAnonymous extends Poll {

    static public class OptionNotAnonymous extends Option{
        @Expose
        @SerializedName("VOTED")
        //private ArrayList<String> voted; //phone numbers of voted for this option
        protected ArrayList<String> voted=new ArrayList<>();
        public OptionNotAnonymous(String text){
            super(text);
            voted=new ArrayList<>();
        }

        public OptionNotAnonymous(String text, Integer pvotesCount){
            super(text, pvotesCount);
            voted=new ArrayList<>();
        }
        public OptionNotAnonymous(){
            super();
            voted=new ArrayList<>();
// Default constructor required for calls to DataSnapshot.getValue(User.class)

        }

        public ArrayList<String> getVoted() {
            return voted;
        }
        public void setVoted(ArrayList<String> voted){
            this.voted=  voted;
        }




        public void addVoted(String phoneofvoted){
            voted.add(phoneofvoted);
            incrementVotesCount();
        }

    }
    public PollNotAnonymous(String title, String question, String id){
        super (title, question, id);
        this.type=0;
    }
    public PollNotAnonymous(){
        super();
        this.type=0;
// Default constructor required for calls to DataSnapshot.getValue(User.class)

    }
    @Override
     public void addOption(String option_text){
        options.put(option_text, new OptionNotAnonymous(option_text));
    }

    public void addVoted(String option_text, String voted){
        //((OptionNotAnonymous)options.get(options.indexOf(option_text))).addVoted(voted);
        for(Option opt : options.values()) {
            if(opt.getText().equals(option_text)) {
                OptionNotAnonymous optna = (OptionNotAnonymous)opt;
                optna.addVoted(voted);
            }
        }

    }


}
