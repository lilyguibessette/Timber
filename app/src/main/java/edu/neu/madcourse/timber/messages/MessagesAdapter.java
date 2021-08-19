package edu.neu.madcourse.timber.messages;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.neu.madcourse.timber.messages.Message;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.timber.R;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesHolder>{
    private static final String TAG = "MessagesAdapter";
    private final ArrayList<Message> messagesHistory;
    private final String my_username;

    public MessagesAdapter(ArrayList<Message> messagesHistory, String my_username) {
        this.messagesHistory = messagesHistory;
        this.my_username = my_username;
    }

    @Override
    public MessagesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message, parent, false);
        return new MessagesHolder(view);
    }

    @Override
    public void onBindViewHolder(MessagesHolder holder, int position) {
        Message currentItem = messagesHistory.get(position);
        Log.e(TAG, currentItem.toString());
        if (currentItem != null) {
            if(currentItem.getUsername().equals(my_username))  {
                holder.linearLayout.setGravity(Gravity.LEFT);
            }
            else {
                holder.linearLayout.setGravity(Gravity.RIGHT);
            }

            Log.e("onBindViewHolder", currentItem.toString());
            holder.username.setText(currentItem.getUsername());
            holder.message.setText(currentItem.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messagesHistory.size();
    }

}
