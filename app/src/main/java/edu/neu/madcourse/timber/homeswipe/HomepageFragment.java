package edu.neu.madcourse.timber.homeswipe;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.location.Location;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.jetbrains.annotations.NotNull;

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

        // defining relevant variables
        my_username = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString("USERNAME", null);
        my_usertype = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString("USERTYPE", null);
        thisProject = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString("ACTIVE_PROJECT", null);
        DatabaseReference projectsRef = FirebaseDatabase.getInstance().getReference(
                "ACTIVE_PROJECTS");

        // location information
        location = Utils.getLocation(this.getActivity(), this.getContext());
        thisLatitude = location.getLatitude();
        thisLongitude = location.getLongitude();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        CardStackView cardStackView = view.findViewById(R.id.homepage);
        action_button = view.findViewById(R.id.profile_action_button);
        select_button = view.findViewById(R.id.select_project_button);

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

        Log.e(TAG, "my project is: " + thisProject);
        Utils.subscribeToMyMessages(thisProject, this.getActivity());

        // if the user is a homeowner
        if (my_usertype == "HOMEOWNERS") {
            // and has no projects listed > give them a message
            if (Objects.isNull(thisProject)) {
                Toast.makeText(getActivity(), "No projects listed! " +
                        "\nPlease create a new project to begin swiping", Toast.LENGTH_LONG).show();
                return getView();
            }

            // else if the user is a contractor
        } else {
            // try to update the radius
            try {
                thisRadius = 20;
                updateContractorRadius();
            } catch (Exception exc) {
                Log.e(TAG, exc.getMessage());
                thisRadius = 20;
            }
        }

        // Using yuyakaido card stack manager for our swiping implementation
        manager = new CardStackLayoutManager(view.getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d = " + direction.name() + " ratio = " + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {

                // log the directional information
                Log.d(TAG, "Project: " + selfProject
                        + " onCardSwiped: p = " + manager.getTopPosition()
                        + " d = " + direction);

                // if user swipes right
                if (direction == Direction.Right) {
                    Log.d(TAG, "Swipe Direction Right");
                    swipedName = adapter.getFirstCard().getUsername();
                    Log.e(TAG, "right swipe username is " + swipedName);

                    // TODO: should we remove this?
                    /*new Thread(() -> {
                        Log.e(TAG, "starting a thread for right swipe");
                        //checkIfMatched(swipedName);
                        Log.e(TAG, "ending a thread for right swipe");
                    }).start();*/

                    // ASYNC OPERATIONS BABY
                    new Thread(() -> {
                        Log.e(TAG, "starting a thread for right swipe");

                        // if the user is a homeowner, assign match to contractor
                        if (my_usertype.equals("HOMEOWNERS")) {
                            swipedOnContractorHandler("Right", swipedName);

                            // otherwise assign the swipe to the project
                        } else {
                            swipedOnProjectHandler("Right", swipedName);
                        }
                        Log.e(TAG, "ending a thread for right swipe");
                    }).start();
                }

                // if the user swipes left
                if (direction == Direction.Left) {
                    Log.d(TAG, "Swipe Direction Left");
                    swipedName = adapter.getFirstCard().getUsername();
                    Log.e(TAG, "left swipe username is " + swipedName);

                    // ASYNC OPERATIONS BABY
                    new Thread(() -> {
                        Log.e(TAG, "starting a thread for left swipe");

                        // if the user is a homeowner, assign rejection to contractor
                        if (my_usertype.equals("HOMEOWNERS")) {
                            swipedOnContractorHandler("Left", swipedName);

                            // otherwise assign the swipe to the project
                        } else {
                            swipedOnProjectHandler("Left", swipedName);
                        }

                        Log.e(TAG, "ending a thread for left swipe");
                    }).start();
                }

                // if user swipes up
                if (direction == Direction.Top) {
                    Log.d(TAG, "Swipe Direction Top");
                }

                // if the user swipes down
                if (direction == Direction.Bottom) {
                    Log.d(TAG, "Swipe Direction Bottom");
                }

                // if there are no more cards to swipe
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
                Log.d(TAG, "onCardCanceled: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                Log.d(TAG, "onCardAppeared: " + position);
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                Log.d(TAG, "onCardDisappeared: " + position);
            }

        });

        // setting details for the card stack manager
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

        // if the user is a homeowner, populate the cardstack with contractors
        if (my_usertype.equals("HOMEOWNERS")) {
            adapter.setCardStack(populateContractorsList());

            // TODO: can remove this now - put text behind cards instead
            /*if (adapter.getItemCount() == 0) {
                // need to wait for database to return in other thread

                //Toast.makeText(getActivity(), "No Contractors found, check back later!", Toast.LENGTH_LONG).show();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                View noMoreContractors = getLayoutInflater().inflate(R.layout.empty_swipe_dialog, null);
                Button confirm = (Button) noMoreContractors.findViewById(R.id.confirm);
                dialogBuilder.setView(noMoreContractors);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }*/

            // otherwise populate the card stack with projects
        } else {
            adapter.setCardStack(populateProjectsList());

            // TODO: can remove this now - put text behind cards instead
            /*if (adapter.getItemCount() == 0) {
                //Toast.makeText(getActivity(), "No Contractors found, check back later!", Toast.LENGTH_LONG).show();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                View noMoreSwipes = getLayoutInflater().inflate(R.layout.empty_swipe_dialog, null);
                TextView tvNoSwipe = noMoreSwipes.findViewById(R.id.textView);
                tvNoSwipe.setText("No more Projects! Check back later!");
                Button confirm = (Button) noMoreSwipes.findViewById(R.id.confirm);
                dialogBuilder.setView(noMoreSwipes);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }*/
        }

        Log.e(TAG, "create adapter");
        cardStackView.setLayoutManager(manager);

        Log.e(TAG, "set manager");
        cardStackView.setAdapter(adapter);

        Log.e(TAG, "set adapter");
        cardStackView.setItemAnimator(new DefaultItemAnimator());

        Log.e(TAG, "set item animator");

        // if the user is a homeowner, set the action button to '+'
        if (my_usertype != null && my_usertype.equals("HOMEOWNERS")) {
            action_button.setText("+");
            // otherwise set the action button to 'RADIUS'
        } else {
            action_button.setText("RADIUS");
        }

        // if a user clicks on the action button
        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // and the user is a homeowner, open the dialog to create a project
                if (my_usertype.equals("HOMEOWNERS")) {
                    Log.e("ProfileFragment", "ProfileFragment to update homeowner");
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, new CreateProjectDialogFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    // else if they are a contractor, open the dialog to adjust the radius
                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    View updateRadiusView = getLayoutInflater().inflate(R.layout.update_radius, null);

                    // creating a seek bar
                    SeekBar seek = (SeekBar) updateRadiusView.findViewById(R.id.seekBar);
                    int start_position = (int) (((start_pos - start) / (end - start)) * 100);
                    discrete = start_pos;
                    seek.setProgress(start_position);

                    // listen for changes to the seek bar
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

                    // define the confirm button on the dialog
                    Button confirm = updateRadiusView.findViewById(R.id.confirm);
                    dialogBuilder.setView(updateRadiusView);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();

                    // when it's clicked, update the profile and then dismiss the dialog
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

        // if a user clicks on the select button
        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the user is a homeowner
                if (my_usertype.equals("HOMEOWNERS")) {
                    Log.e("ProfileFragment", "ProfileFragment to select active project");

                    activeProjectRef.orderByChild("username").equalTo(my_username).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull @NotNull DataSnapshot snapshot) {
                                    Map<String, Object> projectData = (Map<String, Object>) snapshot.getValue();

                                    // if there are no projects in the list, inform the user
                                    if (Objects.isNull(projectData)) {
                                        Toast.makeText(getActivity(), "No Projects to select! " +
                                                "Please create a project", Toast.LENGTH_SHORT).show();
                                        return;

                                        // otherwise bring up a list of projects for the user to select from
                                    } else {
                                        FragmentTransaction fragmentTransaction = getFragmentManager()
                                                .beginTransaction();
                                        fragmentTransaction.replace(R.id.container,
                                                new SelectProjectDialogFragment());
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }
                                }

                                @Override
                                public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {
                                }
                            });

                    // otherwise if the user is a contractor
                } else {
                    // bring up a dialog to change specialty
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    View updateSpecialtyView = getLayoutInflater().inflate(R.layout.update_specialty, null);
                    Button confirm = updateSpecialtyView.findViewById(R.id.confirm);
                    EditText specialtyEdit = updateSpecialtyView.findViewById(R.id.specialtyUpdater);
                    dialogBuilder.setView(updateSpecialtyView);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();

                    // if the user selects confirm, update the profile
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            newSpecialty = specialtyEdit.getText().toString();
                            update_profile();
                            dialog.dismiss();
                        }
                    });
                }

            }
        });
        return view;
    }

    // the function to populate the contractor list
    private List<SwipeCard> populateContractorsList() {
        // This might work? https://stackoverflow.com/questions/64655837/cards-stack-swipe-add-card-in-the-back-after-swiping-removing-top-card
        Log.e(TAG, "populateContractorList called");

        // create a new list
        List<SwipeCard> cardStack = new ArrayList<>();

        contractorsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // get the userData from the snapshot
                            Map<String, Object> userData = (Map<String, Object>) dataSnapshot.getValue();

                            // iterate over the list
                            for (Map.Entry<String, Object> entry : userData.entrySet()) {
                                // get a singleUser
                                Map singleUser = (Map) entry.getValue();

                                // and check if we already swiped (if so, skip)
                                if (checkIfAlreadySwiped(singleUser, thisProject)) {
                                    Log.d(TAG, "continue, already swiped");
                                    continue;
                                }

                                // get the radius from the user
                                Long radius = (Long) singleUser.get("radius");

                                // look at the radius of the user to see if they are too far
                                if (!checkIfLocal(thisLatitude, thisLongitude,
                                        (Double) singleUser.get("latitude"),
                                        (Double) singleUser.get("longitude"),
                                        radius.intValue())) {
                                    Log.d(TAG, "continue, user is too far");
                                    continue;
                                }

                                // otherwise add the card to the cardstack
                                Log.d(TAG, "adding new card");
                                cardStack.add(new SwipeCard(
                                        (String) singleUser.get("image"),
                                        (String) singleUser.get("username"),
                                        (String) singleUser.get("email"),
                                        (String) singleUser.get("zipcode")));

                                // then notify the adapter that the data is updated
                                adapter.notifyDataSetChanged();
                            }
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

    // the function to populate the projects list
    private List<SwipeCard> populateProjectsList() {
        // This might work? https://stackoverflow.com/questions/64655837/cards-stack-swipe-add-card-in-the-back-after-swiping-removing-top-card
        Log.e(TAG, "populateProjectsList called");

        // create a new list
        List<SwipeCard> cardStack = new ArrayList<>();

        activeProjectRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            // get the userData from the snapshot
                            Map<String, Object> userData = (Map<String, Object>) dataSnapshot.getValue();

                            // iterate over the list
                            for (Map.Entry<String, Object> entry : userData.entrySet()) {
                                // get a single user
                                Map singleUser = (Map) entry.getValue();

                                // and check if we already swiped (if so, skip)
                                if (checkIfAlreadySwiped(singleUser, my_username)) {
                                    Log.e(TAG, "continue, swiped");
                                    continue;
                                }

                                // skip if too far
                                if (!checkIfLocal(thisLatitude, thisLongitude, (Double) singleUser.
                                                get("latitude"), (Double) singleUser.get("longitude"),
                                        thisRadius)) {
                                    Log.e(TAG, "continue, too far");
                                    continue;
                                }

                                // otherwise add the card to the cardstack
                                Log.d(TAG, "adding new card?");
                                cardStack.add(new SwipeCard(
                                        (String) singleUser.get("image"),
                                        (String) entry.getKey(),
                                        (String) singleUser.get("description"),
                                        (String) singleUser.get("zipcode")));

                                // then notify the adapter that the data is updated
                                adapter.notifyDataSetChanged();
                            }
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

    // function to handle when a contractor is swiped on
    private void swipedOnContractorHandler(String direction, String swipedName) {
        final boolean[] willMatch = {false};

        // if the swipe is a match
        if (direction.equals("Right")) {
            activeProjectRef.child(thisProject).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // get the project referenced
                                selfProject = dataSnapshot.getValue(Project.class);

                                // if the swipe leads to a match
                                if ((selfProject.getSwipedRightOnList()).contains(swipedName)) {
                                    willMatch[0] = true;

                                    // update the project to include the match
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

                                    // then look at the homeowner
                                    DatabaseReference homeownerRef = database.getReference(
                                            "HOMEOWNERS/" + selfProject.getUsername());
                                    homeownerRef.addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    // get the snapshot of the user data
                                                    Homeowner homeownerUser = dataSnapshot.
                                                            getValue(Homeowner.class);

                                                    // add in the match value to the homeowner's list
                                                    homeownerUser.addMatch(thisProject
                                                            + "_" + swipedName);
                                                    homeownerRef.setValue(homeownerUser).
                                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Log.e(TAG, "updated homeowner " +
                                                                            "user with match succeeded");
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull @NotNull Exception e) {
                                                            Log.e(TAG, "updated homeowner " +
                                                                    "user with match failed");
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            }
                                    );

                                    // when the contractor swipes on a contractor
                                    DatabaseReference projRef = database.getReference("ACTIVE_PROJECTS/" + thisProject);
                                    projRef.addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    // get the project referenced
                                                    Project projectClass = dataSnapshot.getValue(Project.class);
                                                    Log.e("PROJECT:", projectClass.getProject_id()
                                                            + " and this proj" + thisProject
                                                            + "and swipename" + swipedName);
                                                    String homeowner = projectClass.getUsername();
                                                    Log.e("HOMEOWNER:", homeowner);

                                                    // send notifications to the homeowner and the contractor
                                                    Utils.sendNotification(my_username, homeowner, selfProject);
                                                    Utils.sendNotification(my_username, swipedName, selfProject);

                                                    // then add the match to the homeowner's matched project list
                                                    DatabaseReference homeownerRef = database.getReference("HOMEOWNERS/" + homeowner);
                                                    homeownerRef.addListenerForSingleValueEvent(
                                                            new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    Homeowner homeownerUser = dataSnapshot.getValue(Homeowner.class);
                                                                    homeownerUser.addMatch(thisProject + "_" + swipedName);
                                                                    homeownerRef.setValue(homeownerUser).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                                @Override
                                                public void onCancelled
                                                        (DatabaseError error) {
                                                    Log.e(TAG, "update contractor swipedby failed", error.toException());
                                                }
                                            });
                                }

                            }
                        }

                        @Override
                        public void onCancelled
                                (DatabaseError error) {
                            Log.e(TAG, "update contractor swipedby failed", error.toException());
                        }
                    });
        }

        // get the reference to the current card
        currentCardRef = contractorsRef.child(swipedName);
        currentCardRef.get();
        currentCardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public Contractor contractor;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    // get other user so we can add a new message
                    contractor = dataSnapshot.getValue(Contractor.class);
                    if (contractor != null && dataSnapshot != null) {

                        // add message to user
                        Log.e(TAG, "attempting to add: " + thisProject);

                        // if the swipe was a match
                        if (direction.equals("Right")) {
                            contractor.addRightSwipedOn(thisProject);

                            // otherwise put in the left swipe project
                        } else {
                            contractor.addLeftSwipedOn(thisProject);
                        }

                        // if the project is marked as a match, add to the matched list
                        Log.e(TAG, "will match: " + willMatch[0]);
                        if (willMatch[0]) {
                            contractor.getMatchList().add(thisProject);
                        }

                        // send the updates to the contractor
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
            }

            @Override
            public void onCancelled
                    (DatabaseError error) {
                // Getting Post failed, log a message
                Log.e(TAG, "update contractor swipedby failed", error.toException());
            }
        });
    }

    // when a project is swiped on
    private void swipedOnProjectHandler(String direction, String swipedName) {
        final boolean[] willMatch = {false};

        // if the swipe direction is right
        if (direction.equals("Right")) {

            contractorsRef.child(my_username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        // get the project reference
                        selfContractor = dataSnapshot.getValue(Contractor.class);

                        // if the contractor has also swiped right on the project
                        if (selfContractor != null && (selfContractor.getSwipedRightOnList()).contains(swipedName)) {

                            // mark as a match and add it to the match list in the contractor
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
                }

                @Override
                public void onCancelled
                        (DatabaseError error) {
                    // Getting Post failed, log a message
                    Log.e(TAG, "update contractor swipedby failed", error.toException());

                }
            });
        }

        // get the current project reference
        currentCardRef = activeProjectRef.child(swipedName);
        currentCardRef.get();
        currentCardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public Project project;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    project = dataSnapshot.getValue(Project.class);

                    // if the project is valid
                    if (project != null && dataSnapshot != null) {

                        // add message to user
                        Log.e(TAG, "attempting to add: " + my_username);

                        // if the swipe direction is right
                        if (direction.equals("Right")) {

                            // add to the project's swiped right list
                            project.addRightSwipedOn(my_username);

                            // otherwise add to the project's left swipe list
                        } else {
                            project.addLeftSwipedOn(my_username);
                        }

                        // if there is a match, add the user to the project's match list
                        Log.e(TAG, "will match: " + willMatch[0]);
                        if (willMatch[0]) {
                            project.getMatchList().add(my_username);

                            // send a notification to the user
                            Utils.sendNotification(my_username, swipedName, project);
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
            }

            @Override
            public void onCancelled
                    (DatabaseError error) {
                // Getting Post failed, log a message
                Log.e(TAG, "update contractor swipedby failed", error.toException());
            }
        });
    }

    // function to check if the projecct has already gone through the stack for the user
    private boolean checkIfAlreadySwiped(Map currentUser, String userName) throws NullPointerException {
        try {
            // if in the user's swiped right or swiped left list, return true
            if (((ArrayList<String>) currentUser.get("swipedRightOnList")).contains(userName) ||
                    ((ArrayList<String>) currentUser.get("swipedLeftOnList")).contains(userName)) {
                return true;

                // otherwise return false
            } else {
                return false;
            }

            // catch for exceptions
        } catch (NullPointerException exc) {
            Log.e(TAG, exc.getMessage());
        }
        return false;
    }

    // function to check if the two users are within the right distance to show up in the list
    private boolean checkIfLocal(Double myLatitude, Double myLongitude, Double otherLatitude,
                                 Double otherLongitude, Integer radius) {
        // logging all of the data
        Log.d(TAG, "myLat: " + myLatitude);
        Log.d(TAG, "myLong: " + myLongitude);
        Log.d(TAG, "otherLat: " + otherLatitude);
        Log.d(TAG, "otherLong: " + otherLongitude);
        Log.d(TAG, "distance in miles: " + Utils.findDistance(myLatitude, myLongitude,
                otherLatitude, otherLongitude));
        Log.d(TAG, "radius: " + radius);

        try {
            // get the distance between the two locations and check if within radius
            if (Utils.findDistance(myLatitude, myLongitude, otherLatitude, otherLongitude) <= radius) {
                Log.d(TAG, "returning true");
                return true;
                // if it's not within the radius, return false
            } else {
                Log.d(TAG, "returning false");
                return false;
            }

            // catch for exceptions
        } catch (NullPointerException exc) {
            Log.e(TAG, exc.getMessage());
        }
        return false;
    }

    // function to update a contractor radius
    public void updateContractorRadius() throws NullPointerException {
        contractorsRef.child(my_username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // get the project referenced
                    selfContractor = dataSnapshot.getValue(Contractor.class);
                    try {
                        thisRadius = selfContractor.getRadius();
                    } catch (NullPointerException exc) {
                        return;
                    }
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

    // function to update a profile
    private void update_profile() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
            // connect to the database and look at the users
            my_username = sharedPreferences.getString("USERNAME", null);
            my_usertype = sharedPreferences.getString("USERTYPE", null);
            DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference(
                    my_usertype + "/" + my_username);

            myUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // if the user exists, get their data
                    if (dataSnapshot.exists()) {
                        // if the user is a homeowner, get their data
                        if (my_usertype.equals("HOMEOWNERS")) {
                            Homeowner my_user = dataSnapshot.getValue(Homeowner.class);

                            // otherwise if the user is a contractor, get their data
                        } else {
                            Contractor my_user = dataSnapshot.getValue(Contractor.class);

                            // set new values for specialty and radius for the contractor
                            if (newSpecialty != null) {
                                my_user.setSpecialty(newSpecialty);
                            }
                            my_user.setRadius(discrete);

                            // the update the user with the new values
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putString("RADIUS", String.valueOf(discrete));
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
