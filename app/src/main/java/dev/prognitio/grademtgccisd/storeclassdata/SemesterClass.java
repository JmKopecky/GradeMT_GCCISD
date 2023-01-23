package dev.prognitio.grademtgccisd.storeclassdata;

import java.util.ArrayList;
import java.util.Collections;

import dev.prognitio.grademtgccisd.compareutil.CompareClassesByPeriod;

public class SemesterClass {

    ArrayList<SchoolClass> classList = new ArrayList<>();
    int semesterNumber;
    int classCount;

    public SemesterClass(int semesterNumber, ArrayList<SchoolClass> classList) {
        this.semesterNumber = semesterNumber;
        this.classList = classList;
        this.classCount = classList.size();
    }

    public SemesterClass(int semesterNumber) {
        this.semesterNumber = semesterNumber;
        this.classCount = 0;
    }


    public void addClass(SchoolClass schoolClass) {
        if (classList.isEmpty()) {
            classList.add(schoolClass);
        } else if (semesterNumber == 0) {
            //add the class at the next empty index
            schoolClass.setPeriod(classCount+1);
            classList.add(schoolClass);
        } else {
            //add the class at its period
            classList.add(schoolClass.getPeriod()-1, schoolClass);
        }
        classCount++;
    }

    public void sortClassesByPeriod() {
        if (!(classList.size() <= 1)) {
            Collections.sort(classList, new CompareClassesByPeriod());
        }
    }

    public int getClassCount() {
        return classCount;
    }

    //getter and setter methods
    public int getSemesterNumber() {
        return semesterNumber;
    }
    public void setSemesterNumber(int semesterNumber) {
        this.semesterNumber = semesterNumber;
    }
    public SchoolClass getClassAtIndex(int index) {
        return classList.get(index);
    }
    public ArrayList<SchoolClass> getClassList() {
        return classList;
    }

}
