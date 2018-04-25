package com.psu.teamlegacy.lifevault;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AddActivity extends AppCompatActivity {
    private String loginID;
    private String password;
    SQLiteDatabase theDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
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

        Button addButton = findViewById(R.id.addBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (checkIfNotExists(loginID, ((EditText)findViewById(R.id.newEntryTitle)).getText().toString())){
                    addNoteIntoDatabase(loginID, password, ((EditText)findViewById(R.id.newEntryTitle)).getText().toString(),
                            ((EditText)findViewById(R.id.newEntryText)).getText().toString());
                }
                else{
                    Toast.makeText(getApplicationContext(), "Title not accepted", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button cancelButton = findViewById(R.id.cancelBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                gotoHomeActivity();
            }
        });


    }

    public void addNoteIntoDatabase(String loginID, String password, String title, String text){
        if (!text.equals("")){
            /*

            //ENCRYPT TEXT

            ContentValues values = new ContentValues();
            values.put("id", loginID);
            values.put("title", title);
            values.put("text", newHash);

            try {
                    db.insert("notes",null,values);

                } else {
                    String where = "_id = " + rowid;
                    db.update("jokes", values, where,null);
                    setResult(RESULT_DB_CHANGED);
                }

                finish(); // Quit activity
            } catch (SQLException e) {
                Toast.makeText(this,"Error updating database.",Toast.LENGTH_LONG).show();
            } */
        }
    }

    public boolean checkIfNotExists(String loginID, String newTitle){

        String where = "id = " + loginID + " AND title = " + newTitle;
        String[] projection = {"title"};
        Cursor cursor = theDB.query("notes", projection, where, null, null, null, null);
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

    //Opens HomeActivity
    private void gotoHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("LOGIN_ID", loginID);
        intent.putExtra("PASSWORD", password);
        ((EditText) findViewById(R.id.passwordField)).setText("");
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("loginID", loginID);
        savedInstanceState.putString("password", password);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        loginID = savedInstanceState.getString("loginID");
        password = savedInstanceState.getString("password");
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
