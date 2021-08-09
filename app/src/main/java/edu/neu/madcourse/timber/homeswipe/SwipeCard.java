package edu.neu.madcourse.timber.homeswipe;

public class SwipeCard {
    private int image;
    private String name;
    private String details;

    public SwipeCard() {
    }

    public SwipeCard(int image, String name, String details) {
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