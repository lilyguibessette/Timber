package edu.neu.madcourse.timber.profile;

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
import edu.neu.madcourse.timber.users.Project;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectHolder>{
    private final ArrayList<Project> projectList;
    View view;

    public ProjectAdapter(ArrayList<Project> projectList) {
        this.projectList = projectList;
    }

    @Override
    public ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new ProjectHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectHolder holder, int position) {
        Project currentItem = projectList.get(position);
        if (currentItem != null) {
            Log.e("onBindViewHolder", currentItem.toString());
            holder.username.setText(currentItem.getUsername());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageReference.child(currentItem.getImage());
            Glide.with(view)
                    .asBitmap()
                    .load(imageRef)
                    .into(holder.image);
            holder.description.setText(currentItem.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

}
