package edu.neu.madcourse.timber.messages;

import android.os.Bundle;

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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.matches.MatchesFragment;

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
    String other_username;
    private Button back;
    private Button sendMessage;
    private FloatingActionButton markComplete;
    private FloatingActionButton unMatch;

    public MessagesFragment() {
        // Required empty public constructor
    }


    public MessagesFragment(String other_username) {
        this.other_username = other_username;
        // Required empty public constructor
    }

    public static MessagesFragment newInstance(String other_username) {
        MessagesFragment fragment = new MessagesFragment();
        fragment.other_username = other_username;
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
        messagesSize = 0;

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

            }
        });
        markComplete = view.findViewById(R.id.complete_button);
        markComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // use dialog for add link

            }
        });

        sendMessage = view.findViewById(R.id.message_send);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // use dialog for add link
                String message = ((EditText) view.findViewById(R.id.message_write)).getText().toString();
                // send message to database
            }
        });

    }





}