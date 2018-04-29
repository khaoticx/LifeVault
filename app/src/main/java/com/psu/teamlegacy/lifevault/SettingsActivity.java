package com.psu.teamlegacy.lifevault;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.app.PendingIntent.getActivity;

public class SettingsActivity extends AppCompatActivity {
    private String loginID;
    private String password;
    private SimpleCursorAdapter adapter;
    private SQLiteDatabase theDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme", "");
        if (theme.equals("light")) {
            this.setTheme(R.style.light);
        } else {
            this.setTheme(R.style.dark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (theme.equals("light")) {
            ((RadioButton)findViewById(R.id.lightRadioBtn)).setChecked(true);
        } else if (theme.equals("dark")) {
            ((RadioButton)findViewById(R.id.darkRadioBtn)).setChecked(true);
        } else {
            Log.e("ERRRRRROOOOOR", "THEME NOT CORRECT!");
        }

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

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                Toast.makeText(getApplicationContext(), "error, login and password not correct", Toast.LENGTH_LONG).show();
            } else {
                loginID = extras.getString("LOGIN_ID");

            }
        } else {
            loginID = (String) savedInstanceState.getSerializable("LOGIN_ID");

        }

        RadioGroup radioGroup = findViewById(R.id.themeRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.lightRadioBtn){
                    Log.wtf(" CLICKED ON:", "LIGHT");
                    setATheme("light");

                }

                if (checkedId == R.id.darkRadioBtn ){
                    Log.wtf(" CLICKED ON:", "DARK");
                    setATheme("dark");
                }
            }
        });

        Button okBtn = findViewById(R.id.OKbutton);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                intent.putExtra("LOGIN_ID", loginID);
                intent.putExtra("PASSWORD", password);
                startActivity(intent);
                finish();
            }
        });
    }

    public void setATheme(String selectedTheme) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", selectedTheme);
        editor.apply();

        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("LOGIN_ID", loginID);
        intent.putExtra("PASSWORD", password);
        startActivity(intent);
        finish();
    }
}
