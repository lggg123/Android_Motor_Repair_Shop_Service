package com.brainyapps.motolabz.Models;

/**
 * Created by HappyBear on 8/31/2018.
 */

public class Report {
    final static String TABLE_NAME = "userReports";
    public String reporterID = "";
    public String reason = "";
    public String userID = "";
    public Long time = 0L;
    public String userReportID = "";

    public Report(){
        reporterID = "";
        reason = "";
        userID = "";
        time = 0L;
        userReportID = "";
    }
}
