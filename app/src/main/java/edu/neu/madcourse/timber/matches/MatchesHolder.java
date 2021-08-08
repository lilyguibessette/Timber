package edu.neu.madcourse.timber.matches;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.timber.R;

public class MatchesHolder extends RecyclerView.ViewHolder {

    public TextView username;
    public ImageView image;
    public TextView last_message;

    public MatchesHolder(View matchesView) {
        super(matchesView);
        username = matchesView.findViewById(R.id.match_username);
        image = matchesView.findViewById(R.id.match_image);
        last_message = matchesView.findViewById(R.id.match_last_message);
    }
}
