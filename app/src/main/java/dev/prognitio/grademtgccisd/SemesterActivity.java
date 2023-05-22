package dev.prognitio.grademtgccisd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import dev.prognitio.grademtgccisd.storeclassdata.SchoolClass;

public class SemesterActivity extends AppCompatActivity {

    ImageButton semesterNavButton, overallNavButton;
    ArrayList<SchoolClass> classes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);

        classes = DataActivity.classManager.getClasses();
        genTable(classes);

        overallNavButton = findViewById(R.id.overallNavButton);
        semesterNavButton = findViewById(R.id.semesterNavButton);

        overallNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent overallViewIntent = new Intent(getApplicationContext(), OverallViewActivity.class);
                startActivity(overallViewIntent);
            }
        });

        semesterNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do nothing.
            }
        });
    }
    
    public void genTable(ArrayList<SchoolClass> classList) {
        TableLayout table = (TableLayout) findViewById(R.id.DataTableOverall);

        //gen layout params
        TextView classHeaderElem = findViewById(R.id.ClassNameHeader);
        LinearLayout.LayoutParams classParams = (LinearLayout.LayoutParams) classHeaderElem.getLayoutParams();
        classParams.weight = 1.0f;

        TextView reportCard1 = findViewById(R.id.Rp1Header);
        LinearLayout.LayoutParams rp1params = (LinearLayout.LayoutParams) reportCard1.getLayoutParams();
        rp1params.weight = 1.0f;

        TextView reportCard2 = findViewById(R.id.Rp2Header);
        LinearLayout.LayoutParams rp2params = (LinearLayout.LayoutParams) reportCard2.getLayoutParams();
        rp2params.weight = 1.0f;

        TextView examHeader = findViewById(R.id.semHeader);
        LinearLayout.LayoutParams examHeaderParams = (LinearLayout.LayoutParams) examHeader.getLayoutParams();
        examHeaderParams.weight = 1.0f;

        TextView finalHeader = findViewById(R.id.finalGradeHeader);
        LinearLayout.LayoutParams finalParams = (LinearLayout.LayoutParams) finalHeader.getLayoutParams();
        finalParams.weight = 1.0f;

        TextView gpaHeader = findViewById(R.id.GPAHeader);
        LinearLayout.LayoutParams gpaParams = (LinearLayout.LayoutParams) gpaHeader.getLayoutParams();
        gpaParams.weight = 1.0f;
        
        
        int highestSemesterValue = 0;
        for (SchoolClass classVal:classList) {
            //get highest semester value.
            int semesterValue = classVal.getSemester();
            if (semesterValue > highestSemesterValue) {
                highestSemesterValue = semesterValue;
            }
        }
        
        ArrayList<SchoolClass> targetClasses = new ArrayList<>();
        for (SchoolClass classVal:classList) {
            if (classVal.getSemester() == highestSemesterValue) {
                targetClasses.add(classVal);
            }
        }
        
        for (SchoolClass classVal:targetClasses) {
            TableRow row = new TableRow(this);
            
            String className = classVal.getClassName();
            String rp1 = classVal.getGrade().get("reportCard1");
            String rp2 = classVal.getGrade().get("reportCard2");
            String exam = classVal.getGrade().get("exam");
            String total = classVal.getGrade().get("total");
            String gpa = String.valueOf(classVal.getGpa());

            TextView classText = new TextView(this);
            TextView rp1Text = new TextView(this);
            TextView rp2Text = new TextView(this);
            TextView examText = new TextView(this);
            TextView totalText = new TextView(this);
            TextView gpaText = new TextView(this);


            classText.setText(className);
            rp1Text.setText(rp1);
            rp2Text.setText(rp2);
            examText.setText(exam);
            totalText.setText(total);
            gpaText.setText(gpa);

            classText.setTextColor(getResources().getColor(R.color.grey_main));
            rp1Text.setTextColor(getResources().getColor(R.color.grey_main));
            rp2Text.setTextColor(getResources().getColor(R.color.grey_main));
            examText.setTextColor(getResources().getColor(R.color.grey_main));
            totalText.setTextColor(getResources().getColor(R.color.grey_main));
            gpaText.setTextColor(getResources().getColor(R.color.grey_main));

            classText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            rp1Text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            rp2Text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            examText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            totalText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            gpaText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            classText.setLayoutParams(classParams);
            rp1Text.setLayoutParams(rp1params);
            rp2Text.setLayoutParams(rp2params);
            examText.setLayoutParams(examHeaderParams);
            totalText.setLayoutParams(finalParams);
            gpaText.setLayoutParams(gpaParams);

            classText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            rp1Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            rp2Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            examText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            totalText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            gpaText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            row.addView(classText);
            row.addView(rp1Text);
            row.addView(rp2Text);
            row.addView(examText);
            row.addView(totalText);
            row.addView(gpaText);

            table.addView(row);

        }
    }
}