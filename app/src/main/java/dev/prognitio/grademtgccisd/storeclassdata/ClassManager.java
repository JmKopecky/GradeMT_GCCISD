package dev.prognitio.grademtgccisd.storeclassdata;

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
}
