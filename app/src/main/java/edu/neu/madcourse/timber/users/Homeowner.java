package edu.neu.madcourse.timber.users;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

public class Homeowner implements User {
    private String username;
    private String token;
    private double latitude;
    private double longitude;
    private String firstName;
    private String lastName;
    private String zipcode;
    private String email;
    private String phoneNumber;
    private String image;
    private ArrayList<String> activeProjectList;
    private ArrayList<String> completedProjectList;
    private ArrayList<String> swipedOnList;
    private ArrayList<String> matchList;

    public Homeowner(){
    }

    public Homeowner(String username,
                     String token,
                     double latitude,
                     double longitude,
                     String firstName,
                     String lastName,
                     String email,
                     String zipcode,
                     String phoneNumber) throws NullPointerException{

        this.username = username;
        this.username = Objects.requireNonNull(username, "username must not be null");

        this.token = token;
        this.token = Objects.requireNonNull(token, "username must not be null");

        this.latitude = latitude;

        this.longitude = longitude;

        this.firstName = firstName;
        this.firstName = Objects.requireNonNull(firstName, "businessName must not be null");

        this.lastName = lastName;
        this.lastName = Objects.requireNonNull(lastName, "taxID must not be null");

        this.email = email;
        this.email = Objects.requireNonNull(email, "email must not be null");

        this.zipcode = zipcode;
        this.zipcode = Objects.requireNonNull(zipcode, "zipcode must not be null");

        this.phoneNumber = phoneNumber;
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "phoneNumber must not be null");
        this.image = "default_profile_pic.PNG";


        this.activeProjectList = new ArrayList<>();
        this.completedProjectList = new ArrayList<>();
        this.swipedOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();


        this.activeProjectList.add("EMPTY");
        this.completedProjectList.add("EMPTY");
        this.swipedOnList.add("EMPTY");
        this.matchList.add("EMPTY");
    }

    public Homeowner(String username,
                     String token,
                     String firstName,
                     String lastName,
                     String email,
                     String zipcode,
                     String phoneNumber) throws NullPointerException{

        this.username = username;
        this.username = Objects.requireNonNull(username, "username must not be null");

        this.token = token;
        this.token = Objects.requireNonNull(token, "username must not be null");


        this.firstName = firstName;
        this.firstName = Objects.requireNonNull(firstName, "businessName must not be null");

        this.lastName = lastName;
        this.lastName = Objects.requireNonNull(lastName, "taxID must not be null");

        this.email = email;
        this.email = Objects.requireNonNull(email, "email must not be null");

        this.zipcode = zipcode;
        this.zipcode = Objects.requireNonNull(zipcode, "zipcode must not be null");

        this.phoneNumber = phoneNumber;
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "phoneNumber must not be null");

        this.activeProjectList = new ArrayList<>();
        this.completedProjectList = new ArrayList<>();
        this.swipedOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() { return this.token; }

    public double getLatitude(){return this.latitude; }
    public double getLongitude(){return this.longitude; }
    //public void setLatitude(Location location){this.latitude = location.getLatitude();}
    //public void setLongitude(Location location){ this.longitude  = location.getLongitude() ;}

    public void setLatitude(double latitude){this.latitude =latitude;}
    public void setLongitude(double longitude){ this.longitude  = longitude;}


    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getZipcode() { return zipcode; }

    public String getEmail() { return email; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setImage(String image) { this.image = image;}

    public String getImage() { return this.image;}

    public void addActiveProject(String project_id){
        this.activeProjectList.add(project_id);
    }
    public void addCompleteProject(String project_id){
        completedProjectList.add(project_id);
    }
    public void removeActiveProject(String project_id){
        activeProjectList.remove(project_id);
    }

    public void addSwipedOn(String username){
        swipedOnList.add(username);
    }
    public void addMatch(String username){
        matchList.add(username);
    }

    public ArrayList<String> getActiveProjectList(){
        return this.activeProjectList;
    }

    public ArrayList<String> getMatchList(){
        return this.matchList;
    }
    public ArrayList<String> getCompletedProjectList(){
        return this.completedProjectList;
    }

    public ArrayList<String> getSwipedOnList(){
        return this.swipedOnList;
    }
}