package com.brainyapps.motolabz.Models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HappyBear on 9/1/2018.
 */

public class TaskStatus {
    private final static String TABLE_NAME = "tasks";
    public String customerID = "";
    public String key = "";
    public String mechanicID = "";
    public String shopID = "";
    public String status = "pending";
    public Long time = 0L;
    public Map<String, Object> services = new HashMap<>();

    public TaskStatus(){
        customerID = "";
        key = "";
        mechanicID = "";
        shopID = "";
        status = "pending";
        time = 0L;
        services = new HashMap<>();
    }
}
