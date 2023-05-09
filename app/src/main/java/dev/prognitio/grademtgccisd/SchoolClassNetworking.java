package dev.prognitio.grademtgccisd;

import android.os.AsyncTask;

import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.javascript.host.dom.NodeList;
import org.htmlunit.javascript.host.html.HTMLElement;
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
import java.util.Objects;

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
            ArrayList<ArrayList<String>> response;


            try {
                String cookie = signInAndGetSessionID(cookies, url); //sign on
                response = getAndParseData(cookie, url[4], url[3], url[2]); //pull needed data
                if (response.get(0).isEmpty() || response.get(1).isEmpty()) {
                    response = getAndParseData(cookie, url[4], url[3], url[2]);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println(Arrays.toString(e.getStackTrace()));
                throw new RuntimeException(e);
            }

            System.out.println(response.get(0).toString());
            System.out.println(response.get(1).toString());

            return response;
        }

        protected void onPostExecute(ArrayList<ArrayList<String>> result) {
            System.out.println("Running onPostExecute");
            if (result.get(0).isEmpty() || result.get(1).isEmpty()) {
                //one of the classDatas is empty, do not try to build class objects.
                System.out.println("ERROR: Some class data is missing, cannot proceed with format. Aborting.");
            } else {
                //continue as normal, format data into SchoolClass objects and hand it over to ClassManager
                try {
                    ArrayList<SchoolClass> classes;
                    classes = genClassesFromStrings(result);
                    DataActivity.classManager.replaceClassData(classes);
                    System.out.println(DataActivity.classManager.toString());
                } catch (Exception e) {
                    System.out.println("Error encountered while trying to generate and store class data from scraped strings.");
                    System.out.println(e.getMessage());
                    System.out.println(Arrays.toString(e.getStackTrace()));
                    throw new RuntimeException(e);
                }

                System.out.println("No errors encountered in adding classes to classManager");
            }
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

    protected static ArrayList<ArrayList<String>> getAndParseData(String cookie, String userAgent, String rpUrl, String ccUrl) throws IOException {
        ArrayList<String> RPClassStrings = new ArrayList<>();
        ArrayList<String> CCClassStrings = new ArrayList<>();
        ArrayList<ArrayList<String>> response = new ArrayList<>();

        WebClient client = new WebClient(); //establish client and sign on to rpcard and credit summary pages.
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setPrintContentOnFailingStatusCode(true);
        client.getCookieManager().setCookiesEnabled(true);
        client.addRequestHeader("user-agent", userAgent);
        client.getCookieManager().addCookie(new Cookie("teams.gccisd.net", "JSESSIONID", cookie));
        HtmlPage RPPage = client.getPage(rpUrl);
        if (RPPage.getTitleText().isEmpty()) {
            System.out.println("Failed to load: Report Card Page");
        } else {
            System.out.println("Loaded: " + RPPage.getTitleText());
        }
        Document RPPageDoc = Jsoup.parse(RPPage.asXml());
        HtmlPage CCPage = client.getPage(ccUrl);
        if (CCPage.getTitleText().isEmpty()) {
            System.out.println("Failed to load: Credit Summary Page");
        } else {
            System.out.println("Loaded: " + CCPage.getTitleText());
        }
        Document CCPageDoc = Jsoup.parse(CCPage.asXml());
        Elements targetRPElems = RPPageDoc.select(".borderLeftGrading");
        for (Element e:targetRPElems) {
            RPClassStrings.add(e.attr("cellKey"));
        }
        Elements targetCCElems = CCPageDoc.select("table.ssTable");
        if (!targetCCElems.isEmpty()) {
            targetCCElems.remove(0);
        }
        targetCCElems = targetCCElems.select("tr").select("[valign=\"top\"]");
        for (Element e:targetCCElems) {
            //System.out.println(e.text());
            Elements tdContainer = e.select("td");
            StringBuilder classString = new StringBuilder();
            for (Element f:tdContainer) {
                classString.append(",").append(f.text());
            }
            classString = new StringBuilder(classString.substring(1));
            CCClassStrings.add(classString.toString());
        }
        System.out.println(CCClassStrings.size());

        if (CCClassStrings.isEmpty() || RPClassStrings.isEmpty()) {
            //Data grab failed for some reason.
            System.out.println("Data scraping failed: one of the String containers is empty. This would most likely be caused by an unsuccessful attempt to access the necessary web pages.");
            //System.out.println(RPPageDoc.html());
            //System.out.println(CCPageDoc.html());
        }

        response.add(RPClassStrings);
        response.add(CCClassStrings);
        return response;
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
                    semester = classString.split(",")[3];
                }
                if (semester.equals("1")) {
                    classStringsSemester1.add(classString);
                } else {
                    classStringsSemester2.add(classString);
                }
            }

            if(!classStringsSemester1.isEmpty()) {
                //gen a class object from it.
                ArrayList<String> grades = new ArrayList<>();
                String classSourceString = classStringsSemester1.get(0);
                for(String classString:classStringsSemester1) {
                    if(classString.contains("=")) {
                        String grade = classString.split(",")[4].split("=")[1];
                        grades.add(grade);
                    } else {
                        String grade = classString.split(",")[5];
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
                    period = trimPeriodString(classSourceString.split(",")[6].split("=")[1]);
                    yearTaken = 0;
                } else {
                    classCreatorName = classSourceString.split(",")[2];
                    teacher = "unknown";
                    gpa = 0;
                    gpaMax = 0;
                    semester = 1;
                    period = 1;
                    yearTaken = Integer.parseInt(classSourceString.split(",")[0]);
                }
                sClass = new SchoolClass(classCreatorName, teacher, gpa, gpaMax, grades, semester, period, yearTaken);
                output.add(sClass);
            }

            if(!classStringsSemester2.isEmpty()) {
                //gen a class object from it.
                ArrayList<String> grades = new ArrayList<>();
                String classSourceString = classStringsSemester2.get(0);
                for(String classString:classStringsSemester2) {
                    if(classString.contains("=")) {
                        //System.out.println(classString);
                        String grade = classString.split(",")[3].split("=")[1];
                        grades.add(grade);
                    } else {
                        String grade = classString.split(",")[5];
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
                    period = trimPeriodString(classSourceString.split(",")[6].split("=")[1]);
                    yearTaken = 0;
                } else {
                    classCreatorName = classSourceString.split(",")[2];
                    teacher = "unknown";
                    gpa = 0;
                    gpaMax = 0;
                    semester = 2;
                    period = 1;
                    yearTaken = Integer.parseInt(classSourceString.split(",")[0]);
                }
                sClass = new SchoolClass(classCreatorName, teacher, gpa, gpaMax, grades, semester, period, yearTaken);
                output.add(sClass);
            }

        }


        return output;
    }

    public static int trimPeriodString(String period) {
        String target = String.valueOf(period.charAt(period.length() - 1));
        return Integer.parseInt(target);
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
            String className = classString.split(",")[2];
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
            if (Objects.equals(className, targetClass)) {
                output.add(classString);
            }
        }
        //for credit summary classes
        for(String classString:classStrings.get(1)) {
            String className = classString.split(",")[2];
            if (Objects.equals(className, targetClass)) {
                output.add(classString);
            }
        }
        return output;
    }

}
