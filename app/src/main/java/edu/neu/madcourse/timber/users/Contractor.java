package edu.neu.madcourse.timber.users;

import java.util.ArrayList;
import java.util.Objects;

public class Contractor implements User {
    private String username, token, businessName, taxID, email, zipcode, phoneNumber, image,
            specialty;
    private ArrayList<String> activeProjectList, completedProjectList, swipedRightOnList,
            swipedLeftOnList, matchList;
    private double latitude, longitude;
    private int workRadius;

    public Contractor() {
    }

    public Contractor(String username,
                      String token,
                      double latitude,
                      double longitude,
                      String businessName,
                      String taxID,
                      String email,
                      String zipcode,
                      String phoneNumber) throws NullPointerException {
        this.username = username;
        this.username = Objects.requireNonNull(username, "username must not be null");

        this.token = token;
        this.token = Objects.requireNonNull(token, "token must not be null");

        this.latitude = latitude;
        this.longitude = longitude;

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
        this.swipedRightOnList = new ArrayList<>();
        this.swipedLeftOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();
        this.activeProjectList.add("EMPTY");
        this.completedProjectList.add("EMPTY");
        this.swipedRightOnList.add("EMPTY");
        this.swipedLeftOnList.add("EMPTY");
        this.matchList.add("EMPTY");

        this.workRadius = 20;
        this.image = "default_profile_pic.PNG";
        this.specialty = "No specialties added.";
    }


    public Contractor(String username,
                      String token,
                      String businessName,
                      String taxID,
                      String email,
                      String zipcode,
                      String phoneNumber) throws NullPointerException {
        this.username = username;
        this.username = Objects.requireNonNull(username, "username must not be null");

        this.token = token;
        this.token = Objects.requireNonNull(token, "token must not be null");

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
        this.swipedRightOnList = new ArrayList<>();
        this.swipedLeftOnList = new ArrayList<>();
        this.matchList = new ArrayList<>();

        this.activeProjectList.add("EMPTY");
        this.completedProjectList.add("EMPTY");
        this.swipedRightOnList.add("EMPTY");
        this.swipedLeftOnList.add("EMPTY");
        this.matchList.add("EMPTY");

        this.workRadius = 20;
        this.image = "default_profile_pic.PNG";
        this.specialty = "No specialties added.";
    }

    public String getToken() {
        return this.token;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTaxID() {
        return taxID;
    }

    public void setTaxID(String taxID) {
        this.taxID = taxID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getRadius() {
        return workRadius;
    }

    public void setRadius(int workRadius) {
        this.workRadius = workRadius;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<String> getMatchList() {
        return this.matchList;
    }

    public ArrayList<String> getActiveProjectList() {
        return this.activeProjectList;
    }

    public ArrayList<String> getCompletedProjectList() {
        return this.completedProjectList;
    }

    public void addRightSwipedOn(String username) { swipedRightOnList.add(username); }

    public void addLeftSwipedOn(String username) { swipedLeftOnList.add(username); }

    public ArrayList<String> getSwipedRightOnList() { return this.swipedRightOnList; }

    public ArrayList<String> getSwipedLeftOnList() { return this.swipedLeftOnList; }

    //public void setLatitude(Location location){this.latitude = location.getLatitude();}
    //public void setLongitude(Location location){ this.longitude  = location.getLongitude() ;}


}