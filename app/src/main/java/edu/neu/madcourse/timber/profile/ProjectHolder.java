package edu.neu.madcourse.timber.profile;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.timber.R;

public class ProjectHolder extends RecyclerView.ViewHolder {

    public TextView username;
    public ImageView image;
    public TextView description;
    public TextView type;

    public ProjectHolder(View profileView) {
        super(profileView);
        username = profileView.findViewById(R.id.post_username);
        image = profileView.findViewById(R.id.post_image);
        description = profileView.findViewById(R.id.post_description);
        type = profileView.findViewById(R.id.post_type);
    }
}
