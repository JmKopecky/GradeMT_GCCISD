package dev.prognitio.grademtgccisd.compareutil;

import java.util.Comparator;

import dev.prognitio.grademtgccisd.storeclassdata.SchoolClass;

public class CompareClassesByPeriod implements Comparator<SchoolClass> {
    @Override
    public int compare(SchoolClass schoolClass, SchoolClass t1) {
        Integer class1Period = schoolClass.getPeriod();
        Integer class2Period = t1.getPeriod();
        return class1Period.compareTo(class2Period);
    }
}
