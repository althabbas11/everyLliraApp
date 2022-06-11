package com.bmp601.everylira;

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

public class SignInActivity extends AppCompatActivity {

    EditText username, password;
    Button signInButton, signUpButton;
    CheckBox keepSignedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        keepSignedIn = findViewById(R.id.keepSignedIn);

        signInButton = findViewById(R.id.sign_in_btn);
        signUpButton = findViewById(R.id.sign_up_btn);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUsername = username.getText().toString();
                String currentPassword = password.getText().toString();

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

                String searchQuery = ExpensesDB.USERS_KEY_USERNAME + " = '" + currentUsername + "' AND " + ExpensesDB.USERS_KEY_PASSWORD + " = '" + currentPassword + "'";
                Cursor c = getContentResolver().query(UsersContentProvider.USERS_URI, null, searchQuery, null, null);
                if (c.getCount() > 0) {
                    Toast.makeText(getBaseContext(), "Signed in successfully.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                } else {
                    Toast.makeText(getBaseContext(), "Wrong credentials.", Toast.LENGTH_LONG).show();
                }
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
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