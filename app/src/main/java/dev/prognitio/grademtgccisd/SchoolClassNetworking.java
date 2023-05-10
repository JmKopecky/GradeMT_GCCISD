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
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dev.prognitio.grademtgccisd.storeclassdata.SchoolClass;

public class SchoolClassNetworking {

    static String homeUrl = "https://teams.gccisd.net/selfserve/EntryPointHomeAction.do?parent=false";
    static String signInUrl = "https://teams.gccisd.net/selfserve/SignOnLoginAction.do?userLoginId=USERNAME&userPassword=PASSWORDNUMBER";
    static String creditsUrl = "https://teams.gccisd.net/selfserve/PSSViewStudentGradPlanCreditSummaryAction.do";
    static String reportCardUrl = "https://teams.gccisd.net/selfserve/PSSViewReportCardsAction.do";
    static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36";


    public static String generateSignInURL(String type, String username, String password) {
        String url = signInUrl;
        String rpUrl = reportCardUrl;
        String ccUrl = creditsUrl;
        switch (type) {
            case "signIn": {
                url = url.replace("USERNAME", username);
                url = url.replace("PASSWORDNUMBER", password);
                return url;
            }
            case "rp": {
                rpUrl = rpUrl.replace("USERNAME", username);
                rpUrl = rpUrl.replace("PASSWORD", password);
                return rpUrl;
            }
            case "cs": {
                ccUrl = ccUrl.replace("USERNAME", username);
                ccUrl = ccUrl.replace("PASSWORD", password);
                return ccUrl;
            }
            default: {
                return "";
            }
        }
    }

    public static void runGetDataTask(String username, String password) {
        new getDataTask().execute(homeUrl, generateSignInURL("signIn", username, password), generateSignInURL("cs", username, password), generateSignInURL("rp", username, password), userAgent);
    }


    private static class getDataTask extends AsyncTask<String, Void, ArrayList<ArrayList<String>>> {
        protected ArrayList<ArrayList<String>> doInBackground(String... url) {
            ArrayList<Document> pages = new ArrayList<>();
            pages = signInAndRetrievePages(url[0], url[1], url[3], url[2]);

            ArrayList<ArrayList<String>> response = new ArrayList<>();
            response = parseDocuments(pages.get(0), pages.get(1));
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

    protected static ArrayList<Document> signInAndRetrievePages(String initialUrl, String signOnUrl, String reportCardUrl, String creditSummaryUrl) {
        ArrayList<Document> output = new ArrayList<>();
        try {
            //setup webclient
            WebClient client = new WebClient();
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
            client.getOptions().setPrintContentOnFailingStatusCode(true);
            client.getCookieManager().setCookiesEnabled(true);
            client.addRequestHeader("user-agent", userAgent);
            client.getOptions().setRedirectEnabled(true);

            HtmlPage initialPage = client.getPage(initialUrl);
            HtmlPage signInPage = client.getPage(signOnUrl);
            HtmlPage reportCardPage = client.getPage(reportCardUrl);
            HtmlPage creditSummaryPage = client.getPage(creditSummaryUrl);
            Document reportCardDocument = Jsoup.parse(reportCardPage.asXml());
            Document creditSummaryDocument = Jsoup.parse(creditSummaryPage.asXml());
            output.add(reportCardDocument);
            output.add(creditSummaryDocument);

        } catch (Exception e) {
            System.out.println("Error encountered with loading and retrieving pages in signInAndRetrievePages();");
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return output;
    }

    protected static ArrayList<ArrayList<String>> parseDocuments(Document reportCardDoc, Document creditSummaryDoc) {
        ArrayList<String> RPClassStrings = new ArrayList<>();
        ArrayList<String> CCClassStrings = new ArrayList<>();
        ArrayList<ArrayList<String>> output = new ArrayList<>();

        //parse report card data
        Elements targetRPElems = reportCardDoc.select(".borderLeftGrading");
        for (Element e:targetRPElems) {
            RPClassStrings.add(e.attr("cellKey"));
        }

        //parse credit summary data
        Elements targetCCElems = creditSummaryDoc.select("table.ssTable");
        if (!targetCCElems.isEmpty()) {
            targetCCElems.remove(0);
        }
        targetCCElems = targetCCElems.select("tr").select("[valign=\"top\"]");
        for (Element e:targetCCElems) {
            Elements tdContainer = e.select("td");
            StringBuilder classString = new StringBuilder();
            for (Element f:tdContainer) {
                classString.append(",").append(f.text());
            }
            classString = new StringBuilder(classString.substring(1));
            CCClassStrings.add(classString.toString());
        }

        //add strings to output
        output.add(RPClassStrings);
        output.add(CCClassStrings);
        return output;
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
