package com.bmp601.everylira;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class AddCategoryActivity extends AppCompatActivity {

    // Declaring variables
    EditText categoryName, categoryDescription;
    Button addCategory, deleteCategoryBtn, cancelButton;
    private String mode, categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // To show the navigate up arrow in the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initializing variables
        categoryName = findViewById(R.id.categoryName);
        categoryDescription = findViewById(R.id.categoryDescription);
        addCategory = findViewById(R.id.addCategory);
        deleteCategoryBtn = findViewById(R.id.deleteCategoryBtn);
        cancelButton = findViewById(R.id.cancel);

        // Getting the value of mode from the bundle
        if (this.getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras();
            mode = bundle.getString("mode");
        }

        // If mode is set to "add", hide the delete button
        if (mode.trim().equalsIgnoreCase("add")) {
            deleteCategoryBtn.setVisibility(View.GONE);
        }
        // Otherwise, change the text of addCategory button to Save
        // And load the category info to the EditText views
        else {
            addCategory.setText(R.string.save);
            Bundle bundle = this.getIntent().getExtras();
            categoryId = bundle.getString("rowId");
            loadCategoryInfo();
        }

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredCategoryName = categoryName.getText().toString();
                String enteredCategoryDescription = categoryDescription.getText().toString();

                // Validate user input (Category name is required)
                if (enteredCategoryName.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter category name", Toast.LENGTH_LONG).show();
                    return;
                }
                // Category name cannot be equal to none (letter cases ignored)
                if (enteredCategoryName.trim().equalsIgnoreCase("None")) {
                    Toast.makeText(getBaseContext(), "Category name cannot be \"None\"", Toast.LENGTH_LONG).show();
                    return;
                }


                ContentValues categoryValues = new ContentValues();
                categoryValues.put(ExpensesDB.CATEGORIES_KEY_NAME, enteredCategoryName);
                categoryValues.put(ExpensesDB.CATEGORIES_KEY_DESCRIPTION, enteredCategoryDescription);

                // insert a record
                if (mode.trim().equalsIgnoreCase("add")) {

                    // Search query to find if the entered category name already exists
                    String searchQuery = ExpensesDB.CATEGORIES_KEY_NAME + " = '" + enteredCategoryName + "'";
                    Cursor c = getContentResolver().query(CategoriesContentProvider.CATEGORIES_URI, null, searchQuery, null, null);
                    if (c.getCount() > 0) {
                        Toast.makeText(getBaseContext(), "Category already exists.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    c.close();

                    // Insert the category values, using the CategoriesContentProvider
                    Uri rowID = getContentResolver().insert(CategoriesContentProvider.CATEGORIES_URI, categoryValues);
                    long newID = ContentUris.parseId(rowID);
                    if (newID <= 0) {
                        Toast.makeText(getBaseContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(getBaseContext(), "Category added successfully.", Toast.LENGTH_LONG).show();
                    // Notify the previous activity (ie. AddExpenseActivity of a change to the categories list, so spinner choices get updated)
                    setResult(RESULT_OK, new Intent());
                }
                // update a record
                else {
                    Uri categoryUri = Uri.parse(CategoriesContentProvider.CATEGORIES_URI + "/" + categoryId);
                    getContentResolver().update(categoryUri, categoryValues, null, null);
                    Toast.makeText(getBaseContext(), "Category updated successfully.", Toast.LENGTH_LONG).show();
                }

                finish();
            }
        });


        deleteCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // An alert dialog will be shown to confirm deletion
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddCategoryActivity.this);
                dialogBuilder.setMessage(R.string.category_delete_confirm);
                dialogBuilder.setCancelable(true);

                dialogBuilder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Uri expenseURI = Uri.parse(CategoriesContentProvider.CATEGORIES_URI + "/" + categoryId);
                                getContentResolver().delete(expenseURI, null, null);

                                dialog.cancel();

                                // Since this category is being deleted, all expenses with this category will have the category "None"
                                String searchString = ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = " + categoryId;
                                Cursor expensesCursor = getContentResolver().query(ExpensesContentProvider.EXPENSES_URI, null, searchString, null, null);

                                while (expensesCursor.moveToNext()) {
                                    ContentValues expenseValues = new ContentValues();
                                    expenseValues.put(ExpensesDB.EXPENSES_KEY_ITEM_ID, expensesCursor.getString(expensesCursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_ITEM_ID)));
                                    // Update the expense category id to 0 (None)
                                    expenseValues.put(ExpensesDB.EXPENSES_KEY_CATEGORY_ID, 0);
                                    expenseValues.put(ExpensesDB.EXPENSES_KEY_PRICE, expensesCursor.getString(expensesCursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_PRICE)));
                                    expenseValues.put(ExpensesDB.EXPENSES_KEY_DATE, expensesCursor.getString(expensesCursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_DATE)));
                                    expenseValues.put(ExpensesDB.EXPENSES_KEY_DESCRIPTION, expensesCursor.getString(expensesCursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_DESCRIPTION)));

                                    Uri expenseUri = Uri.parse(ExpensesContentProvider.EXPENSES_URI + "/" + expensesCursor.getString(expensesCursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_ID)));
                                    getContentResolver().update(expenseUri, expenseValues, null, null);
                                }

                                expensesCursor.close();

                                Toast.makeText(getApplicationContext(), R.string.category_deleted, Toast.LENGTH_LONG).show();

                                finish();
                            }
                        });

                dialogBuilder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                dialogBuilder.create().show();

            }
        });

        // Finish the current activity, with no changes
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });
    }

    // Based on the rowId (the category ID) get the name of the category and its description from the CategoriesContentProvider
    private void loadCategoryInfo() {
        Uri uri = Uri.parse(CategoriesContentProvider.CATEGORIES_URI + "/" + categoryId);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            // Filling EditText views with their values
            categoryName.setText(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.CATEGORIES_KEY_NAME)));
            categoryDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.CATEGORIES_KEY_DESCRIPTION)));
            cursor.close();
        }
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