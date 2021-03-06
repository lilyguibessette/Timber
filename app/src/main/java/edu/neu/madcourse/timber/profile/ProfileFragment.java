package edu.neu.madcourse.timber.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.profile.update.UpdateContractorProfileDialogFragment;
import edu.neu.madcourse.timber.profile.update.UpdateHomeownerProfileDialogFragment;
import edu.neu.madcourse.timber.users.Contractor;
import edu.neu.madcourse.timber.users.Project;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    //TODO Listeners aren't working !!!
    // Recycler view related variables
    private final ArrayList<Project> projects = new ArrayList<>();
    private RecyclerView activeProjectsRecyclerView;
    private LinearLayoutManager activeProjectLayoutManager;
    private ProjectAdapter activeProjectsAdapter;
    private int projectSize = 0;
    private RecyclerView completedProjectsRecyclerView;
    private LinearLayoutManager completedProjectLayoutManager;
    private ProjectAdapter completedProjectsAdapter;
    private int completedProjectSize = 0;
    private Contractor selfContractor;
    public String my_username;
    public String my_usertype;
    public String my_param1;
    public String my_param2;
    public String my_email;
    public String my_zip;
    public String my_phone;

    private static final String KEY_OF_MATCH = "KEY_OF_MATCH";
    private static final String NUMBER_OF_MATCHES = "NUMBER_OF_MATCHES";
    private static final String USERNAME = "USERNAME";
    private static final String USERTYPE = "USERTYPE";
    private static final String CONTRACTORS = "CONTRACTORS";
    private static final String HOMEOWNERS = "HOMEOWNERS";
    private static String CLIENT_REGISTRATION_TOKEN;
    private static final String TAG = "MatchesFragment";
    private DatabaseReference contractorsRef = FirebaseDatabase.getInstance().getReference(
            "CONTRACTORS");

    String other_username;
    Button profile_settings;

    int discrete;
    int start = 0; //you need to give starting value of SeekBar
    int end = 1000; //you need to give end value of SeekBar
    int start_pos = 20; //you need to give starting position value of SeekBar

    private FirebaseDatabase database;
    private DatabaseReference myUserProjListRef;
    private ArrayList<ChildEventListener> completedProjectsListenerList;
    private ArrayList<DatabaseReference> completedProjectsRefList;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
        DatabaseReference projectsRef = FirebaseDatabase.getInstance().getReference(
                "ACTIVE_PROJECTS");
        // connect to the database and look at the users
        my_username = sharedPreferences.getString(USERNAME, null);
        my_usertype = sharedPreferences.getString(USERTYPE, null);

        // Initialize our received history size to 0
        projectSize = 0;

        // get saved state and initialize the recyclerview
        initialProjectsData(savedInstanceState);
        createDatabaseResources();
        //Project test_project = new Project("mangoes", "TEST", "PLUMBING", 7000, "image placeholder.PNG", "completedProjectsthis is a test post 3");
        //projects.add(test_project);
        //projects.add(test_project);
        //projects.add(test_project);
        //projects.add(test_project);


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.e(TAG, "We made it before the recycler view");

        createRecyclerView(view);
        Log.e(TAG, "We made it after the recycler view");

        //TextView profile_username = view.findViewById(R.id.profile_username);
        //profile_username.setText(my_username);


        profile_settings = view.findViewById(R.id.profile_settings);
        profile_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (my_usertype.equals(HOMEOWNERS)) {
                    Log.e("ProfileFragment", "ProfileFragment to update homeowner");
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, new UpdateHomeownerProfileDialogFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    Log.e("ProfileFragment", "ProfileFragment to update contractor");
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, new UpdateContractorProfileDialogFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                // use dialog for add link

            }
        });

        return view;
    }

    private void initialProjectsData(Bundle savedInstanceState) {

        // recreate the sticker history on orientation change or open
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_MATCHES)) {
            if (projects == null || projects.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_MATCHES + "_ACTIVE");
                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String username = savedInstanceState.getString(KEY_OF_MATCH + "_ACTIVE"
                            + i + "0");
                    String description = savedInstanceState.getString(KEY_OF_MATCH + "_ACTIVE"
                            + i + "1");
                    String image = savedInstanceState.getString(KEY_OF_MATCH + "_ACTIVE"
                            + i + "2");
                    String type = savedInstanceState.getString(KEY_OF_MATCH + "_ACTIVE"
                            + i + "3");
                    projects.add(new Project(username, type, image,
                            description));
                }
            }

            if (projects == null || projects.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_MATCHES + "_COMPLETED");
                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String username = savedInstanceState.getString(KEY_OF_MATCH + "_COMPLETED"
                            + i + "0");
                    String description = savedInstanceState.getString(KEY_OF_MATCH + "_COMPLETED"
                            + i + "1");
                    String image = savedInstanceState.getString(KEY_OF_MATCH + "_COMPLETED"
                            + i + "2");
                    String type = savedInstanceState.getString(KEY_OF_MATCH + "_COMPLETED" + i + "3");
                    projects.add(new Project(username, type, image,
                            description));
                }
            }
        }
    }

    private void createRecyclerView(View view) {
        // Create the recyclerview and populate it with the history
        activeProjectsRecyclerView = view.findViewById(R.id.projects_feed);
        Log.e(TAG, "Profile projects: " + activeProjectsRecyclerView.toString());
        activeProjectLayoutManager = new LinearLayoutManager(view.getContext());
        activeProjectsRecyclerView.setHasFixedSize(true);
        activeProjectsAdapter = new ProjectAdapter(projects);
        activeProjectsRecyclerView.setAdapter(activeProjectsAdapter);
        activeProjectLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        activeProjectsRecyclerView.setLayoutManager(activeProjectLayoutManager);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.DOWN) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(getContext(), "Project Complete!", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getLayoutPosition();
                Project project = projects.get(position);
                projects.remove(position);
                activeProjectsAdapter.notifyItemRemoved(position);
                // project change status in database
                // get project id do stuff etc
                //TODO
            }
        });
        itemTouchHelper.attachToRecyclerView(activeProjectsRecyclerView);
    }


    // add child event listeners for database for new cards for projects
    private void createDatabaseResources() {
        database = FirebaseDatabase.getInstance();
        if (my_usertype != null && my_usertype.equals("HOMEOWNERS")) {
            myUserProjListRef = database.getReference("HOMEOWNERS/"+my_username+"/completedProjectList");
        } else{
            myUserProjListRef = database.getReference("CONTRACTORS/"+my_username+"/completedProjectList");
        }
        myUserProjListRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                ArrayList<String> projectData = (ArrayList<String>) snapshot.getValue();
                if( projectData != null){
                for(String each : projectData){
                    if (!each.equals("EMPTY")) {
                        Log.e(TAG, each);
                        DatabaseReference completedProjectsRef = database.getReference("COMPLETED_PROJECTS/" + each);
                        Log.e(TAG, completedProjectsRef.toString());
                        setCompletedProjectsListener(completedProjectsRef);
                    }
                };}

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

            }
        });
    }


    // sets listener for changes to received history; updates the messages received on device
    public void setCompletedProjectsListener(DatabaseReference completedProjectsRef){
        ValueEventListener completedProjectsListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                Log.e(TAG, "274 onChildAdded:" + snapshot);
                Project project = snapshot.getValue(Project.class);
                Log.e(TAG, "276 project:" + project);
                if(!Objects.isNull(project)) {
                    Log.e(TAG, "277 onChildAdded:" + project.project_id);

                    // Add new project from the db to this device's stickerhistory
                    projects.add(0, project);
                    Log.e(TAG, "281 projects:" + projects);

                    // update recyclerView adapter to add the new project
                    activeProjectsAdapter.notifyItemInserted(0);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

            }
        };
        completedProjectsRef.addListenerForSingleValueEvent(completedProjectsListener);
    }
}

/*

    private void create_project() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
            // connect to the database and look at the users
            my_username = sharedPreferences.getString(USERNAME, null);
            my_usertype = sharedPreferences.getString(USERTYPE, null);
            DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference(
                    my_usertype + "/" + my_username);

            myUserRef.addValueEventListener(new ValueEventListener() {
                public User my_user;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // if the user exists, get their data
                    if (dataSnapshot.exists()) {
                        //my_user = dataSnapshot.getValue(User.class);
                    } else {
                        if (my_usertype.equals(HOMEOWNERS)) {
                            myUserRef.setValue(new Homeowner(my_username,
                                    CLIENT_REGISTRATION_TOKEN,
                                    my_param1,
                                    my_param2,
                                    my_email,
                                    my_zip,
                                    my_phone));
                        } else {
                            myUserRef.setValue(new Contractor(my_username,
                                    CLIENT_REGISTRATION_TOKEN,
                                    my_param1,
                                    my_param2,
                                    my_email,
                                    my_zip,
                                    my_phone));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // if getting post failed, log a message
                    Log.w(TAG, "update profile onCancelled",
                            databaseError.toException());
                }
            });

        }).start();
    }
 */