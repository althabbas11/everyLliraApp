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

public class SignUpActivity extends AppCompatActivity {

    EditText username, password, passwordConfirmation;
    Button signInButton, signUpButton;
    CheckBox keepSignedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.itemName);
        password = findViewById(R.id.itemPrice);
        passwordConfirmation = findViewById(R.id.password_confirmation);
        keepSignedIn = findViewById(R.id.keepSignedIn);

        signInButton = findViewById(R.id.sign_in_btn);
        signUpButton = findViewById(R.id.sign_up_btn);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUsername = username.getText().toString();
                String currentPassword = password.getText().toString();
                String currentPasswordConfirmation = passwordConfirmation.getText().toString();

                // check for blanks
                if (currentUsername.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter username", Toast.LENGTH_LONG).show();
                    return;
                }

                // check for blanks
                if (currentPassword.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter password", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!currentPassword.equals(currentPasswordConfirmation)) {
                    Toast.makeText(getBaseContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
                    return;
                }

                String searchQuery = ExpensesDB.USERS_KEY_USERNAME + " = '" + currentUsername + "'";
                Cursor c = getContentResolver().query(UsersContentProvider.USERS_URI, null, searchQuery, null, null);
                if (c.getCount() > 0) {
                    Toast.makeText(getBaseContext(), "Username already exists.", Toast.LENGTH_LONG).show();
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(ExpensesDB.USERS_KEY_USERNAME, currentUsername);
                values.put(ExpensesDB.USERS_KEY_PASSWORD, currentPassword);

                Uri rowID = getContentResolver().insert(UsersContentProvider.USERS_URI, values);
                long newID = ContentUris.parseId(rowID);
                if (newID <= 0) {
                    Toast.makeText(getBaseContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                Toast.makeText(getBaseContext(), "Signed up successfully.", Toast.LENGTH_LONG).show();
                finish();
            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("everyLiraSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username", username.getText().toString());
        editor.putBoolean("keepSignedIn", keepSignedIn.isChecked());

        editor.apply();
    }

}