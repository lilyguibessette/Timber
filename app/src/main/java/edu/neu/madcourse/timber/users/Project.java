package edu.neu.madcourse.timber.users;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.timber.messages.Message;

public class Project {
    public String username, image, description, project_id, project_name, project_type;
    private double latitude, longitude;
    public int budget;
    public boolean completed;
    private ArrayList<String> swipedRightOnList, swipedLeftOnList;
    private List<String> matchList = new ArrayList<>();

    // from contractor, messagethread
    private HashMap<String, ArrayList<Message>> messageThreads = new HashMap<>();

    public Project() {
    }

    // for completed recycler view newsfeed
    public Project(String username, String project_type, String image, String description) {
        this.username = username;
        this.image = image;
        this.project_id = username + "_" + project_name;
        this.description = description;
        this.project_type = project_type;
        this.completed = true;

        this.swipedRightOnList = new ArrayList<>();
        this.swipedLeftOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();

        this.swipedRightOnList.add("EMPTY");
        this.swipedLeftOnList.add("EMPTY");
        this.matchList.add("EMPTY");

        ArrayList<Message> welcome = new ArrayList<>();
        welcome.add(new Message("WELCOME", username,"Welcome to Timber!"));
        this.messageThreads.put("WELCOME", welcome);
    }

    public Project(String username, String project_name, String project_type, int budget,
                   String image, String description) {
        this.username = username;
        this.image = image;
        this.description = description;
        this.project_id = username + "_" + project_name;
        this.budget = budget;
        this.project_type = project_type;
        this.completed = false;

        this.swipedRightOnList = new ArrayList<>();
        this.swipedLeftOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();

        this.swipedRightOnList.add("EMPTY");
        this.swipedLeftOnList.add("EMPTY");
        this.matchList.add("EMPTY");

        ArrayList<Message> welcome = new ArrayList<>();
        welcome.add(new Message("WELCOME",username, "Welcome to Timber!"));
        this.messageThreads.put("WELCOME", welcome);
    }
    public Project(String username, String project_name, String project_type, int budget,
                   String description) {
        this.username = username;
        if (this.image == null) {
            this.image = "image placeholder.PNG";
            Log.e("PROJECT", "proj made with default image");
        }
        this.description = description;
        this.project_id = username + "_" + project_name;
        this.budget = budget;
        this.project_type = project_type;
        this.completed = false;

        this.swipedRightOnList = new ArrayList<>();
        this.swipedLeftOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();

        this.swipedRightOnList.add("EMPTY");
        this.swipedLeftOnList.add("EMPTY");
        this.matchList.add("EMPTY");

        ArrayList<Message> welcome = new ArrayList<>();
        welcome.add(new Message("WELCOME", username, "Welcome to Timber!"));
        this.messageThreads.put("WELCOME", welcome);

    }

    public Project(String username, String project_name, String project_type, int budget,
                   String image, String description, double latitude, double longitude) {
        this.username = username;
        this.image = image;
        this.description = description;
        this.project_id = username + "_" + project_name;
        this.budget = budget;
        this.project_type = project_type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.completed = false;

        this.swipedRightOnList = new ArrayList<>();
        this.swipedLeftOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();

        this.swipedRightOnList.add("EMPTY");
        this.swipedLeftOnList.add("EMPTY");
        this.matchList.add("EMPTY");

        ArrayList<Message> welcome = new ArrayList<>();
        welcome.add(new Message("WELCOME", username,"Welcome to Timber!"));
        this.messageThreads.put("WELCOME", welcome);
    }

    public Project(String username, String project_name, String project_type, int budget,
                   String image, String description, double latitude, double longitude,
                   boolean completed) {
        this.username = username;
        this.image = image;
        this.description = description;
        this.project_id = username + "_" + project_name;
        this.budget = budget;
        this.project_type = project_type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.completed = completed;

        this.swipedRightOnList = new ArrayList<>();
        this.swipedLeftOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();

        this.swipedRightOnList.add("EMPTY");
        this.swipedLeftOnList.add("EMPTY");
        this.matchList.add("EMPTY");

        ArrayList<Message> welcome = new ArrayList<>();
        welcome.add(new Message("WELCOME", username, "Welcome to Timber!"));
        this.messageThreads.put("WELCOME", welcome);
    }

    public String getUsername() {
        return this.username;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
        Log.e("PROJECT", "set image called in " + this.project_id + "new image = " + this.image + " from " + image);
    }

    public String getDescription() {
        return this.description;
    }

    public String getProject_id() {
        return this.project_id;
    }

    public String getProject_type() {
        return this.project_type;
    }

    public String getProject_name() {
        return this.project_name;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public ArrayList<String> getSwipedRightOnList() {
        return this.swipedRightOnList;
    }

    public ArrayList<String> getSwipedLeftOnList() {
        return this.swipedLeftOnList;
    }

    public List<String> getMatchList() {
        return this.matchList;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void addRightSwipedOn(String username) {
        swipedRightOnList.add(username);
    }

    public void addLeftSwipedOn(String username) {
        swipedLeftOnList.add(username);
    }

    public HashMap<String, ArrayList<Message>> getMessageThreads() {
        return this.messageThreads;
    }

    public void addMessage(String username, Message message) {
        if (this.messageThreads.containsKey(username)) {
            ArrayList<Message> msgs = this.messageThreads.get(username);
            msgs.add(message);
        } else {
            ArrayList<Message> msgs = new ArrayList<Message>();
            msgs.add(message);
            this.messageThreads.put(username, msgs);
        }
    }
}
