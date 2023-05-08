package dev.prognitio.grademtgccisd;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import dev.prognitio.grademtgccisd.storeclassdata.ClassManager;
import dev.prognitio.grademtgccisd.storeclassdata.SchoolClass;
import dev.prognitio.grademtgccisd.storeclassdata.SemesterClass;

public class MainActivity extends AppCompatActivity {

    public static ClassManager classManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SchoolClassNetworking.runGetDataTask();
        //Deserialize and get all data, build a list of semesters, then pass it to the classmanager below.
        ArrayList<SemesterClass> semesterClasses = new ArrayList<>();
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
            //get a list of all classes
            ArrayList<SchoolClass> classList = new ArrayList<>();
            if (sharedPref.getInt("classCount", 0) > 0) {
                for (int i = 0; i < sharedPref.getInt("classCount", 0); i++) {
                    String referenceKey = "class_" + i;
                    SchoolClass schoolclass = SchoolClass.createWithString(sharedPref.getString(referenceKey, null));
                    classList.add(schoolclass);
                }
            }
            //Separate the classes into semester objects
            semesterClasses.add(new SemesterClass(0));
            semesterClasses.add(new SemesterClass(1));
            semesterClasses.add(new SemesterClass(2));
            semesterClasses.add(new SemesterClass(3));
            semesterClasses.add(new SemesterClass(4));
            semesterClasses.add(new SemesterClass(5));
            semesterClasses.add(new SemesterClass(6));
            semesterClasses.add(new SemesterClass(7));
            semesterClasses.add(new SemesterClass(8));

            for (SchoolClass schoolclass:classList) {
                switch (schoolclass.getSemester()) {
                    case 0: semesterClasses.get(0).addClass(schoolclass);
                    case 1: semesterClasses.get(1).addClass(schoolclass);
                    case 2: semesterClasses.get(2).addClass(schoolclass);
                    case 3: semesterClasses.get(3).addClass(schoolclass);
                    case 4: semesterClasses.get(4).addClass(schoolclass);
                    case 5: semesterClasses.get(5).addClass(schoolclass);
                    case 6: semesterClasses.get(6).addClass(schoolclass);
                    case 7: semesterClasses.get(7).addClass(schoolclass);
                    case 8: semesterClasses.get(8).addClass(schoolclass);
                }
            }
        }
        editor.apply();
        classManager = new ClassManager(semesterClasses);
        classManager.sortData();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_prefs_class_data_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Save data here.
        int classCount = 0;
        int classIndex = 0;
        if (classManager.containsData()) {
            for (SemesterClass semester:classManager.getSemesterList()) {
                classCount += semester.getClassCount();
                for (SchoolClass schoolClass:semester.getClassList()) {
                    String referenceKey = "class_" + classIndex;
                    editor.putString(referenceKey, schoolClass.convertToString());
                    classIndex++;
                }
            }
        }
        editor.putInt("classCount", classCount);
        editor.apply();
    }
}