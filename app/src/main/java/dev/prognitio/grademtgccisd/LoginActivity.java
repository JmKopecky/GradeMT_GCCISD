package dev.prognitio.grademtgccisd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    Button signOnButton;
    EditText usernameField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Context context = getApplicationContext();
        signOnButton = findViewById(R.id.loginButton);
        usernameField = findViewById(R.id.inputUserName);
        passwordField = findViewById(R.id.inputPassword);

        signOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log("Submitting form data for LoginActivity.", LogType.DEBUG, "LoginActivity");
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_prefs_class_data_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putBoolean("hasInitialSetupOccured", true);
                editor.apply();
                Intent switchActivityIntent = new Intent(context, DataActivity.class);
                startActivity(switchActivityIntent);
            }
        });
    }
}