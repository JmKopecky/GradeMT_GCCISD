package dev.prognitio.grademtgccisd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import dev.prognitio.grademtgccisd.storeclassdata.SchoolClass;

public class OverallViewActivity extends AppCompatActivity {

    ArrayList<SchoolClass> classes;

    ImageButton semesterNavButton, overallNavButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_view);
        Context context = getApplicationContext();
        classes = DataActivity.classManager.getClasses();
        genTable(classes);

        overallNavButton = findViewById(R.id.overallNavButton);
        semesterNavButton = findViewById(R.id.semesterNavButton);

        overallNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do nothing.
            }
        });

        semesterNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent semesterViewIntent = new Intent(context, SemesterActivity.class);
                startActivity(semesterViewIntent);
            }
        });
    }

    public void genTable(ArrayList<SchoolClass> classes) {
        TableLayout table = (TableLayout) findViewById(R.id.DataTableOverall);

        //gen layout params
        TextView classHeaderElem = findViewById(R.id.ClassNameHeader);
        LinearLayout.LayoutParams classParams = (LinearLayout.LayoutParams) classHeaderElem.getLayoutParams();
        classParams.weight = 1.0f;

        TextView semesterHeaderElem = findViewById(R.id.Semesterheader);
        LinearLayout.LayoutParams semesterParams = (LinearLayout.LayoutParams) semesterHeaderElem.getLayoutParams();
        semesterParams.weight = 1.0f;

        TextView gradeHeaderElem = findViewById(R.id.Rp1Header);
        LinearLayout.LayoutParams gradeParams = (LinearLayout.LayoutParams) gradeHeaderElem.getLayoutParams();
        gradeParams.weight = 1.0f;

        TextView gpaHeaderElem = findViewById(R.id.GPAHeader);
        LinearLayout.LayoutParams gpaParams = (LinearLayout.LayoutParams) gpaHeaderElem.getLayoutParams();
        gpaParams.weight = 1.0f;



        for (SchoolClass classVal:classes) {
            TableRow row = new TableRow(this);

            String className = classVal.getClassName();
            String semester = String.valueOf(classVal.getSemester());
            String grade = String.valueOf(DataActivity.determineTotalGradeFromGradeMap(classVal.getGrade()));
            String gpa = "" + classVal.getGpa();

            TextView classText = new TextView(this);
            TextView semesterText = new TextView(this);
            TextView gradeText = new TextView(this);
            TextView gpaText = new TextView(this);

            classText.setText(className);
            semesterText.setText(semester);
            gradeText.setText(grade);
            gpaText.setText(gpa);

            classText.setTextColor(getResources().getColor(R.color.grey_main));
            semesterText.setTextColor(getResources().getColor(R.color.grey_main));
            gradeText.setTextColor(getResources().getColor(R.color.grey_main));
            gpaText.setTextColor(getResources().getColor(R.color.grey_main));

            classText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            semesterText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            gradeText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            gpaText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            classText.setLayoutParams(classParams);
            semesterText.setLayoutParams(semesterParams);
            gradeText.setLayoutParams(gradeParams);
            gpaText.setLayoutParams(gpaParams);

            row.addView(classText);
            row.addView(semesterText);
            row.addView(gradeText);
            row.addView(gpaText);

            table.addView(row);
        }
    }
}