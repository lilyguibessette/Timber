package edu.neu.madcourse.timber.profile.update;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import edu.neu.madcourse.timber.MainActivity;
import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.profile.ProfileFragment;
import edu.neu.madcourse.timber.users.Contractor;
import edu.neu.madcourse.timber.users.Homeowner;

public class UpdateHomeownerProfileDialogFragment extends  DialogFragment{
    private static final String TAG = "UpdateProfileDialogFragment";
    private Button updateButton;
    private Button createButton;
    private Button logout;
    public String my_username;
    public String my_usertype = "HOMEOWNERS";
    public String my_param1;
    public String my_param2;
    public String my_email;
    public String my_zip;
    public String my_phone;

    public UpdateHomeownerProfileDialogFragment() {
        // Required empty public constructor
    }

    public static UpdateHomeownerProfileDialogFragment newInstance() {
        UpdateHomeownerProfileDialogFragment fragment = new UpdateHomeownerProfileDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_account_homeowner, container, false);

        createButton = view.findViewById(R.id.update_account);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_username = ((EditText) view.findViewById(R.id.update_username)).getText().toString();
                my_param1 = ((EditText) view.findViewById(R.id.update_param1)).getText().toString();
                my_param2 = ((EditText) view.findViewById(R.id.update_param2)).getText().toString();
                my_email = ((EditText) view.findViewById(R.id.update_email)).getText().toString();
                my_zip = ((EditText) view.findViewById(R.id.update_zip)).getText().toString();
                my_phone = ((EditText) view.findViewById(R.id.update_phone)).getText().toString();
                Log.e("UpdateProfileDialogFragment", "UpdateProfileDialogFragment create click");
                // use dialog for add link
                update_profile();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new ProfileFragment());
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(), "Update complete" , Toast.LENGTH_SHORT).show();
                fragmentTransaction.commit();


            }
        });

        updateButton = view.findViewById(R.id.cancel_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("UpdateProfileDialogFragment", "UpdateProfileDialogFragment cancel click");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new ProfileFragment());
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(), "going to cancel" , Toast.LENGTH_SHORT).show();
                fragmentTransaction.commit();
            }
        });
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("UpdateProfileDialogFragment", "UpdateProfileDialogFragment cancel click");
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TimberSharedPref",
                        MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("USERNAME", null);
                myEdit.putString("USERTYPE", null);
                myEdit.commit();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        return view;
    }

    private void update_profile() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
            // connect to the database and look at the users
            my_username = sharedPreferences.getString("USERNAME", null);
            my_usertype = sharedPreferences.getString("USERTYPE", null);
            DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference(
                    my_usertype + "/" + my_username);

            myUserRef.addValueEventListener(new ValueEventListener() {
                public User my_user;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // if the user exists, get their data
                    if (dataSnapshot.exists()) {
                        Homeowner my_user = dataSnapshot.getValue(Homeowner.class);
                        //my_user.setImage();
                        // add setters to my_user
                        myUserRef.setValue(my_user);

                    } else {
                        // log error
                        Log.e(TAG, "cant update");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // if getting post failed, log a message
                    Log.w(TAG, "update profile onCancelled",
                            databaseError.toException());
                }
            });

        }).start();
    }


}



/*

    public void onDialogPositiveClick(DialogFragment viewFragment) {
        if (my_usertype != null && my_usertype.equals(HOMEOWNERS)) {
            // change to projects
            Dialog view = viewFragment.getDialog();
            //radioGroupUserType = (RadioGroup) createUserDialog.findViewById(R.id.radiogroup_usertype);
            //int selectedUserType = radioGroupUserType.getCheckedRadioButtonId();
            Log.e(TAG, " ondialog pos click");

            my_username = ((EditText) view.findViewById(R.id.update_username)).getText().toString();
            my_param1 = ((EditText) view.findViewById(R.id.update_param1)).getText().toString();
            my_param2 = ((EditText) view.findViewById(R.id.update_param2)).getText().toString();
            my_email = ((EditText) view.findViewById(R.id.update_email)).getText().toString();
            my_zip = ((EditText) view.findViewById(R.id.update_zip)).getText().toString();
            my_phone = ((EditText) view.findViewById(R.id.update_phone)).getText().toString();
            //TODO SET IMAGE HERE?

            if (my_usertype != null && my_username != null
                    && my_param1 != null && my_param2 != null
                    && my_email != null && my_zip != null && my_phone != null) {
                view.dismiss();
                update_profile();
                Toast.makeText(getContext(), "Project Created!", Toast.LENGTH_SHORT).show();
                // move to swipe screen for contractors?
            } else {
                Toast.makeText(getActivity(), R.string.create_project_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            //TODO
            Dialog view = viewFragment.getDialog();
            //radioGroupUserType = (RadioGroup) createUserDialog.findViewById(R.id.radiogroup_usertype);
            //int selectedUserType = radioGroupUserType.getCheckedRadioButtonId();
            Log.e(TAG, " ondialog pos click");

            my_username = ((EditText) view.findViewById(R.id.update_username)).getText().toString();
            my_param1 = ((EditText) view.findViewById(R.id.update_param1)).getText().toString();
            my_param2 = ((EditText) view.findViewById(R.id.update_param2)).getText().toString();
            my_email = ((EditText) view.findViewById(R.id.update_email)).getText().toString();
            my_zip = ((EditText) view.findViewById(R.id.update_zip)).getText().toString();
            my_phone = ((EditText) view.findViewById(R.id.update_phone)).getText().toString();
            //TODO SET IMAGE HERE?

            if (my_usertype != null && my_username != null
                    && my_param1 != null && my_param2 != null
                    && my_email != null && my_zip != null && my_phone != null) {
                view.dismiss();
                update_profile();
                Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                // move to swipe screen for contractors?
            } else {
                Toast.makeText(getActivity(), R.string.update_account_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

 */