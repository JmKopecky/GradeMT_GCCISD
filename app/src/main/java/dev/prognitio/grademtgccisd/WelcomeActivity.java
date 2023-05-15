package dev.prognitio.grademtgccisd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {


    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        boolean hasSetupOccured = false;
        //check if user has logged in previously. If so, redirect to main screen, else, continue.
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_prefs_class_data_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (!(sharedPref.contains("hasInitialSetupOccured"))) {
            editor.putBoolean("hasInitialSetupOccured", false);
            hasSetupOccured = false;
            Logger.log("Could not find previous setup data. ", LogType.DEBUG, "WelcomeActivity");
        } else {
            hasSetupOccured = sharedPref.getBoolean("hasInitialSetupOccured", false);
        }
        editor.apply();

        if (hasSetupOccured) {
            //redirect to main page
        }

        loginButton = findViewById(R.id.welcomeBeginSetupButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log("Welcome onclick ran, switching activity to LoginActivity", LogType.DEBUG, "WelcomeActivity");
                Intent switchActivityIntent = new Intent(context, LoginActivity.class);
                startActivity(switchActivityIntent);
            }
        });
    }
}