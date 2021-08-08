package edu.neu.madcourse.timber.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.fragment.app.DialogFragment;

import edu.neu.madcourse.timber.R;

public class UpdateProfileDialogFragment extends  DialogFragment{

    public interface UpdateProfileDialogListener {
        void onDialogPositiveClick(DialogFragment projectDialog);
        void onDialogNegativeClick(DialogFragment projectDialog);
    }
    UpdateProfileDialogFragment.UpdateProfileDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.create_account, null))
                // Add action buttons
                .setPositiveButton(R.string.update_account, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(UpdateProfileDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(UpdateProfileDialogFragment.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (UpdateProfileDialogFragment.UpdateProfileDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Activity must implement UpdateProfileDialogListener");
        }
    }
}

