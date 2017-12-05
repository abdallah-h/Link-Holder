package com.example.abdallah.linksholder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abdallah on 2017-12-02.
 */

public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "shelter.db";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + Contract.DataEntry.TABLE_NAME + "(" +
                    Contract.DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Contract.DataEntry.COLUMN_NAME + " TEXT, " +
                    Contract.DataEntry.COLUMN_URL + " TEXT);";



    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Contract.DataEntry.TABLE_NAME;

    public DbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
