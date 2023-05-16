package dev.prognitio.grademtgccisd;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;

import dev.prognitio.grademtgccisd.storeclassdata.SchoolClass;

public class OverallViewActivity extends AppCompatActivity {

    ArrayList<SchoolClass> classes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_view);
        Context context = getApplicationContext();
        classes = DataActivity.classManager.getClasses();
        genTable(classes);
    }

    public void genTable(ArrayList<SchoolClass> classes) {
        TableLayout table = (TableLayout) findViewById(R.id.DataTableOverall);

        //gen layout params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;

        for (SchoolClass classVal:classes) {
            TableRow row = new TableRow(this);

            String className = classVal.getClassName() + " " + classVal.getTeacher();
            String grade = classVal.getGrade().get("total");
            String gpa = "" + classVal.getGpa();

            TextView classText = new TextView(this);
            TextView gradeText = new TextView(this);
            TextView gpaText = new TextView(this);

            classText.setText(className);
            gradeText.setText(grade);
            gpaText.setText(gpa);

            classText.setTextColor(getResources().getColor(R.color.grey_main));
            gradeText.setTextColor(getResources().getColor(R.color.grey_main));
            gpaText.setTextColor(getResources().getColor(R.color.grey_main));

            classText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            gradeText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            gpaText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            /*
            classText.setLayoutParams(params);
            gradeText.setLayoutParams(params);
            gpaText.setLayoutParams(params);
             */

            row.addView(classText);
            row.addView(gradeText);
            row.addView(gpaText);

            table.addView(row);
        }
    }
}