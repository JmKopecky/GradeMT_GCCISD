package dev.prognitio.grademtgccisd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.prognitio.grademtgccisd.storeclassdata.ClassManager;

public class DataActivity extends AppCompatActivity {

    public static ClassManager classManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        SchoolClassNetworking.runGetDataTask();
        //Deserialize and get all data, build a list of semesters, then pass it to the classmanager below.
        classManager = new ClassManager();
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_prefs_class_data_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (!(sharedPref.contains("hasInitialSetupOccured"))) {
            editor.putBoolean("hasInitialSetupOccured", false);
        }
        editor.apply();

        if (!sharedPref.getBoolean("hasInitialSetupOccured", false)) {
            //do basic value setup, before user setup
        } else {
            //do assuming that data has been setup completely
            String classManagerJson = sharedPref.getString("classmanager", "");
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            classManager = gson.fromJson(classManagerJson, ClassManager.class);
        }
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_prefs_class_data_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Save data here.
        editor.putString("classmanager", classManager.toString());
        editor.apply();
    }
}