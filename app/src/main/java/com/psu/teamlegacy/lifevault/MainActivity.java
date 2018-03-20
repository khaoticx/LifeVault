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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
                try {
                    if (verifyPassword()){
                        gotoHomeActivity();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),
                                "Login failed", Toast.LENGTH_LONG).show();
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
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

        //Attach a listener to EmergencyRecovery Button
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

    //Positive click listener for recovery dialog fragment
    public void onPositiveClick() {
        //Temporary toast to test button.
        Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_LONG).show();
    }

    private boolean verifyPassword() throws NoSuchAlgorithmException {
        final String FILE_PATH = ((EditText) findViewById(R.id.loginField)).getText().toString()+ ".txt";
        File file = new File(getApplicationContext().getFilesDir(),FILE_PATH);
        if (file.exists()){
            byte[] oldHash = new byte[(int)file.length()];

            try {
                FileInputStream inputStream = openFileInput(FILE_PATH);
                inputStream.read(oldHash);
                inputStream.close();
            } catch (FileNotFoundException e) {
                Log.d("VERIFY_PASSWORD", "FILE NOT FOUND");
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] passwordByte = ((EditText) findViewById(R.id.passwordField)).getText().toString().getBytes();
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(passwordByte);
            byte[] newHash = md.digest();

             /*
            Log.d("OLD_HASH", Base64.encodeToString(oldHash, DEFAULT));
            Log.d("NEW_HASH", Base64.encodeToString(newHash, DEFAULT));
            */

            if (Base64.encodeToString(oldHash, Base64.DEFAULT).equals(Base64.encodeToString(newHash, Base64.DEFAULT))){
                return true;
            }

            return false;
        }
        else{
            Log.d("VERIFY PASSWORD", "FILE NOT EXIST");
            return false;
        }

    }

    //Opens HomeActivity
    private void gotoHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("LOGIN_ID", ((EditText) findViewById(R.id.loginField)).getText().toString());
        intent.putExtra("PASSWORD", ((EditText) findViewById(R.id.passwordField)).getText().toString());
        ((EditText) findViewById(R.id.passwordField)).setText("");
        startActivity(intent);
    }

    //Opens new NewAccountActivity
    private void goToNewAccountActivity(){
        Intent intent = new Intent(this, NewAccountActivity.class);
        startActivity(intent);
    }
}

