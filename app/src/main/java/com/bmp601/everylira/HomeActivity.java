package com.bmp601.everylira;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private MyDatabaseHelper dbHelper;
    private SimpleCursorAdapter dataAdapter;

    boolean logOut = false;
    TextView noExpenses;
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new MyDatabaseHelper(getBaseContext());

        noExpenses = findViewById(R.id.noExpenses);
        imageView = findViewById(R.id.imageView);

        displayListView();

        FloatingActionButton addExpense = findViewById(R.id.floatingActionButton);
        addExpense.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addExpense = new Intent(getBaseContext(), AddExpenseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "add");
                addExpense.putExtras(bundle);
                startActivity(addExpense);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Starts a new or restarts an existing Loader in this manager
        getLoaderManager().restartLoader(0, null, this);
    }

    private void displayListView() {
        // The desired columns to be bound
        String[] columns = new String[]{
                ExpensesDB.EXPENSES_KEY_PRICE,
                ExpensesDB.EXPENSES_KEY_DATE,
                ExpensesDB.ITEMS_KEY_NAME,
                ExpensesDB.CATEGORIES_KEY_NAME
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.expensePrice,
                R.id.expenseDate,
                R.id.expenseItemName,
                R.id.expenseCategoryName,
        };

        // create an adapter from the SimpleCursorAdapter
        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.expense_info,
                null,
                columns,
                to,
                0);

        // get reference to the ListView
        ListView listView = (ListView) findViewById(R.id.expensesList);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        //Ensures a loader is initialized and active.
        getLoaderManager().initLoader(0, null, this);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                String rowId =
                        cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_ID));

                // starts a new Intent to update/delete a Country
                // pass in row Id to create the Content URI for a single row
                Intent editExpense = new Intent(getBaseContext(), AddExpenseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "update");
                bundle.putString("rowId", rowId);
                editExpense.putExtras(bundle);
                startActivity(editExpense);
            }
        });
    }

    // This is called when a new Loader needs to be created.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ExpensesDB.EXPENSES_KEY_ID,
                ExpensesDB.EXPENSES_KEY_PRICE,
                ExpensesDB.EXPENSES_KEY_DATE,
                ExpensesDB.ITEMS_KEY_NAME,
                ExpensesDB.CATEGORIES_KEY_NAME
        };
        CursorLoader cursorLoader = new CursorLoader(this,
                ExpensesContentProvider.EXPENSES_ITEMS_URI, projection, null, null, null);


        Log.w("worked", "YES");

//        String query = "SELECT Expenses.price, Items.name FROM Expenses INNER JOIN Items ON Expenses.itemId = Items._id";
//        Cursor c = dbHelper.getWritableDatabase().rawQuery(query, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)

        if (data.getCount() == 0){
            noExpenses.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            noExpenses.setText(R.string.noExpensesAdded);
        } else {
            noExpenses.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        }
        dataAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        dataAdapter.swapCursor(null);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.categories:
                intent = new Intent(getApplicationContext(), CategoriesActivity.class);
                startActivity(intent);
                return true;
            case R.id.userInfo:
                intent = new Intent(getApplicationContext(), UserInfoActivity.class);
                startActivity(intent);
                return true;
            case R.id.reports:
                intent = new Intent(getApplicationContext(), ReportsActivity.class);
                startActivity(intent);
                return true;
            case R.id.addExpense:
                intent = new Intent(getApplicationContext(), AddExpenseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "add");
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.signOut:
                logOut = true;
                intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.about:
                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;


            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("everyLiraSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (logOut)
            editor.putBoolean("keepSignedIn", false);


        editor.apply();
    }

}