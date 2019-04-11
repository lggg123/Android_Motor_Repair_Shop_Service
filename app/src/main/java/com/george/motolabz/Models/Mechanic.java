package com.brainyapps.motolabz.Models;

/**
 * Created by HappyBear on 8/18/2018.
 */

public class Mechanic {
    public static final String TABLE_NAME = "users";
    public Long createdAt = 0l;
    public String userID = "";
    public String shopID = "";
    public String userEmail = "";
    public String userType = "mechanic";
    public String photoUrl = "";
    public String phone = "";
    public String licenseNumber = "";
    public String fullName = "";
    public String shopName = "";
    public String description = "";
    public String signupCode = "";
    public Integer rate = 0;
    public Integer rateCount = 0;
    public Double latitude = 0.0d;
    public Double longitude = 0.0d;
    public Boolean available = true;
    public Boolean isBan = false;
    public Boolean locationservice = false;

    public Mechanic(){
        createdAt = 0l;
        userID = "";
        shopID = "";
        userEmail = "";
        userType = "mechanic";
        photoUrl = "";
        phone = "";
        licenseNumber = "";
        fullName = "";
        shopName = "";
        description = "";
        signupCode = "";
        rate = 0;
        rateCount = 0;
        latitude = 0.0d;
        longitude = 0.0d;
        available = true;
        isBan = false;
        locationservice = false;
    }
}
