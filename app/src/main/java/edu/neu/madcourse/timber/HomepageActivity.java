package edu.neu.madcourse.timber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomepageActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    public String my_username;
    public String my_usertype;
    public String my_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Getting current username that is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        my_username = sharedPreferences.getString("userName", "Not found");
        my_token = sharedPreferences.getString("CLIENT_REGISTRATION_TOKEN", "Not found");

        // If we don't have the userName or token, restart the login activity
        if(my_username == "Not found" || my_token == "Not found"){
            Intent intent = new Intent(HomepageActivity.this, MainActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.main_screen);
        bottomNavigation = findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(HomepageFragment.newInstance());

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        openFragment(HomepageFragment.newInstance());
                        return true;
                }
                switch (item.getItemId()) {
                    case R.id.menu_feed:
                        openFragment(fragment_news_feed.newInstance());
                        return true;
                }
                return false;
            };
}