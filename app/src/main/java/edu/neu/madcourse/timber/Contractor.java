package edu.neu.madcourse.timber;

import android.location.Location;

public class Contractor {
    private String username;
    private String token;
    private Location location;
    private String businessName;
    private String taxID;
    private String email;
    private String zipcode;
    private String phoneNumber;


    public Contractor(){
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
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() { return this.token; }

    public Location getLocation(){return this.location; }

    public String getBusinessName() { return businessName; }

    public String getTaxID() { return taxID; }

    public String getEmail() { return email; }

    public String getZipcode() { return zipcode; }

    public String getPhoneNumber() {return phoneNumber; }
}