package edu.neu.madcourse.timber.users;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

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
    private String specialty;


    public Contractor(){
    }

    public Contractor(String username,
                      String token,
                      String businessName,
                      String taxID,
                      String email,
                      String zipcode,
                      String phoneNumber) throws NullPointerException{
        this.username = username;
        this.username = Objects.requireNonNull(username, "username must not be null");

        this.token = token;
        this.token = Objects.requireNonNull(token, "username must not be null");

        this.businessName = businessName;
        this.businessName = Objects.requireNonNull(businessName, "businessName must not be null");

        this.taxID = taxID;
        this.taxID = Objects.requireNonNull(taxID, "taxID must not be null");

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
        this.workRadius = 20;
        this.specialty = "No specialties added.";
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
        this.specialty = "No specialties added.";
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