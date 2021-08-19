package edu.neu.madcourse.timber.homeswipe;

import static android.content.Context.MODE_PRIVATE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.fcm_server.Utils;
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
    String thisUserType;
    String thisUser;
    String thisProject;
    Location location;
    double thisLatitude;
    double thisLongitude;
    double distanceLimit = 20.0;
    Integer thisRadius;
    Homeowner selfHomeowner;
    Contractor selfContractor;
    Project selfProject;

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
        thisUser = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString("USERNAME", null);
        thisUserType = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString("USERTYPE", null);

        contractorsRef.child(thisUser).addListenerForSingleValueEvent(new ValueEventListener() {
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
        sendNotificationToUserTopic(thisUser);

        if(Objects.isNull(thisProject)){
            Toast.makeText(getActivity(), "No Project to swipe for! Please create a project to begin swiping", Toast.LENGTH_LONG).show();
            return getView();
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

                        if (thisUserType.equals("HOMEOWNERS")) {
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

                        if (thisUserType.equals("HOMEOWNERS")) {
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
                    if (thisUserType.equals("HOMEOWNERS")) {
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
                Log.e(TAG, "onCardAppeared: " + position);
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

        Log.e(TAG, "adding initial cardstack with userType: " + thisUserType);
        Log.e(TAG, "I am: " + thisUserType + " " + thisUser);
        if (thisUserType.equals("HOMEOWNERS")) {
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
                                Log.e(TAG, "continue, swiped");
                                continue;
                            }

                            // skip if too far
                            if (!checkIfLocal(thisLatitude, thisLongitude,
                                    (Double) singleUser.get("latitude"),
                                    (Double) singleUser.get("longitude"),
                                    (Integer) singleUser.get("radius"))) {
                                Log.e(TAG, "continue, too far");
                                continue;
                            }

                            // Add them all at once?
                            Log.e(TAG, "adding new card?");
                            // Add them all at once?
                            cardStack.add(new SwipeCard(
                                    (String) singleUser.get("image"),
                                    (String) singleUser.get("username"),
                                    "Default description text here"));
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
                        Map<String, Object> userData = (Map<String, Object>) dataSnapshot.
                                getValue();
                        //Get map of users in datasnapshot
                        for (Map.Entry<String, Object> entry : userData.entrySet()) {

                            //Get user map
                            Map singleUser = (Map) entry.getValue();
                            //Get phone field and append to list

                            //Log.e(TAG,entry.getKey());

                            // skip cards which we already swiped
                            if (checkIfAlreadySwiped(singleUser, thisUser)) {
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
                            Log.e(TAG, "adding new card?");

                            cardStack.add(new SwipeCard(
                                    (String) singleUser.get("image"),
                                    (String) entry.getKey(),
                                    "Default description text here"));
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
                    if ((selfProject.getSwipedRightOnList()).contains(swipedName)) ;
                    {
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
            contractorsRef.child(thisUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // get the project referenced
                    selfContractor = dataSnapshot.getValue(Contractor.class);
                    if ((selfContractor.getSwipedRightOnList()).contains(swipedName)) ;
                    {
                        willMatch[0] = true;
                        selfContractor.getMatchList().add(swipedName);
                        contractorsRef.child(thisUser).setValue(selfContractor).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    Log.e(TAG, "attempting to add: " + thisUser);
                    if (direction.equals("Right")) {
                        project.addRightSwipedOn(thisUser);
                    } else {
                        project.addLeftSwipedOn(thisUser);
                    }

                    Log.e(TAG, "will match: " + willMatch[0]);
                    if (willMatch[0]) {
                        project.getMatchList().add(thisUser);
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
        Log.e(TAG, "myLat: " + myLatitude);
        Log.e(TAG, "myLong: " + myLongitude);
        Log.e(TAG, "otherLat: " + otherLatitude);
        Log.e(TAG, "otherLong: " + otherLongitude);
        Log.e(TAG, "distance in miles: " + Utils.findDistance(myLatitude, myLongitude, otherLatitude, otherLongitude));
        Log.e(TAG, "radius: " + radius);

        try {
            // TODO: should use the getRadius function that contractors have - tried but got a null pointer
            if (Utils.findDistance(myLatitude, myLongitude, otherLatitude, otherLongitude) <= radius) {
                Log.e(TAG, "returning true");
                return true;
            } else {
                Log.e(TAG, "returning false");
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
                    ("Timber", thisUser, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notifications for " + thisUser);
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
        FirebaseMessaging.getInstance().subscribeToTopic(thisUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Failed to subscribed to "
                                    + thisUser, Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getActivity(), "Subscribed to "
                                + thisUser, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
