package edu.neu.madcourse.timber.fcm_server;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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



    /**
    Checking Distance between two points
     */
    // if we want an adjustable radius -> public boolean findDistance(double otherLatitude, double otherLongitude, int searchRadius) {
    // math supplemented by these posts:
    // https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
    // https://gis.stackexchange.com/questions/5821/calculating-latitude-longitude-x-miles-from-point
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



    public static Location getLocation(Activity activity, Context context){

        // TODO: can we stick this in the user class and initialize in the onCreate
        //  then save to database?

        // define the location manager
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // if the location manager permissions are not enabled, request access from user
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(
                    context);
            alert.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes",
                    (dialog, which) -> context.startActivity(new Intent
                            (Settings.ACTION_LOCATION_SOURCE_SETTINGS))).
                    setNegativeButton("No", (dialog, which) -> dialog.cancel());
            alert.create().show();
        } else {
            // otherwise move forward by checking permissions again
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                // if permissions aren't enabled, request from user
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.
                                ACCESS_FINE_LOCATION}, 1);
            } else {
                // if permissions are granted, find the last location
                Location location = locationManager.getLastKnownLocation
                        (LocationManager.GPS_PROVIDER);
                return location;
            }
        }
        return null;
    }
}
