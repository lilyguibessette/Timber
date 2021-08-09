package edu.neu.madcourse.timber.profile;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.users.Contractor;
import edu.neu.madcourse.timber.users.Homeowner;
import edu.neu.madcourse.timber.users.Project;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements CreateActionDialogListener {
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
        // connect to the database and look at the users
        my_username = sharedPreferences.getString(USERNAME, null);
        my_usertype = sharedPreferences.getString(USERTYPE, null);

        // Initialize our received history size to 0
        projectSize = 0;

        // get saved state and initialize the recyclerview
        initialProjectsData(savedInstanceState);

        projects.add(new Project("apples", "image placeholder.PNG", "completedProjectsthis is a test post 1", "TYPE PLUMBING"));
        projects.add(new Project("peaches", "image placeholder.PNG", "completedProjectsthis is a test post 2", "TYPE PLUMBING"));
        projects.add(new Project("mangoes", "image placeholder.PNG", "completedProjectsthis is a test post 3", "TYPE PLUMBING"));
        projects.add(new Project("watermelons", "image placeholder.PNG", "completedProjectsthis is a test post 4", "TYPE PLUMBING"));


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.e(TAG, "We made it before the recycler view");

        createRecyclerView(view);
        Log.e(TAG, "We made it after the recycler view");

        action_button = view.findViewById(R.id.profile_action_button);
        if (my_usertype != null && my_usertype.equals(HOMEOWNERS)) {
            //set text
            action_button.setText("Add New Project");

        } else {
            action_button.setText("Update Profile");
        }
        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // use dialog for add link
                startActionDialog();
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
                    String description = savedInstanceState.getString(KEY_OF_MATCH+ "_ACTIVE"
                            + i + "1");
                    String image = savedInstanceState.getString(KEY_OF_MATCH + "_ACTIVE"
                            + i + "2");
                    String type = savedInstanceState.getString(KEY_OF_MATCH+ "_ACTIVE"
                            + i + "3");
                    projects.add(new Project(username,image,
                            description, type));
                }
            }

            if (projects == null || projects.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_MATCHES + "_COMPLETED");
                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String username = savedInstanceState.getString(KEY_OF_MATCH + "_COMPLETED"
                            + i + "0");
                    String description = savedInstanceState.getString(KEY_OF_MATCH+ "_COMPLETED"
                            + i + "1");
                    String image = savedInstanceState.getString(KEY_OF_MATCH + "_COMPLETED"
                            + i + "2");
                    String type = savedInstanceState.getString(KEY_OF_MATCH +"_COMPLETED"+i + "3" );
                    projects.add(new Project(username,image,
                            description, type));
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

    public void startActionDialog() {
        if (my_usertype != null && my_usertype.equals(HOMEOWNERS)) {
            DialogFragment createProjectDialogFragment = new CreateProjectDialogFragment();
            createProjectDialogFragment.show(getChildFragmentManager(), "createProjectDialogFragment");
        } else {
            DialogFragment updateProfileDialogFragment = new UpdateProfileDialogFragment();
            updateProfileDialogFragment.show(getChildFragmentManager(), "updateProfileDialogFragment");
        }
    }

    public void onDialogPositiveClick(DialogFragment actionDialogFragment) {
        if (my_usertype != null && my_usertype.equals(HOMEOWNERS)) {
            // change to projects
            Dialog actionDialog = actionDialogFragment.getDialog();
            //radioGroupUserType = (RadioGroup) createUserDialog.findViewById(R.id.radiogroup_usertype);
            //int selectedUserType = radioGroupUserType.getCheckedRadioButtonId();
            Log.e(TAG, " ondialog pos click");

            my_username = ((EditText) actionDialog.findViewById(R.id.update_username)).getText().toString();
            my_param1 = ((EditText) actionDialog.findViewById(R.id.update_param1)).getText().toString();
            my_param2 = ((EditText) actionDialog.findViewById(R.id.update_param2)).getText().toString();
            my_email = ((EditText) actionDialog.findViewById(R.id.update_email)).getText().toString();
            my_zip = ((EditText) actionDialog.findViewById(R.id.update_zip)).getText().toString();
            my_phone = ((EditText) actionDialog.findViewById(R.id.update_phone)).getText().toString();
            //TODO SET IMAGE HERE?

            if (my_usertype != null && my_username != null
                    && my_param1 != null && my_param2 != null
                    && my_email != null && my_zip != null && my_phone != null) {
                actionDialog.dismiss();
                update_profile();
                Toast.makeText(getContext(), "Project Created!", Toast.LENGTH_SHORT).show();
                // move to swipe screen for contractors?
            } else {
                Toast.makeText(getActivity(), R.string.create_project_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            //TODO
            Dialog actionDialog = actionDialogFragment.getDialog();
            //radioGroupUserType = (RadioGroup) createUserDialog.findViewById(R.id.radiogroup_usertype);
            //int selectedUserType = radioGroupUserType.getCheckedRadioButtonId();
            Log.e(TAG, " ondialog pos click");

            my_username = ((EditText) actionDialog.findViewById(R.id.update_username)).getText().toString();
            my_param1 = ((EditText) actionDialog.findViewById(R.id.update_param1)).getText().toString();
            my_param2 = ((EditText) actionDialog.findViewById(R.id.update_param2)).getText().toString();
            my_email = ((EditText) actionDialog.findViewById(R.id.update_email)).getText().toString();
            my_zip = ((EditText) actionDialog.findViewById(R.id.update_zip)).getText().toString();
            my_phone = ((EditText) actionDialog.findViewById(R.id.update_phone)).getText().toString();
            //TODO SET IMAGE HERE?

            if (my_usertype != null && my_username != null
                    && my_param1 != null && my_param2 != null
                    && my_email != null && my_zip != null && my_phone != null) {
                actionDialog.dismiss();
                update_profile();
                Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                // move to swipe screen for contractors?
            } else {
                Toast.makeText(getActivity(), R.string.update_account_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment updateAccountDialog) {
        updateAccountDialog.dismiss();
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
                        if (my_usertype.equals(HOMEOWNERS) ){
                            Homeowner my_user = dataSnapshot.getValue(Homeowner.class);
                            //my_user.setImage();
                            // add setters to my_user
                            myUserRef.setValue(my_user);
                        } else{
                            Contractor my_user = dataSnapshot.getValue(Contractor.class);
                            myUserRef.setValue(my_user);
                        }

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