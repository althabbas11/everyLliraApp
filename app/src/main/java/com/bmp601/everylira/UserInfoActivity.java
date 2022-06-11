package com.bmp601.everylira;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
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

    EditText username, newPassword;
    String previousUsername = null;
    Button updateCredentials, signOut;
    int userId;
    boolean credentialsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        username = findViewById(R.id.username);
        newPassword = findViewById(R.id.newPassword);
        updateCredentials = findViewById(R.id.updateCredentials);
        signOut = findViewById(R.id.signOut);

        Cursor c1 = getContentResolver().query(UsersContentProvider.USERS_URI, null, null, null, null);
        c1.moveToFirst();
        userId = Integer.parseInt(c1.getString(c1.getColumnIndexOrThrow("_id")));

        updateCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUsername = username.getText().toString();
                String currentPassword = newPassword.getText().toString();

                // check for blanks
                if (currentUsername.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter username", Toast.LENGTH_LONG).show();
                    return;
                }

                // check for blanks
                if (currentPassword.trim().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please enter your old password\n or a new password", Toast.LENGTH_LONG).show();
                    return;
                }

                String searchQuery = ExpensesDB.USERS_KEY_USERNAME + " = '" + currentUsername + "'";
                Cursor c = getContentResolver().query(UsersContentProvider.USERS_URI, null, searchQuery, null, null);
                if (c.getCount() > 0 && !Objects.equals(previousUsername, currentUsername)) {
                    Toast.makeText(getBaseContext(), "Username already exists.", Toast.LENGTH_LONG).show();
                    return;
                }


                ContentValues values = new ContentValues();
                values.put(ExpensesDB.USERS_KEY_USERNAME, currentUsername);
                values.put(ExpensesDB.USERS_KEY_PASSWORD, currentPassword);

                Uri userUri = Uri.parse(UsersContentProvider.USERS_URI + "/" + userId);
                getContentResolver().update(userUri, values, null, null);
                Toast.makeText(getBaseContext(), "Credentials updated successfully.", Toast.LENGTH_LONG).show();
                credentialsChanged = true;
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("everyLiraSP", Context.MODE_PRIVATE);
        String spUsername = sharedPreferences.getString("username", "");
        username.setText(spUsername);
        if (previousUsername == null)
            previousUsername = username.getText().toString();
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("everyLiraSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (credentialsChanged)
            editor.putString("username", username.getText().toString());
        editor.apply();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}