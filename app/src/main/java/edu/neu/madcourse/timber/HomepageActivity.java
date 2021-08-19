package edu.neu.madcourse.timber;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.neu.madcourse.timber.fcm_server.Utils;
import edu.neu.madcourse.timber.homeswipe.HomepageFragment;
import edu.neu.madcourse.timber.matches.MatchesFragment;
import edu.neu.madcourse.timber.newsfeed.NewsFeedFragment;
import edu.neu.madcourse.timber.profile.ProfileFragment;

import static android.content.ContentValues.TAG;
import static androidx.core.app.ActivityCompat.startActivityForResult;
import static edu.neu.madcourse.timber.MainActivity.REQUEST_IMAGE_CAPTURE;

public class HomepageActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    public String my_username;
    public String my_usertype;
    public String my_token;
    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        // Getting current username that is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
        my_username = sharedPreferences.getString("USERNAME", "Not found");
        my_usertype = sharedPreferences.getString("USERTYPE", "Not found");
        my_token = sharedPreferences.getString("CLIENT_REGISTRATION_TOKEN", "Not found");

        // If we don't have the userName or token, restart the login activity
        if(my_username == "Not found" || my_token == "Not found"){
            Intent intent = new Intent(HomepageActivity.this, MainActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.main_screen);
        bottomNavigation = findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(HomepageFragment.newInstance());

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        openFragment(HomepageFragment.newInstance());
                        return true;
                }
                switch (item.getItemId()) {
                    case R.id.menu_feed:
                        openFragment(NewsFeedFragment.newInstance());
                        return true;
                }
                switch (item.getItemId()) {
                    case R.id.menu_messaging:
                        openFragment(MatchesFragment.newInstance());
                        return true;
                }
                switch (item.getItemId()) {
                    case R.id.menu_profile:
                        openFragment(ProfileFragment.newInstance());
                        return true;
                }
                return false;
            };

    // TODO: took this from the other project and modified to our variable names
    // Create notification channel and subscribe user to their channel
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel
                    ("Timber", my_username, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notifications for " + my_username);
            NotificationManager notificationManager = this.
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Utils.subscribeToMyMessages(my_username, this);
        }
    }



/* // Might be useful, scales an image to an imageview; from https://developer.android.com/training/camera/photobasics#java
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }*/
}