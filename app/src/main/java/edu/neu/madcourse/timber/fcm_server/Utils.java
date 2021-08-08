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
}
