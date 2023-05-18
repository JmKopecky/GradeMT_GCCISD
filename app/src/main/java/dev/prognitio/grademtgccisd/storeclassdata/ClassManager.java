package dev.prognitio.grademtgccisd.storeclassdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class ClassManager {

    ArrayList<SchoolClass> classList = new ArrayList<>();
    int classCount = 0;

    public ClassManager(ArrayList<SchoolClass> classList) {
        if (classList != null) {
            this.classList = classList;
            this.classCount = classList.size();
        }
    }
    public ClassManager() {
        //empty constructor
    }

    public void replaceClassData(ArrayList<SchoolClass> classes) {
        this.classList = classes;
    }

    public ArrayList<SchoolClass> getClasses() {
        return classList;
    }

    public boolean containsData() {
        return !classList.isEmpty();
    }

    public void sortData() {
        this.classList = SchoolClass.sortClassArrayBySemester(classList);
    }

    public String toString() {
        String result;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        result = gson.toJson(this);
        return result;
    }
}


