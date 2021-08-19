package edu.neu.madcourse.timber.homeswipe;

public class SwipeCard {
    private String image;
    private String name;
    private String details;
    private String zipcode;


    public SwipeCard() {
    }

    public SwipeCard(String image, String name, String details, String zipcode) {
        this.image = image;
        this.name = name;
        this.details = details;
        this.zipcode = zipcode;
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

    public String getZipcode() {
        return zipcode;
    }
}