package com.psu.teamlegacy.lifevault;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    public class displayAddDialog extends DialogFragment implements View.OnClickListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_addBtn:
                    Toast.makeText(getApplicationContext(),
                            "YAYYYYYY", Toast.LENGTH_LONG).show();
                    break;
                case R.id.dialog_cancelBtn:
                    Toast.makeText(getApplicationContext(),
                            "YOOOOOOOOOOOOOO", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.dialog_add_notes, container, false);
            Button addBtn = findViewById(R.id.dialog_addBtn);
            Button newAccountButton = findViewById(R.id.dialog_cancelBtn);
            addBtn.setOnClickListener(this);
            newAccountButton.setOnClickListener(this);
            /*
            Button addBtn = findViewById(R.id.dialog_addBtn);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                }

            Button newAccountButton = findViewById(R.id.dialog_cancelBtn);
            newAccountButton.setOnClickListener(new View.OnClickListener() {

                }*/
            return v;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_add:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                DialogFragment dialogFragment = new displayAddDialog();
                dialogFragment.show(ft, "dialog");

                return true;

            case R.id.home_logout:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;
            case R.id.home_settings:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;
            default:
                //The user's action was not recognized. Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
