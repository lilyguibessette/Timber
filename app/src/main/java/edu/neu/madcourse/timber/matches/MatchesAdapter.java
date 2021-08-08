package edu.neu.madcourse.timber.matches;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.timber.R;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesHolder>{
    private final ArrayList<Match> matchesHistory;

    public MatchesAdapter(ArrayList<Match> matchesHistory) {
        this.matchesHistory = matchesHistory;
    }

    @Override
    public MatchesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_contacts, parent, false);
        return new MatchesHolder(view);
    }

    @Override
    public void onBindViewHolder(MatchesHolder holder, int position) {
        Match currentItem = matchesHistory.get(position);
        if (currentItem != null) {
            Log.e("onBindViewHolder", currentItem.toString());
            holder.username.setText(currentItem.getUsername());
            holder.image.setImageResource(currentItem.getImage());
            holder.last_message.setText(currentItem.getLast_message());
        }
    }

    @Override
    public int getItemCount() {
        return matchesHistory.size();
    }

}
