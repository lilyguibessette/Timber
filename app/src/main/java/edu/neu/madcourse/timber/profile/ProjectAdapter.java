package edu.neu.madcourse.timber.profile;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.users.Project;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectHolder>{
    private final ArrayList<Project> projectList;

    public ProjectAdapter(ArrayList<Project> projectList) {
        this.projectList = projectList;
    }

    @Override
    public ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new ProjectHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectHolder holder, int position) {
        Project currentItem = projectList.get(position);
        if (currentItem != null) {
            Log.e("onBindViewHolder", currentItem.toString());
            holder.username.setText(currentItem.getUsername());
            String db_image = currentItem.getImage();

            holder.image;
            holder.description.setText(currentItem.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

}
