package edu.neu.madcourse.timber.homeswipe;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.profile.ProfileFragment;

public class SelectProjectDialogFragment extends DialogFragment {
    private static final String TAG = "SelectProjectDialogFragment";
    private Button cancelButton;
    private Button selectButton;
    private RecyclerView selectProjectRecyclerView;
    private LinearLayoutManager selectProjectLayoutManager;
    private SelectProjectAdapter selectProjectAdapter;
    private String my_username;
    private final ArrayList<String> projects = new ArrayList<>();

    public SelectProjectDialogFragment() {
        // Required empty public constructor
    }

    public static SelectProjectDialogFragment newInstance() {
        SelectProjectDialogFragment fragment = new SelectProjectDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_project, container, false);
        my_username = getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE).getString(
                "USERNAME", null);

        Log.e(TAG,my_username);

        DatabaseReference projectsRef = FirebaseDatabase.getInstance().getReference(
                "ACTIVE_PROJECTS");

        projectsRef.orderByChild("username").equalTo(my_username).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@androidx.annotation.NonNull @NotNull DataSnapshot snapshot) {
                Map<String,Object> projectData = (Map<String,Object>) snapshot.getValue();

                for(Map.Entry<String, Object> each : projectData.entrySet()){
                    Map singleProject = (Map) each.getValue();
                    projects.add((String) singleProject.get("project_id"));
                };

                createRecyclerView(view);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

            }
        });



        //Create a recycler view...ugh


        cancelButton = view.findViewById(R.id.cancel_select_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("SelectProjectDialogFragment", "SelectProjectDialogFragment cancel click");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new HomepageFragment());
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(), "going to cancel" , Toast.LENGTH_SHORT).show();
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    private void createRecyclerView(View view) {
        // Create the recyclerview and populate it with the history
        selectProjectRecyclerView = view.findViewById(R.id.select_project_recycler);

        Log.e(TAG, "Profile projects: " + selectProjectRecyclerView.toString());
        selectProjectLayoutManager = new LinearLayoutManager(view.getContext());
        selectProjectRecyclerView.setHasFixedSize(true);
        selectProjectAdapter = new SelectProjectAdapter(projects);
        selectProjectRecyclerView.setAdapter(selectProjectAdapter);
        selectProjectLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        selectProjectRecyclerView.setLayoutManager(selectProjectLayoutManager);



        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.DOWN) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(getContext(), "Project Complete!", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getLayoutPosition();
                selectProjectAdapter.notifyItemRemoved(position);
                // project change status in database
                // get project id do stuff etc
                //TODO
            }
        });
        itemTouchHelper.attachToRecyclerView(selectProjectRecyclerView);
    }

}
