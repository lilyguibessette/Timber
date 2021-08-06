package edu.neu.madcourse.timber;

import android.location.Location;

public class User {
    public String username;
    public String token;
    public Location location;

    public User(){
    }

    public User(String username, String token){
        this.username = username;
        this.token = token;
        this.location = location;
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() {
        return this.token;
    }

    public Location getLocation(){return this.location; }
}
