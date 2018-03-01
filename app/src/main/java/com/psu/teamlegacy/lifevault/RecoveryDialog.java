package com.psu.teamlegacy.lifevault;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RecoveryDialog extends DialogFragment {
    //Callback interface definition
    public interface DialogListener {
        void onPositiveClick();
    }

    DialogListener mListener;
    @Override

    //Defines callback code
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the AlertDialog class to build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm recovery")
                .setMessage("Begin recovery process?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onPositiveClick();
                    }
                })
                .setNegativeButton("Cancel", new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
        return builder.create();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DialogListener) activity;
        }
        catch(ClassCastException e) {
            throw e;
        }
    }
}