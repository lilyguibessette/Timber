package edu.neu.madcourse.timber;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // GPS variables
    private LocationManager locationManager;
    private Location location;
    private double latitude;
    private double longitude;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        // TODO: Homescreen design

        // TODO bottom menu: https://github.com/PhanVanLinh/AndroidBottomMenu
        BottomNavigationView bottomNavigation =
                (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        // TODO: this has been depreciated so need the updated stuffs
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item);
            return true;
        });

        // TODO: can we stick this in a class and initialize in the onCreate?
        // TODO: maybe where we read user information
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
                    Toast.makeText(this, "Latitude: " + latitude + "\nLongitude: " +
                            longitude, Toast.LENGTH_SHORT).show();
                } else {
                    // if there is no location, send error to the user
                    Toast.makeText(this,
                            "Unable to find location data", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // bottom menu switch cases
    // TODO: make and assign these fragments
    // TODO: move screens to fragments instead of regular layouts
    // https://androidresearch.wordpress.com/2016/10/29/android-bottomnavigationview-example/
    private void handleBottomNavigationItemSelected(MenuItem item) {
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
    }

    // math supplemented by these posts:
    // https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
    // https://gis.stackexchange.com/questions/5821/calculating-latitude-longitude-x-miles-from-point
    public boolean findDistance(double otherLatitude, double otherLongitude, int searchRadius) {
        int R = 6371; // radius of the earth
        double M = 3958.761; // convert Radians to miles

        double latDistance = Math.toRadians(otherLatitude - latitude);
        double lonDistance = Math.toRadians(otherLongitude - longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(otherLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = Math.sqrt(Math.pow(R * c * M, 2));
        return (distance <= searchRadius);
    }
}