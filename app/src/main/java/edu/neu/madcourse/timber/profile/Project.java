package edu.neu.madcourse.timber.profile;

public class Project {
    public String username;
    public int image;
    public String description;

    public Project() {
    }

    public Project(String username, int image, String description) {
        this.username = username;
        this.image = image;
        this.description = description;
    }

    public String getUsername() {
        return this.username;
    }

    public int getImage() {
        return image;
    }

    public String getDescription() {
        return this.description;
    }
}
