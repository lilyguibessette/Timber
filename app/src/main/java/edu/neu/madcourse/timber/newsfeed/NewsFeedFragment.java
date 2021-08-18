package edu.neu.madcourse.timber.newsfeed;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.users.Project;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFeedFragment} factory method to
 * create an instance of this fragment.
 */
public class NewsFeedFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    // Recycler view related variables
    private final ArrayList<Project> newsFeedHistory = new ArrayList<>();
    private RecyclerView newsFeedRecyclerView;
    private NewsFeedAdapter newsFeedAdapter;
    private RecyclerView.LayoutManager newsPostLayoutManager;
    private int newsFeedSize = 0;
    public Uri photoURI;

    // Database Resources
    private FirebaseDatabase database;
    private DatabaseReference completedProjectsRef;
    private ChildEventListener completedProjectsListener;

    private static final String KEY_OF_POST = "KEY_OF_POST";
    private static final String NUMBER_OF_POSTS = "NUMBER_OF_POSTS";

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    public static NewsFeedFragment newInstance() {
        NewsFeedFragment fragment = new NewsFeedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize our received history size to 0
        newsFeedSize = 0;

        // get saved state and initialize the recyclerview
        initialNewsFeedData(savedInstanceState);

        newsFeedHistory.add(new Project("apples", "image placeholder.PNG", "this is a test post 1", "TYPE PLUMBING"));
        newsFeedHistory.add(new Project("peaches", "image placeholder.PNG", "this is a test post 2", "TYPE PLUMBING"));
        newsFeedHistory.add(new Project("mangoes", "image placeholder.PNG", "this is a test post 3", "TYPE PLUMBING"));
        newsFeedHistory.add(new Project("watermelons", "image placeholder.PNG", "this is a test post 4", "TYPE PLUMBING"));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        Log.e(TAG, "We made it before the recycler view");

        createRecyclerView(view);
        Log.e(TAG, "We made it after the recycler view");

        //dispatchTakePictureIntent();

        return view;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        Log.e(TAG,"checking takepicture intent");
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(getActivity(),
                    "edu.neu.madcourse.timber",
                    photoFile);
            Log.e(TAG,"photoURI: " + photoURI);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        //}
        Log.e(TAG,"exiting dispatch take picture");
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = image.getAbsolutePath();
        Log.e(TAG,"file is: " + image + " storage dir: " + storageDir);
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");

                File f = new File(photoURI.getPath());
                Log.e(TAG,"What is immediate filesize? " + f.length());


                Log.e(TAG,"what is file? " + DocumentFile.fromSingleUri(getActivity(),photoURI).getName());

                f = new File(photoURI.getPath());
                Log.e(TAG,"what is upload filesize? " + f.length());

                Log.e(TAG,"starting file upload");

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference mainActivityImageRef = storageReference.child(photoURI.getLastPathSegment());

                UploadTask uploadTask = mainActivityImageRef.putFile(photoURI);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.e(TAG,"finish file upload");
                    }
                });
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    } //onActivityResult

    private void initialNewsFeedData(Bundle savedInstanceState) {

        // recreate the sticker history on orientation change or open
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_POSTS)) {
            if (newsFeedHistory == null || newsFeedHistory.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_POSTS);
                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String post_username = savedInstanceState.getString(KEY_OF_POST
                            + i + "0");
                    String post_image = savedInstanceState.getString(KEY_OF_POST
                            + i + "1");
                    String post_description = savedInstanceState.getString(KEY_OF_POST
                            + i + "2");
                    String post_type = savedInstanceState.getString(KEY_OF_POST +i + "3");
                    newsFeedHistory.add(new Project(
                            post_username, post_image, post_description, post_type
                    ));
                }
            }
        }



    }

    private void createRecyclerView(View view) {
        // Create the recyclerview and populate it with the history

        newsFeedRecyclerView = view.findViewById(R.id.news_feed_recycler);
        Log.e(TAG,"newsFeed: " + newsFeedRecyclerView.toString());
        newsPostLayoutManager = new LinearLayoutManager(view.getContext());
        newsFeedRecyclerView.setHasFixedSize(true);
        newsFeedAdapter = new NewsFeedAdapter(newsFeedHistory,photoURI);
        newsFeedRecyclerView.setAdapter(newsFeedAdapter);
        newsFeedRecyclerView.setLayoutManager(newsPostLayoutManager);
    }







    /**
     * LISTENERS FOR DATA CHANGES
     * - Listen for change for number of stickers sent
     * - Listen for change in all users to validate
     * - Listen for change in received history
     */
    private void createDatabaseResources() {
        database = FirebaseDatabase.getInstance();
        completedProjectsRef = database.getReference("COMPLETED_PROJECTS");
        setCompletedProjectsListener();
    }


    // sets listener for changes to received history; updates the messages received on device
    public void setCompletedProjectsListener(){
        completedProjectsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new data item has been added, add it to the list
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Project project = dataSnapshot.getValue(Project.class);
                Log.d(TAG, "onChildAdded:" + project.project_id);

                // Add new project from the db to this device's stickerhistory
                newsFeedHistory.add(0, project);

                // update recyclerView adapter to add the new project
                newsFeedAdapter.notifyItemInserted(0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        };
        completedProjectsRef.addChildEventListener(completedProjectsListener);
    }
}

//TODO - getting ERROR on this page for images
//  StorageException has occurred.
//    Object does not exist at location.
//     Code: -13010 HttpResult: 404