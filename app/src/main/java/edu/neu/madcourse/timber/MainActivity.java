package edu.neu.madcourse.timber;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;
import edu.neu.madcourse.timber.fcm_server.Utils;

public class MainActivity extends AppCompatActivity {

    // login screen variables
    private static final String TAG = MainActivity.class.getSimpleName();
    public String my_username;
    public String my_usertype;
    public String my_token;
    private static final String USERNAME = "USERNAME";
    private static final String USERTYPE = "USERTYPE";
    private static final String CONTRACTORS = "CONTRACTORS";
    private static final String HOMEOWNERS = "HOMEOWNERS";
    private static String CLIENT_REGISTRATION_TOKEN;
    private static String SERVER_KEY = ""; // TODO: set up connection to database
    private static Button login_button;
    private static Button createUserButton;
    private RadioGroup radioGroupUserType;
    private RadioButton radioButtonUserType;

    // GPS variables
    private LocationManager locationManager;
    private Location location;
    private double latitude;
    private double longitude;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(USERNAME)) {
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
        }
        setContentView(R.layout.login_screen);
        login_button = findViewById(R.id.login_button);
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

        // if the username is not null, go to the ReceivedActivity class
        if (my_username != null) {
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
        }

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

        createUserButton = findViewById(R.id.create_account_button);
        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // use dialog for add link
                startCreateUserDialog();
            }
        });
    }


    public void startLinkCollectorDialog() {
        DialogFragment linkDialog = new LinkCollectorDialogFragment();
        linkDialog.show(getSupportFragmentManager(), "LinkDialogFragment");
    }

    public void onDialogPositiveClick(DialogFragment linkDialog) {
        Dialog createUserDialog = linkDialog.getDialog();


        radiogroup_type = (RadioGroup) findViewById(R.id.radiogroup_usertype);
        int selectedNationalityId = radioGroupNationality.getCheckedRadioButtonId();
        radioButtonNationality = (RadioButton) findViewById(selectedNationalityId);
        nationality = radioButtonNationality.getText().toString();

        my_username = ((EditText) createUserDialog.findViewById(R.id.create_username)).getText().toString();
        param1 = ((EditText) createUserDialog.findViewById(R.id.param1)).getText().toString();
        param2 = ((EditText) createUserDialog.findViewById(R.id.param2)).getText().toString();
        email = ((EditText) createUserDialog.findViewById(R.id.create_email)).getText().toString();
        zip = ((EditText) createUserDialog.findViewById(R.id.create_zip)).getText().toString();
        phone = ((EditText) createUserDialog.findViewById(R.id.create_phone)).getText().toString();

        if (my_usertype != null && my_username != null) {
            linkDialog.dismiss();
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, R.string.new_account_confirm, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else {
            Toast.makeText(MainActivity.this, R.string.create_account_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment linkDialog) {
        linkDialog.dismiss();
    }

        private void login_user(){
            new Thread(() -> {
                // connect to the database and look at the users
                DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference(
                        my_usertype +"/" + my_username);

                myUserRef.addValueEventListener(new ValueEventListener() {
                    public User my_user;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // if the user exists, get their data
                        if (dataSnapshot.exists()) {
                            my_user = dataSnapshot.getValue(User.class);
                        } else {
                            // else create a new user and store their token
                            if (my_usertype == HOMEOWNERS) {
                                myUserRef.setValue(new Homeowner(my_username,
                                        CLIENT_REGISTRATION_TOKEN,
                                        ));
                            } else{
                                myUserRef.setValue(new Contractor(my_username,
                                        CLIENT_REGISTRATION_TOKEN,
                                        Utils.getLocation(),
                                         businessName,
                                         taxID,
                                         email,
                                         zipcode,
                                         phoneNumber));
                            }
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

/*


        // hide the action bar for aesthetics
        getSupportActionBar().hide();



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

    //}

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

}