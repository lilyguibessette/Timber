package edu.neu.madcourse.timber.profile.select_project;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.profile.ProjectHolder;
import edu.neu.madcourse.timber.users.Project;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class SelectProjectAdapter extends RecyclerView.Adapter<SelectProjectAdapter.ViewHolder>{
    private final ArrayList<String> projectList;
    private int selectedPos = RecyclerView.NO_POSITION;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView projectName;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            projectName = view.findViewById(R.id.select_project_project_name);
        }

        public void setItem(String item) {
            projectName.setText(item);
        }

        @Override
        public void onClick(View view) {
            Log.e(TAG, "onClick " + getPosition() + " " + projectName.getText());
            Toast.makeText(view.getContext(), "Selected: " + projectName.getText() , Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("TimberSharedPref",
                    MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("ACTIVE_PROJECT", projectName.getText().toString());
            myEdit.commit();
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }
    }

    View view;

    public SelectProjectAdapter(ArrayList<String> projectList) {
        this.projectList = projectList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_select_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setSelected(selectedPos == position);
        String currentItem = projectList.get(position);
        Log.e(TAG,holder.toString());
        if (currentItem != null) {
            Log.e("onBindViewHolder", currentItem.toString());
            holder.projectName.setText(currentItem);
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

}
