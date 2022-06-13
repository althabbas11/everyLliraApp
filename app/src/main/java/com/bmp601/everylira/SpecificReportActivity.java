package com.bmp601.everylira;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class SpecificReportActivity extends AppCompatActivity {

    // Declaring variables
    private String kindOfReport;
    TextView reportName, year, month, totalCostTextView, totalCostValue, currency, noExpenses;
    Spinner yearsSpinner, monthsSpinner;
    Button getReport;
    ListView expensesListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_report);

        // To show the navigate up arrow in the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initializing variables
        noExpenses = findViewById(R.id.noExpenses);
        reportName = findViewById(R.id.reportName);
        year = findViewById(R.id.year);
        month = findViewById(R.id.month);
        totalCostValue = findViewById(R.id.totalCostValue);
        yearsSpinner = findViewById(R.id.yearsSpinner);
        monthsSpinner = findViewById(R.id.monthsSpinner);
        getReport = findViewById(R.id.getReport);
        totalCostTextView = findViewById(R.id.totalCostTextView);
        currency = findViewById(R.id.currency);
        expensesListView = findViewById(R.id.expensesList);

        // Columns to show
        String[] from = new String[]{ExpensesDB.ITEMS_KEY_NAME, ExpensesDB.CATEGORIES_KEY_NAME, ExpensesDB.EXPENSES_KEY_PRICE, ExpensesDB.EXPENSES_KEY_DATE};
        // The XML defined views which the data will be bound to
        int[] to = new int[]{R.id.expenseItemName, R.id.expenseCategoryName, R.id.expensePrice, R.id.expenseDate};

        // Getting the value of kindOfReport from the bundle
        if (this.getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras();
            kindOfReport = bundle.getString("kindOfReport");
        }

        // List of years to add to the years spinner
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1990; i <= thisYear; i++)
            years.add(Integer.toString(i));

        // Setting an array adapter, and applying a layout to it
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // List of months to add to the months spinner
        ArrayList<String> months = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
            // Since the format of months is MM
            if (i < 10)
                months.add("0" + i);
            else
                months.add(Integer.toString(i));
        }

        // Setting an array adapter, and applying a layout to it
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, months);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Handling what report to display depending on the value of kindOfReport
        // In the following if statements each case will make some views VISIBLE or GONE
        if (kindOfReport.trim().equalsIgnoreCase("yearlyReports")) {

            reportName.setText(R.string.yearlyReports);
            reportName.setVisibility(View.VISIBLE);

            year.setVisibility(View.VISIBLE);
            totalCostValue.setVisibility(View.GONE);
            totalCostTextView.setVisibility(View.GONE);
            currency.setVisibility(View.GONE);
            yearsSpinner.setVisibility(View.VISIBLE);
            getReport.setVisibility(View.VISIBLE);

            // Setting the adapter of the years spinner
            yearsSpinner.setAdapter(yearsAdapter);
            // Set the selected year to the current year
            yearsSpinner.setSelection(thisYear - 1990);

            getReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selectedYear = yearsSpinner.getSelectedItem().toString();

                    String[] args = {selectedYear};

                    // First we need to make a query on the sum of expenses within the selected year
                    // The selected year will be passed to the query
                    Cursor c1 = getContentResolver().query(ExpensesContentProvider.EXPENSES_YEAR_TOTAL_URI, null, null, args, null);
                    c1.moveToFirst();

                    // If no expenses found, set the total cost value to zero
                    if (c1.getString(c1.getColumnIndexOrThrow("Total")) == null)
                        totalCostValue.setText("0");
                    else
                        totalCostValue.setText(c1.getString(c1.getColumnIndexOrThrow("Total")));

                    c1.close();

                    // Second, we need to make a query on expenses within the selected year
                    // The selected year will be passed to the query too
                    Cursor c2 = getContentResolver().query(ExpensesContentProvider.EXPENSES_YEAR_URI, null, null, args, null);

                    // If no expenses found, make the noExpenses textview visible
                    if (c2.getCount() == 0) {
                        noExpenses.setVisibility(View.VISIBLE);
                        noExpenses.setText(R.string.no_expenses);
                    } else
                        noExpenses.setVisibility(View.GONE);

                    // Create a SimpleCursorAdapter to show the selected columns within expense_info.xml
                    SimpleCursorAdapter dataAdapter;
                    dataAdapter = new SimpleCursorAdapter(SpecificReportActivity.this, R.layout.expense_info, c2, from, to, 0);

                    expensesListView.setAdapter(dataAdapter);

                    totalCostValue.setVisibility(View.VISIBLE);
                    totalCostTextView.setVisibility(View.VISIBLE);
                    currency.setVisibility(View.VISIBLE);
                }
            });
        }

        if (kindOfReport.trim().equalsIgnoreCase("monthlyReports")) {

            reportName.setText(R.string.monthlyReports);
            reportName.setVisibility(View.VISIBLE);

            year.setVisibility(View.VISIBLE);
            month.setVisibility(View.VISIBLE);
            totalCostValue.setVisibility(View.GONE);
            totalCostTextView.setVisibility(View.GONE);
            currency.setVisibility(View.GONE);
            yearsSpinner.setVisibility(View.VISIBLE);
            monthsSpinner.setVisibility(View.VISIBLE);
            getReport.setVisibility(View.VISIBLE);

            yearsSpinner.setAdapter(yearsAdapter);
            yearsSpinner.setSelection(thisYear - 1990);

            monthsSpinner.setAdapter(monthsAdapter);

            getReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selectedYear = yearsSpinner.getSelectedItem().toString();
                    String selectedMonth = monthsSpinner.getSelectedItem().toString();

                    String[] args = {selectedYear, selectedMonth};

                    // First, we need to make a query on the sum of expenses within the selected month and year
                    // The selected month and year will be passed to the query
                    Cursor c1 = getContentResolver().query(ExpensesContentProvider.EXPENSES_MONTH_TOTAL_URI, null, null, args, null);
                    c1.moveToFirst();

                    if (c1.getString(c1.getColumnIndexOrThrow("Total")) == null)
                        totalCostValue.setText("0");
                    else
                        totalCostValue.setText(c1.getString(c1.getColumnIndexOrThrow("Total")));

                    c1.close();

                    // Second, we need to make a query on the expenses within the selected month and year
                    Cursor c2 = getContentResolver().query(ExpensesContentProvider.EXPENSES_MONTH_URI, null, null, args, null);

                    if (c2.getCount() == 0) {
                        noExpenses.setVisibility(View.VISIBLE);
                        noExpenses.setText(R.string.no_expenses);
                    } else
                        noExpenses.setVisibility(View.GONE);

                    SimpleCursorAdapter dataAdapter;
                    dataAdapter = new SimpleCursorAdapter(SpecificReportActivity.this, R.layout.expense_info, c2, from, to, 0);

                    c2.close();

                    expensesListView.setAdapter(dataAdapter);

                    totalCostValue.setVisibility(View.VISIBLE);
                    totalCostTextView.setVisibility(View.VISIBLE);
                    currency.setVisibility(View.VISIBLE);
                }
            });
        }


        if (kindOfReport.trim().equalsIgnoreCase("categoryReport")) {
            year.setText(R.string.category);

            reportName.setText(R.string.categoryReport);
            reportName.setVisibility(View.VISIBLE);

            year.setVisibility(View.VISIBLE);
            totalCostValue.setVisibility(View.GONE);
            totalCostTextView.setVisibility(View.GONE);
            currency.setVisibility(View.GONE);
            yearsSpinner.setVisibility(View.VISIBLE);
            getReport.setVisibility(View.VISIBLE);

            // A list of all available categories, using the CategoriesContentProvider
            ArrayList<String> categoriesList = new ArrayList<String>();
            Cursor categoriesCursor = getContentResolver().query(CategoriesContentProvider.CATEGORIES_URI, null, null, null, null);

            while (categoriesCursor.moveToNext()) {
                String currentCategory = categoriesCursor.getString(categoriesCursor.getColumnIndexOrThrow("categoryName"));
                categoriesList.add(currentCategory);
            }

            categoriesCursor.close();

            // NOTE that here years spinner will be used as a categories spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            yearsSpinner.setAdapter(adapter);


            getReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selectedCategory = yearsSpinner.getSelectedItem().toString();

                    String[] args = {selectedCategory};

                    // First, we need to make a query on the sum of expenses of the selected category
                    // The selected category will be passed to the query
                    Cursor c1 = getContentResolver().query(ExpensesContentProvider.EXPENSES_CATEGORY_TOTAL, null, null, args, null);
                    c1.moveToFirst();

                    if (c1.getString(c1.getColumnIndexOrThrow("Total")) == null)
                        totalCostValue.setText("0");
                    else
                        totalCostValue.setText(c1.getString(c1.getColumnIndexOrThrow("Total")));

                    c1.close();

                    // Second, we need to make a query on the expenses of the selected category
                    Cursor c2 = getContentResolver().query(ExpensesContentProvider.EXPENSES_CATEGORY_REPORT_URI, null, null, args, null);

                    if (c2.getCount() == 0) {
                        noExpenses.setVisibility(View.VISIBLE);
                        noExpenses.setText(R.string.no_expenses);
                    } else
                        noExpenses.setVisibility(View.GONE);


                    SimpleCursorAdapter dataAdapter;
                    dataAdapter = new SimpleCursorAdapter(SpecificReportActivity.this, R.layout.expense_info, c2, from, to, 0);

                    c2.close();

                    expensesListView.setAdapter(dataAdapter);

                    totalCostValue.setVisibility(View.VISIBLE);
                    totalCostTextView.setVisibility(View.VISIBLE);
                    currency.setVisibility(View.VISIBLE);
                }
            });
        }

        if (kindOfReport.trim().equalsIgnoreCase("purchasedItemsReport")) {

            reportName.setText(R.string.purchasedItemsReport);
            reportName.setVisibility(View.VISIBLE);

            // First, we need to make a query on the sum of expenses of all purchased items (items with price more than zero)
            Cursor c1 = getContentResolver().query(ExpensesContentProvider.EXPENSES_PAID_COST_URI, null, null, null, null);
            c1.moveToFirst();
            totalCostValue.setText(c1.getString(c1.getColumnIndexOrThrow("Total")));

            c1.close();

            // Second, we need to make a query on the expenses of all purchased items
            Cursor c2 = getContentResolver().query(ExpensesContentProvider.EXPENSES_PAID_REPORT_URI, null, null, null, null);

            if (c2.getCount() == 0) {
                noExpenses.setVisibility(View.VISIBLE);
                noExpenses.setText(R.string.no_expenses);
            } else
                noExpenses.setVisibility(View.GONE);

            SimpleCursorAdapter dataAdapter;
            dataAdapter = new SimpleCursorAdapter(this, R.layout.expense_info, c2, from, to, 0);

            c2.close();

            expensesListView.setAdapter(dataAdapter);
        }

        if (kindOfReport.trim().equalsIgnoreCase("serviceReport")) {

            reportName.setText(R.string.servicesReport);
            reportName.setVisibility(View.VISIBLE);

            // First, we need to make a query on the sum of expenses that are services
            Cursor c1 = getContentResolver().query(ExpensesContentProvider.EXPENSES_SERVICES_COST_URI, null, null, null, null);
            c1.moveToFirst();
            totalCostValue.setText(c1.getString(c1.getColumnIndexOrThrow("Total")));

            c1.close();

            // Second, we need to make a query on the expenses that are services
            Cursor c2 = getContentResolver().query(ExpensesContentProvider.EXPENSES_SERVICES_REPORT_URI, null, null, null, null);

            if (c2.getCount() == 0) {
                noExpenses.setVisibility(View.VISIBLE);
                noExpenses.setText(R.string.no_expenses);
            } else
                noExpenses.setVisibility(View.GONE);

            SimpleCursorAdapter dataAdapter;
            dataAdapter = new SimpleCursorAdapter(this, R.layout.expense_info, c2, from, to, 0);

            c2.close();

            expensesListView.setAdapter(dataAdapter);
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