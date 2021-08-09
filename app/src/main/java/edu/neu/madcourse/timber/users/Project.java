package edu.neu.madcourse.timber.users;

import android.location.Location;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.timber.messages.Message;

public class Project {
    public String username;
    public String image;
    public String description;
    public String project_id;
    public String project_name;
    public int budget;
    public String project_type;
    private double latitude;
    private double longitude;
    public boolean completed;
    private List<String> swipedByList= new ArrayList<>();
    private List<String> matchList= new ArrayList<>();
    private HashMap<String, ArrayList<Message>> messageThreads = new HashMap<>();// from contractor, messagethread

    public Project() {
    }


// for completed recycler view newsfeed
    public Project(String username, String project_type, String image, String description) {
        this.username = username;
        this.image = image;
        this.description = description;
        this.project_type = project_type;
        this.completed = true;
        this.project_id = username + "_" + project_name;
        this.swipedByList = new ArrayList<>();
        this.matchList = new ArrayList<>();
        this.swipedByList.add("EMPTY");
        this.matchList.add("EMPTY");
        ArrayList<Message> welcome = new ArrayList<>();
        welcome.add(new Message("WELCOME","Welcome to Timber!"));
        this.messageThreads.put("WELCOME", welcome);
    }

    public Project(String username, String project_name, String project_type, int budget, String image, String description) {
        this.username = username;
        this.image = image;
        this.description = description;
        this.project_id = username + "_" + project_name;
        this.budget = budget;
        this.project_type = project_type;
        this.completed = false;
        this.swipedByList = new ArrayList<>();
        this.matchList = new ArrayList<>();
        this.swipedByList.add("EMPTY");
        this.matchList.add("EMPTY");
        ArrayList<Message> welcome = new ArrayList<>();
        welcome.add(new Message("WELCOME","Welcome to Timber!"));
        this.messageThreads.put("WELCOME", welcome);
    }

    public Project(String username, String project_name, String project_type, int budget, String description) {
        this.username = username;
        this.image = "image placeholder.PNG";
        this.description = description;
        this.project_id = username + "_" + project_name;
        this.budget = budget;
        this.project_type = project_type;
        this.completed = false;
        this.swipedByList = new ArrayList<>();
        this.matchList = new ArrayList<>();
        this.swipedByList.add("EMPTY");
        this.matchList.add("EMPTY");
        ArrayList<Message> welcome = new ArrayList<>();
        welcome.add(new Message("WELCOME","Welcome to Timber!"));
        this.messageThreads.put("WELCOME", welcome);

    }

    public Project(String username, String project_name, String project_type, int budget, String image, String description, double latitude,
                   double longitude) {
        this.username = username;
        this.image = image;
        this.description = description;
        this.project_id = username + "_" + project_name;
        this.budget = budget;
        this.project_type = project_type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.completed = false;
        this.swipedByList = new ArrayList<>();
        this.matchList = new ArrayList<>();
        this.swipedByList.add("EMPTY");
        this.matchList.add("EMPTY");
        ArrayList<Message> welcome = new ArrayList<>();
        welcome.add(new Message("WELCOME","Welcome to Timber!"));
        this.messageThreads.put("WELCOME", welcome);
    }

    public Project(String username, String project_name, String project_type, int budget, String image, String description, double latitude,
                   double longitude, boolean completed) {
        this.username = username;
        this.image = image;
        this.description = description;
        this.project_id = username + "_" + project_name;
        this.budget = budget;
        this.project_type = project_type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.completed = completed;

        this.swipedByList = new ArrayList<>();
        this.matchList = new ArrayList<>();

        this.swipedByList.add("EMPTY");
        this.matchList.add("EMPTY");
        ArrayList<Message> welcome = new ArrayList<>();
        welcome.add(new Message("WELCOME","Welcome to Timber!"));
        this.messageThreads.put("WELCOME", welcome);
    }

    public String getUsername() {
        return this.username;
    }

    public String getImage() {
        return this.image;
    }

    public String getDescription() {
        return this.description;
    }

    public String getProject_id()
    {return this.project_id;}


    public String getProject_type(){
        return this.project_type;
    }
    public String getProject_name(){
        return this.project_name;
    }
    public void setCompleted(boolean completed){
        this.completed = completed;
    }
    public List<String> getSwipedByList(){
        return this.swipedByList;
    }
    public List<String> getMatchList(){
        return this.matchList;
    }
    public double getLatitude(){return this.latitude; }
    public double getLongitude(){return this.longitude; }
    public void setLatitude(double latitude){this.latitude =latitude;}
    public void setLongitude(double longitude){ this.longitude  = longitude;}

    public HashMap<String, ArrayList<Message>> getMessageThreads(){
        return this.messageThreads;
    }

    public void addMessage(String username, Message message){
         if(this.messageThreads.containsKey(username)) {
             ArrayList<Message> msgs = this.messageThreads.get(username);
             msgs.add(message);
         } else{
             ArrayList<Message> msgs = new ArrayList<Message>();
             msgs.add(message);
             this.messageThreads.put(username, msgs);
         }
    }
}
