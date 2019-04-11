package com.brainyapps.motolabz.Models;

/**
 * Created by HappyBear on 8/30/2018.
 */

public class Channel {
    public static final String TABLE_NAME = "messages";
    public String userID = "";
    public String lastMsg = "";
    public Long time = 0L;
    public Boolean isRead = false;

    public Channel(){
        userID = "";
        lastMsg = "";
        time = 0L;
        isRead = false;
    }
}
