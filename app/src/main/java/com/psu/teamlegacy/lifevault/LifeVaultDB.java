package com.psu.teamlegacy.lifevault;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by jeh5723 on 3/22/18.
 */

public class LifeVaultDB extends SQLiteOpenHelper {
    interface OnDBReadyListener {
        void onDBReady(SQLiteDatabase db);
    }

    public static final int DB_VERSION = 2;
    public static final String DB_NAME = "lifeVault.db";

    private static LifeVaultDB lvDB;

    private Context appContext;

    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE user (id TEXT PRIMARY KEY, " +
                    "email TEXT, " +
                    "salt INTEGER, " +
                    "hash TEXT, " +
                    "timeout INTEGER, " +
                    "remail TEXT, " +
                    "recoverstarttime INTEGER)";

    private static final String SQL_CREATE_NOTES_TABLE =
            "CREATE TABLE notes (id TEXT, title TEXT, data TEXT, iv TEXT, salt TEXT, PRIMARY KEY (id, title))";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS user";

    private static final String SQL_SET_PW_BEGIN =
            "UPDATE user SET hash = ";
    private static final String SQL_SET_PW_MIDDLE =
            " WHERE id = ";

    public LifeVaultDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        appContext = context.getApplicationContext();
    }

    public void updatePW(SQLiteDatabase db, String id, String value){
        String query = SQL_SET_PW_BEGIN + id +
                SQL_SET_PW_MIDDLE + value + ";";
        db.execSQL(query);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("DB onCreate()", "running");
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_NOTES_TABLE);
        Log.e("DB onCreate()", "done");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static synchronized LifeVaultDB getInstance(Context context) {
        if (lvDB == null)
            lvDB = new LifeVaultDB(context);
        return lvDB;
    }

    public void getWritableDatabase(OnDBReadyListener listener) {
        new OpenDbAsyncTask().execute(listener);
    }

    public void asyncWritableDatabase(OnDBReadyListener listener) {
        new OpenDbAsyncTask().execute(listener);
    }

    private static class OpenDbAsyncTask extends AsyncTask<OnDBReadyListener,Void,SQLiteDatabase> {
        OnDBReadyListener listener;

        @Override
        protected SQLiteDatabase doInBackground(OnDBReadyListener... params){
            listener = params[0];
            return LifeVaultDB.lvDB.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase db) {
            //Make that callback
            listener.onDBReady(db);
        }
    }
}
