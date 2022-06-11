package com.bmp601.everylira;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddExpenseActivity extends AppCompatActivity {

    // Calender instance to choose expense date
    final Calendar myCalendar = Calendar.getInstance();

    private String mode, expenseId;
    int itemId;

    // Declaring variables
    Spinner categoriesSpinner;
    Button addNewCategory, addExpenseBtn, deleteBtn, cancelBtn;
    EditText itemName, itemDescription, itemPrice, purchaseDate, purchaseDescription;
    CheckBox isService;

    // To determine if the spinner list needs to updated
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == RESULT_OK) {
                if (result.getData() != null)
                    loadSpinnerValues();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initializing variables
        categoriesSpinner = (Spinner) findViewById(R.id.categoriesSpinner);
        addNewCategory = findViewById(R.id.addNewCategory);
        addExpenseBtn = findViewById(R.id.addExpenseBtn);
        deleteBtn = findViewById(R.id.deleteExpenseBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        itemPrice = findViewById(R.id.itemPrice);
        purchaseDate = findViewById(R.id.purchaseDate);
        purchaseDescription = findViewById(R.id.purchaseDescription);
        isService = findViewById(R.id.isService);

        if (this.getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras();
            mode = bundle.getString("mode");
        }

        // if in add mode disable the delete option
        if (mode.trim().equalsIgnoreCase("add")) {
            deleteBtn.setEnabled(false);
        }
        // get the rowId for the specific country
        else {
            addExpenseBtn.setText(R.string.save);
            Bundle bundle = this.getIntent().getExtras();
            expenseId = bundle.getString("rowId");
            loadExpenseInfo();
        }


        // Loading spinner values (categories)
        loadSpinnerValues();


        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        purchaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                //following line to restrict future date selection
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddCategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "add");
                intent.putExtras(bundle);
                startForResult.launch(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri expenseURI = Uri.parse(ExpensesContentProvider.EXPENSES_URI + "/" + expenseId);
                getContentResolver().delete(expenseURI, null, null);

                Uri itemURI = Uri.parse(ItemsContentProvider.ITEMS_URI + "/" + itemId);
                getContentResolver().delete(itemURI, null, null);

                finish();
            }
        });

        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentItemName = itemName.getText().toString();
                boolean currentIsService = isService.isChecked();
                String currentItemDescription = itemDescription.getText().toString();
                double currentExpensePrice;
                if (itemPrice.getText().toString().isEmpty())
                    currentExpensePrice = 0;
                else
                    currentExpensePrice = Double.parseDouble(itemPrice.getText().toString());
                String currentCategoryName = categoriesSpinner.getSelectedItem().toString();
                String currentExpenseDate = purchaseDate.getText().toString();
                String currentExpenseDescription = purchaseDescription.getText().toString();

                // check for blanks
                if (currentItemName.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter item name", Toast.LENGTH_LONG).show();
                    return;
                }

                // insert a record
                if (mode.trim().equalsIgnoreCase("add")) {
                    ContentValues itemValues = new ContentValues();
                    itemValues.put(ExpensesDB.ITEMS_KEY_NAME, currentItemName);
                    itemValues.put(ExpensesDB.ITEMS_KEY_IS_SERVICE, currentIsService);
                    itemValues.put(ExpensesDB.ITEMS_KEY_DESCRIPTION, currentItemDescription);


                    Uri itemRowID = getContentResolver().insert(ItemsContentProvider.ITEMS_URI, itemValues);
                    long newItemID = ContentUris.parseId(itemRowID);
                    if (newItemID <= 0) {
                        Toast.makeText(getBaseContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        return;
                    }


                    String searchQuery = ExpensesDB.CATEGORIES_KEY_NAME + " = '" + currentCategoryName + "'";
                    Cursor c = getContentResolver().query(CategoriesContentProvider.CATEGORIES_URI, null, searchQuery, null, null);
                    c.moveToFirst();
                    ContentValues expenseValues = new ContentValues();
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_ITEM_ID, newItemID);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_CATEGORY_ID, c.getInt(0));
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_PRICE, currentExpensePrice);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_DATE, currentExpenseDate);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_DESCRIPTION, currentExpenseDescription);


                    Uri expenseRowID = getContentResolver().insert(ExpensesContentProvider.EXPENSES_URI, expenseValues);
                    long newExpenseID = ContentUris.parseId(expenseRowID);
                    if (newExpenseID <= 0) {
                        Toast.makeText(getBaseContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(getBaseContext(), "Expense added successfully.", Toast.LENGTH_LONG).show();
                    finish();
                }
                // update a record
                else {

                    ContentValues itemValues = new ContentValues();
                    itemValues.put(ExpensesDB.ITEMS_KEY_NAME, currentItemName);
                    itemValues.put(ExpensesDB.ITEMS_KEY_IS_SERVICE, currentIsService);
                    itemValues.put(ExpensesDB.ITEMS_KEY_DESCRIPTION, currentItemDescription);

                    Uri itemUri = Uri.parse(ItemsContentProvider.ITEMS_URI + "/" + itemId);
                    getContentResolver().update(itemUri, itemValues, null, null);


                    String searchQuery = ExpensesDB.CATEGORIES_KEY_NAME + " = '" + currentCategoryName + "'";
                    Cursor c = getContentResolver().query(CategoriesContentProvider.CATEGORIES_URI, null, searchQuery, null, null);
                    c.moveToFirst();
                    ContentValues expenseValues = new ContentValues();
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_ITEM_ID, itemId);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_CATEGORY_ID, c.getInt(0));
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_PRICE, currentExpensePrice);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_DATE, currentExpenseDate);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_DESCRIPTION, currentExpenseDescription);

