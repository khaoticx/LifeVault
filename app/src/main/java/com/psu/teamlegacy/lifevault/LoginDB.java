package com.psu.teamlegacy.lifevault;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jeh5723 on 3/22/18.
 */

public class LoginDB  extends SQLiteOpenHelper{
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "login.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE user (id TEXT PRIMARY KEY, " +
            "email TEXT, " +
            "salt INTEGER, " +
            "hash TEXT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS user";
    public LoginDB(Context context){
       super(context, DB_NAME, null, DB_VERSION);
    }
}
