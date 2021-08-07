package edu.neu.madcourse.timber;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import static android.content.ContentValues.TAG;

public class SwipeTestActivity extends AppCompatActivity {

    private GestureDetector mDetector;
    TextView textOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_swipe);

        // this is the view we will add the gesture detector to
        View myView = findViewById(R.id.swipeArea);
        textOutput = findViewById(R.id.swipeHappenedTest);

        // get the gesture detector
        mDetector = new GestureDetector(this, new MyGestureListener());

        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector
        myView.setOnTouchListener(touchListener);
    }

    // This touch listener passes everything on to the gesture detector.
    // That saves us the trouble of interpreting the raw touch events
    // ourselves.
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event
            return mDetector.onTouchEvent(event);

        }
    };

    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        int pos_threshold = 100;
        int speed_threshold = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");

            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("TAG", "onScroll: ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float speedX, float speedY) {
            Log.d("TAG", "onFling: ");

            float xDiff = e2.getX() - e1.getX();
            float yDiff = e2.getY() - e1.getY();

            try{
                if(Math.abs(xDiff) > Math.abs(yDiff)){
                    if(Math.abs(xDiff) > pos_threshold && Math.abs(speedX) > speed_threshold){
                        if(xDiff > 0){
                            textOutput.setText("Swiped Right");

                        } else{
                            textOutput.setText("Swiped Left");
                        }
                        return true;
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            Log.e(TAG,"returning false");
            return false;
        }
    }



}
