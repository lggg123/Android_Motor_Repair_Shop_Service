package com.brainyapps.motolabz.Utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.brainyapps.motolabz.Models.SignupVehicle;
import com.brainyapps.motolabz.Models.VehicleInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HappyBear on 1/4/2019.
 */

public class VehicleInfoManager {
    private static VehicleInfoManager instance;

    public Context mContext;
    public Map<String, Bitmap> vehicleImgList;
    public Map<String, SignupVehicle> vehicleInfoList;

    private VehicleInfoManager(Context context) {
        mContext = context;
        vehicleImgList = new HashMap<>();
        vehicleInfoList = new HashMap<>();
    }

    public static void init(Context context) {
        instance = new VehicleInfoManager(context);
    }

    public static VehicleInfoManager getInstance() {
        return instance;
    }

    public void clear() {
        vehicleImgList = new HashMap<>();
        vehicleInfoList = new HashMap<>();
    }

    public void addVehicleInfo(String key, SignupVehicle signupVehicle){
        vehicleInfoList.put(key, signupVehicle);
    }
    public void removeVehicleInfo(String key){
        vehicleInfoList.remove(key);
    }

    public Map<String, SignupVehicle> getVehicleInfoList(){
        return vehicleInfoList;
    }

    public void addVehicleImg(String key, Bitmap img){
        vehicleImgList.put(key, img);
    }

    public void removeVehicleImg(String key){
        vehicleImgList.remove(key);
    }

    public Map<String, Bitmap> getVehicleImgList(){
        return vehicleImgList;
    }
}
