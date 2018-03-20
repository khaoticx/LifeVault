package com.psu.teamlegacy.lifevault;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        //Attach a listener to the CreateNewAccount button
        Button newAccountButton = findViewById(R.id.createAccountBtn);
        newAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (validateLogin()) {
                    if (loginDoesNotExists()) {
                        if (validatePassword()) {
                            if (((EditText) findViewById(R.id.passwordField)).getText().toString().equals(
                                    ((EditText) findViewById(R.id.confirmPasswordField)).getText().toString())) {
                                try {
                                    //Generate new account if user given password is valid and accepted
                                    generateNewAccount();

                                    //Notify user and bring user back to MainActivity
                                    AlertDialog alertDialog = new AlertDialog.Builder(NewAccountActivity.this).create();
                                    alertDialog.setTitle("");
                                    alertDialog.setMessage("\nYour new account was successfully created.");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();

                                                    //Go back to Main Activity after successful creation of new account.
                                                    goToMainActivity();
                                                }
                                            });
                                    alertDialog.show();



                                } catch (NoSuchAlgorithmException e) {
                                    //e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Passwords do not match", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(NewAccountActivity.this).create();
                            alertDialog.setTitle("Invalid Password");
                            alertDialog.setMessage("\n1) Password must be 8 - 20 characters long" +
                                    "\n2) Must have at least one capital and lower case letter" +
                                    "\n3) Must have at least one numeral digit" +
                                    "\n4) Must have at least one special character");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Login ID not available", Toast.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(NewAccountActivity.this).create();
                    alertDialog.setTitle("Invalid ID");
                    alertDialog.setMessage("\nLogin IDs must start with a letter and consist only of letters, numbers, and underscores");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });

        //Attach a listener to the CreateNewAccount button
        Button cancelButton = findViewById(R.id.cancelBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Cancel and go back to Main activity
                goToMainActivity();
            }
        });
    }

    private boolean validateLogin() {
        String loginID = ((EditText) findViewById(R.id.loginIdField)).getText().toString();
        Pattern pattern;
        Matcher matcher;
        final String LOGIN_PATTERN = "^[A-z]([A-z0-9_]){1,20}$";

        pattern = Pattern.compile(LOGIN_PATTERN);
        matcher = pattern.matcher(loginID);

        return matcher.matches();
    }

    private boolean loginDoesNotExists() {
        File file = new File(getApplicationContext().getFilesDir(), ((EditText) findViewById(R.id.loginIdField)).getText().toString() + ".txt");

        if (file.exists()) {
            Log.d("FILE_EXISTS", "login exists");
            return false;
        } else {
            Log.d("FILE_EXISTS", "login does not exists");
            return true;
        }
    }

    private boolean validatePassword() {
        String password = ((EditText) findViewById(R.id.passwordField)).getText().toString();
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,20}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private void generateNewAccount() throws NoSuchAlgorithmException {
        byte[] passwordByte = ((EditText) findViewById(R.id.passwordField)).getText().toString().getBytes();
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(passwordByte);
        byte[] newHash = md.digest();

        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(((EditText) findViewById(R.id.loginIdField)).getText().toString() + ".txt", Context.MODE_PRIVATE);
            outputStream.write(newHash);
            outputStream.close();
        } catch (Exception e) {
            Log.e("FILE ERROR", "Error during file processing.");
        }

        /* //DEBUG code to see all files created for this app
        Toast.makeText(getApplicationContext(),
                "New account created", Toast.LENGTH_LONG).show();
        String[] str = this.fileList();
        for (String s: str) {
            Log.wtf("ERRORRRRRR", s);
        } */
    }


    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

