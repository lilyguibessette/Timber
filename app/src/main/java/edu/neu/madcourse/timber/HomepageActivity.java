package edu.neu.madcourse.timber;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class HomepageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide the action bar for aesthetics
        getSupportActionBar().hide();

        // TODO: if-then to determine the Activity path
        //setContentView(R.layout.homepage_contractor);
        setContentView(R.layout.screen_homepage);

    }
}
