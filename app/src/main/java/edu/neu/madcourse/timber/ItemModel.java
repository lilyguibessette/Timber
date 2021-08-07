package edu.neu.madcourse.timber;

public class ItemModel {
    private int image;
    private String name, details;

    public ItemModel() {
    }

    public ItemModel(int image, String name, String details) {
        this.image = image;
        this.name = name;
        this.details = details;
    }

    public int getImage() {
        return image;
    }

    public String getUsername() {
        return name;
    }

    public String getDetails() {
        return details;
    }
}