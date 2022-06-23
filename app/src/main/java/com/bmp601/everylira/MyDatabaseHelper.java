package com.bmp601.everylira;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// The database helper
public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "ExpensesDB";
    private static final int DATABASE_VERSION = 1;

    MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ExpensesDB.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ExpensesDB.onUpgrade(db, oldVersion, newVersion);
    }
}