package dev.prognitio.grademtgccisd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import dev.prognitio.grademtgccisd.storeclassdata.ClassManager;
import dev.prognitio.grademtgccisd.storeclassdata.SchoolClass;

public class DataActivity extends AppCompatActivity {

    public static ClassManager classManager;
    static Context context;
    static String homeUrl = "https://teams.gccisd.net/selfserve/EntryPointHomeAction.do?parent=false";
    static String signInUrl = "https://teams.gccisd.net/selfserve/SignOnLoginAction.do?userLoginId=USERNAME&userPassword=PASSWORDNUMBER";
    static String creditsUrl = "https://teams.gccisd.net/selfserve/PSSViewStudentGradPlanCreditSummaryAction.do";
    static String reportCardUrl = "https://teams.gccisd.net/selfserve/PSSViewReportCardsAction.do";
    static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36";


    public static HashMap<String, String[]> classDataReference;

    static {
        classDataReference = new HashMap<String, String[]>();
        //classDataReference.put("", new String[]{"", ""});

        //Onramps courses.
        classDataReference.put("D19625", new String[]{"OR Chem", "5.5"});
        classDataReference.put("CHEM 1401", new String[]{"OR Chem", "6.0"});
        classDataReference.put("D19759", new String[]{"OR Comp Sci", "5.5"});
        classDataReference.put("COMP 1302", new String[]{"OR Comp Sci", "6.0"});
        classDataReference.put("D19725", new String[]{"OR Physics", "5.5"});
        classDataReference.put("PHYS 1301", new String[]{"OR Physics", "6.0"});
        classDataReference.put("D15202", new String[]{"OR College Algebra", "5.5"});
        classDataReference.put("MTH 1314", new String[]{"OR College Algebra", "6.0"});
        classDataReference.put("D05764", new String[]{"OR Pre-Cal", "5.5"});
        classDataReference.put("MATH 2312", new String[]{"OR Pre-Cal", "6.0"});
        classDataReference.put("D15762", new String[]{"OR Statistics", "5.5"});
        classDataReference.put("MATH 1342", new String[]{"OR Statistics", "6.0"});

        //English classes
        classDataReference.put("04123", new String[]{"English 1", "5.0"});
        classDataReference.put("04113", new String[]{"English 1 HON", "5.5"});
        classDataReference.put("04223", new String[]{"English 2", "5.0"});
        classDataReference.put("04213", new String[]{"English 2 HON", "5.5"});
        classDataReference.put("04323", new String[]{"English 3", "5.0"});
        classDataReference.put("04343", new String[]{"AP Lang & Comp", "6.0"});
        classDataReference.put("04423", new String[]{"English 4", "5.0"});
        classDataReference.put("04443", new String[]{"AP Lit & Comp", "6.0"});

        //Math classes
        classDataReference.put("05103", new String[]{"Algebra 1", "5.0"});
        classDataReference.put("05203", new String[]{"Algebra 1 HON", "5.5"});
        classDataReference.put("05603", new String[]{"Geometry", "5.0"});
        classDataReference.put("05353", new String[]{"Geometry HON", "5.5"});
        classDataReference.put("05363", new String[]{"Algebra 2", "5.0"});
        classDataReference.put("05383", new String[]{"Algebra 2 HON", "5.5"});
        classDataReference.put("05753", new String[]{"Pre-Calculus", "5.0"});
        classDataReference.put("05763", new String[]{"Pre-Calculus HON", "5.5"});

        //Science classes
        classDataReference.put("08523", new String[]{"Biology", "5.0"});
        classDataReference.put("08513", new String[]{"Biology HON", "5.5"});
        classDataReference.put("08613", new String[]{"Chemistry", "5.0"});
        classDataReference.put("08623", new String[]{"Chemistry HON", "5.5"});
        classDataReference.put("08723", new String[]{"Physics", "5.0"});
        classDataReference.put("08713", new String[]{"Physics HON", "5.5"});

        classDataReference.put("03703", new String[]{"World Geo", "5.0"});
        classDataReference.put("03713", new String[]{"World Geo HON", "5.5"});
        classDataReference.put("03303", new String[]{"World Hist", "5.0"});
        classDataReference.put("03313", new String[]{"World Hist HON", "5.5"});
        classDataReference.put("03203", new String[]{"US History", "5.0"});
    }

    Button finishButton;
    TextView dataDescText;
    ProgressBar dataRetrievalBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        //Deserialize and get all data, build a list of semesters, then pass it to the classmanager below.
        classManager = new ClassManager();
        context = getApplicationContext();


