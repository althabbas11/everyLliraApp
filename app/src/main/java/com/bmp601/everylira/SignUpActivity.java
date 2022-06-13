package com.bmp601.everylira;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    // Declaring variables
    EditText username, password, passwordConfirmation;
    CheckBox keepSignedIn;
    Button signInButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Assigning variables
        username = findViewById(R.id.signUpUsername);
        password = findViewById(R.id.signUpPassword);
        passwordConfirmation = findViewById(R.id.signUpPasswordConfirmation);
        keepSignedIn = findViewById(R.id.keepSignedIn);
        signInButton = findViewById(R.id.signInBtn);
        signUpButton = findViewById(R.id.signUpBtn);

        // Handling clicks on each button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Username letters case is not taken into account
                String enteredUsername = username.getText().toString().toLowerCase();
                String enteredPassword = password.getText().toString();
                String enteredPasswordConfirmation = passwordConfirmation.getText().toString();

                // Validating username and passwords inputs
                if (enteredUsername.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter username", Toast.LENGTH_LONG).show();
                    return;
                }
                if (enteredPassword.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter password", Toast.LENGTH_LONG).show();
                    return;
                }
                if (enteredPasswordConfirmation.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter password confirmation", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!enteredPassword.equals(enteredPasswordConfirmation)) {
                    Toast.makeText(getBaseContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
                    return;
                }

                // Checking if entered username already exists in the database
                String searchQuery = ExpensesDB.USERS_KEY_USERNAME + " = '" + enteredUsername + "'";
                // Using UsersContentProvider with the passed searchQuery
                Cursor c = getContentResolver().query(UsersContentProvider.USERS_URI, null, searchQuery, null, null);

                // If the username already exists...
                if (c.getCount() > 0) {
                    Toast.makeText(getBaseContext(), "Username already exists.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Gathering entered info in a ContentValues
                ContentValues values = new ContentValues();
                values.put(ExpensesDB.USERS_KEY_USERNAME, enteredUsername);
                // Hashing the password before inserting...
                values.put(ExpensesDB.USERS_KEY_PASSWORD, SignInActivity.MD5(enteredPassword));

                Uri rowID = getContentResolver().insert(UsersContentProvider.USERS_URI, values);
                long newID = ContentUris.parseId(rowID);
                if (newID <= 0) {
                    Toast.makeText(getBaseContext(), R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(getBaseContext(), R.string.signed_up_successfully, Toast.LENGTH_LONG).show();

                // Closing the cursor
                c.close();

                // Navigating to HomeActivity
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to SignUpActivity
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Create a SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("everyLiraSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Add the entered username (it will be used in UserInfo)
        editor.putString("username", username.getText().toString());

        // Add if keepSignedIn is checked
        editor.putBoolean("keepSignedIn", keepSignedIn.isChecked());

        // Store the added values
        editor.apply();
    }
}