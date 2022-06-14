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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

// TODO: Create another listview for uncategorized expenses

// https://www.androiddesignpatterns.com/2012/07/understanding-loadermanager.html
// A LoaderManager is used in order to keep track of the expense listview
// After adding/deleting/editing an expense
public class HomeActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter dataAdapter;

    // Declaring variables
    boolean logOut = false;
    TextView noExpenses;
    ImageView noExpensesImage;
    ListView expensesList;
    FloatingActionButton addExpenseFAB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // Assigning variables
        noExpenses = findViewById(R.id.noExpenses);
        noExpensesImage = findViewById(R.id.noExpensesImage);
        expensesList = findViewById(R.id.expensesList);
        addExpenseFAB = findViewById(R.id.addExpenseFAB);

        // Call displayExpensesList() to generate and show expenses listview
        displayExpensesListView();

        // Handle clicks on the FAB by navigating to AddExpenseActivity
        addExpenseFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addExpense = new Intent(getBaseContext(), AddExpenseActivity.class);

                // When navigating to AddExpenseActivity, a bundle is needed with "mode" entry
                // in order to specify the kind of operation to be done (Add or Update)
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

    private void displayExpensesListView() {
        // Columns to display
        String[] from = new String[]{
                ExpensesDB.EXPENSES_KEY_PRICE,
                ExpensesDB.EXPENSES_KEY_DATE,
                ExpensesDB.ITEMS_KEY_NAME,
                ExpensesDB.CATEGORIES_KEY_NAME
        };

        // The XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.expensePrice,
                R.id.expenseDate,
                R.id.expenseItemName,
                R.id.expenseCategoryName,
        };

        // XML views to be replaced by columns values (expenses_info.xml works LIKE a fragment, but it is not a fragment)
        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.expense_info,
                null,
                from,
                to,
                0);

        // Assign adapter to ListView
        expensesList.setAdapter(dataAdapter);

        //Ensures a loader is initialized and active.
        getLoaderManager().initLoader(0, null, this);

        // Handling clicks on the listview items
        expensesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor indexed to the corresponding row
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the id of the selected listview item
                String rowId =
                        cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_ID));

                // Start a new intent to update or delete an expense
                // pass the rowId to create the Content URI for a single row
                Intent editExpense = new Intent(getBaseContext(), AddExpenseActivity.class);

                // Set mode to update
                Bundle bundle = new Bundle();
                bundle.putString("mode", "update");
                bundle.putString("rowId", rowId);
                editExpense.putExtras(bundle);

                startActivity(editExpense);
            }
        });
    }

    // This is called when a new Loader needs to be created (after any re-opening the activity)
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ExpensesDB.EXPENSES_KEY_ID,
                ExpensesDB.EXPENSES_KEY_PRICE,
                ExpensesDB.EXPENSES_KEY_DATE,
                ExpensesDB.ITEMS_KEY_NAME,
                ExpensesDB.CATEGORIES_KEY_NAME
        };

        // Sorted by date
        CursorLoader cursorLoader = new CursorLoader(this,
                ExpensesContentProvider.EXPENSES_ITEMS_URI, projection, null, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.
        // If the cursor has no records, show the noExpenses textView and imageView
        // Otherwise, hide them
        if (data.getCount() == 0) {
            noExpenses.setVisibility(View.VISIBLE);
            noExpensesImage.setVisibility(View.VISIBLE);
            noExpenses.setText(R.string.noExpensesAdded);
        } else {
            noExpenses.setVisibility(View.GONE);
            noExpensesImage.setVisibility(View.GONE);
        }
        dataAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished() above is about to be closed
        // We need to make sure we are no longer using it.
        dataAdapter.swapCursor(null);
    }

    // Setting the menu options
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

    // Handling clicks on each menu option
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Basically, each option will navigate to a new activity
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
                // Change the logOut value, in order to be saved using SharedPreferences
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

        // Create a SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("everyLiraSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Updating the value of keepSignedIn
        editor.putBoolean("keepSignedIn", !logOut);

        // Store/apply
        editor.apply();
    }
}
