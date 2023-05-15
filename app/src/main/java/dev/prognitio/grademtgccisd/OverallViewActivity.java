package dev.prognitio.grademtgccisd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

import dev.prognitio.grademtgccisd.storeclassdata.SchoolClass;

public class OverallViewActivity extends AppCompatActivity {

    static ArrayList<SchoolClass> classes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_view);
        classes = DataActivity.classManager.getClasses();
        for (SchoolClass classElem:classes) {
            //for each schoolClass, add a new row containing data.
        }
    }
}