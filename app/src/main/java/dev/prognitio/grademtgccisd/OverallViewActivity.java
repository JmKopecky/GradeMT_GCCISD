package dev.prognitio.grademtgccisd;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
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
    TableLayout table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_view);
        Context context = getApplicationContext();
        classes = DataActivity.classManager.getClasses();
        genTable(classes);
    }

    public void genTable(ArrayList<SchoolClass> classes) {
        //table = findViewById(R.id.overallViewTable);

        //gen layout params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1);


    }
}