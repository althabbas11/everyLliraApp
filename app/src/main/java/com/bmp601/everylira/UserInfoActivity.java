package com.bmp601.everylira;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class UserInfoActivity extends AppCompatActivity {

    // Declaring variables
    EditText username, newPassword;
    String previousUsername = null;
    Button updateCredentials, signOut, cancel;
    int userId;
    boolean credentialsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // To show the navigate up arrow in the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initializing variables
        username = findViewById(R.id.username);
        newPassword = findViewById(R.id.newPassword);
        updateCredentials = findViewById(R.id.updateCredentials);
        signOut = findViewById(R.id.signOut);
        cancel = findViewById(R.id.cancel);

        // Getting the user ID
        Cursor c1 = getContentResolver().query(UsersContentProvider.USERS_URI, null, null, null, null);
        c1.moveToFirst();
        userId = Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow("_id")));

        c1.close();

        updateCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredUsername = username.getText().toString();
                String enteredPassword = newPassword.getText().toString();

                // Validating user input (username and password are required)
                // The user is asked to enter the old password or a new password (this is just a demo 😐)
                if (enteredUsername.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter username", Toast.LENGTH_LONG).show();
                    return;
                }
                if (enteredPassword.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter your old password\n or a new password", Toast.LENGTH_LONG).show();
                    return;
                }

                // Checking if the entered username already exists, but it is ok to enter the same username as the previous one
                String searchQuery = ExpensesDB.USERS_KEY_USERNAME + " = '" + enteredUsername + "'";
                Cursor c2 = getContentResolver().query(UsersContentProvider.USERS_URI, null, searchQuery, null, null);
                if (c2.getCount() > 0 && !Objects.equals(previousUsername, enteredUsername)) {
                    Toast.makeText(getBaseContext(), "Username already exists.", Toast.LENGTH_LONG).show();
                    return;
                }

                c2.close();

                ContentValues values = new ContentValues();
                values.put(ExpensesDB.USERS_KEY_USERNAME, enteredUsername);
                // Insert a hashed password
                values.put(ExpensesDB.USERS_KEY_PASSWORD, SignInActivity.MD5(enteredPassword));

                // Updating the user credentials using the UsersContentProvider
                Uri userUri = Uri.parse(UsersContentProvider.USERS_URI + "/" + userId);
                getContentResolver().update(userUri, values, null, null);
                Toast.makeText(getBaseContext(), "Credentials updated successfully.", Toast.LENGTH_LONG).show();

                // credentialsChanged is set to true, to be triggered in the onPause function
                credentialsChanged = true;
                finish();
            }
        });

        // Finish the current activity, with no changes
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create a SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("everyLiraSP", Context.MODE_PRIVATE);
        String spUsername = sharedPreferences.getString("username", "");

        // Retrieve the username stored in the SharedPreferences
        username.setText(spUsername);

        // Set the previousUsername to stored username
        if (previousUsername == null)
            previousUsername = username.getText().toString();
    }


    @Override
    protected void onPause() {
        super.onPause();

        // Create a SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("everyLiraSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Update/Store the new username
        if (credentialsChanged)
            editor.putString("username", username.getText().toString());

        editor.apply();
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