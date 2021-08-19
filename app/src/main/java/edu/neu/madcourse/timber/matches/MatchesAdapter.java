package edu.neu.madcourse.timber.matches;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.neu.madcourse.timber.R;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesHolder>{
    private final ArrayList<Match> matchesHistory;
    private MatchClickListener matchClickListener;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    View view;
    StorageReference imageRef;

    public MatchesAdapter(ArrayList<Match> matchesHistory) {
        this.matchesHistory = matchesHistory;
    }

    @Override
    public MatchesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_match, parent, false);
        return new MatchesHolder(view, matchClickListener);
    }

    @Override
    public void onBindViewHolder(MatchesHolder holder, int position) {
        Match currentItem = matchesHistory.get(position);
        if (currentItem != null) {
            Log.e("onBindViewHolder", currentItem.toString());
            Log.e("onBindViewHolder", currentItem.getProjectName());
            holder.contractor_id.setText(currentItem.getContractor_id());
            holder.projectName.setText(currentItem.getProjectName());
            if( currentItem.getImage() != null) {

                 imageRef = storageReference.child(currentItem.getImage());

            } else {
                 imageRef = storageReference.child("default_profile_pic.PNG");
            }
            Glide.with(view)
                    .load(imageRef)
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return matchesHistory.size();
    }


    public void setOnMatchClickListener(MatchClickListener matchClickListener) {
        this.matchClickListener = matchClickListener;
    }
}
