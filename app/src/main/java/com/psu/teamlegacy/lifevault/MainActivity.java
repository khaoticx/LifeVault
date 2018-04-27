package com.psu.teamlegacy.lifevault;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;


//This class implements the RecoverDialog.DialogListener as a callback interface
public class MainActivity extends AppCompatActivity implements RecoveryDialog.DialogListener {
    SQLiteDatabase theDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //LifeVaultDB db = new LifeVaultDB(this);

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

    @Override
    protected void onResume(){
        super.onResume();
        LifeVaultDB.getInstance(this).getWritableDatabase(new LifeVaultDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                theDB = db;
            }
        });
    }

    //This method will cause the Recovery Dialog to display
    public void confirmRecovery() {
        RecoveryDialog dialog = new RecoveryDialog();
        dialog.show(getFragmentManager(), "RecoveryDialogFragment");
    }

    //Positive click listener for recovery dialog fragment
    public void onPositiveClick(){//String loginID, String newTitle){
        //Temporary toast to test button.
        Date currentTime = Calendar.getInstance().getTime();
        long offsetMult = 1000 * 60 * 60;
        //Toast.makeText(this, "Not Yet Implemented.", Toast.LENGTH_LONG).show();
        // /* // not ready yet
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Cursor cursor = theDB.rawQuery("SELECT * FROM user WHERE id = ?", new String[]{
                    ((TextView) findViewById(R.id.loginField)).getText().toString()
            });
            //Cursor cursor = theDB.rawQuery("SELECT title FROM notes WHERE id=\"" + loginID + "\" AND title=\"" + newTitle + "\";", null);

            if (((TextView) findViewById(R.id.loginField)).getText().toString().equals("")){
                cursor.close();
                return;
            }

            if (cursor.moveToFirst()) {
                int delay = cursor.getInt(cursor.getColumnIndexOrThrow("timeout"));
                long recoverTime = cursor.getLong(cursor.getColumnIndexOrThrow("recoverstarttime"));
                String emailAddress = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String recoverEmailAddress = cursor.getString(cursor.getColumnIndexOrThrow("remail"));
                Date dbRecoverTime = new Date(recoverTime + offsetMult * delay); // assumes milliseconds
                if (recoverTime < 0)
                    Toast.makeText(this, "Recovery email sent to " + emailAddress, Toast.LENGTH_LONG).show();
                else if (dbRecoverTime.compareTo(currentTime) > 0)
                    Toast.makeText(this, "Recovery time is not yet reached.", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, "Recovery email sent to " + recoverEmailAddress, Toast.LENGTH_LONG).show();
                cursor.close();
                return;
            }

            cursor.close();
            return;
        }// */
    }

    //Opens a database, gets hash and salt, then verify user input
    private boolean verifyPassword() throws NoSuchAlgorithmException {
        //Get a database query for the login
        String sqlStr = "SELECT id, salt, hash FROM user WHERE id = ?";
        Cursor c = theDB.rawQuery(sqlStr,
                new String[] {((TextView) findViewById(R.id.loginField)).getText().toString()});

        if (c.moveToFirst()) {
            int salt = c.getInt(c.getColumnIndexOrThrow("salt"));
            String oldHash = c.getString(c.getColumnIndexOrThrow("hash"));
            c.close();

            //Hash the user's input for password
            byte[] passwordByte = (salt + ((EditText) findViewById(R.id.loginField)).getText().toString()).getBytes();
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(passwordByte);
            byte[] newHash = md.digest();

            /* Debug message
            Log.d("OLD_HASH", oldHash);
            Log.d("NEW_HASH64", Base64.encodeToString(newHash, Base64.DEFAULT));
            */

            if (oldHash.equals(Base64.encodeToString(newHash, Base64.DEFAULT)))
                return true;
        } else {
            c.close();
        }
        return false;
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

