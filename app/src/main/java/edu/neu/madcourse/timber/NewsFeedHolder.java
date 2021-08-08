package edu.neu.madcourse.timber;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class NewsFeedHolder extends RecyclerView.ViewHolder {

    public TextView post_username;
    public ImageView post_image_id;
    public TextView post_description;

    public NewsFeedHolder(View newsFeedView) {
        super(newsFeedView);
        post_username = newsFeedView.findViewById(R.id.post_username);
        post_image_id = newsFeedView.findViewById(R.id.post_image);
        post_description = newsFeedView.findViewById(R.id.post_description);
    }
}
