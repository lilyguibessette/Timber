package edu.neu.madcourse.timber.messages;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.neu.madcourse.timber.messages.Message;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.timber.R;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesHolder>{
    private final ArrayList<Message> messagesHistory;

    public MessagesAdapter(ArrayList<Message> messagesHistory) {
        this.messagesHistory = messagesHistory;
    }

    @Override
    public MessagesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new MessagesHolder(view);
    }

    @Override
    public void onBindViewHolder(MessagesHolder holder, int position) {
        Message currentItem = messagesHistory.get(position);
        if (currentItem != null) {
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
