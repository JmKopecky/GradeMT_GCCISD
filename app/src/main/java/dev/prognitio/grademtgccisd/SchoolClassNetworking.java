package dev.prognitio.grademtgccisd;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.Map;

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



    private static class getDataTask extends AsyncTask<String, Void, Document[]> {
        protected Document[] doInBackground(String... url) {
            //declare all connections
            Connection.Response initialConnection;
            Connection.Response signInConnection;
            Connection.Response reportCardConnection;
            Connection.Response creditClassesConnection;
            Map<String, String> cookies; //prepare cookie map for session data
            Document[] response = new Document[2]; //prepare result output
            try {
                //get initial connection to login page
                initialConnection = Jsoup.connect(url[0]).method(Connection.Method.GET).userAgent(url[4]).execute();
                cookies = initialConnection.cookies(); //get cookies (including session id)
                //sign in, then access the report card and credit pages.
                signInConnection = Jsoup.connect(url[1]).method(Connection.Method.GET).userAgent(url[4]).cookies(cookies).execute();
                reportCardConnection = Jsoup.connect(url[2]).method(Connection.Method.GET).userAgent(url[4]).cookies(cookies).execute();
                creditClassesConnection = Jsoup.connect(url[3]).method(Connection.Method.GET).userAgent(url[4]).cookies(cookies).execute();
                /*
                Output the status codes for the report card and credit pages
                System.out.println(reportCardConnection.statusMessage());
                System.out.println(creditClassesConnection.statusMessage());
                 */
                //output page html data
                response[0] = reportCardConnection.parse();
                response[1] = creditClassesConnection.parse();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return response;
        }

        protected void onPostExecute(Document[] result) {
            //do with the data

        }
    }
}
