package edu.neu.madcourse.timber;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedHolder>{

    View view;
    private final ArrayList<NewsFeedPost> newsFeedHistory;

    public NewsFeedAdapter(ArrayList<NewsFeedPost> newsFeedHistory) {
        this.newsFeedHistory = newsFeedHistory;
    }

    @Override
    public NewsFeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new NewsFeedHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsFeedHolder holder, int position) {
        NewsFeedPost currentItem = newsFeedHistory.get(position);
        if (currentItem != null) {
            Log.e("onBindViewHolder", currentItem.toString());
            holder.post_username.setText(currentItem.getUsername());

            // Reference to an image file in Cloud Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference cutePuppyRef = storageReference.child("cute puppy.jpg");
            StorageReference newImageRef = storageReference.child("testImage");

            Bitmap testBM = BitmapFactory.decodeResource(view.getResources(),
                    R.drawable.sample1);
            // Your Bitmap.

            int byteSize = testBM.getRowBytes() * testBM.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
            testBM.copyPixelsToBuffer(byteBuffer);

            // Get the byteArray.
            byte[] byteArray = byteBuffer.array();


            Log.e(TAG,"putBytes start");

            newImageRef.putBytes(byteArray);

            Log.e(TAG,"putBytes end");


            // ImageView in your Activity
            ImageView imageView = view.findViewById(R.id.post_image);


            // Download directly from StorageReference using Glide
            // (See MyAppGlideModule for Loader registration)
            Glide.with(view)
                    .asDrawable()
                    .load(newImageRef)
                    .into(imageView);

            holder.post_image_id.setImageResource(currentItem.getPost_id());
            holder.post_description.setText(currentItem.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return newsFeedHistory.size();
    }

}
