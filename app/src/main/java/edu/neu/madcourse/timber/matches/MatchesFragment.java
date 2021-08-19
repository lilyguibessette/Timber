package edu.neu.madcourse.timber.matches;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.messages.Message;
import edu.neu.madcourse.timber.messages.MessagesFragment;
import edu.neu.madcourse.timber.users.Contractor;
import edu.neu.madcourse.timber.users.Homeowner;
import edu.neu.madcourse.timber.users.Project;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchesFragment} factory method to
 * create an instance of this fragment.
 */
public class MatchesFragment extends Fragment {
    // Recycler view related variables
    private ArrayList<Match> matchesHistory = new ArrayList<>();
    private RecyclerView matchesRecyclerView;
    private RecyclerView.LayoutManager matchesLayoutManager;
    private MatchesAdapter matchesAdapter;
    private int matchesSize = 0;
    public String my_username;
    public String my_usertype;
    private static final String KEY_OF_MATCH = "KEY_OF_MATCH";
    private static final String NUMBER_OF_MATCHES = "NUMBER_OF_MATCHES";

    private static final String TAG = "MatchesFragment";

    private FirebaseDatabase database;
    private DatabaseReference activeProjectsRef;
    private DatabaseReference myUserRef;
    private ChildEventListener myUserListener;

    public MatchesFragment() {
        // Required empty public constructor
    }

    public static MatchesFragment newInstance() {
        MatchesFragment fragment = new MatchesFragment();
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
        matchesSize = 0;
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
        // connect to the database and look at the users
        my_username = sharedPreferences.getString("USERNAME", null);
        my_usertype = sharedPreferences.getString("USERTYPE", null);
        // get saved state and initialize the recyclerview
        createDatabaseResources();
        initialMatchesData(savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_matches, container, false);
        Log.e(TAG, "We made it before the recycler view");

        createRecyclerView(view);
        Log.e(TAG, "We made it after the recycler view");
        return view;
    }



