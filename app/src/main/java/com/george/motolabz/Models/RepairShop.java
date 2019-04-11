package com.brainyapps.motolabz.Models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HappyBear on 8/18/2018.
 */

public class RepairShop {
    public static String TABLE_NAME = "users";
    public Long createdAt = 0l;
    public String userID = "";
    public String userEmail = "";
    public String userType = "repairshop";
    public String photoUrl = "";
    public String fullName = "";
    public String phone = "";
    public String address = "";
    public Double hourlyRate = 0.0d;
    public Double latitude = 0.0d;
    public Double longitude = 0.0d;
    public Boolean available = true;
    public String description = "";
    public String licenseNumber = "";
    public Boolean isBan = false;
    public Map<String, Object> businessTime = new HashMap<>();
    public Map<String, Object> services = new HashMap<>();
    public Map<String, Object> serviceModels = new HashMap<>();

    public RepairShop(){
        createdAt = 0l;
        userID = "";
        userEmail = "";
        userType = "repairshop";
        photoUrl = "";
        phone = "";
        fullName = "";
        description = "";
        address = "";
        hourlyRate = 0.0d;
        latitude = 0.0d;
        longitude = 0.0d;
        available = true;
        licenseNumber = "";
        isBan = false;
        businessTime = new HashMap<>();
        services = new HashMap<>();
        serviceModels = new HashMap<>();
    }
}
