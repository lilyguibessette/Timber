package edu.neu.madcourse.timber;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.DialogFragment;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.neu.madcourse.timber.fcm_server.Utils;
import edu.neu.madcourse.timber.users.Contractor;
import edu.neu.madcourse.timber.users.Homeowner;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

//TODO NOTES
/*
- Photo upload > put some todos around - got photo selection to work and replace the imageview - need to hook up to database
- fix login issues?
- Messaging  notifications and read in recycler views from database
- Finalize database structures
   - Need to plug everything in
- GPS and filtering
- Match notifications
- login is still wonky - need to be careful and clean up where shared prefs are used

 */


public class MainActivity extends AppCompatActivity implements CreateUserDialogFragment.CreateUserDialogListener {

    // login screen variables
    private static final String TAG = MainActivity.class.getSimpleName();
    public String my_username;
    public String my_usertype;
    public String my_param1;
    public String my_param2;
    public String my_email;
    public String my_zip;
    public String my_phone;
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
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private Uri photoURI;

    // GPS variables
    private LocationManager locationManager;
    private Location location;
    private double latitude;
    private double longitude;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        my_username = getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString(
                USERNAME, null);
        my_usertype = getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString(
                USERTYPE, null);
        location = Utils.getLocation(this,this);
        if (my_username != null && my_usertype != null && my_username != "LOGOUT") {
            Log.e(TAG,"start activity 114");
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(USERNAME)) {
            Log.e(TAG,"start activity 114");
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
        }
        setContentView(R.layout.login_screen);

        // hide the action bar for aesthetics
        getSupportActionBar().hide();

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

        login_button = findViewById(R.id.login_button);
        radioGroupUserType = findViewById(R.id.login_usertype);


