package edu.neu.madcourse.timber.fcm_server;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.timber.HomepageFragment;
import edu.neu.madcourse.timber.ItemModel;

// Reference: Firebase Demo 3 from classwork

public class Utils {

    public static String convertStreamToString(InputStream inputStream) {
        // creating a new string builder
        StringBuilder stringBuilder = new StringBuilder();

        // try to read the stream
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String len;

            // while there is still content, add to the string builder
            while ((len = bufferedReader.readLine()) != null) {
                stringBuilder.append(len);
            }

            // then close and return the string builder
            bufferedReader.close();
            return stringBuilder.toString().replace(",", ",\n");

        // look for any exceptions and print them out
        } catch (Exception e) {
            e.printStackTrace();
        }

        // otherwise return an empty string
        return "";
    }

    public static String fcmHttpConnection(String serverToken, JSONObject jsonObject) {

        // try to open the HTTP connection
        try {
            // Open the HTTP connection and send the payload
            HttpURLConnection conn = (HttpURLConnection) new URL("https://fcm.googleapis.com/fcm/send").openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", serverToken);
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            return convertStreamToString(inputStream);

        // look for IOExceptions - if they happen, return NULL
        } catch (IOException e) {
            return "NULL";
        }

    }

    protected void updateLocationData() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        if (checkPermissions()) {
            getLocation();
        } else {
            Log.i(TAG, "Requesting permission");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(LocatorActivity.this, "Location permission denied. Update in settings.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setLocationText(Location location) {
        gps_latitude = location.getLatitude();
        gps_longitude = location.getLongitude();
        latitude.setText(new DecimalFormat("#.0#").format(gps_latitude));
        longitude.setText(new DecimalFormat("#.0#").format(gps_longitude));
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = (Location) task.getResult();
                        if (location != null) {
                            setLocationText
                                    (location);
                        } else {
                            LocationRequest locationRequest = new LocationRequest()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(100)
                                    .setFastestInterval(10)
                                    .setNumUpdates(1);
                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    Location location = locationResult.getLastLocation();
                                    setLocationText(location);
                                }
                            };
                        }
                    } else {
                        Log.w(TAG, "getLocation:exception", task.getException());
                        Toast.makeText(LocatorActivity.this, "Location unavailable.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Snackbar.make(
                    findViewById(R.id.activity_locator),
                    R.string.location_tracking_off,
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Dismiss
                        }
                    })
                    .show();
        }
    }


    public ArrayList<Double> getLocation(){
        List<Double> location = new ArrayList<>();
        //Double latitude =
        //Double longitude =
        return location;
    }

    public double findDistance(double latitude, double longitude, double otherLatitude, double otherLongitude) {
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
