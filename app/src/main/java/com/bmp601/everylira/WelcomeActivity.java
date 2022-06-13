package com.bmp601.everylira;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// WelcomeActivity will first check if a user is already logged in
// If so, it will navigate to the HomeActivity
public class WelcomeActivity extends AppCompatActivity {

    // Declaring variables
    Button signInButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Assigning variables
        signInButton = findViewById(R.id.signInBtn);
        signUpButton = findViewById(R.id.signUpBtn);

        // Handling clicks on each button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to SignInActivity, and finish the current activity
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to SignUpActivity, and finish the current activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Checking a SharedPreferences value keepSignedIn
        // If set to true, the user will be navigated to HomeActivity
        SharedPreferences sharedPreferences = getSharedPreferences("everyLiraSP", Context.MODE_PRIVATE);
        boolean isSignedIn = sharedPreferences.getBoolean("keepSignedIn", false);
        if (isSignedIn) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}