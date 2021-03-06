package edu.neu.madcourse.timber.profile.update;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.neu.madcourse.timber.MainActivity;
import edu.neu.madcourse.timber.R;
import edu.neu.madcourse.timber.profile.ProfileFragment;
import edu.neu.madcourse.timber.users.Contractor;
import edu.neu.madcourse.timber.users.Homeowner;

import static android.content.Context.MODE_PRIVATE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class UpdateContractorProfileDialogFragment extends DialogFragment {
    private static final String TAG = "UpdateProfileDialogFragment";
    private String my_usertype = "CONTRACTORS";
    private static final int PICK_IMAGE = 100;

    private Button cancelButton, updateButton, logout, updateImageButton;
    public String my_username, my_param1, my_specialty, my_param2, my_zip, my_email, my_phone;

    // items related to the update image section
    private ImageView imageView;
    private Bitmap bitmap;
    private InputStream inputStreamImg;
    private File destination = null;
    private String imgPath = null;
    private Uri imageUri;

    private SharedPreferences sharedPreferences;
    private DatabaseReference myUserRef;

    // instance for firebase storage and StorageReference
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public UpdateContractorProfileDialogFragment() {
        // Required empty public constructor
    }

    public static UpdateContractorProfileDialogFragment newInstance() {
        UpdateContractorProfileDialogFragment fragment = new UpdateContractorProfileDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the view
        View view = inflater.inflate(R.layout.update_account_contractor, container, false);

        // get access the user's data
        sharedPreferences = this.getActivity().getSharedPreferences("TimberSharedPref", MODE_PRIVATE);
        my_username = sharedPreferences.getString("USERNAME", null);
        my_usertype = sharedPreferences.getString("USERTYPE", null);
        myUserRef = FirebaseDatabase.getInstance().getReference(my_usertype + "/" + my_username);

        // get image storage data
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // define the interactive objects on the screen
        updateImageButton = view.findViewById(R.id.update_image);
        updateButton = view.findViewById(R.id.update_account);
        cancelButton = view.findViewById(R.id.cancel_button);
        logout = view.findViewById(R.id.logout);
        imageView = view.findViewById(R.id.image);

        // when the user clicks to update the image
        updateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        // when the user clicks to update the details
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "UpdateProfileDialogFragment create click");

                // get the variables to update
                my_username = ((EditText) view.findViewById(R.id.update_username)).getText().toString();
                my_param1 = ((EditText) view.findViewById(R.id.update_param1)).getText().toString();
                my_specialty = ((EditText) view.findViewById(R.id.update_specialty)).getText().toString();
                my_param2 = ((EditText) view.findViewById(R.id.update_param2)).getText().toString();
                my_zip = ((EditText) view.findViewById(R.id.update_zip)).getText().toString();
                my_email = ((EditText) view.findViewById(R.id.update_email)).getText().toString();
                my_phone = ((EditText) view.findViewById(R.id.update_phone)).getText().toString();

                // send those updates to the user profile
                update_profile();

                // send the user back to the previous page (profile)
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new ProfileFragment());
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(), "Update complete", Toast.LENGTH_SHORT).show();
                fragmentTransaction.commit();
            }
        });

        // when the user clicks to cancel, send to previous page
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "UpdateProfileDialogFragment cancel click");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new ProfileFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        // when the user clicks to log out
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "UpdateProfileDialogFragment logout click");
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

    // update the user's profile
    private void update_profile() {
        new Thread(() -> myUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public User my_user;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // if the user exists, get their data
                if (dataSnapshot.exists()) {
                    Contractor my_user = dataSnapshot.getValue(Contractor.class);
                    Log.e(TAG,"Made it to the my_user section");
                    // checking for valid input / no blanks
                    if (!my_username.equals("") & !my_user.getUsername().equals(my_username)) {
                        my_user.setUsername(my_username);
                    }
                    Log.e(TAG,"Made it to the my_username field");
                    if (!my_param1.equals("") & !my_user.getBusinessName().equals(my_param1)) {
                        my_user.setBusinessName(my_param1);
                    }
                    Log.e(TAG,"Made it to the my_param1 field");
                    if (!my_specialty.equals("") & !my_user.getSpecialty().equals(my_specialty)) {
                        my_user.setSpecialty(my_specialty);
                    }
                    Log.e(TAG,"Made it to the my_speciality field");
                    if (!my_param2.equals("") & !my_user.getTaxID().equals(my_param2)) {
                        my_user.setTaxID(my_param2);
                    }
                    Log.e(TAG,"Made it to the my_param2 field");
                    if (!my_email.equals("") & !my_user.getEmail().equals(my_email)) {
                        my_user.setEmail(my_email);
                    }
                    Log.e(TAG,"Made it to the my_email field");
                    if (!my_zip.equals("") & !my_user.getZipcode().equals(my_zip)) {
                        my_user.setZipcode(my_zip);
                    }
                    Log.e(TAG,"Made it to the my_zip field");
                    if (!my_phone.equals("") & !my_user.getPhoneNumber().equals(my_phone)) {
                        my_user.setPhoneNumber(my_phone);
                    }
                    Log.e(TAG,"Made it to the my_phone field");
                    if (!imgPath.equals("") & !my_user.getImage().equals(imgPath)) {
                        my_user.setImage(imgPath);
                    }
                    Log.e(TAG,"Made it to the my_image field");
                    myUserRef.setValue(my_user);
                    Log.e(TAG,"After setting it for the user");
                } else {
                    // if there's an issue log this error
                    Log.e(TAG, "cant update the profile");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // if getting post failed, log a message
                Log.w(TAG, "update profile onCancelled",
                        databaseError.toException());
            }
        })).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent gallery) {
        super.onActivityResult(requestCode, resultCode, gallery);
        if(resultCode == 0) { return; }

        inputStreamImg = null;
        Uri selectedImage = gallery.getData();

        try {
            // try to decipher the image
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().
                    getContentResolver(), selectedImage);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

            // save the image details
            destination = new File(getRealPathFromURI(selectedImage));
            imgPath = destination.getName();
            storageReference.child(imgPath).putFile(selectedImage);

            // set the imageView to show the new pic
            imageView.setImageBitmap(bitmap);

        // catch the try if any errors
        } catch (IOException e) {
            Log.e(TAG, "Error uploading photo");
            e.printStackTrace();
        }
        // otherwise log the photo details
        Log.e(TAG, "Picked photo: " + imgPath);
    }

    // getting the path from the URI
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}



