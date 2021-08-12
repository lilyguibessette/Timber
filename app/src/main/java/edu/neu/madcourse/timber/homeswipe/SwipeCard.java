package edu.neu.madcourse.timber.homeswipe;

public class SwipeCard {
    private String image;
    private String name;
    private String details;


    public SwipeCard() {
    }

    public SwipeCard(String image, String name, String details) {
        this.image = image;
        this.name = name;
        this.details = details;
    }

    public String getImage() {
        return image;
    }

    public String getUsername() {
        return name;
    }

    public String getDetails() {
        return details;
    }
}