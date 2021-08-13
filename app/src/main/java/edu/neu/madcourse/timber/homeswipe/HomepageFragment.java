package edu.neu.madcourse.timber.homeswipe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import edu.neu.madcourse.timber.MainActivity;
import edu.neu.madcourse.timber.OnGetDataListener;
import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.users.Contractor;
import edu.neu.madcourse.timber.users.Homeowner;
import edu.neu.madcourse.timber.users.Project;

import static android.content.Context.MODE_PRIVATE;

public class HomepageFragment extends Fragment {

    private static final String TAG = "HomepageFragment";
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference contractorsRef = database.getReference("CONTRACTORS/");
    DatabaseReference activeProjectRef = database.getReference("ACTIVE_PROJECTS/");
    DatabaseReference currentCardRef;
    DataSnapshot contractorData;
    String swipedName;
    String thisUserType;
    String thisUser;

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
                if (direction == Direction.Right){
                    Log.d(TAG, "Swipe Direction Right");
                    swipedName = adapter.getFirstCard().getUsername();
                    Log.e(TAG,"right swipe username is " + swipedName);

                    // ASYNC OPERATIONS BABY
                    new Thread(() -> {
                        Log.e(TAG,"starting a thread for right swipe");

                        if(thisUserType.equals("HOMEOWNER")) {
                            swipedOnContractorHandler("Right", swipedName);
                        } else {
                            swipedOnProjectHandler("Right", swipedName);
                        }

                        Log.e(TAG,"ending a thread for right swipe");
                    }).start();


                    //Toast.makeText(HomepageFragment.this, "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top){
                    Log.d(TAG, "Swipe Direction Top");
                    //Toast.makeText(HomepageFragment.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left){
                    Log.d(TAG, "Swipe Direction Left");
                    swipedName = adapter.getFirstCard().getUsername();
                    Log.e(TAG,"left swipe username is " + swipedName);

                    // ASYNC OPERATIONS BABY
                    new Thread(() -> {
                        Log.e(TAG,"starting a thread for left swipe");

                        if(thisUserType.equals("HOMEOWNER")) {
                            swipedOnContractorHandler("Left", swipedName);
                        } else {
                            swipedOnProjectHandler("Left", swipedName);
                        }

                        Log.e(TAG,"ending a thread for left swipe");
                    }).start();
                }
                if (direction == Direction.Bottom){
                    Log.d(TAG, "Swipe Direction Bottom");
                    // Toast.makeText(HomepageFragment.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

                if (manager.getTopPosition() == adapter.getItemCount()){
                    if(thisUserType.equals("HOMEOWNER")) {
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

        Log.e(TAG,"adding initial cardstack with userType: " + thisUserType);

        if(thisUserType.equals("HOMEOWNER")) {
            adapter.setCardStack(populateContractorsList());
        } else{
            adapter.setCardStack(populateProjectsList());
        }

        Log.e(TAG,"create adapter");
        Log.e(TAG,"I am: " + thisUserType + " " + thisUser);
        cardStackView.setLayoutManager(manager);

        Log.e(TAG,"set manager");
        cardStackView.setAdapter(adapter);

        Log.e(TAG,"set adapter");

        cardStackView.setItemAnimator(new DefaultItemAnimator());

        Log.e(TAG,"set item animator");
        return view;
    }

    private List<SwipeCard> populateContractorsList() {
        // This might work? https://stackoverflow.com/questions/64655837/cards-stack-swipe-add-card-in-the-back-after-swiping-removing-top-card
        Log.e(TAG,"populateList called");
        List<SwipeCard> cardStack= new ArrayList<>();

        contractorsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String,Object> userData = (Map<String,Object>) dataSnapshot.getValue();
                        //Get map of users in datasnapshot
                        for (Map.Entry<String, Object> entry : userData.entrySet()){

                            //Get user map
                            Map singleUser = (Map) entry.getValue();
                            //Get phone field and append to list
                            Log.e(TAG,entry.toString());
                            try{
                                if(((ArrayList<String>) singleUser.get("swipedRightOnList")).contains(thisUser) ||
                                        ((ArrayList<String>) singleUser.get("swipedLeftOnList")).contains(thisUser)){
                                    continue;
                                }
                            } catch(NullPointerException exc){
                                Log.e(TAG,"null pointer on" + entry.getKey());
                            }
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
        Log.e(TAG,"returning cardstack");
        return cardStack;
    }

    private List<SwipeCard> populateProjectsList() {
        // This might work? https://stackoverflow.com/questions/64655837/cards-stack-swipe-add-card-in-the-back-after-swiping-removing-top-card
        Log.e(TAG,"populateProjectsList called");
        List<SwipeCard> cardStack= new ArrayList<>();

        activeProjectRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String,Object> userData = (Map<String,Object>) dataSnapshot.getValue();
                        //Get map of users in datasnapshot
                        for (Map.Entry<String, Object> entry : userData.entrySet()){

                            //Get user map
                            Map singleUser = (Map) entry.getValue();
                            //Get phone field and append to list

                            Log.e(TAG,entry.getKey());
                            try{
                                if(((ArrayList<String>) singleUser.get("swipedRightOnList")).contains(thisUser) ||
                                    ((ArrayList<String>) singleUser.get("swipedLeftOnList")).contains(thisUser)){
                                    continue;
                                }
                            } catch(NullPointerException exc){
                                Log.e(TAG,"null pointer on" + entry.getKey());
                            }
                            // Add them all at once?
                            cardStack.add(new SwipeCard(
                                    (String) singleUser.get("image"),
                                    (String) entry.getKey(),
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
        Log.e(TAG,"returning cardstack");
        return cardStack;
    }

    private void swipedOnContractorHandler(String direction, String swipedName) {
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
                    Log.e(TAG, "attempting to add: " + thisUser);
                    if(direction.equals("Right")){
                        contractor.addRightSwipedOn(thisUser);
                    } else{
                        contractor.addLeftSwipedOn(thisUser);
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
                    if(direction.equals("Right")){
                        project.addRightSwipedOn(thisUser);
                    } else{
                        project.addLeftSwipedOn(thisUser);
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
}
