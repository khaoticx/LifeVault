package com.psu.teamlegacy.lifevault;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class displayAddDialog extends DialogFragment {

    /*
    SQLiteDatabase theDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LifeVaultDB.getInstance(this).getWritableDatabase(new LifeVaultDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                theDB = db;
            }
        });
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_notes, null);

        final EditText newTitle = view.findViewById(R.id.newEntryTitle);
        final EditText newText = view.findViewById(R.id.newEntryText);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setTitle(R.string.newNoteTitle)
                .setNegativeButton(R.string.cancelBtn,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(R.string.addBtn,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                theDB.checkTitle(HomeActivity.loginID)


                                //Toast.makeText(getActivity().getApplicationContext(),
                                //        "test: "+ newTitle.getText().toString(), Toast.LENGTH_LONG).show();
                            }
                        });

        return builder.create();
    }*/
}
