package com.brainyapps.motolabz.Models;

/**
 * Created by HappyBear on 8/30/2018.
 */


public class Notification {
    public static final String TABLE_NAME = "notifications";
    public String notificationID = "";
    public String postKey = "";
    public Boolean read = false;
    public String receiverID = "";
    public String senderID = "";
    public Long time = 0L;
    public int type = 0;

    public Notification(){
        notificationID = "";
        postKey = "";
        read = false;
        receiverID = "";
        senderID = "";
        time = 0L;
        type = 0;
    }
}
