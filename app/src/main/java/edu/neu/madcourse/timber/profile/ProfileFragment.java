package edu.neu.madcourse.timber.profile;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.profile.create_project.CreateProjectDialogFragment;
import edu.neu.madcourse.timber.profile.select_project.SelectProjectDialogFragment;
import edu.neu.madcourse.timber.profile.update.UpdateContractorProfileDialogFragment;
import edu.neu.madcourse.timber.profile.update.UpdateHomeownerProfileDialogFragment;
import edu.neu.madcourse.timber.users.Contractor;
import edu.neu.madcourse.timber.users.Homeowner;
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
    String other_username;
    Button action_button;
    Button select_button;
    Button profile_settings;

    int discrete;
    int start = 0; //you need to give starting value of SeekBar
    int end = 1000; //you need to give end value of SeekBar
    int start_pos = 20; //you need to give starting position value of SeekBar

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
        Project test_project = new Project("mangoes", "TEST", "PLUMBING", 7000, "image placeholder.PNG", "completedProjectsthis is a test post 3");
        projects.add(test_project);
        projects.add(test_project);
        projects.add(test_project);
        projects.add(test_project);


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.e(TAG, "We made it before the recycler view");

        createRecyclerView(view);
        Log.e(TAG, "We made it after the recycler view");

        //TextView profile_username = view.findViewById(R.id.profile_username);
        //profile_username.setText(my_username);
        action_button = view.findViewById(R.id.profile_action_button);
        select_button = view.findViewById(R.id.profile_action_button_select);
        if (my_usertype != null && my_usertype.equals(HOMEOWNERS)) {
            //set text
            action_button.setText("+");

        } else {
            action_button.setText("RADIUS");
        }
        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (my_usertype.equals(HOMEOWNERS)) {
                    Log.e("ProfileFragment", "ProfileFragment to update homeowner");
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, new CreateProjectDialogFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    View updateRadiusView = getLayoutInflater().inflate(R.layout.update_radius, null);
                    SeekBar seek = (SeekBar) updateRadiusView.findViewById(R.id.seekBar);
                    int start_position = (int) (((start_pos - start) / (end - start)) * 100);
                    discrete = start_pos;
                    seek.setProgress(start_position);
                    seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            // TODO Auto-generated method stub
                            Log.e(TAG, "discrete = " + String.valueOf(discrete));
                            Toast.makeText(getContext(), "discrete = " + String.valueOf(discrete), Toast.LENGTH_SHORT).show();
                        }


                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            float temp = progress;
                            float dis = end - start;
                            discrete = (int) (start + ((temp / 100) * dis));
                        }
                    });
                    Button confirm = (Button) updateRadiusView.findViewById(R.id.confirm);

                    dialogBuilder.setView(updateRadiusView);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            update_profile();
                            dialog.dismiss();
                        }
                    });
                }

            }
        });

        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (my_usertype.equals(HOMEOWNERS)) {
                    Log.e("ProfileFragment", "ProfileFragment to select active project");

                    projectsRef.orderByChild("username").equalTo(my_username).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@androidx.annotation.NonNull @NotNull DataSnapshot snapshot) {
                            Map<String,Object> projectData = (Map<String,Object>) snapshot.getValue();

                            if(Objects.isNull(projectData)){
                                Toast.makeText(getActivity(), "No Projects to select! Please create a project" , Toast.LENGTH_SHORT).show();
                                return;
                            } else{
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.container, new SelectProjectDialogFragment());
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

                        }
                    });


                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    View updateRadiusView = getLayoutInflater().inflate(R.layout.update_radius, null);
                    SeekBar seek = (SeekBar) updateRadiusView.findViewById(R.id.seekBar);
                    int start_position = (int) (((start_pos - start) / (end - start)) * 100);
                    discrete = start_pos;
                    seek.setProgress(start_position);
                    seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            // TODO Auto-generated method stub
                            Log.e(TAG, "discrete = " + String.valueOf(discrete));
                            Toast.makeText(getContext(), "discrete = " + String.valueOf(discrete), Toast.LENGTH_SHORT).show();
                        }


                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            float temp = progress;
                            float dis = end - start;
                            discrete = (int) (start + ((temp / 100) * dis));
                        }
                    });
                    Button confirm = (Button) updateRadiusView.findViewById(R.id.confirm);

                    dialogBuilder.setView(updateRadiusView);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            update_profile();
                            dialog.dismiss();
                        }
                    });
                }

            }
        });

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

    private void update_profile() {
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
                        if (my_usertype.equals(HOMEOWNERS)) {
                            Homeowner my_user = dataSnapshot.getValue(Homeowner.class);
                            //my_user.setImage();
                            // add setters to my_user
                            myUserRef.setValue(my_user);
                        } else {
                            Contractor my_user = dataSnapshot.getValue(Contractor.class);
                            my_user.setRadius(discrete);
                            Toast.makeText(getActivity(), "Discrete is " + discrete
                                    + " so changed radius to " + my_user.getRadius(),
                                    Toast.LENGTH_SHORT).show();
                            myUserRef.setValue(my_user);
                        }

                    } else {
                        if (my_usertype.equals(HOMEOWNERS)) {
                            myUserRef.setValue(new Homeowner(my_username,
                                    CLIENT_REGISTRATION_TOKEN,
                                    //  location,
                                    my_param1,
                                    my_param2,
                                    my_email,
                                    my_zip,
                                    my_phone));

                        } else {
                            myUserRef.setValue(new Contractor(my_username,
                                    CLIENT_REGISTRATION_TOKEN,
                                    //       location,
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

    // add child event listeners for database for new cards for projects

}