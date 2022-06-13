package com.bmp601.everylira;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class SignInActivity extends AppCompatActivity {

    // Declaring variables
    EditText username, password;
    CheckBox keepSignedIn;
    Button signInButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Assigning variables
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        keepSignedIn = findViewById(R.id.keepSignedIn);
        signInButton = findViewById(R.id.signInBtn);
        signUpButton = findViewById(R.id.signUpBtn);

        // Handling clicks on each button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Username letters case is not taken into account
                String enteredUsername = username.getText().toString().toLowerCase();
                // Hashing the entered password
                String enteredPassword = password.getText().toString();

                // Validating username and password inputs
                if (enteredUsername.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter username", Toast.LENGTH_LONG).show();
                    return;
                }
                if (enteredPassword.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter password", Toast.LENGTH_LONG).show();
                    return;
                }

                // Checking user credentials from the database (with a hashed password)
                String searchQuery = ExpensesDB.USERS_KEY_USERNAME + " = '" + enteredUsername + "' AND " + ExpensesDB.USERS_KEY_PASSWORD + " = '" + MD5(enteredPassword) + "'";
                // Using UsersContentProvider with the passed searchQuery
                Cursor c = getContentResolver().query(UsersContentProvider.USERS_URI, null, searchQuery, null, null);

                // If a record found with the given search query, navigate the user to HomeActivity
                if (c.getCount() > 0) {
                    // Display a success toast message
                    Toast.makeText(getBaseContext(), R.string.signed_in_successfully, Toast.LENGTH_LONG).show();

                    // Closing the cursor
                    c.close();

                    // Navigate to HomeActivity
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // If no records found...
                    Toast.makeText(getBaseContext(), "Wrong credentials.", Toast.LENGTH_LONG).show();
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to SignUpActivity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // MD5 hashing function
    // http://stackoverflow.com/a/6565597/221135
    @Nullable
    public static String MD5(@NonNull String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Create a SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("everyLiraSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store the entered username (it will be used in UserInfo)
        editor.putString("username", username.getText().toString());

        // Store if keepSignedIn is checked
        editor.putBoolean("keepSignedIn", keepSignedIn.isChecked());

        // Store the added values
        editor.apply();
    }
}