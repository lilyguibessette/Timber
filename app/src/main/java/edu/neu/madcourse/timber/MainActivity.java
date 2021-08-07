package edu.neu.madcourse.timber;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    // login screen variables
    private static final String TAG = MainActivity.class.getSimpleName();
    public String my_username;
    private static final String USERNAME = "USERNAME";
    private static String CLIENT_REGISTRATION_TOKEN;
    private static String SERVER_KEY = ""; // TODO: set up connection to database
    private static Button login_button;

    // GPS variables
    private LocationManager locationManager;
    private Location location;
    private double latitude;
    private double longitude;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        Intent startButtonActivity = new Intent(getApplicationContext(), TinderSwipeTest.class);
        startActivity(startButtonActivity);

/*


        // hide the action bar for aesthetics
        getSupportActionBar().hide();


        // if the user is returning to the app, open HomepageActivity
        if (savedInstanceState != null && savedInstanceState.containsKey(USERNAME)) {
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
        }

        // and generate the user token for the first time ... then no need to do later
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {

            // if there is an error, display some error information to the user
            if (!task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Something is wrong!",
                        Toast.LENGTH_SHORT).show();

            } else {
                // otherwise if the token worked, store it for later
                if (CLIENT_REGISTRATION_TOKEN == null) {
                    CLIENT_REGISTRATION_TOKEN = task.getResult();
                }
                Log.e("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
            }
        });

        // store the preferences in the username
        my_username = getSharedPreferences("MySharedPref", MODE_PRIVATE).getString(
                "userName", null);

        // if the username is not null, go to the HomepageActivity class
        if (my_username != null) {
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
        }


        // else recognize the login button
        login_button = findViewById(R.id.login_button);

        // Listen for a click on the login button
        login_button.setOnClickListener(view -> {

            // Save down the username from the user
            my_username = ((EditText) findViewById(R.id.enter_username)).getText().toString();

            // Write a message to the database
            login_user();

            // Store the username in shared preferences to skip login if already done
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",
                    MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("userName", my_username);
            myEdit.putString("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
            myEdit.commit();


            // start the new activity
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
        });

        // TODO: Homescreen design - still pending


        // TODO: move screens to fragments instead of regular layouts
        // https://androidresearch.wordpress.com/2016/10/29/android-bottomnavigationview-example/

        // TODO bottom menu: https://github.com/PhanVanLinh/AndroidBottomMenu
        // TODO: this has been depreciated so need the updated stuffs
         findViewById(R.id.bottomNavigationView).setOnNavigationItemSelectedListener(item ->
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        //switchFragment(new RecentsFragment());
                        break;
                    case R.id.menu_profile:
                        //switchFragment(new FavoritesFragment());
                        break;
                    case R.id.menu_search:
                        //switchFragment(new ExploreFragment());
                        break;
                }
        });

        // TODO: can we stick this in the user class and initialize in the onCreate
        //  then save to database?

        // define the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // if the location manager permissions are not enabled, request access from user
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(
                    edu.neu.madcourse.timber.MainActivity.this);
            alert.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes",
                    (dialog, which) -> startActivity(new Intent
                            (Settings.ACTION_LOCATION_SOURCE_SETTINGS))).
                    setNegativeButton("No", (dialog, which) -> dialog.cancel());
            alert.create().show();
        } else {
            // otherwise move forward by checking permissions again
            if (ActivityCompat.checkSelfPermission(
                    edu.neu.madcourse.timber.MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                    edu.neu.madcourse.timber.MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                // if permissions aren't enabled, request from user
                ActivityCompat.requestPermissions(
                        edu.neu.madcourse.timber.MainActivity.this,
                        new String[]{Manifest.permission.
                                ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                // if permissions are granted, find the last location
                location = locationManager.getLastKnownLocation
                        (LocationManager.GPS_PROVIDER);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                // if there is a location, update TextView to include lat and long coordinates
                if (location != null) {
                    double distance = findDistance(38.89511,-77.03637);
                    Toast.makeText(this, "Latitude: " + latitude + "\nLongitude: " +
                            longitude + "\nDistance from Washington, DC: " + distance,
                            Toast.LENGTH_SHORT).show();
                } else {
                    // if there is no location, send error to the user
                    Toast.makeText(this,
                            "Unable to find location data", Toast.LENGTH_SHORT).show();
                }
            }
        }
        */

    }

    /*
    // from previous app
    private void login_user() {
        new Thread(() -> {
            // connect to the database and look at the users
            DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference(
                    "Users/" + my_username);

            myUserRef.addValueEventListener(new ValueEventListener() {

                public User my_user;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // if the user exists, get their data
                    if (dataSnapshot.exists()) {
                        my_user = dataSnapshot.getValue(User.class);
                    } else {
                        // else create a new user and store their token
                        myUserRef.setValue(new User(my_username, CLIENT_REGISTRATION_TOKEN));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // if getting post failed, log a message
                    Log.w(TAG, "my_user start login onCancelled",
                            databaseError.toException());
                }
            });

        }).start();
    }
    */

    // math supplemented by these posts:
    // https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
    // https://gis.stackexchange.com/questions/5821/calculating-latitude-longitude-x-miles-from-point

    // if we want an adjustable radius -> public boolean findDistance(double otherLatitude, double otherLongitude, int searchRadius) {
    public double findDistance(double otherLatitude, double otherLongitude) {
        int R = 6371; // radius of the earth
        //double M = 3958.761; // convert Radians to miles

        double latDistance = Math.toRadians(otherLatitude - latitude);
        double lonDistance = Math.toRadians(otherLongitude - longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(otherLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // to approx. convert to miles
        double distance = Math.round(Math.sqrt(Math.pow(R * c, 2)) * 0.6213712);

        // return (distance <= searchRadius);
        return (distance); // to approx. convert to miles
    }
}