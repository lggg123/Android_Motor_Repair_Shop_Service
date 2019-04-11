package com.brainyapps.motolabz;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Utils.PrefUtils;
import com.brainyapps.motolabz.Utils.VehicleInfoManager;
import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.FirebaseApp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FirebaseManager.init(this);
        VehicleInfoManager.init(this);
        MapsInitializer.initialize(this);
        PrefUtils.init(this);

        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);

//        PrefUtils.getInstance().getSearchModel(PrefUtils.PREF_SEARCH_MODEL,"");
//        PrefUtils.getInstance().getSearchService(PrefUtils.PREF_SEARCH_SERVICE,"");

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                boolean isTutorial = PrefUtils.getInstance().getBoolean(PrefUtils.PREF_TUTORIAL_ON, true);
                if(isTutorial){
                    Intent onboardIntent = new Intent(SplashActivity.this, OnboardingActivity.class);
                    SplashActivity.this.startActivity(onboardIntent);
                    SplashActivity.this.finish();
                }else {
                    Intent signinIntent = new Intent(SplashActivity.this, SigninActivity.class);
                    SplashActivity.this.startActivity(signinIntent);
                    SplashActivity.this.finish();
                }
            }
        }, SPLASH_TIME_OUT);
        Log.e("Key result=", printKeyHash(this));
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
