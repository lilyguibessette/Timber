package edu.neu.madcourse.timber.newsfeed;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static androidx.core.app.ActivityCompat.startActivityForResult;

import edu.neu.madcourse.timber.MainActivity;
import edu.neu.madcourse.timber.R;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedHolder>{

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri photoURI;

    View view;
    private final ArrayList<NewsFeedPost> newsFeedHistory;

    public NewsFeedAdapter(ArrayList<NewsFeedPost> newsFeedHistory) {
        this.newsFeedHistory = newsFeedHistory;
    }

    public NewsFeedAdapter(ArrayList<NewsFeedPost> newsFeedHistory, Uri photoURI) {
        this.newsFeedHistory = newsFeedHistory;
        this.photoURI = photoURI;
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
            StorageReference newImageRef = storageReference.child("JPEG_20210808_212600_3026222070829690284.jpg");




            // ImageView in your Activity
            ImageView imageView = view.findViewById(R.id.post_image);


            // Download directly from StorageReference using Glide
            // (See MyAppGlideModule for Loader registration)
            Glide.with(view)
                    .asBitmap()
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
