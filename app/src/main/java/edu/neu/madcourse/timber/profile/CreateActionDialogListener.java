package edu.neu.madcourse.timber.profile;


import androidx.fragment.app.DialogFragment;

public interface CreateActionDialogListener {
    void onDialogPositiveClick(DialogFragment projectDialog);
    void onDialogNegativeClick(DialogFragment projectDialog);
}