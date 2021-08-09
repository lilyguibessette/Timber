package edu.neu.madcourse.timber.homeswipe;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

import edu.neu.madcourse.timber.R;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<UserCard> items;

    public CardStackAdapter() { }
    public CardStackAdapter(List<UserCard> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_swipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView username, details;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.swipe_image);
            username = itemView.findViewById(R.id.swipe_username);
            details = itemView.findViewById(R.id.swipe_details);
        }

        void setData(UserCard data) {
            Log.e(TAG,"setting data");
            Picasso.get()
                    .load(data.getImage())
                    .fit()
                    .centerCrop()
                    .into(image);
            username.setText(data.getUsername());
            details.setText(data.getDetails());
        }
    }

    public List<UserCard> getCardStack() {
        return items;
    }

    public void addCardToBack(UserCard newCard) {
        items.add(newCard);
    }

    public void setCardStack(List<UserCard> items) {
        this.items = items;
    }
}