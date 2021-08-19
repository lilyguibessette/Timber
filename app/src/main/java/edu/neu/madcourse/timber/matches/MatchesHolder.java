package edu.neu.madcourse.timber.matches;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.timber.R;

public class MatchesHolder extends RecyclerView.ViewHolder {

    public TextView projectName;
    public ImageView image;
//    public TextView last_message;
    public TextView contractor_id;

    public MatchesHolder(View matchesView, MatchClickListener matchClickListener) {
        super(matchesView);
        projectName = matchesView.findViewById(R.id.match_project_id);
        contractor_id = matchesView.findViewById(R.id.match_username);
        Log.e("MatchesHolder", "MatchesHolder  findview username");
        image = matchesView.findViewById(R.id.match_image);
        //last_message = matchesView.findViewById(R.id.match_last_message);

        matchesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matchClickListener != null) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        matchClickListener.onMatchClick(projectName.getText().toString(), contractor_id.getText().toString());
                        Log.e("matchClickListener", "MatchesHolder onClick projectName" + projectName.getText().toString());
                        Log.e("matchClickListener", "MatchesHolder onClick contractor_id" + contractor_id.getText().toString());
                    }
                }
            }
        });
    }
}
