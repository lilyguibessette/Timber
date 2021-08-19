package edu.neu.madcourse.timber.matches;

public class Match {
    public String projectName;
    public String image;
    public String contractor_id;

    public Match() {
    }

    public Match(String projectName, String image, String contractor_id) {
        this.projectName = projectName;
        this.image = image;
        this.contractor_id = contractor_id;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public String getImage() {
        return image;
    }


    public String getContractor_id() {return this.contractor_id;}
}
