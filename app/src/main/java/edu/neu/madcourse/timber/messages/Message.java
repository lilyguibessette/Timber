package edu.neu.madcourse.timber.messages;

public class Message {
    public String username;
    public String to_username;
    public String message;

    public Message() {
    }

    public Message(String from_username, String to_username, String message) {
        this.username = from_username;
        this.to_username = to_username;
        this.message = message;
    }

    public String getUsername() {
        return this.username;
    }

    public String getTo_username() {
        return this.to_username;
    }


    public String getMessage() {
        return this.message;
    }
}
