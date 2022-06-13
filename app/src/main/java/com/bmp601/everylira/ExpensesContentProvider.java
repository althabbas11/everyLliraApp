package com.bmp601.everylira;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ExpensesContentProvider extends ContentProvider {
    private MyDatabaseHelper dbHelper;

    private static final int ALL_EXPENSES = 1;
    private static final int SINGLE_EXPENSE = 2;
    private static final int ALL_EXPENSES_ITEMS = 3;
    private static final int ALL_EXPENSES_SERVICES_REPORT = 4;
    private static final int ALL_EXPENSES_SERVICES_COST = 5;
    private static final int ALL_EXPENSES_PAID_REPORT = 6;
    private static final int ALL_EXPENSES_PAID_COST = 7;
    private static final int ALL_EXPENSES_YEAR = 8;
    private static final int ALL_EXPENSES_YEAR_TOTAL = 9;
    private static final int ALL_EXPENSES_MONTH = 10;
    private static final int ALL_EXPENSES_MONTH_TOTAL = 11;
    private static final int ALL_EXPENSES_CATEGORY_REPORT = 12;
    private static final int ALL_EXPENSES_CATEGORY_TOTAL = 13;

    // authority is the symbolic name of your provider
    // To avoid conflicts with other providers, you should use
    // Internet domain ownership (in reverse) as the basis of your provider authority.
    private static final String AUTHORITY = "com.bmp601.everyLiraContentProviderExpenses";

    // create content URIs from the authority by appending path to database table
    public static final Uri EXPENSES_URI =
            Uri.parse("content://" + AUTHORITY + "/expenses");
    public static final Uri EXPENSES_ITEMS_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesItems");
    public static final Uri EXPENSES_SERVICES_REPORT_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesServicesReport");
    public static final Uri EXPENSES_SERVICES_COST_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesServicesCost");
    public static final Uri EXPENSES_PAID_REPORT_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesPaidReport");
    public static final Uri EXPENSES_PAID_COST_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesPaidCost");
    public static final Uri EXPENSES_YEAR_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesYear");
    public static final Uri EXPENSES_YEAR_TOTAL_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesYearTotal");
    public static final Uri EXPENSES_MONTH_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesMonth");
    public static final Uri EXPENSES_MONTH_TOTAL_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesMonthTotal");
    public static final Uri EXPENSES_CATEGORY_REPORT_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesCategoryReport");
    public static final Uri EXPENSES_CATEGORY_TOTAL =
            Uri.parse("content://" + AUTHORITY + "/expensesCategoryTotal");




    // a content URI pattern matches content URIs using wildcard characters:
    // *: Matches a string of any valid characters of any length.
    // #: Matches a string of numeric characters of any length.
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "expenses", ALL_EXPENSES);
        uriMatcher.addURI(AUTHORITY, "expenses/#", SINGLE_EXPENSE);
        uriMatcher.addURI(AUTHORITY, "expensesItems", ALL_EXPENSES_ITEMS);
        uriMatcher.addURI(AUTHORITY, "expensesServicesReport", ALL_EXPENSES_SERVICES_REPORT);
        uriMatcher.addURI(AUTHORITY, "expensesServicesCost", ALL_EXPENSES_SERVICES_COST);
        uriMatcher.addURI(AUTHORITY, "expensesPaidReport", ALL_EXPENSES_PAID_REPORT);
        uriMatcher.addURI(AUTHORITY, "expensesPaidCost", ALL_EXPENSES_PAID_COST);
        uriMatcher.addURI(AUTHORITY, "expensesYear", ALL_EXPENSES_YEAR);
        uriMatcher.addURI(AUTHORITY, "expensesYearTotal", ALL_EXPENSES_YEAR_TOTAL);
        uriMatcher.addURI(AUTHORITY, "expensesMonth", ALL_EXPENSES_MONTH);
        uriMatcher.addURI(AUTHORITY, "expensesMonthTotal", ALL_EXPENSES_MONTH_TOTAL);
        uriMatcher.addURI(AUTHORITY, "expensesCategoryReport", ALL_EXPENSES_CATEGORY_REPORT);
        uriMatcher.addURI(AUTHORITY, "expensesCategoryTotal", ALL_EXPENSES_CATEGORY_TOTAL);
    }

    // system calls onCreate() when it starts up the provider.
    @Override
    public boolean onCreate() {
        // get access to the database helper
        dbHelper = new MyDatabaseHelper(getContext());
        return false;
    }

    //Return the MIME type corresponding to a content URI
    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {
            case ALL_EXPENSES:
            case ALL_EXPENSES_ITEMS:
            case ALL_EXPENSES_SERVICES_REPORT:
            case ALL_EXPENSES_SERVICES_COST:
            case ALL_EXPENSES_PAID_REPORT:
            case ALL_EXPENSES_PAID_COST:
            case ALL_EXPENSES_YEAR:
            case ALL_EXPENSES_YEAR_TOTAL:
            case ALL_EXPENSES_MONTH:
            case ALL_EXPENSES_MONTH_TOTAL:
            case ALL_EXPENSES_CATEGORY_REPORT:
            case ALL_EXPENSES_CATEGORY_TOTAL:
                return "vnd.android.cursor.dir/vnd.com.bmp601.everyLiraContentProvider.expenses";
            case SINGLE_EXPENSE:
                return "vnd.android.cursor.item/vnd.com.bmp601.everyLiraContentProvider.expenses";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    // The insert() method adds a new row to the appropriate table, using the values
    // in the ContentValues argument. If a column name is not in the ContentValues argument,
    // you may want to provide a default value for it either in your provider code or in
    // your database schema.
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_EXPENSES:
                //do nothing
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        long id = db.insert(ExpensesDB.EXPENSES_TABLE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(EXPENSES_URI + "/" + id);
    }

    // The query() method must return a Cursor object, or if it fails,
    // throw an Exception. If you are using an SQLite database as your data storage,
    // you can simply return the Cursor returned by one of the query() methods of the
    // SQLiteDatabase class. If the query does not match any rows, you should return a
    // Cursor instance whose getCount() method returns 0. You should return null only
    // if an internal error occurred during the query process.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ExpensesDB.EXPENSES_TABLE);
        String query;
        Cursor c;
        switch (uriMatcher.match(uri)) {
            case ALL_EXPENSES:
                //do nothing
                break;
            case ALL_EXPENSES_ITEMS:
                query = "SELECT Expenses._id, Expenses.price, Expenses.date, Categories.categoryName, Items.name FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id INNER JOIN Categories ON Expenses.categoryId = Categories._id ORDER BY Expenses.date ASC";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;
            case ALL_EXPENSES_SERVICES_REPORT:
                query = "SELECT Expenses._id, Expenses.price, Expenses.date, Categories.categoryName, Items.name FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id INNER JOIN Categories ON Expenses.categoryId = Categories._id WHERE Items.isService = 1";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;
            case ALL_EXPENSES_SERVICES_COST:
                query = "SELECT Expenses._id, Expenses.price, Expenses.date, Categories.categoryName, Items.name, SUM(Expenses.price) as Total FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id INNER JOIN Categories ON Expenses.categoryId = Categories._id WHERE Items.isService = 1";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;
            case ALL_EXPENSES_PAID_REPORT:
                query = "SELECT Expenses._id, Expenses.price, Expenses.date, Categories.categoryName, Items.name FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id INNER JOIN Categories ON Expenses.categoryId = Categories._id WHERE Expenses.price != 0";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;
            case ALL_EXPENSES_PAID_COST:
                query = "SELECT Expenses._id, Expenses.price, Expenses.date, Categories.categoryName, Items.name, SUM(Expenses.price) as Total FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id INNER JOIN Categories ON Expenses.categoryId = Categories._id WHERE Expenses.price != 0";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;
            case ALL_EXPENSES_YEAR:
                query = "SELECT Expenses._id, Expenses.price, Expenses.date, Categories.categoryName, Items.name FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id INNER JOIN Categories ON Expenses.categoryId = Categories._id WHERE Expenses.date BETWEEN '" + selectionArgs[0] + "-01-01' AND '" + selectionArgs[0] + "-12-31'";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;
            case ALL_EXPENSES_YEAR_TOTAL:
                query = "SELECT Expenses._id, Expenses.price, Expenses.date, Categories.categoryName, Items.name, SUM(Expenses.price) as Total FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id INNER JOIN Categories ON Expenses.categoryId = Categories._id WHERE Expenses.date BETWEEN '" + selectionArgs[0] + "-01-01' AND '" + selectionArgs[0] + "-12-31'";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;
            case ALL_EXPENSES_MONTH:
                query = "SELECT Expenses._id, Expenses.price, Expenses.date, Categories.categoryName, Items.name FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id INNER JOIN Categories ON Expenses.categoryId = Categories._id WHERE Expenses.date BETWEEN '" + selectionArgs[0] + "-" + selectionArgs[1] + "-01' AND '" + selectionArgs[0] + "-" + selectionArgs[1] + "-31'";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;
            case ALL_EXPENSES_MONTH_TOTAL:
                query = "SELECT Expenses._id, Expenses.price, Expenses.date, Categories.categoryName, Items.name, SUM(Expenses.price) as Total FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id INNER JOIN Categories ON Expenses.categoryId = Categories._id WHERE Expenses.date BETWEEN '" + selectionArgs[0] + "-" + selectionArgs[1] + "-01' AND '" + selectionArgs[0] + "-" + selectionArgs[1] + "-31'";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;
            case ALL_EXPENSES_CATEGORY_REPORT:
                query = "SELECT Expenses._id, Expenses.price, Expenses.date, Categories.categoryName, Items.name FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id INNER JOIN Categories ON Expenses.categoryId = Categories._id WHERE Categories.categoryName = '" + selectionArgs[0]+ "'";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;
            case ALL_EXPENSES_CATEGORY_TOTAL:
                query = "SELECT Expenses._id, Expenses.price, Expenses.date, Categories.categoryName, Items.name, SUM(Expenses.price) as Total FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id INNER JOIN Categories ON Expenses.categoryId = Categories._id WHERE Categories.categoryName = '" + selectionArgs[0]+ "'";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;


            case SINGLE_EXPENSE:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(ExpensesDB.EXPENSES_KEY_ID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        return cursor;

    }

    // The delete() method deletes rows based on the seletion or if an id is
    // provided then it deleted a single row. The methods returns the numbers
    // of records delete from the database. If you choose not to delete the data
    // physically then just update a flag here.
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_EXPENSES:
                //do nothing
                break;
            case SINGLE_EXPENSE:
                String id = uri.getPathSegments().get(1);
                selection = ExpensesDB.EXPENSES_KEY_ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int deleteCount = db.delete(ExpensesDB.EXPENSES_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    // The update method() is same as delete() which updates multiple rows
    // based on the selection or a single row if the row id is provided. The
    // update method returns the number of updated rows.
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_EXPENSES:
                //do nothing
                break;
            case SINGLE_EXPENSE:
                String id = uri.getPathSegments().get(1);
                selection = ExpensesDB.EXPENSES_KEY_ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int updateCount = db.update(ExpensesDB.EXPENSES_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }
}
