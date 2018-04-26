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
import android.util.Base64;
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

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static android.util.Base64.DEFAULT;

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


        final ListView listView = findViewById(R.id.listView);


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
        byte[] decryptedText = null;

        String where = "_id = " + rowid;
        String[] projection = {"rowid _id", "id", "title", "data", "iv", "salt"};
        Cursor cursor = theDB.query("notes", projection, where,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            // Arguments are a way to set values for the dialog that will be passed
            // even if fragment gets destroyed and recreated

            byte[] encryptedText = Base64.decode(cursor.getString(cursor.getColumnIndexOrThrow("data")), Base64.DEFAULT);
            byte[] salt = Base64.decode(cursor.getString(cursor.getColumnIndexOrThrow("salt")), Base64.DEFAULT);
            byte[] iv = Base64.decode(cursor.getString(cursor.getColumnIndexOrThrow("iv")), Base64.DEFAULT);


            try {
                KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256); // AES-256
                SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                byte[] key = f.generateSecret(spec).getEncoded();

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
                decryptedText = cipher.doFinal(encryptedText);

                Bundle args = new Bundle();
                args.putLong("rowid", rowid);
                args.putString("title",cursor.getString(cursor.getColumnIndexOrThrow("title")));
                try {
                    args.putString("data", new String(decryptedText, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                args.putString("iv",cursor.getString(cursor.getColumnIndexOrThrow("iv")));
                args.putString("salt",cursor.getString(cursor.getColumnIndexOrThrow("salt")));

                DisplayNoteDialog dialog = new DisplayNoteDialog();
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "NoteDialog");

            } catch (NoSuchAlgorithmException e) {
                Log.e("NoSuchAlgorithm", e.toString());
            } catch (InvalidKeySpecException e) {
                Log.e("InvalidKeySpec", e.toString());
            } catch (NoSuchPaddingException e) {
                Log.e("(NoSuchPadding", e.toString());
            } catch (InvalidKeyException e) {
                Log.e("InvalidKey", e.toString());
            } catch (InvalidAlgorithmParameterException e) {
                Log.e("InvalidAlgorithm", e.toString());
            } catch (IllegalBlockSizeException e) {
                Log.e("IllegalBlockSize", e.toString());
            } catch (BadPaddingException e) {
                Log.e("BadPadding", e.toString());
            }
        }
        else {
            Toast.makeText(this, "Record could not be retrieved...", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    public Cursor getAllNotes(){
        if (theDB == null){
            Log.e("NULLLLLLLLL"," NULLLLLLLLLL");
        }

        return theDB.rawQuery("SELECT rowid _id,* FROM notes WHERE id = ? ORDER BY title", new String[] {loginID});

    }

    private void updateListView(){
        ListView listView = findViewById(R.id.listView);
        SimpleCursorAdapter listAdapter = (SimpleCursorAdapter) listView.getAdapter();
        Cursor newCursor = getAllNotes();
        listAdapter.changeCursor(newCursor);
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
                theDB = db;
                //updateListView();
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
                intent.putExtra("LOGIN_ID", loginID);
                intent.putExtra("PASSWORD", password);
                startActivity(intent);
                return true;
            default:
                //The user's action was not recognized. Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public static class DisplayNoteDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final long rowid = getArguments().getLong("rowid");
            final String title = getArguments().getString("title");
            final String data = getArguments().getString("data");
            final String iv = getArguments().getString("iv");
            final String salt = getArguments().getString("salt");



            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title)
                    .setMessage(data)
                    .setPositiveButton("Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                    .setNegativeButton("Delete",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });

            return builder.create();
        }
    }
}
