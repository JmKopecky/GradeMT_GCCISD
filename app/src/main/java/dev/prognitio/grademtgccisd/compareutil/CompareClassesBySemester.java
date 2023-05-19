package dev.prognitio.grademtgccisd.compareutil;

import java.util.Comparator;

import dev.prognitio.grademtgccisd.storeclassdata.SchoolClass;

public class CompareClassesBySemester implements Comparator<SchoolClass> {

    @Override
    public int compare(SchoolClass schoolClass, SchoolClass t1) {
        Integer class1Semester = schoolClass.getSemester();
        Integer class2Semester = t1.getSemester();
        return class1Semester.compareTo(class2Semester) * -1;
    }
}
