package com.psu.teamlegacy.lifevault;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.app.PendingIntent.getActivity;

//This class implements the RecoverDialog.DialogListener as a callback interface
public class MainActivity extends AppCompatActivity implements RecoveryDialog.DialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Attach a listener to UI Button
        Button logInButton = findViewById(R.id.LogInBtn);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //This will display a toast if login is failed. Will be implemented correctly in the future
                if (!((EditText) findViewById(R.id.passwordField)).getText().toString().equals("password")) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter \"password\" for debug login", Toast.LENGTH_LONG).show();
                } else {
                    gotoHomeActivity();
                }
            }
        });

        //Attach a listener to the CreateNewAccount button
        Button newAccountButton = findViewById(R.id.newAccountBtn);
        newAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                goToNewAccountActivity();
            }
        });

        //Attach another listener to UI Button
        Button emergencyRecoveryButton = findViewById(R.id.emergencyRecoveryBtn);
        emergencyRecoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //This code will invoke the Recovery Dialog callback
                confirmRecovery();
            }
        });
    }

    //This method will cause the Reecovery Dialog to display
    public void confirmRecovery() {
        RecoveryDialog dialog = new RecoveryDialog();
        dialog.show(getFragmentManager(), "RecoveryDialogFragment");
    }

    public void onPositiveClick() {
        //Temporary toast to test button.
        Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_LONG).show();
    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("LOGIN_ID", ((EditText) findViewById(R.id.loginField)).getText().toString());
        intent.putExtra("PASSWORD", ((EditText) findViewById(R.id.passwordField)).getText().toString());
        ((EditText) findViewById(R.id.passwordField)).setText("");
        startActivity(intent);
    }

    private void goToNewAccountActivity(){
        Intent intent = new Intent(this, NewAccountActivity.class);
        startActivity(intent);
    }
}

