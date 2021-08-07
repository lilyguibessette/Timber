package edu.neu.madcourse.timber;

public class ItemModel {
    private int image;
    private String username, details;

    public ItemModel() {
    }

    public ItemModel(int image, String username, String details) {
        this.image = image;
        this.username = username;
        this.details = details;
    }

    public int getImage() {
        return image;
    }

    public String getUsername() {
        return username;
    }

    public String getDetails() {
        return details;
    }

}