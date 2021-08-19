package edu.neu.madcourse.timber.homeswipe;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.fcm_server.Utils;
import edu.neu.madcourse.timber.homeswipe.create_project.CreateProjectDialogFragment;
import edu.neu.madcourse.timber.users.Contractor;
import edu.neu.madcourse.timber.users.Homeowner;
import edu.neu.madcourse.timber.users.Project;

public class HomepageFragment extends Fragment {

    private static final String TAG = "HomepageFragment";
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference contractorsRef = database.getReference("CONTRACTORS/");
    DatabaseReference activeProjectRef = database.getReference("ACTIVE_PROJECTS/");
    DatabaseReference homeownersRef = database.getReference("HOMEOWNERS/");
    DatabaseReference currentCardRef;
    DataSnapshot contractorData;
    private static String SERVER_KEY = ""; // TODO: set up connection to database
    String swipedName;
    String my_usertype;
    String my_username;
    String thisProject;
    Location location;
    double thisLatitude;
    double thisLongitude;
    double distanceLimit = 20.0;
    Integer thisRadius;
    Homeowner selfHomeowner;
    Contractor selfContractor;
    Project selfProject;

    Button select_button;
    Button action_button;
    String newSpecialty;
    int discrete;
    int start = 0; //you need to give starting value of SeekBar
    int end = 1000; //you need to give end value of SeekBar
    int start_pos = 20; //you need to give starting position value of SeekBar

    public HomepageFragment() {
        // Required empty public constructor
    }

