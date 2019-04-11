package com.brainyapps.motolabz.Utils;

/**
 * Created by HappyBear on 12/25/2018.
 */

public class ApiUtils {
    private ApiUtils() {}
    public static final String BASE_URL = "https://app.motolabz.com/";

    public static ApiInterface getAPIService() {

        return ApiClient.getClient(BASE_URL).create(ApiInterface.class);
    }
}
