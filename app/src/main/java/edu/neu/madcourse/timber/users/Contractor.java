package edu.neu.madcourse.timber.users;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Contractor implements User {
    private String username;
    private String token;
    private Location location;
    private String businessName;
    private String taxID;
    private String email;
    private String zipcode;
    private String phoneNumber;
    private String image;
    private List<String> activeProjectList;
    private List<String> completedProjectList;
    private List<String> swipedOnList;
    private List<String> matchList;
    private int workRadius;


    public Contractor(){
    }

    public Contractor(String username,
                      String token,
                      String businessName,
                      String taxID,
                      String email,
                      String zipcode,
                      String phoneNumber){
        this.username = username;
        this.token = token;
        this.businessName = businessName;
        this.taxID = taxID;
        this.email = email;
        this.zipcode = zipcode;
        this.phoneNumber = phoneNumber;
        this.activeProjectList = new ArrayList<>();
        this.completedProjectList = new ArrayList<>();
        this.swipedOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();
        this.workRadius = 20;
    }

    public Contractor(String username,
                      String token,
                      Location location,
                      String businessName,
                      String taxID,
                      String email,
                      String zipcode,
                      String phoneNumber){
        this.username = username;
        this.token = token;
        this.location = location;
        this.businessName = businessName;
        this.taxID = taxID;
        this.email = email;
        this.zipcode = zipcode;
        this.phoneNumber = phoneNumber;
        this.activeProjectList = new ArrayList<>();
        this.completedProjectList = new ArrayList<>();
        this.swipedOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();
        this.workRadius = 20;
        this.image = "default_profile_pic.PNG";
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() { return this.token; }

    public Location getLocation(){return this.location; }

    public void setLocation(Location location){this.location = location; }

    public String getBusinessName() { return businessName; }

    public String getTaxID() { return taxID; }

    public String getEmail() { return email; }

    public String getZipcode() { return zipcode; }

    public String getPhoneNumber() {return phoneNumber; }

    public void setImage(String image) { this.image = image;}

    public String getImage() { return this.image;}
}