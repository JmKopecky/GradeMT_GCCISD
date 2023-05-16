package dev.prognitio.grademtgccisd.storeclassdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class SchoolClass {

    String className;
    String teacher;
    float gpa;
    float maxGpa;
    HashMap<String, String> grades = new HashMap<>();
    int semester;
    int period;
    int yearTaken;
    
    public SchoolClass(String className, String teacher, float gpa, float maxGpa, HashMap<String, String> grade, int semester, int period, int yearTaken) {
        this.className = className;
        this.teacher = teacher;
        this.gpa = gpa;
        this.maxGpa = maxGpa;
        this.grades = grade;
        this.semester = semester;
        this.period = period;
        this.yearTaken = yearTaken;
    }
    
    public String convertToString() {
        String result;
        //convert this object to json string using Gson library
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        result = gson.toJson(this);
        return result;
    }

    public static SchoolClass createWithString(String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, SchoolClass.class);
    }


    //setter methods
    public void setClassName(String className) {
        this.className = className;
    }
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
    public void setGpa(float gpa) {
        this.gpa = gpa;
    }
    public void setMaxGpa(float maxGpa) {
        this.maxGpa = maxGpa;
    }
    public void setGrade(HashMap<String, String> grade) {
        this.grades = grade;
    }
    public void setSemester(int semester) {
        this.semester = semester;
    }
    public void setPeriod(int period) {
        this.period = period;
    }
    public void setYearTaken(int yearTaken) {
        this.yearTaken = yearTaken;
    }

    //getter methods
    public String getClassName() {
        return className;
    }
    public String getTeacher() {
        return teacher;
    }
    public float getGpa() {
        return gpa;
    }
    public float getMaxGpa() {
        return maxGpa;
    }
    public HashMap<String, String> getGrade() {
        return grades;
    }
    public int getSemester() {
        return semester;
    }
    public int getPeriod() {
        return period;
    }
    public int getYearTaken() {
        return yearTaken;
    }
}