//
//                    Uri expenseRowID = getContentResolver().insert(ExpensesContentProvider.EXPENSES_URI, expenseValues);
//                    long newExpenseID = ContentUris.parseId(expenseRowID);
//                    if (newExpenseID <= 0) {
//                        Toast.makeText(getBaseContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
//                        return;
//                    }
                    Uri expenseUri = Uri.parse(ExpensesContentProvider.EXPENSES_URI + "/" + expenseId);
                    getContentResolver().update(expenseUri, expenseValues, null, null);
                    Toast.makeText(getBaseContext(), "Expense updated successfully.", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // based on the rowId get all information from the Content Provider
    // about that country
    private void loadExpenseInfo() {
        Uri uri = Uri.parse(ExpensesContentProvider.EXPENSES_URI + "/" + expenseId);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            itemId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_ITEM_ID)));
            String itemSearchQuery = ExpensesDB.ITEMS_KEY_ID + " = '" + itemId + "'";
            Cursor itemCursor = getContentResolver().query(ItemsContentProvider.ITEMS_URI, null, itemSearchQuery, null, null);

            int categoryId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_CATEGORY_ID)));
            String categorySearchQuery = ExpensesDB.CATEGORIES_KEY_ID + " = '" + categoryId + "'";
            Cursor categoryCursor = getContentResolver().query(CategoriesContentProvider.CATEGORIES_URI, null, categorySearchQuery, null, null);

            if (itemCursor != null && categoryCursor != null) {
                itemCursor.moveToFirst();
                categoryCursor.moveToFirst();
                itemName.setText(itemCursor.getString(itemCursor.getColumnIndexOrThrow(ExpensesDB.ITEMS_KEY_NAME)));
                isService.setChecked(Integer.parseInt(itemCursor.getString(itemCursor.getColumnIndexOrThrow(ExpensesDB.ITEMS_KEY_IS_SERVICE))) == 1);
                itemDescription.setText(itemCursor.getString(itemCursor.getColumnIndexOrThrow(ExpensesDB.ITEMS_KEY_DESCRIPTION)));

                String categoryName = categoryCursor.getString(categoryCursor.getColumnIndexOrThrow(ExpensesDB.CATEGORIES_KEY_NAME));

                categoriesSpinner.post(new Runnable() {
                    public void run() {
                        categoriesSpinner.setSelection(getIndex(categoriesSpinner, categoryName));
                    }
                });
                itemPrice.setText(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_PRICE)));
                purchaseDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_DATE)));
                purchaseDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_DESCRIPTION)));

            }
        }
    }

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }


    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        purchaseDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void loadSpinnerValues() {
        Cursor c = getContentResolver().query(CategoriesContentProvider.CATEGORIES_URI, null, null, null, null);
        List<String> categories = new ArrayList<String>();
        while (c.moveToNext()) {
            @SuppressLint("Range") String categoryName = c.getString(c.getColumnIndex(ExpensesDB.CATEGORIES_KEY_NAME));
            categories.add(categoryName);
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

}