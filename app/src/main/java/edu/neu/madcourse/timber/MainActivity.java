package edu.neu.madcourse.timber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startButtonActivity = new Intent(getApplicationContext(), SwipeTestActivity.class);
        startActivity(startButtonActivity);
        setContentView(R.layout.swipe_test);
    }
}