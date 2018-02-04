package com.example.elena.ourandroidapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by elena on 25/11/17.
 */

public class Poll implements java.io.Serializable{
    @Expose @SerializedName("TITLE")
    protected String title;
    @Expose @SerializedName("QUESTION")
    protected String question;
    @Expose @SerializedName("ID")
    protected String id;
    @Expose @SerializedName("OPTIONS")
    //protected ArrayList<Option> options;
    protected Map<String, Option> options = new HashMap<>();

    @Expose @SerializedName("TYPE")
    protected int type;
    //@Expose @SerializedName("CHANGED")

    @Expose @SerializedName("PARTICIPANTS")
    protected ArrayList<String> participants= new ArrayList<>();
    @Exclude
    protected int changed;
    @Exclude
    protected int alreadyVoted;

    //protected DatabaseReference pollRef;

    /*public DatabaseReference getRef() {
        return pollRef;
    }
    */
    /*public void setRef(DatabaseReference ref) {
        pollRef = ref;
    }*/
    public int checkIfVoted() {
        return alreadyVoted;
    }

    public void setVoted() {
        alreadyVoted = 1;
    }


    public int getChanged() {
        return changed;
    }

    public void setChanged(int changed) {

        this.changed = changed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type=type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    static public class Option implements java.io.Serializable{
        @Expose @SerializedName("TEXT")
    protected String text;
        @Expose @SerializedName("VOTES_COUNT")
    protected Integer votesCount;
    public Option(String text){
        this.text=text;
        votesCount = 0;
    }
        public Option(){
// Default constructor required for calls to DataSnapshot.getValue(User.class)

        }
        public Option(String text, Integer pvotesCount){
            this.text=text;
            votesCount = pvotesCount;
        }
    public String getText() {
        return text;
    }

    public void incrementVotesCount() {
        this.votesCount++;
    }

        public int getVotesCount() {
            return votesCount;
        }

        public void setText(String text) {
            this.text = text;
        }
    @Override
    public String toString(){
        return text;
    }
    /*
    @Override
        public boolean equals(Object other) {
        if (other == null)
        {
            return false;
        }

        if (this.getClass() != other.getClass())
        {
            return false;
        }
        else {
            return text.equals(((Option)other).getText());
        }
        }
*/

    }



    public Poll(String title, String question, String id){
        this.title = title;
        this.question = question;
        options=new HashMap<>();
        this.id=id;
        this.type=1;
        participants= new ArrayList<>();
        changed=0;
        alreadyVoted=0;
    }
    public Poll(){
        options=new HashMap<>();
// Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.type=1;
        participants= new ArrayList<>();
        changed=0;
        alreadyVoted=0;

    }
    public void addOption(String option_text){
       //options.add(new Option(option_text));
       options.put(option_text, new Option(option_text));
    }

    public void addOption(Option option){

        //options.add(option);
        options.put(option.getText(), option);
    }

    public void addParticipant(String number){

        //options.add(option);
        participants.add(number);
    }

    public void incrementVotesCount(String option_text){
        //int index =options.indexOf(option_text);
        //options.get(option_text).incrementVotesCount();
            for(Option opt : options.values()) {
                if(opt.getText().equals(option_text)) {
                    opt.incrementVotesCount();
                }
            }

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
    public void setQuestion() {
        this.question=question;
    }
    public Map<String, Option> getOptions(){
        return options;
    }
    public void setOptions(Map<String, Option> options){
        this.options=  options;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }
    public void setParticipants(ArrayList<String> participants){
        this.participants=  participants;
    }


    @Override
    public String toString(){
        return "Title: "+title+
                " Question: "+question;
    }
    public static List<Poll> getPolls(){

        List<Poll> polls = new ArrayList<Poll>();

        polls.add(new Poll("Mulholland Drive", "David Lynch", "1"));
        polls.add(new Poll("Interstellar", "Christopher Nolan", "2"));
        polls.add(new Poll("Kill Bill", "Quentin Tarantino",  "3"));
        polls.add(new Poll("The Texas Chain Saw Massacre", "Tobe Hooper", "4"));
        polls.add(new Poll("Videodrome", "David Cronemberg", "5"));
        polls.add(new Poll("My Neighbor Totoro", "Hayao Miyazaki", "6"));
        polls.add(new Poll("Scream", "Wes Craven", "7"));
        polls.add(new Poll("Vertigo", "Alfred Hitchcock", "8"));
        polls.add(new Poll("No Country For Old Men", "Joel and Ethan Coen", "9"));
        polls.add(new Poll("Carrie", "Brian De Palma", "10"));
        polls.add(new Poll("Rosemary's Baby", "Roman Polanski", "11"));


        return polls;

    }
    public String serializeAndReturn() {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonString = gson.toJson(this);
        String result = "Serialized jsonString : "+ jsonString;
        return result;
    }

}
