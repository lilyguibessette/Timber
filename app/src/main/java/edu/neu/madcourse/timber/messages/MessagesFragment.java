package edu.neu.madcourse.timber.messages;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.fcm_server.Utils;
import edu.neu.madcourse.timber.matches.Match;
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
    private MessagesAdapter messagesAdapter;
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
    private String other_user_id;
    // Database Resources
    private FirebaseDatabase database;
    private DatabaseReference myMessagesRef;
    private ValueEventListener myMessagesListener;
    private ChildEventListener myMessageThreadsListener;
    private DatabaseReference myMessageThreadsRef;
    private Project currentProject;
    private TextView projNametv;
    SharedPreferences sharedPreferences;

    public MessagesFragment() {
        // Required empty public constructor
    }


    public MessagesFragment(String project_id, String other_user_id) {
        this.project_id = project_id;
        this.other_user_id = other_user_id;
        // Required empty public constructor
    }

    public static MessagesFragment newInstance(String project_id, String other_user_id) {
        MessagesFragment fragment = new MessagesFragment(project_id, other_user_id);
        if (fragment.getProject_id() != "FAKE") {
            Log.e(TAG, "got proj id from frag creator" + project_id + " " + fragment.getProject_id());
        }
        return fragment;
    }

    public String getProject_id() {
        if (this.project_id == null) {
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
        sharedPreferences = getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
        my_username = sharedPreferences.getString("USERNAME", "Not found");
        my_usertype = sharedPreferences.getString("USERTYPE", "Not found");
        String project_id2 = sharedPreferences.getString("MSGPROJID", "Not found");
        if (project_id == null) {
            project_id = project_id2;
            Log.e(TAG, "got projid from shared pref");
        }

        createDatabaseResources();
        setMyMessagesListener();
        initialMessagesData(savedInstanceState);
        // get saved state and initialize the recyclerview

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
                    String to_username = savedInstanceState.getString(KEY_OF_MSG
                            + i + "1");
                    String message = savedInstanceState.getString(KEY_OF_MSG
                            + i + "2");
                    messageHistory.add(new Message(username,to_username,
                            message));
                }
            }
        } //else fetch database data?

    }

    private void createRecyclerView(View view) {
        // Create the recyclerview and populate it with the history

        sharedPreferences = getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
        my_username = sharedPreferences.getString("USERNAME", "Not found");
        my_usertype = sharedPreferences.getString("USERTYPE", "Not found");
        String project_id2 = sharedPreferences.getString("MSGPROJID", "Not found");
        if (project_id == null) {
            project_id = project_id2;
            Log.e(TAG, "got projid from shared pref");
        }

        createDatabaseResources();
        Log.e(TAG,"other user ID: " + other_user_id);

        messagesRecyclerView = view.findViewById(R.id.messages);
        Log.e(TAG, "messages: " + messagesRecyclerView.toString());
        messageLayoutManager = new LinearLayoutManager(view.getContext());
        messagesRecyclerView.setHasFixedSize(true);
        messagesAdapter = new MessagesAdapter(messageHistory, my_username);
        messagesRecyclerView.setAdapter(messagesAdapter);
        messagesRecyclerView.setLayoutManager(messageLayoutManager);
        projNametv = view.findViewById(R.id.project_name);
        //TODO fix to project name later
        projNametv.setText(project_id);
        back = view.findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Messages", "createrecyclerview back_button click");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new MatchesFragment());
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(), "going to matches from msgs", Toast.LENGTH_SHORT).show();
                fragmentTransaction.commit();
            }
        });
        unMatch = view.findViewById(R.id.unmatch_button);
        unMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if(my_usertype.equals("HOMEOWNERS")){
                    unmatchToDB(project_id,  other_user_id);
                } else{
                    unmatchToDB(project_id,  my_username);
                }
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
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("ACTIVE_PROJECT", null);
                myEdit.commit();

            }
        });

        sendMessage = view.findViewById(R.id.message_send);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            Message message;
            @Override
            public void onClick(View v) {
                // use dialog for add link
                String msg = ((EditText) view.findViewById(R.id.message_write)).getText().toString();
                message = new Message(my_username, other_user_id, msg);
                Utils.sendMessageNotification(my_username, other_user_id, project_id, msg);
                sendMessageToDB(message, project_id);
                ((EditText) view.findViewById(R.id.message_write)).setText("     ");
                // send message to database
            }
        });

        myMessageThreadsRef = database.getReference("ACTIVE_PROJECTS/" + project_id + "/messageThreads");
        myMessageThreadsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e(TAG, "myMessageThreadsRef onChildChanged:" + snapshot.getValue());
                if (snapshot.exists()) {
                    ArrayList<HashMap<String,String>> msgList = (ArrayList<HashMap<String,String>>) snapshot.getValue();
                    if (msgList != null) {
                        // messageHistory = new ArrayList<>();
                        int i = 0;

                        for (HashMap<String,String> msgData : msgList) {
                            if (msgData != null && !msgData.get("message").equals("EMPTY")) {
                                // might need to configure this? messageHistory.removeAll();
                                //  Log.e(TAG, each.toString() + " from " +each.get("username") + " said " +each.get("message"));
                                Log.e(TAG, msgList.get(i).toString() +" NUM "+ i );
                                Log.e(TAG, "msgData for "+ msgData.toString());
                                Log.e(TAG, msgData.get("username"));
                                Log.e(TAG, msgData.get("to_username"));
                                Log.e(TAG, msgData.get("message"));
                                Message msg = new Message(msgData.get("username"),msgData.get("to_username") , msgData.get("message"));
                                if((msg.getTo_username().equals(other_user_id) && msg.getUsername().equals(my_username))
                                        ||( msg.getTo_username().equals(my_username) && msg.getUsername().equals(other_user_id))){
                                    if(i == msgList.size() ) {
                                        messageHistory.add(msg);
                                        //TODO the adapter might end up backwards
                                        messagesAdapter.notifyItemInserted(0);
                                    }
                                    i++;
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                DatabaseReference activeProjectRef = database.getReference("ACTIVE_PROJECTS/" + proj_id);
                activeProjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get other project so we can add a new message
                        currentProject = dataSnapshot.getValue(Project.class);
                        if (activeProjectRef != null && currentProject != null) {
                            // add message to project
                            // set other project to the newly updates other project
                            dataSnapshot.getRef().removeValue();
                            /*
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
                                    });*/
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "proj ref add proj onCancelled", databaseError.toException());
                    }
                });
                DatabaseReference completedProjectRef = database.getReference("COMPLETED_PROJECTS/" + proj_id);
                completedProjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get other user so we can add a new message
                        if (completedProjectRef != null && dataSnapshot != null) {
                            Log.w(TAG, "added proj to completed list: " + proj_id);
                            completedProjectRef.setValue(currentProject).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (currentProject != null) {
                                        Log.w(TAG, "Update received added completed project: " + currentProject.toString());
                                    }
                                    if (currentProject == null) {
                                        Log.w(TAG, "Update received removed completed project");
                                    }

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "FAILED to update project list: " + "completed not changed");
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
                if (my_usertype.equals("HOMEOWNERS")) {
                    DatabaseReference userRef = database.getReference("HOMEOWNERS/" + my_username);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        public Homeowner user;

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // get other user so we can add a new message
                            user = dataSnapshot.getValue(Homeowner.class);
                            if (userRef != null && dataSnapshot != null && user != null) {
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



    // need to iterate over the match lsit for the project and complete for each user?
    // send a sticker to another user's entry in the realtime db
    private void unmatchToDB(String proj_id,  String contractor_id) {
        Log.e(TAG, "Removing "+ proj_id + " and " + contractor_id + " from match lists.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Project[] projectToMove = new Project[1];
                // get references to database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // Update user stats for sending message
                DatabaseReference matchListRef = database.getReference("ACTIVE_PROJECTS/" + proj_id+"/matchList" );

                Log.e(TAG, "matchListRef "+ matchListRef);

                matchListRef.equalTo(contractor_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (matchListRef != null ) {
                                dataSnapshot.getRef().removeValue();
                                Log.e(TAG, "Removing "+ proj_id + " and " + contractor_id + " from match lists.");

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "proj ref remove match onCancelled", databaseError.toException());
                    }
                });
                DatabaseReference contractorMatchRef = database.getReference("CONSTRACTORS/" + contractor_id + "matchList");
                contractorMatchRef.equalTo(proj_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get other user so we can add a new message
                        if (dataSnapshot.exists()) {
                            if (contractorMatchRef != null ) {dataSnapshot.getRef().removeValue();
                                Log.e(TAG, "Removing "+ proj_id + " and " + contractor_id + " from match lists.");
                            }

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



    // send a msg to another user's entry in the realtime db
    private void sendMessageToDB(Message message, String proj_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // get references to database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // Update user stats for sending message
                DatabaseReference projectRef = database.getReference("ACTIVE_PROJECTS/" + proj_id);
                Log.w(TAG, "Update received new project: " + proj_id);
                projectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    public Project proj;

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get other project so we can add a new message
                        proj = dataSnapshot.getValue(Project.class);
                        if (projectRef != null && proj != null ) {
                            // add message to projec
                            if(my_usertype.equals("HOMEOWNERS")){
                                proj.addMessage(other_user_id, message);
                            } else {
                            proj.addMessage(my_username, message);
                        }
                            // set other project to the newly updates other project
                            projectRef.setValue(proj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w(TAG, "Update received new msg to proj: " + proj.toString());
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "FAILED to update project msg list: " + proj.toString());
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

            }
        }).start();
    }


    /**
     * LISTENERS FOR DATA CHANGES
     * - Listen for change for number of stickers sent
     * - Listen for change in all users to validate
     * - Listen for change in received history
     */
    private void createDatabaseResources() {
        database = FirebaseDatabase.getInstance();
        Log.e(TAG, "proj " + project_id + " myuser " + my_username);
        if (my_usertype.equals("HOMEOWNERS")) {
            myMessagesRef = database.getReference("ACTIVE_PROJECTS/" + project_id + "/messageThreads/" + other_user_id);
            Log.e(TAG, "FROM HOMEOWNER proj " + project_id + " contractor " + other_user_id);
        } else {
            myMessagesRef = database.getReference("ACTIVE_PROJECTS/" + project_id + "/messageThreads/" + my_username);
            Log.e(TAG, "FROM CONTRACTOR proj " + project_id + " myuser " + my_username);
        }
        //setMyMessagesListener();
/*
        myMessageThreadsRef = database.getReference("ACTIVE_PROJECTS/" + project_id + "/messageThreads");
        myMessageThreadsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e(TAG, "myMessageThreadsRef onChildChanged:" + snapshot.getValue());
                if (snapshot.exists()) {
                    ArrayList<HashMap<String,String>> msgList = (ArrayList<HashMap<String,String>>) snapshot.getValue();
                    if (msgList != null) {
                        // messageHistory = new ArrayList<>();
                        int i = 0;

                        for (HashMap<String,String> msgData : msgList) {
                            if (msgData != null && !msgData.get("message").equals("EMPTY")) {
                                // might need to configure this? messageHistory.removeAll();
                                //  Log.e(TAG, each.toString() + " from " +each.get("username") + " said " +each.get("message"));
                                Log.e(TAG, msgList.get(i).toString() +" NUM "+ i );
                                Log.e(TAG, "msgData for "+ msgData.toString());
                                Log.e(TAG, msgData.get("username"));
                                Log.e(TAG, msgData.get("to_username"));
                                Log.e(TAG, msgData.get("message"));
                                Message msg = new Message(msgData.get("username"),msgData.get("to_username") , msgData.get("message"));
                                if((msg.getTo_username().equals(other_user_id) && msg.getUsername().equals(my_username))
                                        ||( msg.getTo_username().equals(my_username) && msg.getUsername().equals(other_user_id))){
                                    if(i > msgList.size() -1) {
                                        messageHistory.add(msg);
                                        //TODO the adapter might end up backwards
                                        messagesAdapter.notifyItemInserted(0);
                                    }
                                    i++;
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }


    // sets listener for changes to received history; updates the messages received on device
    public void setMyMessagesListener() {
        myMessagesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(TAG, "myMessagesListener onDataChange:" + snapshot.getValue());
                ArrayList<HashMap<String,String>> msgList = (ArrayList<HashMap<String,String>>) snapshot.getValue();
                //HashMap<String,  HashMap<String, String>> msgData = (HashMap<String,  HashMap<String, String>>) snapshot.getValue();
                if (msgList != null) {
                   // messageHistory = new ArrayList<>();

                    for (HashMap<String,String> msgData : msgList) {
                        if (msgData != null && !msgData.get("message").equals("EMPTY")) {

                            //  Log.e(TAG, each.toString() + " from " +each.get("username") + " said " +each.get("message"));
                            Log.e(TAG, "msgData for "+ msgData.toString());
                            Log.e(TAG, msgData.get("username"));
                            Log.e(TAG, msgData.get("message"));
                            Message msg = new Message(msgData.get("username"),msgData.get("to_username"), msgData.get("message"));
                            if((msg.getTo_username().equals(other_user_id) && msg.getUsername().equals(my_username))
                                    ||( msg.getTo_username().equals(my_username) && msg.getUsername().equals(other_user_id))){
                                messageHistory.add( msg);
                            //TODO the adapter might end up backwards
                                messagesAdapter.notifyItemInserted(0);
                            }
                        }
                    }
                    //  HashMap<String, HashMap<String, String>> msgData = (HashMap<String,  HashMap<String, String>>) snapshot.getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myMessagesRef.addListenerForSingleValueEvent(myMessagesListener);
    }

/*        myMessagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.e(TAG, "onChildAdded:" + dataSnapshot.getKey());
                //   private HashMap<String, ArrayList<Message>> messageThreads = new HashMap<>();
                HashMap<String, Message> msgData = (HashMap<String, Message>) dataSnapshot.getValue();
                Log.e(TAG, msgData.toString());

                if (msgData != null) {
                    for (String each: msgData.keySet()) {
                        if (!msgData.get(each).getMessage().equals("EMPTY") && msgData.get(each).getMessage() != null) {
                            Message msg = msgData.get(each);
                          //  Log.e(TAG, each.toString() + " from " +each.get("username") + " said " +each.get("message"));
                           Log.e(TAG, msg.toString());
                            messageHistory.add(0, msg);
                            messagesAdapter.notifyItemInserted(0);
                        }
                    }

                    ;
                }

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
        myMessagesRef.addChildEventListener(myMessagesListener);
    }
*/
}
