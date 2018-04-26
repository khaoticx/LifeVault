package com.psu.teamlegacy.lifevault;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private String loginID;
    private String password;
    private SQLiteDatabase theDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }
    public boolean checkIfNotExists(String loginID, String newTitle){
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Cursor cursor = theDB.rawQuery("SELECT title FROM notes WHERE id = ? AND title = ?", new String[]{loginID, newTitle});
            //Cursor cursor = theDB.rawQuery("SELECT title FROM notes WHERE id=\"" + loginID + "\" AND title=\"" + newTitle + "\";", null);

            if (newTitle.equals("")){
                cursor.close();
                return false;
            }

            if (cursor.moveToFirst()) {
                cursor.close();
                return false;
            }

            cursor.close();
            return true;
        }

    }
}