    private void initialMatchesData(Bundle savedInstanceState) {

        // recreate the sticker history on orientation change or open
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_MATCHES)) {
            if (matchesHistory == null || matchesHistory.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_MATCHES);
                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String projectName = savedInstanceState.getString(KEY_OF_MATCH
                            + i + "0");
                    String image = savedInstanceState.getString(KEY_OF_MATCH
                            + i + "1");
                    String contractor_id = savedInstanceState.getString(KEY_OF_MATCH
                            + i + "2");
                    matchesHistory.add(new Match(projectName, image, contractor_id));
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Save the sticker history so we can retrieve it on orientation change
        int size = matchesHistory == null ? 0 : matchesHistory.size();
        outState.putInt(NUMBER_OF_MATCHES, size);
        for (int i = 0; i < size; i++) {
            outState.putString(KEY_OF_MATCH
                    + i + "0", matchesHistory.get(i).getProjectName());
            outState.putString(KEY_OF_MATCH
                    + i + "1", matchesHistory.get(i).getImage());
            outState.putString(KEY_OF_MATCH
                    + i + "2", matchesHistory.get(i).getContractor_id());
        }
        super.onSaveInstanceState(outState);
    }

    private void createRecyclerView(View view) {
        // Create the recyclerview and populate it with the history
        matchesRecyclerView = view.findViewById(R.id.matches_rv);
        Log.e(TAG, "Matches: " + matchesRecyclerView.toString());
        matchesLayoutManager = new LinearLayoutManager(view.getContext());
        matchesRecyclerView.setHasFixedSize(true);
        matchesAdapter = new MatchesAdapter(matchesHistory);
        MatchClickListener matchClickListener = new MatchClickListener() {
            @Override
            public void onMatchClick(String project_id, String contractor_id) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TimberSharedPref",
                        MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("MSGPROJID", project_id);
                myEdit.putString("MSGCONID", contractor_id);
                myEdit.commit();
                Log.e("MatchesFragment", "createrecyclerview onMatchClick");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new MessagesFragment(project_id, contractor_id));
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(), "going to msgs from" + project_id + " with " + contractor_id, Toast.LENGTH_SHORT).show();
                fragmentTransaction.commit();

            }
        };
        matchesAdapter.setOnMatchClickListener(matchClickListener);
        matchesRecyclerView.setAdapter(matchesAdapter);
        matchesRecyclerView.setLayoutManager(matchesLayoutManager);

    }

    //not used
    private FragmentTransaction switchFragment(Fragment targetFragment) {
       /* new Runnable() {
            @Override
            public void run() {

                getChildFragmentManager().beginTransaction().replace(R.id.container, targetFragment).commit();
            }
        };
*/
        // Get an instance of the thing
        FragmentTransaction transaction = getChildFragmentManager()
                .beginTransaction();
        transaction
                .replace(R.id.container, targetFragment);

        Log.e("FragmentTransaction", "transaction");

        return transaction;
    }


    // add child event listeners for database for new cards for projects
    private void createDatabaseResources() {
        database = FirebaseDatabase.getInstance();
        if (my_usertype != null && my_usertype.equals("HOMEOWNERS")) {
            myUserRef = database.getReference("HOMEOWNERS/" + my_username + "/matchList");
            DatabaseReference activeProjectsRef = database.getReference("HOMEOWNERS/" + my_username + "/activeProjectList");
            activeProjectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    ArrayList<String> projectData = (ArrayList<String>) snapshot.getValue();
                    if (projectData != null) {
                        for (String each : projectData) {
                            if (!each.equals("EMPTY")) {
                                Log.e(TAG, each);
                                DatabaseReference activeProjectsRef = database.getReference("ACTIVE_PROJECTS/" + each);
                                Log.e(TAG, activeProjectsRef.toString());
                                setHomeOwnerActiveProjectsListener(activeProjectsRef);
                            }
                        }
                        ;
                    }

                }

                @Override
                public void onCancelled(@NotNull DatabaseError error) {

                }
            });
        } else {
            myUserRef = database.getReference("CONTRACTORS/" + my_username + "/matchList");
            myUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    ArrayList<String> projectData = (ArrayList<String>) snapshot.getValue();
                    if (projectData != null) {
                            for (String each : projectData) {
                                if (!each.equals("EMPTY")) {
                                    Log.e(TAG, each);
                                    DatabaseReference activeProjectRef = database.getReference("ACTIVE_PROJECTS/" + each);
                                    Log.e(TAG, activeProjectRef.toString());
                                    setContractorActiveProjectsListener(activeProjectRef);
                                }
                            }

                        ;
                    }

                }

                @Override
                public void onCancelled(@NotNull DatabaseError error) {

                }
            });
        }

    }


    // sets listener for changes to received history; updates the messages received on device
    public void setHomeOwnerActiveProjectsListener(DatabaseReference activeProjectRef) {
        ValueEventListener activeProjectsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                Log.e(TAG, "onChildAdded:" + snapshot.getKey());
                Project project = snapshot.getValue(Project.class);
                if (project != null){
                Log.e(TAG, "onChildAdded:" + project.project_id);
                List<String> contractors =  project.getMatchList();
                for (String each : contractors) {
                    if (!each.equals("EMPTY")) {
                        Log.e(TAG, each);
                        Match match = new Match(project.getProject_id(), project.getImage(),each );
                        matchesHistory.add(0, match);
                        matchesAdapter.notifyItemInserted(0);
                        Log.e(TAG, match.toString() + " " +match.getProjectName() +" " + match.getContractor_id());
                    }
                }
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

            }
        };
        activeProjectRef.addListenerForSingleValueEvent(activeProjectsListener);
    }

    // sets listener for changes to received history; updates the messages received on device
    public void setContractorActiveProjectsListener(DatabaseReference activeProjectRef) {
        ValueEventListener activeProjectsListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                Log.e(TAG, "onChildAdded:" + snapshot.getKey());
                Project project = snapshot.getValue(Project.class);
                if (project != null) {
                    Log.e(TAG, "onChildAdded:" + project.project_id);
                    Match match = new Match(project.getProject_id(), project.getImage(), project.getUsername());
                    matchesHistory.add(0, match);
                    matchesAdapter.notifyItemInserted(0);
                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

            }
        };
        activeProjectRef.addListenerForSingleValueEvent(activeProjectsListener);
    }

}
/*
    public void makeMatchesHomeowner(){
        matchesHistory = new ArrayList<Match>();
        DatabaseReference projectsRef = FirebaseDatabase.getInstance().getReference(
                "ACTIVE_PROJECTS");
        Log.e(TAG, "username " + my_username + " and in homeowner match maker");
        projectsRef.orderByChild("username").equalTo(my_username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull @NotNull DataSnapshot snapshot) {
                Map<String,Object> projectData = (Map<String,Object>) snapshot.getValue();
                for(Map.Entry<String, Object> each : projectData.entrySet()){
                    Map singleProject = (Map) each.getValue();
                    ArrayList<String> matchList = (ArrayList<String>) singleProject.get("matchList");
                    for (int i=1; i< matchList.size(); i++) {
                        String match = matchList.get(i);
                        //TODO UPDATE from project_id to projectname
                        matchesHistory.add(0, new Match((String) singleProject.get("project_id"), (String) singleProject.get("image"), match));
                        matchesAdapter.notifyItemInserted(0);
                    }
                };

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void makeMatchesContractor(ArrayList<String> list ) {
        matchesHistory = new ArrayList<Match>();
        DatabaseReference projectsRef = FirebaseDatabase.getInstance().getReference(
                "ACTIVE_PROJECTS");
        Log.e(TAG, "username " + my_username + " and in contractor match maker");
        for (String project : list) {
            projectsRef.orderByChild("project_id").equalTo(project).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull @NotNull DataSnapshot snapshot) {
                    Map<String, Object> projectData = (Map<String, Object>) snapshot.getValue();
                    for (Map.Entry<String, Object> each : projectData.entrySet()) {
                        Map singleProject = (Map) each.getValue();
                        //TODO UPDATE from project_id to projectname
                            matchesHistory.add(0, new Match((String) singleProject.get("project_id"), (String) singleProject.get("image"), (String) singleProject.get("username")));
                            matchesAdapter.notifyItemInserted(0);

                    };
                }
                @Override
                public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {
                }
            });
        }
    }

    // sets listener for changes to received history; updates the messages received on device
    public void setUserListener(){
        myUserListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new data item has been added, add it to the list
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                if (my_usertype != null && my_usertype.equals("HOMEOWNERS")) {
                    makeMatchesHomeowner();
                } else{
                    Contractor user = dataSnapshot.getValue(Contractor.class);
                    ArrayList<String> list = user.getMatchList();
                    makeMatchesContractor(list);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                if (my_usertype != null && my_usertype.equals("HOMEOWNERS")) {
                    makeMatchesHomeowner();
                } else{
                    Contractor user = dataSnapshot.getValue(Contractor.class);
                    ArrayList<String> list = user.getMatchList();
                    makeMatchesContractor(list);
                }
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
        myUserRef.addChildEventListener(myUserListener);
    }


}

 */