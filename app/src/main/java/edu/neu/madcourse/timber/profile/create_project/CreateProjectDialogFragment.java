package edu.neu.madcourse.timber.profile.create_project;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.fcm_server.Utils;
import edu.neu.madcourse.timber.homeswipe.HomepageFragment;
import edu.neu.madcourse.timber.profile.ProfileFragment;
import edu.neu.madcourse.timber.users.Homeowner;
import edu.neu.madcourse.timber.users.Project;

public class CreateProjectDialogFragment extends DialogFragment {
    private static final String TAG = "CreateProjectDialogFragment";
    private Button cancelButton;
    private Button createButton;
    private String project_name;
    private String project_type;
    private int budget;
    private String project_image;
    private String project_description;
    private String my_username;
    private Location location;

    public CreateProjectDialogFragment() {
        // Required empty public constructor
    }

    public static CreateProjectDialogFragment newInstance() {
        CreateProjectDialogFragment fragment = new CreateProjectDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_project, container, false);
        my_username = getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString(
                "USERNAME", null);
        createButton = view.findViewById(R.id.create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CreateProjectDialogFragment", "CreateProjectDialogFragment create click");
                project_name = ((EditText) view.findViewById(R.id.create_project_name)).getText().toString();
                project_type = ((EditText) view.findViewById(R.id.create_project_type)).getText().toString();
                try {
                    budget = Integer.parseInt(((EditText) view.findViewById(R.id.create_budget)).getText().toString());
                } catch(Exception e){
                    budget = 0;
                }
                // TODO: this might need to be a button to launch the photo library or something
                //project_image = ((EditText) view.findViewById(R.id.update_image)).getText().toString();
                project_description = ((EditText) view.findViewById(R.id.create_project_description)).getText().toString();
                //new Project(my_username, project_name, project_type, budget, project_image, project_description);
                Log.e(TAG, "attempting location");
                location = Utils.getLocation(getActivity(), getContext());
                Log.e(TAG, location.getLatitude() + " " +location.getLongitude());

                // send to database
                Log.e(TAG, my_username+ " "+project_name+ " "+ project_type+ " "+ budget+ " "+ project_image+ " "+ project_description+ " "+ location);
                Project project = new Project(my_username,project_name, project_type, budget, project_image, project_description, location.getLatitude(),location.getLongitude());
                Log.e("CreateProjectDialogFragment", project.getProject_id());

                addProjectToDB(project, my_username);

                Log.e("CreateProjectDialogFragment", "CreateProjectDialogFragment got to homepage on click");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new HomepageFragment());
                // Should this go to Swiping or Profile?
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(), "Project Created! Swipe to find a Contractor!" , Toast.LENGTH_SHORT).show();
                fragmentTransaction.commit();
            }
        });

        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CreateProjectDialogFragment", "CreateProjectDialogFragment cancel click");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new ProfileFragment());
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(), "going to cancel" , Toast.LENGTH_SHORT).show();
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    // send a sticker to another user's entry in the realtime db
    private void addProjectToDB(Project project, String my_username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // get references to database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // Update user stats for sending message
                DatabaseReference projectRef = database.getReference("ACTIVE_PROJECTS/"+project.getProject_id());
                projectRef.addValueEventListener(new ValueEventListener() {
                    public Project proj;
                    public Boolean first_change = true;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get other project so we can add a new message
                        proj = dataSnapshot.getValue(Project.class);
                        if (projectRef != null && first_change){
                            // add message to project
                            // set other project to the newly updates other project
                            projectRef.setValue(project).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w(TAG, "Update received new project: " + project.toString());
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "FAILED to update project list: " + project.toString());
                                        }
                                    });
                        }
                        first_change = false;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "proj ref add proj onCancelled", databaseError.toException());
                    }

                });

                DatabaseReference userRef = database.getReference("HOMEOWNERS/"+my_username);
                // update other user's message history with new message
                userRef.addValueEventListener(new ValueEventListener() {
                    public Homeowner user;
                    public Boolean first_change = true;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get other user so we can add a new message
                        user = dataSnapshot.getValue(Homeowner.class);
                        if (userRef != null && dataSnapshot != null && first_change && user != null){
                            // add message to user
                            ArrayList<String> test = user.getActiveProjectList();
                            Log.w(TAG, "test proj to user list: " + user.toString());
                            Log.w(TAG, "test proj to user list: " + user.getUsername());
                            Log.w(TAG, "test proj to user list: " + test.toString());
                            Log.w(TAG, "test proj to user list: " + project.getProject_id());
                            user.addActiveProject(project.getProject_id());
                            Log.w(TAG, "added proj to user list: " + user.toString());
                            //TODO fix -  not adding to db yet...
                            userRef.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w(TAG, "Update received new project: " + user.toString());

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "FAILED to update project list: " + user.toString());
                                        }
                                    });
                            first_change = false;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "user ref add proj onCancelled", databaseError.toException());
                    }

                });



            }
        }).start();
    }


}
