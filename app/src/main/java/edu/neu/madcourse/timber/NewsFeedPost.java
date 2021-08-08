package edu.neu.madcourse.timber;

public class NewsFeedPost {
    public String username;
    public int post_id;
    public String description;

    public NewsFeedPost() {
    }

    public NewsFeedPost(String username, int post_id, String description) {
        this.username = username;
        this.post_id = post_id;
        this.description = description;
    }

    public String getUsername() {
        return this.username;
    }

    public int getPost_id() {
        return this.post_id;
    }

    public String getDescription() {
        return this.description;
    }
}
