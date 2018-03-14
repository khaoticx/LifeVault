package com.psu.teamlegacy.lifevault;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                if (validateLogin()){
                    if(validatePassword()){
                        if(((EditText) findViewById(R.id.passwordField)).getText().toString().equals(
                                ((EditText) findViewById(R.id.confirmPasswordField)).getText().toString())){
                            generateNewAccount();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),
                                    "Passwords do not match", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Log.d("PASSWORD_INVALID", "PASSWORD NOT VALID: " +
                                ((EditText) findViewById(R.id.passwordField)).getText().toString());
                        Toast.makeText(getApplicationContext(),
                                "Please enter a valid password", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Log.d("ID_NOT_VALID", "ID NOT VALID:" +
                            ((EditText) findViewById(R.id.nameTextField)).getText().toString());
                    Toast.makeText(getApplicationContext(),
                            "Please enter a valid login ID", Toast.LENGTH_LONG).show();
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

    private boolean validateLogin(){
        String loginID = ((EditText) findViewById(R.id.nameTextField)).getText().toString();
        Pattern pattern;
        Matcher matcher;
        final String LOGIN_PATTERN = "^[A-z]([A-z0-9_]){1,20}$";

        pattern = Pattern.compile(LOGIN_PATTERN);
        matcher = pattern.matcher(loginID);

        return matcher.matches();
    }

    private boolean validatePassword(){
        String password = ((EditText) findViewById(R.id.passwordField)).getText().toString();
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,20}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private void generateNewAccount(){

    }

    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

