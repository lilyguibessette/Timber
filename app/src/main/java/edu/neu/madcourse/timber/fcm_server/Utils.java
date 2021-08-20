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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.neu.madcourse.timber.users.Project;

// Reference: Firebase Demo 3 from classwork

public class Utils {
    private static final String SERVER_KEY = "key=AAAA7aKez00:APA91bEkDUkYiAq8qyquB8bS_F5DkDfNDuUgbpnF8yMf0kTsQy3RszivsRmJXZ1sJdN1kjaHDptTXRR6eM6Y7VqxfNE5Z1-52ZkDsaxpPzH26WjzhWuaeUPpB5eAcOPE758dv8ic_akm";

    // function to convert a stream to a string
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

    // function to get the fcm HTTP connection
    public static String fcmHttpConnection(String serverToken, JSONObject jsonObject) {

        // try to open the HTTP connection
        try {
            // open the HTTP connection and send the payload
            HttpURLConnection conn = (HttpURLConnection) new URL("https://fcm.googleapis.com/fcm/send").openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", serverToken);
            conn.setDoOutput(true);

            // send FCM message content
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();

            // read FCM response
            InputStream inputStream = conn.getInputStream();
            return convertStreamToString(inputStream);

            // look for IOExceptions - if they happen, return NULL
        } catch (IOException e) {
            return "NULL";
        }
    }

    // function to check the distance between two locations
    // if we want an adjustable radius -> public boolean findDistance(double otherLatitude, double otherLongitude, int searchRadius) {
    // math supplemented by these posts:
    // https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
    // https://gis.stackexchange.com/questions/5821/calculating-latitude-longitude-x-miles-from-point
    public static double findDistance(double latitude, double longitude, double otherLatitude, double otherLongitude) {
        int R = 6371; // radius of the earth
        double latDistance = Math.toRadians(otherLatitude - latitude);
        double lonDistance = Math.toRadians(otherLongitude - longitude);

        // doing math to find the distance
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(otherLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = Math.round(Math.sqrt(Math.pow(R * c, 2)) * 0.6213712); // to approx. convert to miles

        return (distance);
    }

    // function to get the location
    public static Location getLocation(Activity activity, Context context) {
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

    // function to send a firebase message
    public static void sendNotification(String my_username, String other_user, Project project) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // create the JSON objects
                JSONObject jPayload = new JSONObject();
                JSONObject jNotification = new JSONObject();

                // then try to put the information in the JSON
                try {
                    jNotification.put("title", "New match with " + my_username);
                    jNotification.put("body", "Project: " + project.project_id + "!");
                    jNotification.put("sound", "default");
                    jNotification.put("badge", "1");

                    // Populate the Payload object with our notification information
                    jPayload.put("to", "/topics/" + other_user);
                    jPayload.put("priority", "high");
                    jPayload.put("notification", jNotification);

                    // catch exceptions
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // send to topic of the user we're sending to
                final String messageResponse = Utils.fcmHttpConnection(SERVER_KEY, jPayload);
                Log.d("NOTIFICATION", "NOTIFICATION SENT TO " + other_user + " FROM " + my_username + " ABOUT " + project.getProject_id());
                Log.d("NOTIFICATION", messageResponse);
            }
        }).start();
    }

    public static void sendMessageNotification(String my_username, String other_user, String project, String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jPayload = new JSONObject();
                JSONObject jNotification = new JSONObject();
                try {
                    jNotification.put("title", "New message from " + my_username);
                    jNotification.put("body", "Project: " + project + " " + message);
                    jNotification.put("sound", "default");
                    jNotification.put("badge", "1");
                    // Populate the Payload object with our notification information
                    // sent to topic of the user we're sending to
                    jPayload.put("to", "/topics/" + other_user);
                    jPayload.put("priority", "high");
                    jPayload.put("notification", jNotification);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String messageResponse = Utils.fcmHttpConnection(SERVER_KEY, jPayload);
                Log.d("MESSAGE NOTIFICATION", "NOTIFICATION SENT TO " + other_user + " FROM " + my_username + " ABOUT " + project);
                Log.d("MESSAGE NOTIFICATION", messageResponse);
            }
        }).start();
    }

    public static void subscribeToMyMessages(String topic, Activity activity) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to " + topic;
                        if (!task.isSuccessful()) {
                            msg = "Failed to subscribe to " + topic;
                        }
                        //Toast.makeText(activity.getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
