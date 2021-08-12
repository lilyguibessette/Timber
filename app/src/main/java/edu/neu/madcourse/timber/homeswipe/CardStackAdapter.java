package edu.neu.madcourse.timber.homeswipe;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

import edu.neu.madcourse.timber.R;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<SwipeCard> items;
    View view;
    private Integer counter = 0;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();



    public CardStackAdapter() { }
    public CardStackAdapter(List<SwipeCard> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.card_swipe, parent, false);
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

            Log.e(TAG,"Called ViewHolder");
            image = itemView.findViewById(R.id.swipe_image);
            username = itemView.findViewById(R.id.swipe_username);
            details = itemView.findViewById(R.id.swipe_details);
        }

        void setData(SwipeCard data) {
            Log.e(TAG,"setting data");
            StorageReference imageRef = storageReference.child(data.getImage());
            Glide.with(view)
                    .load(imageRef)
                    .into(image);
            username.setText(data.getUsername());
            details.setText(data.getDetails());
        }
    }

    public List<SwipeCard> getCardStack() {
        return items;
    }

    public void addCardToBack(SwipeCard newCard) {
        items.add(newCard);
    }

    public void setCardStack(List<SwipeCard> items) {
        this.items = items;
    }

    public SwipeCard getFirstCard() {
        counter += 1;

        return this.items.get(counter-1);
    }

}