        SharedPreferences sharedPreferences = getSharedPreferences("TimberSharedPref",
                MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(USERNAME, my_username);
        myEdit.putString(USERTYPE, my_usertype);
        myEdit.commit();
        // if the username is not null, go to the ReceivedActivity class
        if (my_username != null && my_usertype != null) {
            //Log.e(TAG,"autologin username: " + my_username + " usertype: " + my_usertype);
            //startActivity(new Intent(MainActivity.this, HomepageActivity.class));
        }

        login_button.setOnClickListener(view -> {
            SharedPreferences sharedPreferences2 = getSharedPreferences("TimberSharedPref",
                    MODE_PRIVATE);
            SharedPreferences.Editor myEdit2 = sharedPreferences2.edit();

            //TODO VALIDATE LOGIN FROM DB QUERY
            int radioUserType = radioGroupUserType.getCheckedRadioButtonId();
            radioButtonUserType = (RadioButton) findViewById(radioUserType);


            my_usertype = radioButtonUserType.getText().toString().toUpperCase();
            my_username = ((EditText) findViewById(R.id.enter_username)).getText().toString();
            if(my_username.length() == 0){
                return;
            }
            Log.e(TAG,"181, my_usertype is "+ my_usertype);
            DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference(
                    my_usertype + "/" + my_username);
            myEdit2.putString(USERNAME, my_username);
            myEdit2.putString(USERTYPE, my_usertype);
            // Write a message to the database
            login_user();

            // Store the username in shared preferences to skip login if already done

            myEdit2.putString(USERNAME, my_username);
            myEdit2.putString(USERTYPE, my_usertype);
            myEdit2.putString("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
            myEdit2.commit();

            if (my_username != null && my_usertype != null) {
                login_user();
            }
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

    public void startCreateUserDialog() {
        DialogFragment createAccountDialog = new CreateUserDialogFragment();
        createAccountDialog.show(getSupportFragmentManager(), "CreateUserDialogFragment");
    }

    public void onDialogPositiveClick(DialogFragment createAccountDialog) {
        Dialog createUserDialog = createAccountDialog.getDialog();
        radioGroupUserType = (RadioGroup) createUserDialog.findViewById(R.id.radiogroup_usertype);
        int selectedUserType = radioGroupUserType.getCheckedRadioButtonId();

        Log.e(TAG," " + selectedUserType);

        radioButtonUserType = (RadioButton) createUserDialog.findViewById(selectedUserType);
        String usertype = radioButtonUserType.getText().toString();

        Log.e(TAG,usertype);

        if (usertype.equals("Homeowner")) {
            Log.e(TAG,"entered if");
            my_usertype = HOMEOWNERS;
        } else {
            Log.e(TAG,"entered else");
            my_usertype = CONTRACTORS;
        }

        my_username = ((EditText) createUserDialog.findViewById(R.id.create_username)).getText().toString();
        my_param1 = ((EditText) createUserDialog.findViewById(R.id.create_param1)).getText().toString();
        my_param2 = ((EditText) createUserDialog.findViewById(R.id.create_param2)).getText().toString();
        my_email = ((EditText) createUserDialog.findViewById(R.id.create_email)).getText().toString();
        my_zip = ((EditText) createUserDialog.findViewById(R.id.create_zip)).getText().toString();
        my_phone = ((EditText) createUserDialog.findViewById(R.id.create_phone)).getText().toString();

        if (my_usertype != null && my_username != null
                && my_param1 != null && my_param2 != null
                && my_email != null && my_zip != null && my_phone != null) {
            createUserDialog.dismiss();
            SharedPreferences sharedPreferences = getSharedPreferences("TimberSharedPref",
                    MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString(USERNAME, my_username);
            myEdit.putString(USERTYPE, my_usertype);
            myEdit.putString("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
            myEdit.commit();
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, R.string.new_account_confirm, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            Log.e(TAG,"pre login");
            login_user();
            Log.e(TAG,"post login");
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
        } else {
            Toast.makeText(MainActivity.this, R.string.create_account_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment createUserDialog) {
        createUserDialog.dismiss();
    }

    private void login_user() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("TimberSharedPref",
                    MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString(USERNAME, my_username);
            myEdit.putString(USERTYPE, my_usertype);
            // connect to the database and look at the users
            DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference(
                    my_usertype + "/" + my_username);
            location = Utils.getLocation(this, this.getBaseContext());
            myUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // if the user exists, get their data
                    if (dataSnapshot.exists()) {
                        Log.e(TAG,"login_user: User exists in DB");
                        Log.e(TAG,"login_user: " + my_usertype);
                        startActivity(new Intent(MainActivity.this, HomepageActivity.class));
                    } else {
                        Log.e(TAG,"login_user: User does not exist in DB");
                        Log.e(TAG,"login_user: " + my_usertype);
                        Log.e(TAG,location.toString());
                       // Log.e(TAG,location.getLatitude());
                        Log.e(TAG,String.valueOf(location.getLongitude()));
                        if (my_usertype.equals(HOMEOWNERS)) {
                            try {
                                myUserRef.setValue(new Homeowner(my_username,
                                        CLIENT_REGISTRATION_TOKEN,
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        my_param1,
                                        my_param2,
                                        my_email,
                                        my_zip,
                                        my_phone));
                            } catch(NullPointerException exc){
                                Toast.makeText(MainActivity.this, "Invalid login, please check username", Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"null pointer exception");
                                Log.e(TAG,exc.getMessage());
                            }
                        } else {
                            try {
                                myUserRef.setValue(new Contractor(my_username,
                                        CLIENT_REGISTRATION_TOKEN,
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        my_param1,
                                        my_param2,
                                        my_email,
                                        my_zip,
                                        my_phone));
                            } catch(NullPointerException exc){
                                Toast.makeText(MainActivity.this, "Invalid login, please check username", Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"null pointer exception");
                                Log.e(TAG,exc.getMessage());
                            }
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




    public void readData(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                listener.onFailure();
            }
        });

    }
}