package com.brainyapps.motolabz.Models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HappyBear on 8/18/2018.
 */

public class Driver {
    public static final String TABLE_NAME = "users";
    public Long createdAt = 0l;
    public String userID = "";
    public String userEmail = "";
    public String userType = "customer";
    public String fullName = "";
    public String photoUrl = "";
    public String phone = "";
    public Map<String, VehicleInfo> vehicleInfo = new HashMap<>();
    public Double latitude = 0.0d;
    public Double longitude = 0.0d;
    public Boolean isBan = false;
    public Boolean locationservice = false;

    public Driver(){
        createdAt = 0l;
        userID = "";
        userEmail = "";
        userType = "customer";
        fullName = "";
        photoUrl = "";
        phone = "";
        vehicleInfo = new HashMap<>();
        latitude = 0.0d;
        longitude = 0.0d;
        isBan = false;
        locationservice = false;
    }
}