        finishButton = findViewById(R.id.DataTransitionButton);
        dataDescText = findViewById(R.id.DataDescription);
        dataRetrievalBar = findViewById(R.id.dataRetrievalCircle);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OverallViewActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_prefs_class_data_file_key), Context.MODE_PRIVATE);
        runGetDataTask(sharedPref.getString("username", "errorEncountered"), sharedPref.getString("password", "errorEncountered"));
        SharedPreferences.Editor editor = sharedPref.edit();
        if (!(sharedPref.contains("hasInitialSetupOccured"))) {
            editor.putBoolean("hasInitialSetupOccured", false);
        }
        editor.apply();

        if (!sharedPref.getBoolean("hasInitialSetupOccured", false)) {
            //do basic value setup, before user setup
        } else {
            //do assuming that data has been setup completely
            String classManagerJson = sharedPref.getString("classmanager", "");
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            classManager = gson.fromJson(classManagerJson, ClassManager.class);
        }
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.shared_prefs_class_data_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //Save data here.
        editor.putString("classmanager", classManager.toString());
        editor.apply();
    }




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

    public void runGetDataTask(String username, String password) {
        ArrayList<String> urls = new ArrayList<>();
        urls.add(homeUrl);
        urls.add(generateSignInURL("signIn", username, password));
        urls.add(generateSignInURL("cs", username, password));
        urls.add(generateSignInURL("rp", username, password));
        urls.add(userAgent);
        GetDataTask task = new GetDataTask();
        task.execute(urls);
        //new getDataTask().execute(homeUrl, generateSignInURL("signIn", username, password), generateSignInURL("cs", username, password), generateSignInURL("rp", username, password), userAgent);
    }


    private class GetDataTask extends AsyncTask<ArrayList<String>, Void, ArrayList<ArrayList<String>>> { //make not static somehow
        protected ArrayList<ArrayList<String>> doInBackground(ArrayList<String>... input) {
            ArrayList<String> urls = input[0];
            ArrayList<Document> pages = new ArrayList<>();
            pages = signInAndRetrievePages(urls.get(0), urls.get(1), urls.get(3), urls.get(2));

            ArrayList<ArrayList<String>> response = new ArrayList<>();
            response = parseDocuments(pages.get(0), pages.get(1));
            return response;
        }


        protected void onPostExecute(ArrayList<ArrayList<String>> input) {
            System.out.println("Running onPostExecute");
            if (input.get(0).isEmpty() || input.get(1).isEmpty()) {
                //one of the classDatas is empty, do not try to build class objects.
                System.out.println("ERROR: Some class data is missing, cannot proceed with format. Aborting.");
            } else {
                //continue as normal, format data into SchoolClass objects and hand it over to ClassManager
                try {
                    ArrayList<SchoolClass> classes;
                    classes = genClassesFromStrings(input);
                    //prune extra classes
                    classes = pruneDuplicateClasses(classes);
                    DataActivity.classManager.replaceClassData(classes);
                    finishButton.setVisibility(View.VISIBLE);
                    dataDescText.setText("Data retrieval complete.");
                    dataRetrievalBar.setVisibility(View.INVISIBLE);
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

        protected static ArrayList<SchoolClass> pruneDuplicateClasses(ArrayList<SchoolClass> input) {
            ArrayList<SchoolClass> output = input;

            //sort by class type
            ArrayList<SchoolClass> creditSummaryClasses = new ArrayList<>();
            ArrayList<SchoolClass> reportCardClasses = new ArrayList<>();
            for (SchoolClass classVal:output) {
                //if no teacher value, it is a credit summary class.
                if (Objects.equals(classVal.getTeacher(), "")) {
                    creditSummaryClasses.add(classVal);
                } else {
                    reportCardClasses.add(classVal);
                }
            }

            creditSummaryClasses.removeIf(classVal -> Objects.equals(classVal.getGrade().get("total"), ""));
            ArrayList<SchoolClass> toRemoveFromRPCard = new ArrayList<>();
            for (SchoolClass classVal:reportCardClasses) {
                HashMap<String, String> grades = classVal.getGrade();

                //if all grades are empty, remove.
                int emptyCounter = 0;
                for (String key : grades.keySet()) {
                    if (Objects.equals(grades.get(key), "empty")) {
                        emptyCounter++;
                    }
                }
                if (emptyCounter >= 4) {
                    //class is empty, remove.
                    toRemoveFromRPCard.add(classVal);
                }
            }
            //fix concurrentmodificationexception.
            for (SchoolClass classVal:toRemoveFromRPCard) {
                reportCardClasses.remove(classVal);
            }
            ArrayList<SchoolClass> finishedRPClasses = new ArrayList<>();
            for (SchoolClass classVal:reportCardClasses) {
                HashMap<String, String> grades = classVal.getGrade();
                grades.put("total", String.valueOf(determineTotalGradeFromGradeMap(grades)));

                if (Double.valueOf(grades.get("total")) != -1.0) {
                    //add class to new arrayList.
                    SchoolClass newClass = new SchoolClass(classVal.getClassName(), classVal.getTeacher(), classVal.getGpa(), classVal.getMaxGpa(), grades, classVal.getSemester(), classVal.getPeriod(), classVal.getYearTaken());
                    finishedRPClasses.add(newClass);
                }
            }
            output.clear();
            output.addAll(finishedRPClasses);
            output.addAll(creditSummaryClasses);
            return output;
        }

        public static double determineTotalGradeFromGradeMap(HashMap<String, String> grades) {
            String rp1 = grades.get("reportCard1");
            String rp2 = grades.get("reportCard2");
            String exam = grades.get("exam");
            String total = grades.get("total");
            double finalValue = -1;

            if (rp1 != null && rp2 != null && exam != null && total != null) {
                if (total.equals("empty")) {
                    //no final grade, predict it.
                    if (exam.equals("empty")) {
                        //no exam taken, average out report card values.
                        if (rp2.equals("empty")) {
                            grades.put("total", rp1);
                        } else {
                            //average report card grades.
                            if (rp1.matches("\\d*") && rp2.matches("\\d*")) {
                                double reportCard1 = Double.parseDouble(rp1);
                                double reportCard2 = Double.parseDouble(rp2);
                                finalValue = (reportCard1 + reportCard2) / 2.0;
                            }
                        }
                    } else {
                        //exam taken, if exempted average, else use math to calculate final.
                        if (exam.equals("EX")) {
                            if (rp1.matches("\\d*") && rp2.matches("\\d*")) {
                                double reportCard1 = Double.parseDouble(rp1);
                                double reportCard2 = Double.parseDouble(rp2);
                                finalValue = (reportCard1 + reportCard2) / 2.0;
                            }
                        } else {
                            if (rp1.matches("\\d*") && rp2.matches("\\d*")) {
                                //equation for final grade considering exam grade.
                                //each rp card is 3/7, exam is 1/7.
                                double reportCard1 = Double.parseDouble(rp1);
                                double reportCard2 = Double.parseDouble(rp2);
                                finalValue = (reportCard1 * (3 / 7.0)) + (reportCard2 * (3 / 7.0)) + (Double.parseDouble(exam) * (1 / 7.0));
                            }
                        }
                    }
                }
            }

            if (total.equals("empty")) {
                return finalValue;
            } else {
                return Double.parseDouble(total);
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

            for (String className:uniqueClassNames) { //for each class
                //for each class, split class Strings into two semesters.
                ArrayList<String> classStrings = getClassStringFromList(input, className);
                ArrayList<String> semester1Strings = new ArrayList<>();
                ArrayList<String> semester2Strings = new ArrayList<>();

                //split classStrings into two arrayLists
                for (String classString:classStrings) {
                    String semester;
                    if (classString.contains("=")) {
                        //report card
                        semester = determineRPSemester(classString);
                    } else {
                        //credit summary
                        semester = classString.split(",")[3].substring(1);
                    }
                    if (semester.equals("1")) {
                        semester1Strings.add(classString);
                    } else {
                        semester2Strings.add(classString);
                    }
                }
                //function to generate a class object from the semester arraylist
                if (!semester1Strings.isEmpty()) {
                    output.add(genClassFromSemesterString(semester1Strings, 1));
                }
                if (!semester2Strings.isEmpty()) {
                    output.add(genClassFromSemesterString(semester2Strings, 2));
                }
            }
            return output;
        }

        public static SchoolClass genClassFromSemesterString(ArrayList<String> classStringList, int semesterNum) {
            SchoolClass output;
            HashMap<String, String> grades = genGradesFromSemesterString(classStringList);
            //rp card: sectionIndex=DC14,gradeTypeIndex=1st Semester Exam,courseIndex=EDUC 1200,calendarIndex=1,gradeIndex=100,teacherIndex=Jones^ Hollie,dayCodeIndex=T - 8,locIndex=015
            //cs: 2021,08,DIGITAL MEDIA,S2,2,100,0.5,
            String teacher = "";
            String className = "";
            int yearTaken;
            float gpaMax = Float.parseFloat(predictGpaFromString(classStringList.get(0)));
            float currentGpa = 0;
            int period = -1;
            int semester = semesterNum;
            if (classStringList.get(0).contains("=")) {
                //rp card
                String[] stringComponents = classStringList.get(0).split(",");
                for (String section:stringComponents) {
                    String type = section.split("=")[0];
                    String value = section.split("=")[1];
                    switch (type) {
                        case "courseIndex": {
                            className = value;
                        }
                        case "teacherIndex": {
                            teacher = value;
                        }
                        case "dayCodeIndex": {
                            if (type.equals("dayCodeIndex")) {
                                period = -1;
                                period = Integer.parseInt("" + value.charAt(value.length()-1)); //results in error.
                            }
                        }
                    }
                }
                yearTaken = 2025; //replace with current year system.
            } else {
                //credit summary
                className = classStringList.get(0).split(",")[2];
                yearTaken = Integer.parseInt(classStringList.get(0).split(",")[0]);
            }
            output = new SchoolClass(className, teacher, currentGpa, gpaMax, grades, semester, period, yearTaken);
            return output;
        }

        public static HashMap<String, String> genGradesFromSemesterString(ArrayList<String> classStringList) {
            HashMap<String, String> grades = new HashMap<>();
            grades.put("reportCard1", "empty");
            grades.put("reportCard2", "empty");
            grades.put("exam", "empty");
            grades.put("total", "empty");
            if (classStringList.get(0).contains("=")) {
                //report card
                for (String classString:classStringList) {
                    String gradeType = classString.split(",")[1].split("=")[1];
                    String gradeValue;
                    if (classString.contains("gradeIndex")) {
                        //contains a grade value
                        gradeValue = classString.split(",")[4].split("=")[1];
                    } else {
                        //contains no grade value
                        gradeValue = "empty";
                    }
                    switch (gradeType) {
                        case "1st 9 Weeks":
                        case "3rd 9 Weeks": {
                            grades.replace("reportCard1", gradeValue);
                            break;
                        }
                        case "2nd 9 Weeks":
                        case "4th 9 Weeks": {
                            grades.replace("reportCard2", gradeValue);
                            break;
                        }
                        case "1st Semester Exam":
                        case "2nd Semester Exam": {
                            grades.replace("exam", gradeValue);
                            break;
                        }
                        case "1st Semester Avg":
                        case "2nd Summer Avg": {
                            grades.replace("total", gradeValue);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            } else {
                //credit summary 2023,10,ALGEBRA 2 HON,S1,1,100,0.5,
                String gradeValue = classStringList.get(0).split(",")[5];
                grades.replace("reportCard1", gradeValue);
                grades.replace("reportCard2", gradeValue);
                grades.replace("exam", gradeValue);
                grades.replace("total", gradeValue);
            }
            return grades;
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

        public static String predictGpaFromString(String classString) {
            String output = "5.0"; //default gpa for onlevel/regular classes
            HashMap<String, String> stringComponents = new HashMap<>();
            stringComponents.put("sectionIndex", "");
            stringComponents.put("courseIndex", "");
            stringComponents.put("teacherIndex", "");
            if (classString.contains("=")) {
                //rp card
                String[] classStringComponents = classString.split(",");
                for (String target : classStringComponents) {
                    if (target.contains("sectionIndex")) {
                        //for section index predictions.
                        String value = target.split("=")[1];
                        stringComponents.replace("sectionIndex", value);
                    }
                    if (target.contains("courseIndex")) {
                        String value = target.split("=")[1];
                        stringComponents.replace("courseIndex", value);

                    }
                    if (target.contains("teacherIndex")) {
                        String value = target.split("=")[1];
                        stringComponents.replace("courseIndex", value);
                    }
                }
            } else {
                //credit summary
                String[] classStringComponents = classString.split(",");
                String value = classStringComponents[2];
                stringComponents.replace("courseIndex", value);
            }

            //for section index prediction
            String sectionIndexValue = stringComponents.get("sectionIndex");
            if (sectionIndexValue.length() != 0 && (sectionIndexValue.contains("AP") || sectionIndexValue.contains("DC") || sectionIndexValue.contains("OR"))) {
                //College class, including onramps classes, even if semester 1 is not college credit level.
                output = "6.0";
            }
            if (sectionIndexValue.length() != 0 && sectionIndexValue.charAt(0) == 'h') {
                output = "5.5";
            }

            //for course name prediction
            String courseName = stringComponents.get("courseIndex");
            if (courseName.length() != 0) {
                //group of four numbers with a space in front, try regex. This indicates dual credit course
                if (courseName.contains("HON") || courseName.contains("PAP")) {
                    output = "5.5";
                }
                if (courseName.contains("OR") || courseName.contains("AP")) {
                    output = "6.0";
                }
                if (courseName.matches(".* \\d\\d\\d\\d")) {
                    output = "6.0";
                }
            }

            //for teacher name predictions
            String teacher = stringComponents.get("teacherIndex");
            if (teacher.length() != 0 && teacher.contains("College")) {
                output = "6.0";
            }

            //note: unable to detect dual credit courses in credit summary.
            return output;
        }
}