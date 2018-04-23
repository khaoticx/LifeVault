package com.psu.teamlegacy.lifevault;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private String loginID;
    private String password;
    SQLiteDatabase theDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                Toast.makeText(getApplicationContext(), "error, login and password not correct", Toast.LENGTH_LONG).show();
            } else {
                loginID = extras.getString("LOGIN_ID");
                password = extras.getString("PASSWORD");
            }
        } else {
            loginID = (String) savedInstanceState.getSerializable("LOGIN_ID");
            password = (String) savedInstanceState.getSerializable("PASSWORD");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("loginID", loginID);
        savedInstanceState.putString("password", password);
        super.onSaveInstanceState(savedInstanceState);
    }

    //Inflates actionbar's menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
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


}
