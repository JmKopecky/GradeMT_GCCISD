package dev.prognitio.grademtgccisd.storeclassdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;

import dev.prognitio.grademtgccisd.compareutil.CompareSemestersBySemesterNumber;

public class ClassManager {

    ArrayList<SemesterClass> semesterList = new ArrayList<>();
    int semesterCount = 0;

    public ClassManager(ArrayList<SemesterClass> semesterList) {
        if (semesterList != null) {
            this.semesterList = semesterList;
            this.semesterCount = semesterList.size();
        }
    }
    public ClassManager() {
        //empty constructor
    }

    public void replaceClassData(ArrayList<SchoolClass> classes) {
        //until method is made to determine semester based on year and other stuff, just throw all classdata into semester 0.
        ArrayList<SemesterClass> semesterClasses = new ArrayList<>();
        semesterClasses.add(new SemesterClass(0, classes));
        this.semesterList = semesterClasses;
    }

    public boolean containsData() {
        return !semesterList.isEmpty();
    }

    public void sortData() {
        for (SemesterClass semester:semesterList) {
            semester.sortClassesByPeriod();
        }
        Collections.sort(semesterList, new CompareSemestersBySemesterNumber());
    }

    public ArrayList<SemesterClass> getSemesterList() {
        return semesterList;
    }

    public String toString() {
        String result;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        result = gson.toJson(this);
        return result;
    }
}


