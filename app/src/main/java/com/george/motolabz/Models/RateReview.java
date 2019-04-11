package com.brainyapps.motolabz.Models;

/**
 * Created by HappyBear on 8/31/2018.
 */

public class RateReview {
    public final static String TABLE_NAME = "rates";
    public int rate = 0;
    public String rateContent = "";
    public Long time = 0L;
    public String userID = "";

    public RateReview(){
        rate = 0;
        rateContent = "";
        time = 0L;
        userID = "";
    }
}
