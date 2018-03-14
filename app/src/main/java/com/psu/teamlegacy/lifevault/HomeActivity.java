package com.psu.teamlegacy.lifevault;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private String loginID;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                Toast.makeText(getApplicationContext(), "error, login and password not correct",Toast.LENGTH_LONG).show();
            } else {
               loginID = extras.getString("LOGIN_ID");
               password = extras.getString("PASSWORD");
            }
        } else {
            loginID = (String) savedInstanceState.getSerializable("LOGIN_ID");
            password = (String) savedInstanceState.getSerializable("PASSWORD");
        }

    }
}
