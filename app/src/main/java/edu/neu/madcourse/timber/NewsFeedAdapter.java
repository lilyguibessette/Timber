package edu.neu.madcourse.timber;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedHolder>{
    private final ArrayList<NewsFeedPost> newsFeedHistory;

    public NewsFeedAdapter(ArrayList<NewsFeedPost> newsFeedHistory) {
        this.newsFeedHistory = newsFeedHistory;
    }

    @Override
    public NewsFeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new NewsFeedHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsFeedHolder holder, int position) {
        NewsFeedPost currentItem = newsFeedHistory.get(position);
        if (currentItem != null) {
            Log.e("onBindViewHolder", currentItem.toString());
            holder.post_username.setText(currentItem.getUsername());
            holder.post_image_id.setImageResource(currentItem.getPost_id());
            holder.post_description.setText(currentItem.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return newsFeedHistory.size();
    }

}
