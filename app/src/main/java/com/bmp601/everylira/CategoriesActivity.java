package com.bmp601.everylira;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Objects;

// https://www.androiddesignpatterns.com/2012/07/understanding-loadermanager.html
// A LoaderManager is used in order to keep track of the categories listview
// After adding/deleting/editing a category
public class CategoriesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Declaring variables
    Button addCategory;
    ListView categoriesList;
    private SimpleCursorAdapter dataAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // To show the navigate up arrow in the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initializing variables
        addCategory = findViewById(R.id.addCategory);
        categoriesList = findViewById(R.id.categoriesList);

        displayCategoriesListView();

        // Handle click on the addCategory button
        addCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addCategory = new Intent(getBaseContext(), AddCategoryActivity.class);
                // Set mode to "add" when navigating to AddCategoryActivity
                Bundle bundle = new Bundle();
                bundle.putString("mode", "add");
                addCategory.putExtras(bundle);

                startActivity(addCategory);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Starts a new or restarts an existing Loader in this manager
        getLoaderManager().restartLoader(0, null, this);
    }

    private void displayCategoriesListView() {
        // Columns to display
        String[] from = new String[]{
                ExpensesDB.CATEGORIES_KEY_NAME,
                ExpensesDB.CATEGORIES_KEY_DESCRIPTION,
        };

        // The XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.categoryName,
                R.id.categoryDescription,
        };

        // create an adapter from the SimpleCursorAdapter
        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.category_info,
                null,
                from,
                to,
                0);

        // Assign adapter to ListView
        categoriesList.setAdapter(dataAdapter);
        // Ensures a loader is initialized and active.
        getLoaderManager().initLoader(0, null, this);

        // Handling clicks on the listview items
        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor indexed to the corresponding row
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the id of the selected listview item
                String rowId =
                        cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.CATEGORIES_KEY_ID));

                // Start a new intent to update or delete a category
                // pass the rowId to create the Content URI for a single row
                Intent editCategory = new Intent(getBaseContext(), AddCategoryActivity.class);

                // Set mode to update
                Bundle bundle = new Bundle();
                bundle.putString("mode", "update");
                bundle.putString("rowId", rowId);
                editCategory.putExtras(bundle);

                startActivity(editCategory);
            }
        });
    }

    // This is called when a new Loader needs to be created
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ExpensesDB.CATEGORIES_KEY_ID,
                ExpensesDB.CATEGORIES_KEY_NAME,
                ExpensesDB.CATEGORIES_KEY_DESCRIPTION,
        };
        CursorLoader cursorLoader = new CursorLoader(this,
                CategoriesContentProvider.CATEGORIES_URI, projection, null, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in
        dataAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished() above is about to be closed
        // We need to make sure we are no longer using it
        dataAdapter.swapCursor(null);
    }

    // To specify what the navigate up arrow in the action bar does
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}