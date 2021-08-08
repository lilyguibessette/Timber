package edu.neu.madcourse.timber.matches;

public class Match {
    public String username;
    public int image;
    public String last_message;

    public Match() {
    }

    public Match(String username, int image, String last_message) {
        this.username = username;
        this.image = image;
        this.last_message = last_message;
    }

    public String getUsername() {
        return this.username;
    }

    public int getImage() {
        return image;
    }

    public String getLast_message() {
        return this.last_message;
    }
}