    public static HomepageFragment newInstance() {
        HomepageFragment fragment = new HomepageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get Username
        my_username = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString("USERNAME", null);
        my_usertype = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString("USERTYPE", null);

        contractorsRef.child(my_username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get the project referenced
                selfContractor = dataSnapshot.getValue(Contractor.class);
                //thisRadius = selfContractor.getRadius();
            }

            @Override
            public void onCancelled
                    (DatabaseError error) {
                // Getting Post failed, log a message
                Log.e(TAG, "cancelled get radius", error.toException());

            }
        });

        thisProject = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString("ACTIVE_PROJECT", null);
        Log.e(TAG,"my project is: " + thisProject);
        createNotificationChannel();
        // TODO: TEST to use FireBaseMessaging to push a notification
        // TODO: need to add in the server key
        sendNotificationToUserTopic(my_username);

        if(my_usertype == "HOMEOWNERS"){
            thisProject = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString("ACTIVE_PROJECT", null);

            if(Objects.isNull(thisProject)){
                Toast.makeText(getActivity(), "No Project to swipe for! Please create a project to begin swiping", Toast.LENGTH_LONG).show();
                return getView();
            }
        } else{
            try{
                thisRadius = 20;
                updateContractorRadius();
            } catch(Exception exc){
                Log.e(TAG,exc.getMessage());
                thisRadius = 20;
            }
        }

        Location location = Utils.getLocation(this.getActivity(), this.getContext());
        thisLatitude = location.getLatitude();
        thisLongitude = location.getLongitude();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        CardStackView cardStackView = view.findViewById(R.id.homepage);

        // Using yuyakaido card stack manager for our swiping implementation
        manager = new CardStackLayoutManager(view.getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                Log.e(TAG, "122 my project is: " + selfProject);
                if (direction == Direction.Right) {
                    Log.d(TAG, "Swipe Direction Right");
                    swipedName = adapter.getFirstCard().getUsername();
                    Log.e(TAG, "right swipe username is " + swipedName);

                    new Thread(() -> {
                        Log.e(TAG, "starting a thread for right swipe");

                        //checkIfMatched(swipedName);

                        Log.e(TAG, "ending a thread for right swipe");
                    }).start();

                    // ASYNC OPERATIONS BABY
                    new Thread(() -> {
                        Log.e(TAG, "starting a thread for right swipe");

                        if (my_usertype.equals("HOMEOWNERS")) {
                            swipedOnContractorHandler("Right", swipedName);
                        } else {
                            swipedOnProjectHandler("Right", swipedName);
                        }

                        Log.e(TAG, "ending a thread for right swipe");
                    }).start();
                    //Toast.makeText(HomepageFragment.this, "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top) {
                    Log.d(TAG, "Swipe Direction Top");
                    //Toast.makeText(HomepageFragment.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left) {
                    Log.d(TAG, "Swipe Direction Left");
                    swipedName = adapter.getFirstCard().getUsername();
                    Log.e(TAG, "left swipe username is " + swipedName);

                    // ASYNC OPERATIONS BABY
                    new Thread(() -> {
                        Log.e(TAG, "starting a thread for left swipe");

                        if (my_usertype.equals("HOMEOWNERS")) {
                            swipedOnContractorHandler("Left", swipedName);
                        } else {
                            swipedOnProjectHandler("Left", swipedName);
                        }

                        Log.e(TAG, "ending a thread for left swipe");
                    }).start();
                }
                if (direction == Direction.Bottom) {
                    Log.d(TAG, "Swipe Direction Bottom");
                    // Toast.makeText(HomepageFragment.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }
                if (manager.getTopPosition() == adapter.getItemCount()) {
                    if (my_usertype.equals("HOMEOWNERS")) {
                        Toast.makeText(getActivity(), "No more contactors available", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "No more projects available", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                Log.d(TAG, "onCardAppeared: " + position);
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                //TextView tv = view.findViewById(R.id.item_name);
                //Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

        });

        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(30.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(false);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        manager.setAutoMeasureEnabled(false);
        adapter = new CardStackAdapter();

        Log.e(TAG, "adding initial cardstack with userType: " + my_usertype);
        Log.e(TAG, "I am: " + my_usertype + " " + my_username);
        if (my_usertype.equals("HOMEOWNERS")) {
            adapter.setCardStack(populateContractorsList());
            if(adapter.getItemCount() == 0){
                //Toast.makeText(getActivity(), "No Contractors found, check back later!", Toast.LENGTH_LONG).show();
            }
        } else{
            adapter.setCardStack(populateProjectsList());
            if(adapter.getItemCount() == 0){
                //Toast.makeText(getActivity(), "No Projects found, check back later!", Toast.LENGTH_LONG).show();
            }
        }

        Log.e(TAG, "create adapter");
        cardStackView.setLayoutManager(manager);

        Log.e(TAG, "set manager");
        cardStackView.setAdapter(adapter);

        Log.e(TAG, "set adapter");

        cardStackView.setItemAnimator(new DefaultItemAnimator());

        Log.e(TAG, "set item animator");

        action_button = view.findViewById(R.id.profile_action_button);
        if (my_usertype != null && my_usertype.equals("HOMEOWNERS")) {
            //set text
            action_button.setText("+");

        } else {
            action_button.setText("RADIUS");
        }
        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (my_usertype.equals("HOMEOWNERS")) {
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


        // Adding new select button
        DatabaseReference projectsRef = FirebaseDatabase.getInstance().getReference(
                "ACTIVE_PROJECTS");
        select_button = view.findViewById(R.id.select_project_button);
        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (my_usertype.equals("HOMEOWNERS")) {
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
                    View updateSpecialtyView = getLayoutInflater().inflate(R.layout.update_specialty, null);
                    EditText specialtyEdit = (EditText) updateSpecialtyView.findViewById(R.id.specialtyUpdater);
                    newSpecialty = specialtyEdit.getText().toString();
                    Button confirm = (Button) updateSpecialtyView.findViewById(R.id.confirm);
                    dialogBuilder.setView(updateSpecialtyView);
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
        return view;
    }

    private List<SwipeCard> populateContractorsList() {
        // This might work? https://stackoverflow.com/questions/64655837/cards-stack-swipe-add-card-in-the-back-after-swiping-removing-top-card
        Log.e(TAG, "populateList called");
        List<SwipeCard> cardStack = new ArrayList<>();

        contractorsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> userData = (Map<String, Object>) dataSnapshot.getValue();
                        //Get map of users in datasnapshot
                        for (Map.Entry<String, Object> entry : userData.entrySet()) {

                            //Get user map
                            Map singleUser = (Map) entry.getValue();
                            //Get phone field and append to list
                            //Log.e(TAG,entry.toString());

                            // skip cards which we already swiped
                            if (checkIfAlreadySwiped(singleUser, thisProject)) {
                                Log.d(TAG, "continue, swiped");
                                continue;
                            }

                            Long radius = (Long) singleUser.get("radius");
                            Integer intRadius = radius.intValue();

                            // skip if too far
                            if (!checkIfLocal(thisLatitude, thisLongitude,
                                    (Double) singleUser.get("latitude"),
                                    (Double) singleUser.get("longitude"),
                                    intRadius)) {
                                Log.d(TAG, "continue, too far");
                                continue;
                            }

                            // Add them all at once?
                            Log.d(TAG, "adding new card");
                            // Add them all at once?
                            cardStack.add(new SwipeCard(
                                    (String) singleUser.get("image"),
                                    (String) singleUser.get("username"),
                                    (String) singleUser.get("email"),
                                    (String) singleUser.get("zipcode")));
                            /*
                            // add cards as they load (ish, basically the same result as above anyways)
                            adapter.addCardToBack(new SwipeCard(
                                    (String) singleUser.get("image"),
                                    (String) singleUser.get("username"),
                                    "Default description text here"));
                            */
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        Log.e(TAG, "returning cardstack");
        return cardStack;
    }

    private List<SwipeCard> populateProjectsList() {
        // This might work? https://stackoverflow.com/questions/64655837/cards-stack-swipe-add-card-in-the-back-after-swiping-removing-top-card
        Log.e(TAG, "populateProjectsList called");
        List<SwipeCard> cardStack = new ArrayList<>();

        activeProjectRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> userData = (Map<String, Object>) dataSnapshot.getValue();
                        //Get map of users in datasnapshot
                        for (Map.Entry<String, Object> entry : userData.entrySet()) {

                            //Get user map
                            Map singleUser = (Map) entry.getValue();
                            //Get phone field and append to list

                            //Log.e(TAG,entry.getKey());

                            // skip cards which we already swiped
                            if (checkIfAlreadySwiped(singleUser, my_username)) {
                                Log.e(TAG, "continue, swiped");
                                continue;
                            }

                            // skip if too far
                            if (!checkIfLocal(thisLatitude, thisLongitude, (Double) singleUser.
                                    get("latitude"), (Double) singleUser.get("longitude"),thisRadius)) {
                                Log.e(TAG, "continue, too far");
                                continue;
                            }

                            // Add them all at once?
                            Log.d(TAG, "adding new card?");

                            cardStack.add(new SwipeCard(
                                    (String) singleUser.get("image"),
                                    (String) entry.getKey(),
                                    (String) singleUser.get("description"),
                                    (String) singleUser.get("zipcode")));
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        Log.e(TAG, "returning cardstack");
        return cardStack;
    }

    private void swipedOnContractorHandler(String direction, String swipedName) {
        final boolean[] willMatch = {false};
        // Check if project/contractor will match first
        if (direction.equals("Right")) {
            activeProjectRef.child(thisProject).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // get the project referenced
                            selfProject = dataSnapshot.getValue(Project.class);
                            if ((selfProject.getSwipedRightOnList()).contains(swipedName)) {
                                willMatch[0] = true;
                                selfProject.getMatchList().add(swipedName);
                                activeProjectRef.child(thisProject).setValue(selfProject).
                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.e(TAG, "updated project with match succeeded");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                Log.e(TAG, "updated project with match failed");
                                            }
                                        });


                                String homeowner = selfProject.getUsername();
                                DatabaseReference homeownerRef = database.getReference("HOMEOWNERS/"+homeowner);
                                homeownerRef.addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Homeowner homeownerUser = dataSnapshot.getValue(Homeowner.class);
                                                homeownerUser.addMatch(thisProject + "_" + swipedName);
                                                homeownerRef.setValue(homeownerUser).addOnSuccessListener(new OnSuccessListener<Void>(){
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.e(TAG, "updated homeowner user with match succeeded");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull @NotNull Exception e) {
                                                        Log.e(TAG, "updated homeowner user with match failed");
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        }
                                );
                            }

                        }

                        @Override
                        public void onCancelled
                                (DatabaseError error) {
                            // Getting Post failed, log a message
                            Log.e(TAG, "update contractor swipedby failed", error.toException());
                }
            });
        }



        currentCardRef = contractorsRef.child(swipedName);

        currentCardRef.get();

        currentCardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public Contractor contractor;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get other user so we can add a new message
                contractor = dataSnapshot.getValue(Contractor.class);
                if (contractor != null && dataSnapshot != null) {
                    // add message to user
                    Log.e(TAG, "attempting to add: " + thisProject);
                    if (direction.equals("Right")) {
                        contractor.addRightSwipedOn(thisProject);
                    } else {
                        contractor.addLeftSwipedOn(thisProject);
                    }
                    Log.e(TAG, "will match: " + willMatch[0]);
                    if (willMatch[0]) {
                        contractor.getMatchList().add(thisProject);
                    }

                    currentCardRef.setValue(contractor).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "updated contractor on swipe list");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "FAILED to update contactor on swipe list");
                                }
                            });
                }
            }

            @Override
            public void onCancelled
                    (DatabaseError error) {
                // Getting Post failed, log a message
                Log.e(TAG, "update contractor swipedby failed", error.toException());
            }
        });
    }

    private void swipedOnProjectHandler(String direction, String swipedName) {
        final boolean[] willMatch = {false};
        // Check if project/contractor will match first
        if (direction.equals("Right")) {
            contractorsRef.child(my_username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // get the project referenced
                    selfContractor = dataSnapshot.getValue(Contractor.class);
                    if ((selfContractor.getSwipedRightOnList()).contains(swipedName)) {
                        willMatch[0] = true;
                        selfContractor.getMatchList().add(swipedName);
                        contractorsRef.child(my_username).setValue(selfContractor).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e(TAG, "updated project with match succeeded");
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Log.e(TAG, "updated project with match failed");
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled
                        (DatabaseError error) {
                    // Getting Post failed, log a message
                    Log.e(TAG, "update contractor swipedby failed", error.toException());

                }
            });
        }

        currentCardRef = activeProjectRef.child(swipedName);
        currentCardRef.get();
        currentCardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public Project project;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get other user so we can add a new message
                project = dataSnapshot.getValue(Project.class);
                if (project != null && dataSnapshot != null) {
                    // add message to user
                    Log.e(TAG, "attempting to add: " + my_username);
                    if (direction.equals("Right")) {
                        project.addRightSwipedOn(my_username);
                    } else {
                        project.addLeftSwipedOn(my_username);
                    }

                    Log.e(TAG, "will match: " + willMatch[0]);
                    if (willMatch[0]) {
                        project.getMatchList().add(my_username);
                    }

                    currentCardRef.setValue(project).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "updated project on swipe list");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "FAILED to update contactor on swipe list");
                                }
                            });
                }
            }

            @Override
            public void onCancelled
                    (DatabaseError error) {
                // Getting Post failed, log a message
                Log.e(TAG, "update contractor swipedby failed", error.toException());
            }
        });
    }

    private boolean checkIfAlreadySwiped(Map currentUser, String userName) throws NullPointerException {
        try {
            if (((ArrayList<String>) currentUser.get("swipedRightOnList")).contains(userName) ||
                    ((ArrayList<String>) currentUser.get("swipedLeftOnList")).contains(userName)) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException exc) {
            Log.e(TAG, exc.getMessage());
        }
        return false;
    }

    private boolean checkIfLocal(Double myLatitude, Double myLongitude, Double otherLatitude, Double otherLongitude, Integer radius) {
        Log.d(TAG, "myLat: " + myLatitude);
        Log.d(TAG, "myLong: " + myLongitude);
        Log.d(TAG, "otherLat: " + otherLatitude);
        Log.d(TAG, "otherLong: " + otherLongitude);
        Log.d(TAG, "distance in miles: " + Utils.findDistance(myLatitude, myLongitude, otherLatitude, otherLongitude));
        Log.d(TAG, "radius: " + radius);

        try {
            // TODO: should use the getRadius function that contractors have - tried but got a null pointer
            if (Utils.findDistance(myLatitude, myLongitude, otherLatitude, otherLongitude) <= radius) {
                Log.d(TAG, "returning true");
                return true;
            } else {
                Log.d(TAG, "returning false");
                return false;
            }
        } catch (NullPointerException exc) {
            Log.e(TAG, exc.getMessage());
        }
        return false;
    }

    // TODO: took this from the other project and modified to our variable names
    // Create notification channel and subscribe user to their channel
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel
                    ("Timber", my_username, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notifications for " + my_username);
            NotificationManager notificationManager = getActivity().
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            subscribeToMyMessages();
        }
    }

    // TODO: need to plug in to the positive match so users are notified
    // FireBase Message to user topic when sending sticker
    public void sendNotificationToUserTopic(String other_user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jPayload = new JSONObject();
                JSONObject jNotification = new JSONObject();
                try {
                    jNotification.put("title", "New message from " + other_user);
                    jNotification.put("body", "You matched with :" + other_user);
                    jNotification.put("sound", "default");
                    jNotification.put("badge", "1");

                    // Populate the Payload object with our notification information
                    // sent to topic of the user we're sending to
                    jPayload.put("to", "/topics/" + other_user);
                    jPayload.put("priority", "high");
                    jPayload.put("notification", jNotification);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //final String messageResponse = Utils.fcmHttpConnection(SERVER_KEY, jPayload);
                Log.d(TAG, "Notification sent to " + other_user);
                //Log.d(TAG, messageResponse);
            }
        }).start();
    }

    // TODO: took this from the other project and modified to our variable names
    // Subscribe a user to their own topic so they can receive notifications when they get messages
    public void subscribeToMyMessages() {
        FirebaseMessaging.getInstance().subscribeToTopic(my_username)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Failed to subscribed to "
                                    + my_username, Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getActivity(), "Subscribed to "
                                + my_username, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void updateContractorRadius() throws NullPointerException {
        contractorsRef.child(my_username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get the project referenced
                selfContractor = dataSnapshot.getValue(Contractor.class);
                try{
                    thisRadius = selfContractor.getRadius();
                } catch(NullPointerException exc){
                    return;
                }
            }

            @Override
            public void onCancelled
                    (DatabaseError error) {
                // Getting Post failed, log a message
                Log.e(TAG, "cancelled get radius", error.toException());

            }
        });
    }

    private void update_profile() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
            // connect to the database and look at the users
            my_username = sharedPreferences.getString("USERNAME", null);
            my_usertype = sharedPreferences.getString("USERTYPE", null);
            DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference(
                    my_usertype + "/" + my_username);

            myUserRef.addValueEventListener(new ValueEventListener() {
                public User my_user;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // if the user exists, get their data
                    if (dataSnapshot.exists()) {
                        if (my_usertype.equals("HOMEOWNERS")) {
                            Homeowner my_user = dataSnapshot.getValue(Homeowner.class);
                            //my_user.setImage();
                            // add setters to my_user
                            myUserRef.setValue(my_user);
                        } else {
                            Contractor my_user = dataSnapshot.getValue(Contractor.class);
                            //String currentSpecialty = my_user.getSpecialty();
                            if(newSpecialty != null){
                                my_user.setSpecialty(newSpecialty);
                            }
                            my_user.setRadius(discrete);
                            Toast.makeText(getActivity(), "Discrete is " + discrete
                                            + " so changed radius to " + my_user.getRadius(),
                                    Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putString("RADIUS",String.valueOf(discrete));
                            myEdit.putString("USERTYPE", my_usertype);
                            myEdit.commit();
                            myUserRef.setValue(my_user);
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



}
