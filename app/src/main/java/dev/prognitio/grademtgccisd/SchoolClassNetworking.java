package dev.prognitio.grademtgccisd;

import android.os.AsyncTask;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.util.Cookie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dev.prognitio.grademtgccisd.storeclassdata.SchoolClass;

public class SchoolClassNetworking {

    static String homeUrl = "https://teams.gccisd.net/selfserve/EntryPointHomeAction.do?parent=false";
    static String signInUrl = "https://teams.gccisd.net/selfserve/SignOnLoginAction.do?parent=false&teamsStaffUser=N&x-tab-id=undefined&selectedIndexId=-1&selectedTable=&smartFormName=SmartForm&focusElement=&userLoginId=USERNAME&userPassword=PASSWORDNUMBER";
    static String creditsUrl = "https://teams.gccisd.net/selfserve/PSSViewStudentGradPlanCreditSummaryAction.do?x-tab-id=undefined";
    static String reportCardUrl = "https://teams.gccisd.net/selfserve/PSSViewReportCardsAction.do?x-tab-id=undefined";
    static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36";


    public static String generateSignInURL() {
        String url = signInUrl;
        url = url.replace("USERNAME", "3010919");
        url = url.replace("PASSWORDNUMBER", "03052007");
        return url;
    }

    public static void runGetDataTask() {
        new getDataTask().execute(homeUrl, generateSignInURL(), creditsUrl, reportCardUrl, userAgent);
    }



    private static class getDataTask extends AsyncTask<String, Void, ArrayList<ArrayList<String>>> {
        protected ArrayList<ArrayList<String>> doInBackground(String... url) {
            //declare all connections
            Map<String, String> cookies = null; //prepare cookie map for session data
            ArrayList<ArrayList<String>> response = new ArrayList<>();

            try {
                //.select(".borderLeftGrading"); .attr("cellKey");
                //.select("[title=\"Student Category Details\"] .even, [title=\"Student Category Details\"] .odd");
                String cookie = signInAndGetSessionID(cookies, url);
                WebClient client = new WebClient();
                client.getOptions().setCssEnabled(false);
                client.getOptions().setJavaScriptEnabled(false);
                client.getOptions().setPrintContentOnFailingStatusCode(true);
                client.getCookieManager().setCookiesEnabled(true);
                client.addRequestHeader("user-agent", url[4]);
                client.getCookieManager().addCookie(new Cookie("teams.gccisd.net", "JSESSIONID", cookie));
                HtmlPage RPPage = client.getPage(url[3]);
                System.out.println("Loaded: " + RPPage.getTitleText() + ".");
                HtmlPage CCPage = client.getPage(url[2]);
                System.out.println("Loaded: " + CCPage.getTitleText() + ".");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return response;
        }

        protected void onPostExecute(ArrayList<ArrayList<String>> result) {
            //do with the data

            System.out.println("Running onPostExecute");
            //System.out.println(result.get(0).get(0));

            //build SchoolClass objects with the strings obtained.
            ArrayList<SchoolClass> classes;
            //classes = genClassesFromStrings(result);
            //MainActivity.classManager;

        }
    }

