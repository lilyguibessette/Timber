package edu.neu.madcourse.timber;

import android.location.Location;

public class Homeowner {
    private String username;
    private String token;
    private Location location;
    private String firstName;
    private String lastName;
    private String zipcode;
    private String email;
    private String phoneNumber;

    public Homeowner(){
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
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() { return this.token; }

    public Location getLocation(){return this.location; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getZipcode() { return zipcode; }

    public String getEmail() { return email; }

    public String getPhoneNumber() { return phoneNumber; }
}