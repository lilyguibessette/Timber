package edu.neu.madcourse.timber.matches;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.timber.R;

public class MatchesHolder extends RecyclerView.ViewHolder {

    public TextView username;
    public ImageView image;
    public TextView last_message;

    public MatchesHolder(View matchesView, MatchClickListener matchClickListener) {
        super(matchesView);
        username = matchesView.findViewById(R.id.match_username);
        Log.e("MatchesHolder", "MatchesHolder  findview username");
        image = matchesView.findViewById(R.id.match_image);
        last_message = matchesView.findViewById(R.id.match_last_message);

        matchesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matchClickListener != null) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        matchClickListener.onMatchClick(username.getText().toString());
                        Log.e("matchClickListener", "MatchesHolder onClick");
                    }
                }
            }
        });
    }
}
