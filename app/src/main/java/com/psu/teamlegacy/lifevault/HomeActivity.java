package com.psu.teamlegacy.lifevault;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    private String loginID;
    private String password;
    private SimpleCursorAdapter adapter;
    private SQLiteDatabase theDB;


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


        LifeVaultDB.getInstance(this).getWritableDatabase(new LifeVaultDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                theDB = db;
                onCreateSetupListView();
            }
        });
    }

    public void onCreateSetupListView(){


        ListView listView = findViewById(R.id.listView);


        Cursor cursor = getAllNotes();
        adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, new String[]{"title"}, new int[]{R.id.listViewEntry}, 0);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                displayNote(id);
            }
        });
        
       /*
       adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 1) {
                    final long rowid = cursor.getLong(cursor.getColumnIndex("title"));
                    view.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            //opens dialog
                        }
                    });
                    return true;
                }
                return false;
            }
        });



        */
    }

    public void displayNote(long rowid){

    }

    public Cursor getAllNotes(){
        if (theDB == null){
            Log.e("NULLLLLLLLL"," NULLLLLLLLLL");
        }

        return theDB.rawQuery("SELECT rowid _id,* FROM notes WHERE id = ? ORDER BY title", new String[] {loginID});

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

    //Inflates actionbar's menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        LifeVaultDB.getInstance(this).getWritableDatabase(new LifeVaultDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                Log.e("RESUMMMMING", "AAAAAAAAA");
                theDB = db;
                if (theDB == null){
                    Log.e("NULLLLLLL YO", "AAAAAAAAAAA");
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.home_add:
                intent = new Intent(this, AddActivity.class);
                intent.putExtra("LOGIN_ID", loginID);
                intent.putExtra("PASSWORD", password);
                startActivity(intent);
                return true;

            case R.id.home_logout:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.home_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                //The user's action was not recognized. Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
