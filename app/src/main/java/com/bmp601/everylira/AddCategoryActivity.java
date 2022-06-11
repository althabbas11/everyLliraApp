package com.bmp601.everylira;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.ContentValues;
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

    EditText categoryName, categoryDescription;
    Button addCategory, deleteCategoryBtn, cancelButton;
    private String mode, categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        categoryName = findViewById(R.id.categoryName);
        categoryDescription = findViewById(R.id.categoryDescription);
        addCategory = findViewById(R.id.addCategory);
        deleteCategoryBtn = findViewById(R.id.deleteCategoryBtn);
        cancelButton = findViewById(R.id.cancel);

        if (this.getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras();
            mode = bundle.getString("mode");
        }

        // if in add mode disable the delete option
        if (mode.trim().equalsIgnoreCase("add")) {
            deleteCategoryBtn.setEnabled(false);
        }
        // get the rowId for the specific country
        else {
            addCategory.setText(R.string.save);
            Bundle bundle = this.getIntent().getExtras();
            categoryId = bundle.getString("rowId");
            loadCategoryInfo();
        }

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentCategoryName = categoryName.getText().toString();
                String currentCategoryDescription = categoryDescription.getText().toString();

                // check for blanks
                if (currentCategoryName.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter category name", Toast.LENGTH_LONG).show();
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(ExpensesDB.CATEGORIES_KEY_NAME, currentCategoryName);
                values.put(ExpensesDB.CATEGORIES_KEY_DESCRIPTION, currentCategoryDescription);

                // insert a record
                if (mode.trim().equalsIgnoreCase("add")) {

                    String searchQuery = ExpensesDB.CATEGORIES_KEY_NAME + " = '" + currentCategoryName + "'";
                    Cursor c = getContentResolver().query(CategoriesContentProvider.CATEGORIES_URI, null, searchQuery, null, null);
                    if (c.getCount() > 0) {
                        Toast.makeText(getBaseContext(), "Category already exists.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Uri rowID = getContentResolver().insert(CategoriesContentProvider.CATEGORIES_URI, values);
                    long newID = ContentUris.parseId(rowID);
                    if (newID <= 0) {
                        Toast.makeText(getBaseContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(getBaseContext(), "Category added successfully.", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
                // update a record
                else {
                    Uri categoryUri = Uri.parse(CategoriesContentProvider.CATEGORIES_URI + "/" + categoryId);
                    getContentResolver().update(categoryUri, values, null, null);
                    Toast.makeText(getBaseContext(), "Category updated successfully.", Toast.LENGTH_LONG).show();
                    finish();
                }


            }
        });

        deleteCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri expenseURI = Uri.parse(CategoriesContentProvider.CATEGORIES_URI + "/" + categoryId);
                getContentResolver().delete(expenseURI, null, null);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });
    }


    private void loadCategoryInfo() {
        Uri uri = Uri.parse(CategoriesContentProvider.CATEGORIES_URI + "/" + categoryId);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            categoryName.setText(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.CATEGORIES_KEY_NAME)));
            categoryDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.CATEGORIES_KEY_DESCRIPTION)));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}