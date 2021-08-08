package edu.neu.madcourse.timber;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFeedFragment} factory method to
 * create an instance of this fragment.
 */
public class NewsFeedFragment extends Fragment {

    // Recycler view related variables
    private final ArrayList<NewsFeedPost> newsFeedHistory = new ArrayList<>();
    private RecyclerView newsFeedRecyclerView;
    private int newsFeedSize = 0;

    private static final String KEY_OF_STICKER = "KEY_OF_POST";
    private static final String NUMBER_OF_POSTS = "NUMBER_OF_POSTS";

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    public static NewsFeedFragment newInstance() {
        NewsFeedFragment fragment = new NewsFeedFragment();
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
        newsFeedSize = 0;

        // get saved state and initialize the recyclerview
        initialNewsFeedData(savedInstanceState);
        createRecyclerView();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_feed, container, false);
    }

    private void initialNewsFeedData(Bundle savedInstanceState) {
        newsFeedHistory.add(new NewsFeedPost("apples", 1, "this is a test post 1"));
        newsFeedHistory.add(new NewsFeedPost("peaches", 2, "this is a test post 2"));


        /*// recreate the sticker history on orientation change or open
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_POSTS)) {
            if (newsFeedHistory == null || newsFeedHistory.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_POSTS);
                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String post_username = savedInstanceState.getString(KEY_OF_STICKER
                            + i + "0");
                    String post_image = savedInstanceState.getString(KEY_OF_STICKER
                            + i + "1");
                    String post_description = savedInstanceState.getString(KEY_OF_STICKER
                            + i + "2");
                    newsFeedHistory.add(new NewsFeedPost(post_username,
                            Integer.parseInt(post_image), post_description));
                }
            }
        }*/
    }

    private void createRecyclerView() {
        // Create the recyclerview and populate it with the sticker history
        newsFeedRecyclerView = newsFeedRecyclerView.findViewById(R.id.post_recycler);
        newsFeedRecyclerView.setHasFixedSize(true);
        newsFeedRecyclerView.setAdapter(new NewsFeedAdapter(newsFeedHistory));
        newsFeedRecyclerView.setLayoutManager(new LinearLayoutManager(
                newsFeedRecyclerView.getContext()));
    }

}