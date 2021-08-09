package edu.neu.madcourse.timber.users;

import android.location.Location;

public interface User {
    public String getUsername();
    public String getToken() ;
    public String getEmail() ;
    public String getZipcode() ;
    public String getPhoneNumber() ;
    public void setImage(String image) ;
}

