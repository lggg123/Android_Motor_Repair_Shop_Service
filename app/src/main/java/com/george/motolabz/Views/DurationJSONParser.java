package com.brainyapps.motolabz.Views;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HappyBear on 9/2/2018.
 */

public class DurationJSONParser {
    public String parse(JSONObject jObject){

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        String duration = "";
        try {

            jRoutes = jObject.getJSONArray("routes");
            jLegs = ((JSONObject)jRoutes.get(0)).getJSONArray("legs");
            duration = (String)((JSONObject)((JSONObject)jLegs.get(0)).get("duration")).get("text");

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return duration;
    }
}
