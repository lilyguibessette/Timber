package edu.neu.madcourse.timber.messages;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.timber.R;

public class MessagesHolder extends RecyclerView.ViewHolder {

    public TextView username;
    public TextView message;
    public LinearLayout linearLayout;

    public MessagesHolder(View messagesView) {
        super(messagesView);
        linearLayout = messagesView.findViewById(R.id.linearlayout);
        username = messagesView.findViewById(R.id.message_username);
        message = messagesView.findViewById(R.id.card_message_content);
    }
}
