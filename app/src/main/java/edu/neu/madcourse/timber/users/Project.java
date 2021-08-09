package edu.neu.madcourse.timber.users;

import android.location.Location;

public class Project {
    public String username;
    public String image;
    public String description;
    public String project_id;
    public String project_name;
    public int budget;
    public String project_type;
    public Location location;

    public Project() {
    }

    // make project
    // project name
    // image
    // budget
    // location

    public Project(String username, String project_name, String image, int budget, String description, String project_type) {
        this.username = username;
        this.image = image;
        this.description = description;
        this.project_id = username + "_" + project_name;
        this.budget = budget;
        this.project_type = project_type;

    }

    public Project(String username, String project_name,  int budget, String description, String project_type) {
        this.username = username;
        this.image = "image placeholder.PNG";;
        this.description = description;
        this.project_id = username + "_" + project_name;
        this.budget = budget;
        this.project_type = project_type;

    }

    public Project(String username, String project_name, String image, int budget, String description, String project_type, Location location) {
        this.username = username;
        this.image = image;
        this.description = description;
        this.project_id = username + "_" + project_name;
        this.budget = budget;
        this.project_type = project_type;
        this.location = location;

    }

    public String getUsername() {
        return this.username;
    }

    public String getImage() {
        return this.image;
    }

    public String getDescription() {
        return this.description;
    }
}
