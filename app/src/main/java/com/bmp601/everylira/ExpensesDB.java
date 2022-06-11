package com.bmp601.everylira;

import android.database.sqlite.SQLiteDatabase;

public class ExpensesDB {

    public static final String USERS_TABLE = "Users";
    public static final String USERS_KEY_ID = "_id";
    public static final String USERS_KEY_USERNAME = "username";
    public static final String USERS_KEY_PASSWORD = "password";


    private static final String USERS_TABLE_CREATE =
            "CREATE TABLE if not exists " + USERS_TABLE + " (" +
                    USERS_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    USERS_KEY_USERNAME + " TEXT," +
                    USERS_KEY_PASSWORD + " TEXT," +
                    " UNIQUE (" + USERS_KEY_USERNAME +"));";

    public static final String ITEMS_TABLE = "Items";
    public static final String ITEMS_KEY_ID = "_id";
    public static final String ITEMS_KEY_NAME = "name";
    public static final String ITEMS_KEY_IS_SERVICE = "isService";
    public static final String ITEMS_KEY_DESCRIPTION = "description";

    private static final String ITEMS_TABLE_CREATE =
            "CREATE TABLE if not exists " + ITEMS_TABLE + " (" +
                    ITEMS_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ITEMS_KEY_NAME + " TEXT," +
                    ITEMS_KEY_IS_SERVICE + " INTEGER," +
                    ITEMS_KEY_DESCRIPTION + " TEXT" +
                    ");";

    public static final String CATEGORIES_TABLE = "Categories";
    public static final String CATEGORIES_KEY_ID = "_id";
    public static final String CATEGORIES_KEY_NAME = "categoryName";
    public static final String CATEGORIES_KEY_DESCRIPTION = "description";

    private static final String CATEGORIES_TABLE_CREATE =
            "CREATE TABLE if not exists " + CATEGORIES_TABLE + " (" +
                    CATEGORIES_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CATEGORIES_KEY_NAME + " TEXT," +
                    CATEGORIES_KEY_DESCRIPTION + " TEXT" +
                    ");";

    public static final String EXPENSES_TABLE = "Expenses";
    public static final String EXPENSES_KEY_ID = "_id";
    public static final String EXPENSES_KEY_ITEM_ID = "itemId";
    public static final String EXPENSES_KEY_CATEGORY_ID = "categoryId";
    public static final String EXPENSES_KEY_PRICE = "price";
    public static final String EXPENSES_KEY_DATE = "date";
    public static final String EXPENSES_KEY_DESCRIPTION = "description";

    private static final String EXPENSES_TABLE_CREATE =
            "CREATE TABLE if not exists " + EXPENSES_TABLE + " (" +
                    EXPENSES_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    EXPENSES_KEY_ITEM_ID + " INTEGER," +
                    EXPENSES_KEY_CATEGORY_ID + " INTEGER," +
                    EXPENSES_KEY_PRICE + " REAL," +
                    EXPENSES_KEY_DATE + " TEXT," +
                    EXPENSES_KEY_DESCRIPTION + " TEXT" +
                    ");";


    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(ITEMS_TABLE_CREATE);
        db.execSQL(CATEGORIES_TABLE_CREATE);
        db.execSQL(EXPENSES_TABLE_CREATE);
    }
}
