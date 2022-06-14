package com.bmp601.everylira;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
    EditText itemName, itemDescription, itemPrice, purchaseDate, purchaseDescription;
    CheckBox isService;
    Spinner categoriesSpinner;
    Button addNewCategory, addExpenseBtn, deleteBtn, cancelBtn;

    // To determine if the spinner list needs to updated
    // the AddCategory activity will return a result with OK code, if a new category got added
    // https://youtu.be/Ke9PaRdMcgc
    // (There are other ways, but this is enough and simple)
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

        // To show the navigate up arrow in the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initializing variables
        itemName = findViewById(R.id.itemName);
        isService = findViewById(R.id.isService);
        itemDescription = findViewById(R.id.itemDescription);
        categoriesSpinner = findViewById(R.id.categoriesSpinner);
        addNewCategory = findViewById(R.id.addNewCategory);
        itemPrice = findViewById(R.id.itemPrice);
        purchaseDate = findViewById(R.id.purchaseDate);
        purchaseDescription = findViewById(R.id.purchaseDescription);
        addExpenseBtn = findViewById(R.id.addExpenseBtn);
        deleteBtn = findViewById(R.id.deleteExpenseBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        // Getting the value of mode from the bundle
        if (this.getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras();
            mode = bundle.getString("mode");
        }

        // If mode is set to "add", hide the delete button
        if (mode.trim().equalsIgnoreCase("add")) {
            deleteBtn.setVisibility(View.GONE);
        }
        // Otherwise, change the text of addExpenseBtn to Save
        // And load the expense info to the EditText views
        else {
            addExpenseBtn.setText(R.string.save);
            Bundle bundle = this.getIntent().getExtras();
            expenseId = bundle.getString("rowId");
            loadExpenseInfo();
        }

        // Loading spinner values (categories)
        loadSpinnerValues();

        // Initialize a DatePickerDialog
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                // Update the EditText of the purchase date
                updateDateValue();
            }
        };

        // Show a date picker when pressed on purchaseDate view
        purchaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                // Setting the max date that can be chosen to the current day
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        // Navigate to AddCategoryActivity when pressed
        addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddCategoryActivity.class);

                // The mode is set to add
                Bundle bundle = new Bundle();
                bundle.putString("mode", "add");
                intent.putExtras(bundle);

                // Since startActivityForResult is deprecated
                startForResult.launch(intent);
            }
        });

        // If mode is set to update, the delete button will be visible
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // An alert dialog will be shown to confirm deletion
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddExpenseActivity.this);
                dialogBuilder.setMessage(R.string.expense_delete_confirm);
                dialogBuilder.setCancelable(true);

                dialogBuilder.setPositiveButton(
                        R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Delete the expense
                                Uri expenseURI = Uri.parse(ExpensesContentProvider.EXPENSES_URI + "/" + expenseId);
                                getContentResolver().delete(expenseURI, null, null);
                                // Delete the item of the expense
                                Uri itemURI = Uri.parse(ItemsContentProvider.ITEMS_URI + "/" + itemId);
                                getContentResolver().delete(itemURI, null, null);

                                Toast.makeText(getApplicationContext(), R.string.expense_deleted, Toast.LENGTH_LONG).show();

                                dialog.cancel();

                                finish();
                            }
                        });

                dialogBuilder.setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                dialogBuilder.create().show();
            }
        });


        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String enteredItemName = itemName.getText().toString();
                boolean enteredIsService = isService.isChecked();
                String enteredItemDescription = itemDescription.getText().toString();
                double enteredExpensePrice;

                if (itemPrice.getText().toString().isEmpty())
                    enteredExpensePrice = 0;
                else
                    enteredExpensePrice = Double.parseDouble(itemPrice.getText().toString());

                String enteredCategoryName = categoriesSpinner.getSelectedItem().toString();
                String enteredExpenseDate = purchaseDate.getText().toString();
                String enteredExpenseDescription = purchaseDescription.getText().toString();

                // Validating user inputs (itemName, and purchaseDate are required)
                if (enteredItemName.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter item name", Toast.LENGTH_LONG).show();
                    return;
                }
                if (enteredExpenseDate.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please specify purchase date", Toast.LENGTH_LONG).show();
                    return;
                }


                // Insert a record
                if (mode.trim().equalsIgnoreCase("add")) {
                    // Filling item info
                    ContentValues itemValues = new ContentValues();
                    itemValues.put(ExpensesDB.ITEMS_KEY_NAME, enteredItemName);
                    itemValues.put(ExpensesDB.ITEMS_KEY_IS_SERVICE, enteredIsService);
                    itemValues.put(ExpensesDB.ITEMS_KEY_DESCRIPTION, enteredItemDescription);


                    Uri itemRowID = getContentResolver().insert(ItemsContentProvider.ITEMS_URI, itemValues);
                    long newItemID = ContentUris.parseId(itemRowID);

                    if (newItemID <= 0) {
                        Toast.makeText(getBaseContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Getting selected category ID
                    String searchQuery = ExpensesDB.CATEGORIES_KEY_NAME + " = '" + enteredCategoryName + "'";
                    Cursor c = getContentResolver().query(CategoriesContentProvider.CATEGORIES_URI, null, searchQuery, null, null);
                    c.moveToFirst();

                    int categoryId = c.getCount() == 0 ? 0 : c.getInt(0);

                    // Filling expense inf
                    ContentValues expenseValues = new ContentValues();
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_ITEM_ID, newItemID);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_CATEGORY_ID, categoryId);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_PRICE, enteredExpensePrice);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_DATE, enteredExpenseDate);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_DESCRIPTION, enteredExpenseDescription);

                    c.close();

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
                    itemValues.put(ExpensesDB.ITEMS_KEY_NAME, enteredItemName);
                    itemValues.put(ExpensesDB.ITEMS_KEY_IS_SERVICE, enteredIsService);
                    itemValues.put(ExpensesDB.ITEMS_KEY_DESCRIPTION, enteredItemDescription);

                    Uri itemUri = Uri.parse(ItemsContentProvider.ITEMS_URI + "/" + itemId);
                    getContentResolver().update(itemUri, itemValues, null, null);

                    // Getting selected category ID
                    String searchQuery = ExpensesDB.CATEGORIES_KEY_NAME + " = '" + enteredCategoryName + "'";
                    Cursor c = getContentResolver().query(CategoriesContentProvider.CATEGORIES_URI, null, searchQuery, null, null);
                    c.moveToFirst();

                    ContentValues expenseValues = new ContentValues();
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_ITEM_ID, itemId);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_CATEGORY_ID, enteredCategoryName.equalsIgnoreCase("none") ? 0 : c.getInt(0));
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_PRICE, enteredExpensePrice);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_DATE, enteredExpenseDate);
                    expenseValues.put(ExpensesDB.EXPENSES_KEY_DESCRIPTION, enteredExpenseDescription);

                    c.close();

                    Uri expenseUri = Uri.parse(ExpensesContentProvider.EXPENSES_URI + "/" + expenseId);
                    getContentResolver().update(expenseUri, expenseValues, null, null);

                    Toast.makeText(getBaseContext(), "Expense updated successfully.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        // Finish the current activity, with no changes
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Based on the rowId (the expense ID) get all info from the needed content providers
    private void loadExpenseInfo() {
        Uri uri = Uri.parse(ExpensesContentProvider.EXPENSES_URI + "/" + expenseId);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            itemId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_ITEM_ID)));

            // Searching for the item using the ItemsContentProvider (for the item info)
            String itemSearchQuery = ExpensesDB.ITEMS_KEY_ID + " = '" + itemId + "'";
            Cursor itemCursor = getContentResolver().query(ItemsContentProvider.ITEMS_URI, null, itemSearchQuery, null, null);

            int categoryId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_CATEGORY_ID)));

            // Searching for the name of the category using the CategoriesContentProvider
            String categorySearchQuery = ExpensesDB.CATEGORIES_KEY_ID + " = '" + categoryId + "'";
            Cursor categoryCursor = getContentResolver().query(CategoriesContentProvider.CATEGORIES_URI, null, categorySearchQuery, null, null);

            itemCursor.moveToFirst();
            categoryCursor.moveToFirst();

            // Filling EditText views with their values
            itemName.setText(itemCursor.getString(itemCursor.getColumnIndexOrThrow(ExpensesDB.ITEMS_KEY_NAME)));
            isService.setChecked(Integer.parseInt(itemCursor.getString(itemCursor.getColumnIndexOrThrow(ExpensesDB.ITEMS_KEY_IS_SERVICE))) == 1);
            itemDescription.setText(itemCursor.getString(itemCursor.getColumnIndexOrThrow(ExpensesDB.ITEMS_KEY_DESCRIPTION)));

            // Handling the non-categorized items
            String categoryName = categoryId == 0 ? "None" : categoryCursor.getString(categoryCursor.getColumnIndexOrThrow(ExpensesDB.CATEGORIES_KEY_NAME));

            // Update the spinner selected value
            // https://stackoverflow.com/a/17370964/10756728
            categoriesSpinner.post(new Runnable() {
                public void run() {
                    // getIndex(Spinner, String) returns the position of a string in a spinner list
                    categoriesSpinner.setSelection(getIndex(categoriesSpinner, categoryName));
                }
            });

            itemPrice.setText(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_PRICE)));
            purchaseDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_DATE)));
            purchaseDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesDB.EXPENSES_KEY_DESCRIPTION)));

            itemCursor.close();
            categoryCursor.close();
        }
    }

    private int getIndex(Spinner spinner, String targetString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(targetString))
                return i;

        }
        return 0;
    }

    // To update the date view text
    private void updateDateValue() {
        // This format is used for easy access with the database
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        purchaseDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void loadSpinnerValues() {
        // Reading/querying categories using CategoriesContentProvider
        Cursor c = getContentResolver().query(CategoriesContentProvider.CATEGORIES_URI, null, null, null, null);
        // Empty list to be filled with available categories
        List<String> categories = new ArrayList<String>();

        categories.add("None");

        while (c.moveToNext()) {
            @SuppressLint("Range") String categoryName = c.getString(c.getColumnIndex(ExpensesDB.CATEGORIES_KEY_NAME));
            categories.add(categoryName);
        }

        c.close();

        // Setting and adapter for the spinner
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(adapter);
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