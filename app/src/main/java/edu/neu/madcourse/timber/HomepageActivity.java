package edu.neu.madcourse.timber;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity {

    private static final String TAG = "HomepageActivity";
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_homepage);

        CardStackView cardStackView = findViewById(R.id.homepage_card_stack_view);

        // Using yuyakaido card stack manager for our swiping implementation
        manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right){
                    Toast.makeText(HomepageActivity.this, "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top){
                    Toast.makeText(HomepageActivity.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left){
                    Toast.makeText(HomepageActivity.this, "Direction Left", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom){
                    Toast.makeText(HomepageActivity.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

                // Paginating
                if (manager.getTopPosition() == adapter.getItemCount() - 5){
                    paginate();
                }

            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                //TextView tv = view.findViewById(R.id.item_name);
                //Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                //TextView tv = view.findViewById(R.id.item_name);
                //Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }
        });

        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(30.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(false);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());

        adapter = new CardStackAdapter(addList());

        Log.e(TAG,"create adapter");

        cardStackView.setLayoutManager(manager);

        Log.e(TAG,"set manager");
        cardStackView.setAdapter(adapter);

        Log.e(TAG,"set adapter");

        cardStackView.setItemAnimator(new DefaultItemAnimator());

        Log.e(TAG,"set item animator");

    }

    private void paginate() {
        List<ItemModel> old = adapter.getItems();
        List<ItemModel> fresh = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(old, fresh);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setItems(fresh);
        result.dispatchUpdatesTo(adapter);
    }

    private List<ItemModel> addList() {
        Log.e(TAG,"addList called");
        List<ItemModel> items = new ArrayList<>();
        items.add(new ItemModel(R.drawable.sample1, "Markonah", "24"));
        items.add(new ItemModel(R.drawable.sample2, "Marpuah", "20"));
        items.add(new ItemModel(R.drawable.sample3, "Sukijah", "27"));
        items.add(new ItemModel(R.drawable.sample4, "Markobar", "19"));
        items.add(new ItemModel(R.drawable.sample5, "Marmut", "25"));

        items.add(new ItemModel(R.drawable.sample1, "Markonah", "24"));
        items.add(new ItemModel(R.drawable.sample2, "Marpuah", "20"));
        items.add(new ItemModel(R.drawable.sample3, "Sukijah", "27"));
        items.add(new ItemModel(R.drawable.sample4, "Markobar", "19"));
        items.add(new ItemModel(R.drawable.sample5, "Marmut", "25"));
        return items;
    }
}