    protected static String signInAndGetSessionID(Map<String, String> cookies, String[] url) throws IOException {
        Connection.Response initialConnection;
        Connection.Response signInConnection;
        //get initial connection to login page
        initialConnection = Jsoup.connect(url[0]).method(Connection.Method.GET).userAgent(url[4]).execute();
        cookies = initialConnection.cookies(); //get cookies (including session id)
        //sign in, then access the report card and credit pages.
        signInConnection = Jsoup.connect(url[1]).method(Connection.Method.GET).userAgent(url[4]).cookies(cookies).execute();
        return cookies.get("JSESSIONID");
    }
    public static ArrayList<SchoolClass> genClassesFromStrings(ArrayList<ArrayList<String>> input) {
        ArrayList<SchoolClass> output = new ArrayList<>();

        ArrayList<String> uniqueClassNames = getUniqueClassNames(input);
        //get each unique class name. loop through that array,
        //and for each unique class name, split into semesters.
        //For each semester, combine into a single class object.



        for(String className:uniqueClassNames) {
            //get list of all class strings for a given className
            ArrayList<String> stringsForClass;
            stringsForClass = getClassStringFromList(input, className);
            ArrayList<String> classStringsSemester1 = new ArrayList<>();
            ArrayList<String> classStringsSemester2 = new ArrayList<>();

            //split class strings into arrays based on semester, then create the class object.

            for (String classString:stringsForClass) {
                //for each element, if semester is unique, add to list.
                String semester;
                if (classString.contains("=")) { //if classString sourced from report card
                    semester = determineRPSemester(classString);
                } else {
                    semester = classString.split(" ")[3];
                }
                if (semester.equals("1")) {
                    classStringsSemester1.add(classString);
                } else {
                    classStringsSemester2.add(classString);
                }
            }

            if(!classStringsSemester1.isEmpty()) {
                //gen a class object from it.
                ArrayList<Float> grades = new ArrayList<>();
                String classSourceString = classStringsSemester1.get(0);
                for(String classString:classStringsSemester1) {
                    if(classString.contains("=")) {
                        Float grade = Float.valueOf(classString.split(",")[4].split("=")[1]);
                        grades.add(grade);
                    } else {
                        Float grade = Float.valueOf(classString.split(" ")[1]);
                        grades.add(grade);
                    }
                }
                SchoolClass sClass;
                String classCreatorName;
                String teacher;
                float gpa;
                float gpaMax;
                int semester;
                int period;
                int yearTaken;
                if(classSourceString.contains("=")) {
                    classCreatorName = classSourceString.split(",")[2].split("=")[1];
                    teacher = classSourceString.split(",")[5].split("=")[1];
                    gpa = 0;
                    gpaMax = 0;
                    semester = 1;
                    period = Integer.parseInt(classSourceString.split(",")[6].split("=")[1]);
                    yearTaken = 0;
                } else {
                    classCreatorName = classSourceString.split(" ")[2];
                    teacher = "unknown";
                    gpa = 0;
                    gpaMax = 0;
                    semester = 1;
                    period = 1;
                    yearTaken = Integer.parseInt(classSourceString.split(" ")[0]);
                }
                sClass = new SchoolClass(classCreatorName, teacher, gpa, gpaMax, grades, semester, period, yearTaken);
                output.add(sClass);
            }

            if(!classStringsSemester2.isEmpty()) {
                //gen a class object from it.
                ArrayList<Float> grades = new ArrayList<>();
                String classSourceString = classStringsSemester2.get(0);
                for(String classString:classStringsSemester2) {
                    if(classString.contains("=")) {
                        Float grade = Float.valueOf(classString.split(",")[4].split("=")[1]);
                        grades.add(grade);
                    } else {
                        Float grade = Float.valueOf(classString.split(" ")[1]);
                        grades.add(grade);
                    }
                }
                SchoolClass sClass;
                String classCreatorName;
                String teacher;
                float gpa;
                float gpaMax;
                int semester;
                int period;
                int yearTaken;
                if(classSourceString.contains("=")) {
                    classCreatorName = classSourceString.split(",")[2].split("=")[1];
                    teacher = classSourceString.split(",")[5].split("=")[1];
                    gpa = 0;
                    gpaMax = 0;
                    semester = 2;
                    period = Integer.parseInt(classSourceString.split(",")[6].split("=")[1]);
                    yearTaken = 0;
                } else {
                    classCreatorName = classSourceString.split(" ")[2];
                    teacher = "unknown";
                    gpa = 0;
                    gpaMax = 0;
                    semester = 2;
                    period = 1;
                    yearTaken = Integer.parseInt(classSourceString.split(" ")[0]);
                }
                sClass = new SchoolClass(classCreatorName, teacher, gpa, gpaMax, grades, semester, period, yearTaken);
                output.add(sClass);
            }

        }


        return output;
    }

    public static String determineRPSemester(String classString) {
        String semesterValue = classString.split(",")[1].split("=")[1];
        switch (semesterValue) {
            case "1st 9 weeks":
            case "2nd 9 weeks":
            case "1st Semester Exam":
            case "1st Semester Avg":
                return "1";
            case "3rd 9 weeks":
            case "4th 9 weeks":
            case "2nd Semester Exam":
            case "2nd Summer Avg":
                return "2";
            default: return "0";
        }
    }

    public static ArrayList<String> getUniqueClassNames(ArrayList<ArrayList<String>> classStrings) {
        ArrayList<String> classNames = new ArrayList<>();

        //for report card classes
        for(String classString:classStrings.get(0)) {
            String className = classString.split(",")[2].split("=")[1];
            if (!classNames.contains(className)) {
                classNames.add(className);
            }
        }
        //for credit summary classes
        for(String classString:classStrings.get(1)) {
            String className = classString.split(" ")[2];
            if (!classNames.contains(className)) {
                classNames.add(className);
            }
        }
        return classNames;
    }

    public static ArrayList<String> getClassStringFromList(ArrayList<ArrayList<String>> classStrings, String targetClass) {
        ArrayList<String> output = new ArrayList<>();

        //for report card classes
        for(String classString:classStrings.get(0)) {
            String className = classString.split(",")[2].split("=")[1];
            if (className == targetClass) {
                output.add(classString);
            }
        }
        //for credit summary classes
        for(String classString:classStrings.get(1)) {
            String className = classString.split(" ")[2];
            if (className == targetClass) {
                output.add(classString);
            }
        }

        return output;
    }

}
