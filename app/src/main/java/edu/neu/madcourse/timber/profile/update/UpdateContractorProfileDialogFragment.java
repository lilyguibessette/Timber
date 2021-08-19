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
    public String my_username, my_param1, my_param2, my_email, my_zip, my_phone;

    private ImageView imageView;
    private Bitmap bitmap;
    private InputStream inputStreamImg;
    private File destination = null;
    private String imgPath = null;
    private Uri imageUri;

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
        View view = inflater.inflate(R.layout.update_account_contractor, container, false);

        updateImageButton = view.findViewById(R.id.update_image);
        imageView = view.findViewById(R.id.image);
        updateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        updateButton = view.findViewById(R.id.update_account);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("UpdateProfileDialogFragment", "UpdateProfileDialogFragment create click");
                // use dialog for add link
                my_username = ((EditText) view.findViewById(R.id.update_username)).getText().toString();
                my_param1 = ((EditText) view.findViewById(R.id.update_param1)).getText().toString();
                my_param2 = ((EditText) view.findViewById(R.id.update_param2)).getText().toString();
                my_email = ((EditText) view.findViewById(R.id.update_email)).getText().toString();
                my_zip = ((EditText) view.findViewById(R.id.update_zip)).getText().toString();
                my_phone = ((EditText) view.findViewById(R.id.update_phone)).getText().toString();
                update_profile();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new ProfileFragment());
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(), "Update complete", Toast.LENGTH_SHORT).show();
                fragmentTransaction.commit();

            }
        });

        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("UpdateProfileDialogFragment", "UpdateProfileDialogFragment cancel click");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new ProfileFragment());
                fragmentTransaction.addToBackStack(null);
                Toast.makeText(getActivity(), "going to cancel", Toast.LENGTH_SHORT).show();
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

            myUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                public User my_user;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // if the user exists, get their data
                    if (dataSnapshot.exists()) {
                        Contractor my_user = dataSnapshot.getValue(Contractor.class);

                        if (!my_username.equals("") & !my_user.getUsername().equals(my_username)) {
                            my_user.setUsername(my_username);
                        }
                        if (!my_param1.equals("") & !my_user.getBusinessName().equals(my_param1)) {
                            my_user.setBusinessName(my_param1);
                        }
                        if (!my_param2.equals("") & !my_user.getTaxID().equals(my_param2)) {
                            my_user.setTaxID(my_param2);
                        }
                        if (!my_email.equals("") & !my_user.getEmail().equals(my_email)) {
                            my_user.setEmail(my_email);
                        }
                        if (!my_zip.equals("") & !my_user.getZipcode().equals(my_zip)) {
                            my_user.setZipcode(my_zip);
                        }
                        if (!my_phone.equals("") & !my_user.getPhoneNumber().equals(my_phone)) {
                            my_user.setPhoneNumber(my_phone);
                        }
                        if (!imgPath.equals("") & !my_user.getImage().equals(imgPath)) {
                            my_user.setImage(imgPath);
                        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent gallery) {
        super.onActivityResult(requestCode, resultCode, gallery);
        if(resultCode == 0){
            return;
        }
        inputStreamImg = null;
        Uri selectedImage = gallery.getData();

        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().
                    getContentResolver(), selectedImage);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            destination = new File(getRealPathFromURI(selectedImage));
            imgPath = destination.getName();

            if (imgPath != null) {

                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();
                // Defining the child of storageReference
                StorageReference ref = storageReference.child(imgPath);
                ref.putFile(selectedImage);
            }

            imageView.setImageBitmap(bitmap);

        } catch (IOException e) {
            Toast.makeText(getActivity(), "Photo error",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), "Picked photo:" +
                gallery.getData().toString(), Toast.LENGTH_SHORT).show();
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}



