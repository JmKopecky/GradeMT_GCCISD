package dev.prognitio.grademtgccisd.compareutil;

import java.util.Comparator;

import dev.prognitio.grademtgccisd.storeclassdata.SemesterClass;

public class CompareSemestersBySemesterNumber implements Comparator<SemesterClass> {
    @Override
    public int compare(SemesterClass semesterClass, SemesterClass t1) {
        Integer semesterClass1 = semesterClass.getSemesterNumber();
        Integer semesterClass2 = t1.getSemesterNumber();
        return semesterClass1.compareTo(semesterClass2);
    }
}
