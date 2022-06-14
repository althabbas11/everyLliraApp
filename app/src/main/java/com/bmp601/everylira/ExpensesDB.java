package com.bmp601.everylira;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

// The SQLite database Object of the project
public class ExpensesDB {

    // 1 - Users table:
    public static final String USERS_TABLE = "Users";
    // Columns
    public static final String USERS_KEY_ID = "_id";
    public static final String USERS_KEY_USERNAME = "username";
    public static final String USERS_KEY_PASSWORD = "password";
    // Create query
    private static final String USERS_TABLE_CREATE =
            "CREATE TABLE if not exists " + USERS_TABLE + " (" +
                    USERS_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    USERS_KEY_USERNAME + " TEXT," +
                    USERS_KEY_PASSWORD + " TEXT," +
                    " UNIQUE (" + USERS_KEY_USERNAME + "));";


    // 2 - Items table:
    public static final String ITEMS_TABLE = "Items";
    // Columns:
    public static final String ITEMS_KEY_ID = "_id";
    public static final String ITEMS_KEY_NAME = "name";
    public static final String ITEMS_KEY_IS_SERVICE = "isService";
    public static final String ITEMS_KEY_DESCRIPTION = "description";
    // Create query:
    private static final String ITEMS_TABLE_CREATE =
            "CREATE TABLE if not exists " + ITEMS_TABLE + " (" +
                    ITEMS_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ITEMS_KEY_NAME + " TEXT," +
                    ITEMS_KEY_IS_SERVICE + " INTEGER," +
                    ITEMS_KEY_DESCRIPTION + " TEXT" +
                    ");";


    // 3 - Categories table:
    public static final String CATEGORIES_TABLE = "Categories";
    // Columns:
    public static final String CATEGORIES_KEY_ID = "_id";
    public static final String CATEGORIES_KEY_NAME = "categoryName";
    public static final String CATEGORIES_KEY_DESCRIPTION = "description";
    // Create query:
    private static final String CATEGORIES_TABLE_CREATE =
            "CREATE TABLE if not exists " + CATEGORIES_TABLE + " (" +
                    CATEGORIES_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CATEGORIES_KEY_NAME + " TEXT," +
                    CATEGORIES_KEY_DESCRIPTION + " TEXT" +
                    ");";


    // 4 - Expenses table:
    public static final String EXPENSES_TABLE = "Expenses";
    // Columns:
    public static final String EXPENSES_KEY_ID = "_id";
    public static final String EXPENSES_KEY_ITEM_ID = "itemId";
    public static final String EXPENSES_KEY_CATEGORY_ID = "categoryId";
    public static final String EXPENSES_KEY_PRICE = "price";
    public static final String EXPENSES_KEY_DATE = "date";
    public static final String EXPENSES_KEY_DESCRIPTION = "description";
    // Create query:
    private static final String EXPENSES_TABLE_CREATE =
            "CREATE TABLE if not exists " + EXPENSES_TABLE + " (" +
                    EXPENSES_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    EXPENSES_KEY_ITEM_ID + " INTEGER," +
                    EXPENSES_KEY_CATEGORY_ID + " INTEGER," +
                    EXPENSES_KEY_PRICE + " REAL," +
                    EXPENSES_KEY_DATE + " TEXT," +
                    EXPENSES_KEY_DESCRIPTION + " TEXT" +
                    ");";


    // Tables to create after the Expense database gets created
    public static void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(ITEMS_TABLE_CREATE);
        db.execSQL(CATEGORIES_TABLE_CREATE);
        db.execSQL(EXPENSES_TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Expenses DB", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EXPENSES_TABLE);
        onCreate(db);
    }
}
