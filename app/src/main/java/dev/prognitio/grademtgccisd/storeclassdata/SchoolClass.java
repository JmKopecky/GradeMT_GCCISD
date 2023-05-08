package dev.prognitio.grademtgccisd.storeclassdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SchoolClass {

    String className;
    float gpa;
    float maxGpa;
    float grade;
    int semester;
    int period;
    int yearTaken;
    
    public SchoolClass(String class string, boolean isReportCard) {
        if(isReportCard) {    
        this.className = classString.split(“,”)[2].split(“=“)[1];
        this.gpa = gpa; (function to guess gpa)
        this.maxGpa = maxGpa; (function to guess gpa)
        this.grade = classString.split(“,”)[4].split(“=“)[1];
        this.semester = semester; (first or 2nd 9 weeks = 1, else 2)
        this.period = classString.split(“,”)[6].split(“=“)[1];
        this.yearTaken = 
} else {
        this.className = classString.split(“ “)[2];
        this.gpa = gpa; (function to guess gpa)
        this.maxGpa = maxGpa; (function to guess gpa)
        this.grade = classString.split(“ “)[1];
        this.semester = classString.split(“ “)[3];
        this.period = ;
        this.yearTaken = classString.split(“ “)[0];
}
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
    public void setGpa(float gpa) {
        this.gpa = gpa;
    }
    public void setMaxGpa(float maxGpa) {
        this.maxGpa = maxGpa;
    }
    public void setGrade(float grade) {
        this.grade = grade;
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
    public float getGpa() {
        return gpa;
    }
    public float getMaxGpa() {
        return maxGpa;
    }
    public float getGrade() {
        return grade;
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
