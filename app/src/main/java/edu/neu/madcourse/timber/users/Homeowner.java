package edu.neu.madcourse.timber.users;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Homeowner implements User {
    private String username;
    private String token;
    private Location location;
    private String firstName;
    private String lastName;
    private String zipcode;
    private String email;
    private String phoneNumber;
    private String image;
    private List<String> activeProjectList;
    private List<String> completedProjectList;
    private List<String> swipedOnList;
    private List<String> matchList;

    public Homeowner(){
    }

    public Homeowner(String username,
                     String token,
                     String firstName,
                     String lastName,
                     String email,
                     String zipcode,
                     String phoneNumber){
        this.username = username;
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.zipcode = zipcode;
        this.phoneNumber = phoneNumber;
        this.activeProjectList = new ArrayList<>();
        this.completedProjectList = new ArrayList<>();
        this.swipedOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();
        this.image = "default_profile_pic.PNG";
    }

    public Homeowner(String username,
                     String token,
                     Location location,
                     String firstName,
                     String lastName,
                     String email,
                     String zipcode,
                     String phoneNumber){
        this.username = username;
        this.token = token;
        this.location = location;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.zipcode = zipcode;
        this.phoneNumber = phoneNumber;
        this.activeProjectList = new ArrayList<>();
        this.completedProjectList = new ArrayList<>();
        this.swipedOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() { return this.token; }

    public Location getLocation(){return this.location; }

    public void setLocation(Location location){this.location = location; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getZipcode() { return zipcode; }

    public String getEmail() { return email; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setImage(String image) { this.image = image;}

    public String getImage() { return this.image;}
}