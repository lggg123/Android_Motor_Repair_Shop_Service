package com.brainyapps.motolabz.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.brainyapps.motolabz.Models.VehicleInfo;

/**
 * Created by HappyBear on 12/10/2018.
 */

public class PrefUtils {
    public static final String PREF_TUTORIAL_ON = "pref_tutorial_on";

    public static final String PREF_SEARCH_MODEL = "pref_search_model";
    public static final String PREF_SEARCH_SERVICE = "pref_search_service";
    public static final String PREF_SEARCH_DESCRIPTION = "pref_search_description";

    private static PrefUtils instance;

    private SharedPreferences prefs;

    private PrefUtils(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PrefUtils getInstance() {
        return instance;
    }

    public static void init(Context context) {
        instance = new PrefUtils(context);
    }

    public void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public boolean getTrue(){
        return true;
    }

    public void setSearchModel(String key, String search_model){
        prefs.edit().putString(key, search_model).commit();
    }
    public String getSearchModel(String key, String defaultValue){
        return prefs.getString(key, defaultValue);
    }

    public void setSearchService(String key, String search_service){
        prefs.edit().putString(key, search_service).commit();
    }
    public String getSearchService(String key, String defaultValue){
        return prefs.getString(key, defaultValue);
    }

    public void setDescService(String key, String search_desc){
        prefs.edit().putString(key, search_desc).commit();
    }
    public String getDescService(String key, String defaultValue){
        return prefs.getString(key, defaultValue);
    }
}
