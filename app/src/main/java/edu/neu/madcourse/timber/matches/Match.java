package edu.neu.madcourse.timber.matches;

public class Match {
    public String projectName;
    public int image;
    public String last_message;

    public Match() {
    }

    public Match(String projectName, int image, String last_message) {
        this.projectName = projectName;
        this.image = image;
        this.last_message = last_message;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public int getImage() {
        return image;
    }

    public String getLast_message() {
        return this.last_message;
    }
}
