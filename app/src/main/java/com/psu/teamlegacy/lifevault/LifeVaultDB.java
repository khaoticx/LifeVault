package com.psu.teamlegacy.lifevault;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

/**
 * Created by jeh5723 on 3/22/18.
 */

public class LifeVaultDB extends SQLiteOpenHelper{
    interface OnDBReadyListener {
        void onDBReady(SQLiteDatabase db);
    }
    public static final int DB_VERSION = 2;
    public static final String DB_NAME = "lifeVault.db";

    private static LifeVaultDB lvDB;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE user (id TEXT PRIMARY KEY, " +
            "email TEXT, " +
            "salt INTEGER, " +
            "hash TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS user";

    public LifeVaultDB(Context context){
       super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }



    public static synchronized LifeVaultDB getInstance(Context context){
        if (lvDB == null)
            lvDB = new LifeVaultDB(context);
        return lvDB;
    }

    public void getWritableDatabase(OnDBReadyListener listener){
        new OpenDbAsyncTask().execute(listener);
    }

    private static class OpenDbAsyncTask extends AsyncTask<OnDBReadyListener, Void, SQLiteDatabase> {
        OnDBReadyListener listener;

        @Override
        protected SQLiteDatabase doInBackground(OnDBReadyListener... params){
            listener = params[0];
            return LifeVaultDB.lvDB.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase db){
            listener.onDBReady(db);
        }
    }
}
