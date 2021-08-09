package edu.neu.madcourse.timber.homeswipe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

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

import edu.neu.madcourse.timber.OnGetDataListener;
import edu.neu.madcourse.timber.R;

public class HomepageFragment extends Fragment {

    private static final String TAG = "HomepageFragment";
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
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
                    //Toast.makeText(HomepageFragment.this, "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top){
                    Log.d(TAG, "Swipe Direction Top");
                    //Toast.makeText(HomepageFragment.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left){
                    Log.d(TAG, "Swipe Direction Left");
                    //Toast.makeText(HomepageFragment.this, "Direction Left", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom){
                    Log.d(TAG, "Swipe Direction Bottom");
                    // Toast.makeText(HomepageFragment.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

                // Paginating
                if (manager.getTopPosition() == adapter.getItemCount() - 5){
                    paginate();
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
                //TextView tv = view.findViewById(R.id.item_name);
                //Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
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

        adapter = new CardStackAdapter();

        Log.e(TAG,"adding initial cardstack");

        adapter.setCardStack(populateList());

        Log.e(TAG,"create adapter");

        cardStackView.setLayoutManager(manager);

        Log.e(TAG,"set manager");
        cardStackView.setAdapter(adapter);

        Log.e(TAG,"set adapter");

        cardStackView.setItemAnimator(new DefaultItemAnimator());

        Log.e(TAG,"set item animator");
        return view;
    }

    private void paginate() {
        List<SwipeCard> old = adapter.getCardStack();
        List<SwipeCard> fresh = new ArrayList<>(populateList());
        CardStackCallback callback = new CardStackCallback(old, fresh);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setCardStack(fresh);
        result.dispatchUpdatesTo(adapter);
    }

    private List<SwipeCard> populateList() {
        // This might work? https://stackoverflow.com/questions/64655837/cards-stack-swipe-add-card-in-the-back-after-swiping-removing-top-card
        Log.e(TAG,"populateList called");
        List<SwipeCard> cardStack= new ArrayList<>();
        final ArrayList<String>[] userNames = new ArrayList[]{new ArrayList<String>()};
/*
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(
                "HOMEOWNERS");

        readData(usersRef, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                //got data from database....now you can use the retrieved data
                userNames[0] = collectUsers((Map<String,Object>) dataSnapshot.getValue());

                for(int i = 0; i < userNames[0].size();i++){
                    Log.e(TAG,"for loop: " + userNames[0].get(i));
                    cardStack.add(new UserCard(R.drawable.sample1, userNames[0].get(i), "24"));
                }

            }
            @Override
            public void onStart() {
                //when starting
                Log.e("ONSTART", "Started");
            }

            @Override
            public void onFailure() {
                Log.e("onFailure", "Failed");
            }
        });



        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        userNames[0] = collectUsers((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        Log.e(TAG,"start for loop");
        for(int i = 0; i < userNames[0].size();i++){
            Log.e(TAG,"for loop: " + userNames[0].get(i));
            adapter.addCardToBack(new UserCard(R.drawable.sample1, userNames[0].get(i), "24"));
        }
        Log.e(TAG,"end for loop");*/

        cardStack.add(new SwipeCard(R.drawable.sample1, "Markonah", "24"));
        cardStack.add(new SwipeCard(R.drawable.sample2, "Marpuah", "20"));
        cardStack.add(new SwipeCard(R.drawable.sample3, "Sukijah", "27"));
        cardStack.add(new SwipeCard(R.drawable.sample4, "Markobar", "19"));
        cardStack.add(new SwipeCard(R.drawable.sample5, "Marmut", "25"));
        Log.e(TAG,"returning cardstack");
        return cardStack;
    }


    // This doesn't work
    public List<SwipeCard> atomicPopulateList(){
        List<SwipeCard> cardStack= new ArrayList<>();
        final AtomicBoolean done = new AtomicBoolean(false);
        final AtomicReference<String> userName = new AtomicReference<>();
        final ArrayList<String>[] userNames = new ArrayList[]{new ArrayList<String>()};

        //assuming you have already called firebase initialization code for admin sdk, android etc
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("HOMEOWNERS");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {
                userNames[0] = collectUsers((Map<String,Object>) dataSnapshot.getValue());
                for(int i = 0; i < userNames[0].size();i++){
                    Log.e(TAG,"for loop: " + userNames[0].get(i));
                    cardStack.add(new SwipeCard(R.drawable.sample1, userNames[0].get(i), "24"));
                }
                done.set(true);
            }

            public void onCancelled(DatabaseError error) {
                
            }
        });
        while (!done.get()){
            Log.e(TAG,"not done");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        return cardStack;
    }

    // Doesn't work
    private void addCards() {
        new Thread(() -> {
            Log.e(TAG,"trying to add testman");
            adapter.addCardToBack(new SwipeCard(R.drawable.sample1, "testman", "24"));
            Log.e(TAG,"add testman done");
            Log.e(TAG,"addCards called");
            final ArrayList<String>[] userNames = new ArrayList[]{new ArrayList<String>()};

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(
                    "CONTRACTORS");

            usersRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of users in datasnapshot
                            userNames[0] = collectUsers((Map<String,Object>) dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
            Log.e(TAG,"start for loop");

            for(int i = 0; i < userNames[0].size();i++){
                Log.e(TAG,"for loop: " + userNames[0].get(i));
                adapter.addCardToBack(new SwipeCard(R.drawable.sample1, userNames[0].get(i), "24"));
            }
            Log.e(TAG,"end for loop");
        }).start();
    }

    private ArrayList<String> collectUsers(Map<String,Object> users) {
        Log.e(TAG,"collectUsers called");

        ArrayList<String> userNames = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            userNames.add((String) singleUser.get("username"));
        }

        return userNames;
    }

    public void readData(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


}
