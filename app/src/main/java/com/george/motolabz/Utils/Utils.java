package com.brainyapps.motolabz.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.brainyapps.motolabz.Views.AlertFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by HappyBear on 8/12/2018.
 */

public class Utils {
    public static Date ServerTime;
    public static double ServerOffset = 0.0;

    public static final int PERMISSIONS_REQUEST_LOCATION = 9007;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkLocationPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {

            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidEmail(String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }

    public static boolean containsCharacter(String string) {
        return string.matches(".*[a-zA-Z].*");
    }

    public static boolean isDouble(String string){
        return string.matches("[0-9]+(.){0,1}[0-9]*");
    }
    public static boolean isInterger(String string){
        return string.matches("[0-9]*");
    }

    public static boolean containsNumber(String string) {
        return string.matches(".*\\d.*");
    }

    public static boolean overLength(String string) {
        if(string.length() >= 6){
            return true;
        }else {
            return false;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean checkConnection(Context context) {
        boolean networkStatus = isNetworkAvailable(context);
        if (!networkStatus) {
            AlertFactory.showAlert(context, "Warning",
                    "Oops! Please connect to the internet and try again.");
        }
        return networkStatus;
    }

    public static String converteTimestamp(long mileSegundos) {
        double estimatedServerTimeMs = System.currentTimeMillis() + Utils.ServerOffset;
        ServerTime = new Date((long)estimatedServerTimeMs);
        long period = ServerTime.getTime() - mileSegundos;
        long value = TimeUnit.MINUTES.convert(period, TimeUnit.MILLISECONDS);
        if (value == 0) {
            return "Just Now";
        } else if (value < 60) {
            return String.valueOf(value) + " mins ago";
        } else if (value == 60) {
            return "1 hour ago";
        } else if (value < 120) {
            return "1 hour " + String.valueOf(value - 60) + " mins ago";
        } else if (value < 720) {
            return "" + String.valueOf(value / 60) + " hours ago";
        } else if (value < 1440) {
            return "Today " + new SimpleDateFormat("HH:mm").format(new Date(mileSegundos));
        } else if (value < 2880) {
            return "Yesterday " + new SimpleDateFormat("HH:mm").format(new Date(mileSegundos));
        }
        return new SimpleDateFormat("MM/dd, yyyy").format(new Date(mileSegundos));
    }

    public static String converteTimestamp(String mileSegundos) {
        double estimatedServerTimeMs = System.currentTimeMillis() + Utils.ServerOffset;
        ServerTime = new Date((long)estimatedServerTimeMs);
        long time = Long.parseLong(mileSegundos);

        long period = ServerTime.getTime() - time;
        long value = TimeUnit.MINUTES.convert(period, TimeUnit.MILLISECONDS);
        if (value == 0) {
            return "Just Now";
        } else if (value < 60) {
            return String.valueOf(value) + " mins ago";
        } else if (value == 60) {
            return "1 hour ago";
        } else if (value < 120) {
            return "1 hour " + (value - 60) + " mins ago";
        } else if (value < 720) {
            return "" + String.valueOf(value / 60) + " hours ago";
        } else if (value < 1440) {
            return "Today " + new SimpleDateFormat("HH:mm").format(new Date(time));
        } else if (value < 2880) {
            return "Yesterday " + new SimpleDateFormat("HH:mm").format(new Date(time));
        }
        return new SimpleDateFormat("MM/dd, yyyy").format(new Date(time));
    }

    public static String getDistance(Location location1, Location location2){
        return (new DecimalFormat("##.##").format(location1.distanceTo(location2)/1609));
    }

    public static int getScreenWidth(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        return width;
    }
}
