package com.bmp601.everylira;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    // Declaring variables
    Button yearlyReports, monthlyReports, categoryReport, purchasedItemsReport, serviceReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // To show the navigate up arrow in the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initializing variables
        // All buttons will be setOnClickListener
        yearlyReports = findViewById(R.id.yearlyReports);
        yearlyReports.setOnClickListener(this);

        monthlyReports = findViewById(R.id.monthlyReports);
        monthlyReports.setOnClickListener(this);

        categoryReport = findViewById(R.id.categoryReport);
        categoryReport.setOnClickListener(this);

        purchasedItemsReport = findViewById(R.id.purchasedItemsReport);
        purchasedItemsReport.setOnClickListener(this);

        serviceReport = findViewById(R.id.serviceReport);
        serviceReport.setOnClickListener(this);
    }

    // Each button view will navigate to the same activity but with different bundle data (kindOfReport)
    @SuppressLint("NonConstantResourceId")
    public void onClick(@NonNull View v) {
        Intent intent = new Intent(getApplicationContext(), SpecificReportActivity.class);
        Bundle bundle;
        switch (v.getId()) {
            case R.id.yearlyReports:
                bundle = new Bundle();
                bundle.putString("kindOfReport", "yearlyReports");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.monthlyReports:
                bundle = new Bundle();
                bundle.putString("kindOfReport", "monthlyReports");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.categoryReport:
                bundle = new Bundle();
                bundle.putString("kindOfReport", "categoryReport");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.purchasedItemsReport:
                bundle = new Bundle();
                bundle.putString("kindOfReport", "purchasedItemsReport");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.serviceReport:
                bundle = new Bundle();
                bundle.putString("kindOfReport", "serviceReport");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
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