package edu.neu.madcourse.timber.newsfeed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

import edu.neu.madcourse.timber.R;

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

            UploadTask uploadTask = newImageRef.putBytes(byteArray);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e(TAG,"upload completed");
                }
            });


            Log.e(TAG,"putBytes end");


            // ImageView in your Activity
            ImageView imageView = view.findViewById(R.id.post_image);

            newImageRef.getBytes(999999999).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                }
            });


            // Download directly from StorageReference using Glide
            // (See MyAppGlideModule for Loader registration)
            /*Glide.with(view)
                    .asBitmap()
                    .load()
                    .into(imageView);*/

            holder.post_image_id.setImageResource(currentItem.getPost_id());
            holder.post_description.setText(currentItem.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return newsFeedHistory.size();
    }

}
