package com.example.elena.ourandroidapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elena on 25/11/17.
 */

public class Poll {
    private String title;
    private String question;

    public Poll(String title, String question){
        this.title = title;
        this.question = question;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    @Override
    public String toString(){
        return "Title: "+title+
                " Question: "+question;
    }
    public static List<Poll> getPolls(){

        List<Poll> polls = new ArrayList<Poll>();

        polls.add(new Poll("Mulholland Drive", "David Lynch"));
        polls.add(new Poll("Interstellar", "Christopher Nolan"));
        polls.add(new Poll("Kill Bill", "Quentin Tarantino"));
        polls.add(new Poll("The Texas Chain Saw Massacre", "Tobe Hooper"));
        polls.add(new Poll("Videodrome", "David Cronemberg"));
        polls.add(new Poll("My Neighbor Totoro", "Hayao Miyazaki"));
        polls.add(new Poll("Scream", "Wes Craven"));
        polls.add(new Poll("Vertigo", "Alfred Hitchcock"));
        polls.add(new Poll("No Country For Old Men", "Joel and Ethan Coen"));
        polls.add(new Poll("Carrie", "Brian De Palma"));
        polls.add(new Poll("Rosemary's Baby", "Roman Polanski"));


        return polls;

    }
}
