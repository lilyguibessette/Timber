package edu.neu.madcourse.timber.profile.create_project;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.fcm_server.Utils;
import edu.neu.madcourse.timber.homeswipe.HomepageFragment;
import edu.neu.madcourse.timber.profile.ProfileFragment;
import edu.neu.madcourse.timber.users.Homeowner;
import edu.neu.madcourse.timber.users.Project;

public class CreateProjectDialogFragment extends DialogFragment {
    private static final String TAG = "CreateProjectDialogFragment";
    private static final int PICK_IMAGE = 100;

    private Button cancelButton, createButton;
    private String my_username, project_name, project_type, project_description, imgPath;
    private Location location;
    private Project project;
    private int budget;

    // items related to the update image section
    private ImageView imageView;
    private Bitmap bitmap;
    private InputStream inputStreamImg;
    private File destination = null;

    private SharedPreferences sharedPreferences;
    private DatabaseReference userRef;

    // instance for firebase storage and StorageReference
    private FirebaseStorage storage;
    private StorageReference storageReference;

    // get references to database
    private FirebaseDatabase database;
    private DatabaseReference projectRef;

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

        // get access the user's data
        sharedPreferences = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
        my_username = sharedPreferences.getString("USERNAME", null);

        // get image storage data
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseDatabase.getInstance();

        // define the interactive objects on the screen
        imageView = view.findViewById(R.id.add_image);
        createButton = view.findViewById(R.id.create_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        // default image path
        imgPath = "default_profile_pic.PNG";

        // when the user clicks ot add in an image
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "CreateProjectDialogFragment create click");

                // get the variables to update
                project_name = ((EditText) view.findViewById(R.id.create_project_name)).getText().toString();
                project_type = ((EditText) view.findViewById(R.id.create_project_type)).getText().toString();
                try {
                    budget = Integer.parseInt(((EditText) view.findViewById(R.id.create_budget)).getText().toString());
                } catch (Exception e) {
                    budget = 0;
                }
                project_description = ((EditText) view.findViewById(R.id.create_project_description)).getText().toString();

                // finding the location of the user and assigning it to the project
                Log.e(TAG, "attempting location");
                location = Utils.getLocation(getActivity(), getContext());
                Log.e(TAG, location.getLatitude() + " " + location.getLongitude());

                // send to database
                Log.e(TAG, my_username + " "
                        + project_name + " "
                        + project_type + " "
                        + budget + " "
                        + imgPath + " "
                        + project_description + " "
                        + location);
                project = new Project(my_username,
                        project_name,
                        project_type,
                        budget,
                        imgPath,
                        project_description,
                        location.getLatitude(),
                        location.getLongitude());
                Log.e(TAG, project.getProject_id());

                // add to the database
                addProjectToDB(project, my_username);

                // send the user back to the previous page (swiping)
                Log.e(TAG, "CreateProjectDialogFragment got to homepage on click");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new HomepageFragment());
                fragmentTransaction.addToBackStack(null);
                sharedPreferences.edit().putString("ACTIVE_PROJECT", project_name);
                Toast.makeText(getActivity(), "Project Created! Swipe to find a Contractor!",
                        Toast.LENGTH_SHORT).show();
                fragmentTransaction.commit();
            }
        });

        // when the user clicks to cancel, send to previous page
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "CreateProjectDialogFragment cancel click");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new ProfileFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    // add the project to the database
    private void addProjectToDB(Project project, String my_username) {
        new Thread(() -> {
            // get the project reference from the database
            projectRef = database.getReference("ACTIVE_PROJECTS/" + project.getProject_id());
            projectRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (projectRef != null) {
                        // set the image of the project
                        dataSnapshot.getValue(Project.class).setImage(project.getImage());

                        projectRef.setValue(project).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.w(TAG, "SUCCESS - received new project: "
                                        + project.toString());
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "FAILED - did not update project list: "
                                                + project.toString());
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "proj ref add proj onCancelled", databaseError.toException());
                }
            });

            // get the user reference from the database
            userRef = database.getReference("HOMEOWNERS/" + my_username);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                public Homeowner user;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // get other user so we can add a new message
                    user = dataSnapshot.getValue(Homeowner.class);
                    if (userRef != null && dataSnapshot != null && user != null) {

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
                                Log.w(TAG, "SUCCESS received new project: " + user.toString());

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "FAILED did not update project list: " + user.toString());
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "user ref add proj onCancelled", databaseError.toException());
                }
            });
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent gallery) {
        super.onActivityResult(requestCode, resultCode, gallery);
        if(resultCode == 0) { return; }

        inputStreamImg = null;
        Uri selectedImage = gallery.getData();

        try {
            // try to decipher the image
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().
                    getContentResolver(), selectedImage);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

            // save the image details
            destination = new File(getRealPathFromURI(selectedImage));
            imgPath = destination.getName();
            storageReference.child(imgPath).putFile(selectedImage);

            // set the imageView to show the new pic
            imageView.setImageBitmap(bitmap);

            // catch the try if any errors
        } catch (IOException e) {
            Log.e(TAG, "Error uploading photo");
            e.printStackTrace();
        }
        // otherwise log the photo details
        Log.e(TAG, "Picked photo: " + imgPath);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


}
