package edu.neu.madcourse.timber.messages;

public class Message {
    public String username;
    public String message;

    public Message() {
    }

    public Message(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return this.username;
    }


    public String getMessage() {
        return this.message;
    }
}
