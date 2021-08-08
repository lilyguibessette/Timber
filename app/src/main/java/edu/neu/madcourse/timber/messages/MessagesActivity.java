package edu.neu.madcourse.timber.messages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.timber.HomepageActivity;
import edu.neu.madcourse.timber.MainActivity;
import edu.neu.madcourse.timber.R;

public class MessagesActivity extends AppCompatActivity {
    // Recycler view related variables
    private final ArrayList<Message> messageHistory = new ArrayList<>();
    private RecyclerView messagesRecyclerView;
    private RecyclerView.LayoutManager messageLayoutManager;
    private int messagesSize = 0;
    private static final String KEY_OF_MSG = "KEY_OF_MSG";
    private static final String NUMBER_OF_MSGS = "NUMBER_OF_MSGS";
    private static final String TAG = "MessagesFragment";
    String other_username;
    private Button back_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_message);
        messagesSize = 0;

        // get saved state and initialize the recyclerview
        initialMessagesData(savedInstanceState);
        messageHistory.add(new Message("apples", "this is a test post 1"));
        messageHistory.add(new Message("peaches", "this is a test post 2"));
        messageHistory.add(new Message("mangoes", "this is a test post 3"));
        messageHistory.add(new Message("watermelons","this is a test post 4"));
        createRecyclerView();
        back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(view -> {
            // start the new activity
            // TODO decide how to handle if as activiy or frag for messages
            startActivity(new Intent(MessagesActivity.this, HomepageActivity.class));
        });
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

    private void createRecyclerView() {
        // Create the recyclerview and populate it with the history
        messagesRecyclerView = findViewById(R.id.messages);
        Log.e(TAG,"messages: " + messagesRecyclerView.toString());
        messageLayoutManager = new LinearLayoutManager(this);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setAdapter(new MessagesAdapter(messageHistory));
        messagesRecyclerView.setLayoutManager(messageLayoutManager);
    }
}
