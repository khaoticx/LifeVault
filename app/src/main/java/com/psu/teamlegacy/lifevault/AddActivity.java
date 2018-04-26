package com.psu.teamlegacy.lifevault;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
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
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static android.util.Base64.DEFAULT;

public class AddActivity extends AppCompatActivity {
    private String loginID;
    private String password;
    private SQLiteDatabase theDB;

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
            }
            else {
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

    public void addNoteIntoDatabase(String loginID, String password, String title, String data){
        if (!data.equals("")){
            try {
                byte[] unencryptedText = data.getBytes("UTF-8");

                SecureRandom random = new SecureRandom();
                byte[] salt = new byte[16];
                random.nextBytes(salt);

                KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256); // AES-256
                SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                byte[] key = f.generateSecret(spec).getEncoded();

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                SecretKeySpec specKey = new SecretKeySpec(key, "AES");
                cipher.init(Cipher.ENCRYPT_MODE, specKey);

                //Remove trace of message
                for (int i = 0; i < unencryptedText.length; i++) {
                    unencryptedText[i] = 0;
                }

                ContentValues values = new ContentValues();
                values.put("id", loginID);
                values.put("title", title);
                values.put("data", Base64.encodeToString(cipher.doFinal(unencryptedText), Base64.DEFAULT));
                values.put("iv", Base64.encodeToString(cipher.getIV(), Base64.DEFAULT));
                values.put("salt", Base64.encodeToString(salt, Base64.DEFAULT));

                try {
                    theDB.insert("notes",null,values);
                    gotoHomeActivity();
                } catch (SQLException ex) {
                    Log.e("SQLException", ex.toString());
                    Toast.makeText(this,"Error, new note not added.",Toast.LENGTH_LONG).show();
                }

            } catch (UnsupportedEncodingException ex){
                Log.e("UnsupportedEncoding", ex.toString());
            } catch (NoSuchPaddingException ex){
                Log.e("NoSuchPadding", ex.toString());
            } catch (NoSuchAlgorithmException ex){
                Log.e("NoSuchAlgorithm", ex.toString());
            } catch (InvalidKeyException ex){
                Log.e("InvalidKey", ex.toString());
            } catch (IllegalBlockSizeException ex) {
                    Log.e("IllegalBlockSize", ex.toString());
            } catch (BadPaddingException ex) {
                    Log.e("BadPadding", ex.toString());
            } catch (InvalidKeySpecException ex) {
                Log.e("InvalidKeySpec", ex.toString());
            }
        }
        else {
            Toast.makeText(this, "Note is empty..", Toast.LENGTH_LONG).show();
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



    //Opens HomeActivity
    private void gotoHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("LOGIN_ID", loginID);
        intent.putExtra("PASSWORD", password);
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
    protected void onResume(){
        super.onResume();
        LifeVaultDB.getInstance(this).getWritableDatabase(new LifeVaultDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                theDB = db;
                Log.e("RESUMMMMME", "RERWERRWERERERW");
            }
        });
    }
}
