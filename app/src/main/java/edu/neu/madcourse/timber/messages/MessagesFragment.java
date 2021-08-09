package edu.neu.madcourse.timber.messages;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.matches.MatchesFragment;
import edu.neu.madcourse.timber.users.Homeowner;
import edu.neu.madcourse.timber.users.Project;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagesFragment} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment {
    // Recycler view related variables
    private final ArrayList<Message> messageHistory = new ArrayList<>();
    private RecyclerView messagesRecyclerView;
    private RecyclerView.LayoutManager messageLayoutManager;
    private int messagesSize = 0;
    private static final String KEY_OF_MSG = "KEY_OF_MSG";
    private static final String NUMBER_OF_MSGS = "NUMBER_OF_MSGS";
    private static final String TAG = "MessagesFragment";
    public String project_id;
    String my_username;
    String my_usertype;
    private ImageView back;
    private Button sendMessage;
    private ImageView markComplete;
    private ImageView unMatch;


    public MessagesFragment() {
        // Required empty public constructor
    }


    public MessagesFragment(String project_id) {
        this.project_id = project_id;
        // Required empty public constructor
    }

    public static MessagesFragment newInstance(String project_id) {
        MessagesFragment fragment = new MessagesFragment(project_id);
        if (fragment.getProject_id() != "FAKE") {
            Log.e(TAG, "got proj id from frag creator" + project_id + " " + fragment.getProject_id());
        }
        return fragment;
    }

    public  String getProject_id(){
        if (project_id == null){
            return "FAKE";
        }
        return this.project_id;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize our received history size to 0
        messagesSize = 0;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
        my_username = sharedPreferences.getString("USERNAME", "Not found");
        my_usertype = sharedPreferences.getString("USERTYPE", "Not found");
        String project_id2 = sharedPreferences.getString("MSGPROJID", "Not found");
        if (project_id == null){
            project_id = project_id2;
            Log.e(TAG, "got projid from shared pref");
        }


        // get saved state and initialize the recyclerview
        initialMessagesData(savedInstanceState);
        messageHistory.add(new Message("apples", "this is a test post 1"));
        messageHistory.add(new Message("peaches", "this is a test post 2"));
        messageHistory.add(new Message("mangoes", "this is a test post 3"));
        messageHistory.add(new Message("watermelons","this is a test post 4"));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recycler_message, container, false);
        createRecyclerView(view);
        return view;
    }

    private void initialMessagesData(Bundle savedInstanceState) {

        // recreate the sticker history on orientation change or open
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_MSGS)) {
            if (messageHistory == null || messageHistory.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_MSGS);
                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String username = savedInstanceState.getString(KEY_OF_MSG
                            + i + "0");
                    String message = savedInstanceState.getString(KEY_OF_MSG
                            + i + "1");
                    messageHistory.add(new Message(username,
                            message));
                }
            }
        } //else fetch database data?

    }

    private void createRecyclerView(View view) {
        // Create the recyclerview and populate it with the history
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
        my_username = sharedPreferences.getString("USERNAME", "Not found");
        my_usertype = sharedPreferences.getString("USERTYPE", "Not found");
        String project_id2 = sharedPreferences.getString("MSGPROJID", "Not found");
        if (project_id == null){
            project_id = project_id2;
            Log.e(TAG, "got projid from shared pref");
        }
        messagesRecyclerView = view.findViewById(R.id.messages);
        Log.e(TAG,"messages: " + messagesRecyclerView.toString());
        messageLayoutManager = new LinearLayoutManager(view.getContext());
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setAdapter(new MessagesAdapter(messageHistory));
        messagesRecyclerView.setLayoutManager(messageLayoutManager);
        back = view.findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Messages", "createrecyclerview back_button click");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new MatchesFragment());
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(), "going to matches from msgs" , Toast.LENGTH_SHORT).show();
                fragmentTransaction.commit();
            }
        });
        unMatch = view.findViewById(R.id.unmatch_button);
        unMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // use dialog for add link
                // remove this match from match lists so they don't communicate anymore
            }
        });
        markComplete = view.findViewById(R.id.complete_button);
        markComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO figure out all the movems for the marking of complete and what info we need here
               markProjectCompleteToDB(project_id, my_username);

            }
        });

        sendMessage = view.findViewById(R.id.message_send);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // use dialog for add link
                String msg = ((EditText) view.findViewById(R.id.message_write)).getText().toString();
                Message message = new Message(my_username, msg);
                sendMessageToDB(message, project_id);
                ((EditText) view.findViewById(R.id.message_write)).setText(" ");
                // send message to database
            }
        });

    }


    // need to iterate over the match lsit for the project and complete for each user?
    // send a sticker to another user's entry in the realtime db
    private void markProjectCompleteToDB(String proj_id, String my_username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Project[] projectToMove = new Project[1];
                // get references to database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // Update user stats for sending message
                DatabaseReference activeProjectRef = database.getReference("ACTIVE_PROJECTS/"+proj_id);
                activeProjectRef.addValueEventListener(new ValueEventListener() {
                    public Project proj;
                    public Boolean first_change = true;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get other project so we can add a new message
                        projectToMove[0] = dataSnapshot.getValue(Project.class);
                        if (activeProjectRef != null && proj != null && first_change){
                            // add message to project
                            // set other project to the newly updates other project
                            activeProjectRef.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w(TAG, "Update received removed project from active: " + proj_id);
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "FAILED to update project list: " + proj_id);
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
                DatabaseReference completedProjectRef = database.getReference("COMPLETED_PROJECTS/"+proj_id);
                completedProjectRef.addValueEventListener(new ValueEventListener() {
                    public Boolean first_change = true;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get other user so we can add a new message
                        if (completedProjectRef != null && dataSnapshot != null && first_change ){
                            Log.w(TAG, "added proj to completed list: " + proj_id);
                            completedProjectRef.setValue(projectToMove[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w(TAG, "Update received added completed project: " + projectToMove[0].toString());

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "FAILED to update project list: " +"completed not changed");
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
                if (my_usertype.equals("HOMEOWNERS")) {
                    DatabaseReference userRef = database.getReference("HOMEOWNERS/" + my_username);
                    userRef.addValueEventListener(new ValueEventListener() {
                        public Homeowner user;
                        public Boolean first_change = true;

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // get other user so we can add a new message
                            user = dataSnapshot.getValue(Homeowner.class);
                            if (userRef != null && dataSnapshot != null && first_change && user != null) {
                                // add message to user
                                Log.w(TAG, "test proj to user list: " + user.toString());
                                Log.w(TAG, "test proj to user list: " + user.getUsername());
                                user.removeActiveProject(proj_id);
                                user.addCompleteProject(proj_id);
                                Log.w(TAG, "added proj to user list: " + user.toString());
                                userRef.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.w(TAG, "Update received removed and added (moved) project: " + user.toString());
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


            }
        }).start();
    }



    // send a sticker to another user's entry in the realtime db
    private void sendMessageToDB(Message message, String proj_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // get references to database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // Update user stats for sending message
                DatabaseReference projectRef = database.getReference("ACTIVE_PROJECTS/"+proj_id);
                Log.w(TAG, "Update received new project: " + proj_id);
                projectRef.addValueEventListener(new ValueEventListener() {
                    public Project proj;
                    public Boolean first_change = true;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get other project so we can add a new message
                        proj = dataSnapshot.getValue(Project.class);
                        if (projectRef != null && proj != null &&first_change){
                            // add message to projec
                            proj.addMessage(my_username, message);
                            // set other project to the newly updates other project
                            projectRef.setValue(proj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w(TAG, "Update received new project: " + proj.toString());
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "FAILED to update project list: " + proj.toString());
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

            }
        }).start();
    }

}