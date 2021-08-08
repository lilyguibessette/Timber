package edu.neu.madcourse.timber.matches;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.timber.matches.Match;
import edu.neu.madcourse.timber.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchesFragment} factory method to
 * create an instance of this fragment.
 */
public class MatchesFragment extends Fragment {
    // Recycler view related variables
    private final ArrayList<Match> matchesHistory = new ArrayList<>();
    private RecyclerView matchesRecyclerView;
    private RecyclerView.LayoutManager matchesLayoutManager;
    private int matchesSize = 0;

    private static final String KEY_OF_MATCH = "KEY_OF_MATCH";
    private static final String NUMBER_OF_MATCHES = "NUMBER_OF_MATCHES";

    private static final String TAG = "MatchesFragment";
    String other_username;

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
        // get saved state and initialize the recyclerview
        initialMatchesData(savedInstanceState);

        matchesHistory.add(new Match("apples", R.drawable.timber_full, "this is a test post 1 in matches"));
        matchesHistory.add(new Match("peaches", R.drawable.timber_icon, "this is a test post 2in matches"));
        matchesHistory.add(new Match("mangoes", R.drawable.timber_full, "this is a test post 3in matches"));
        matchesHistory.add(new Match("watermelons", R.drawable.timber_icon, "this is a test post 4in matches"));

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
                    String username = savedInstanceState.getString(KEY_OF_MATCH
                            + i + "0");
                    String last_message = savedInstanceState.getString(KEY_OF_MATCH
                            + i + "1");
                    String image = savedInstanceState.getString(KEY_OF_MATCH
                            + i + "2");
                    matchesHistory.add(new Match(username,Integer.parseInt(image),
                             last_message));
                }
            }
        }
    }

    private void createRecyclerView(View view) {
        // Create the recyclerview and populate it with the history
        matchesRecyclerView = view.findViewById(R.id.matches_rv);
        Log.e(TAG,"Matches: " + matchesRecyclerView.toString());
        matchesLayoutManager = new LinearLayoutManager(view.getContext());
        matchesRecyclerView.setHasFixedSize(true);
        matchesRecyclerView.setAdapter(new MatchesAdapter(matchesHistory));
        matchesRecyclerView.setLayoutManager(matchesLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();
                Match match = matchesHistory.get(position);
                other_username = match.getUsername();
            }
        });

        itemTouchHelper.attachToRecyclerView(matchesRecyclerView);
    }





}