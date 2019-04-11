package com.brainyapps.motolabz;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.DriversView.DriverFilterSearchActivity;
import com.brainyapps.motolabz.Fragments.SignupAddVehicleFragment;
import com.brainyapps.motolabz.Fragments.SignupEmailFragment;
import com.brainyapps.motolabz.Fragments.SignupInfoFragment;
import com.brainyapps.motolabz.Fragments.SignupMechanicInfoFragment;
import com.brainyapps.motolabz.Fragments.SignupSelectUserTypeFragment;
import com.brainyapps.motolabz.Fragments.SignupShopInfoFragment;
import com.brainyapps.motolabz.MechanicsView.MechanicMainActivity;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.Models.SignupVehicle;
import com.brainyapps.motolabz.Models.UserAuthInfo;
import com.brainyapps.motolabz.Models.VehicleInfo;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Utils.VehicleInfoManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.walnutlabs.android.ProgressHUD;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements SignupSelectUserTypeFragment.OnSignupTypeListener, SignupEmailFragment.OnSignupEmailListener, SignupInfoFragment.OnSignupInfoListener, SignupMechanicInfoFragment.OnSignupMechanicInfoListener, SignupShopInfoFragment.OnSignupShopInfoListener, SignupAddVehicleFragment.OnSignupAddVehicleListener{
    private static final int FRAGMENT_SIGNUPTYPE_TAG = 0;
    private static final int FRAGMENT_SIGNUPEMAIL_TAG = 1;
    private static final int FRAGMENT_SIGNUPUSER_TAG = 2;
    private static final int FRAGMENT_SIGNUPMECHANCIC_TAG = 3;
    private static final int FRAGMENT_SIGNUPSHOP_TAG = 4;
    private static final int FRAGMENT_SIGNUP_ADD_VEHICLE_TAG = 5;

    private int currentPosition = 0;

    private Map<String, Fragment> mFragmentMap;
    private StorageReference storePhoto;
    private Fragment mFragment;
    private FirebaseAuth auth;

    private String userType = "";
    private String signupType = "email";
    private String mEmail = "";
    private String mPassword = "";

    private String avatar_url = "";
    private String social_userId = "";
    private String social_avatar_url = "";

    private int count = 0;
    private int uploaded_count = 0;
    Bundle bundle = new Bundle();

    public Map<String, String> vehicleUrlList = new HashMap<>();

    private ProgressHUD mProgressDialog;

    private void showProgressHUD(String text) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = ProgressHUD.show(this, text, true);
        mProgressDialog.show();
    }

    private void hideProgressHUD() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        bundle = getIntent().getExtras();
        if(bundle!=null){
            signupType = "socialMedia";
            UserAuthInfo info = bundle.getParcelable("userAuthInfo");
            social_userId = info.userId;
            social_avatar_url = info.photoUrl;
        }
        mFragmentMap = new HashMap<>();
        mFragmentMap.put(SignupSelectUserTypeFragment.FRAGMENT_TAG, new SignupSelectUserTypeFragment().newInstance(this));
        mFragmentMap.put(SignupEmailFragment.FRAGMENT_TAG, new SignupEmailFragment().newInstance(this));
        mFragmentMap.put(SignupInfoFragment.FRAGMENT_TAG, new SignupInfoFragment().newInstance(this));
        mFragmentMap.put(SignupAddVehicleFragment.FRAGMENT_TAG, new SignupAddVehicleFragment().newInstance(this));
        mFragmentMap.put(SignupMechanicInfoFragment.FRAGMENT_TAG, new SignupMechanicInfoFragment().newInstance(this));
        mFragmentMap.put(SignupShopInfoFragment.FRAGMENT_TAG, new SignupShopInfoFragment().newInstance(this));

        showFragment(FRAGMENT_SIGNUPTYPE_TAG, true);
    }
    public void showFragment(int position, Boolean isPushed) {
        mFragment = null;

        currentPosition = position;

        switch (position) {
            case FRAGMENT_SIGNUPTYPE_TAG:
                mFragment = mFragmentMap.get(SignupSelectUserTypeFragment.FRAGMENT_TAG);

                ((SignupSelectUserTypeFragment)mFragment).setOnSignupTypeListener(this);
                break;
            case FRAGMENT_SIGNUPEMAIL_TAG:
                mFragment = mFragmentMap.get(SignupEmailFragment.FRAGMENT_TAG);

                ((SignupEmailFragment)mFragment).setOnSignupEmailListener(this);
                break;
            case FRAGMENT_SIGNUPUSER_TAG:
                mFragment = mFragmentMap.get(SignupInfoFragment.FRAGMENT_TAG);

                ((SignupInfoFragment)mFragment).setOnSignupInfoListener(this);
                break;
            case FRAGMENT_SIGNUPMECHANCIC_TAG:
                mFragment = mFragmentMap.get(SignupMechanicInfoFragment.FRAGMENT_TAG);

                ((SignupMechanicInfoFragment)mFragment).setOnSignupMechanicInfoListener(this);
                break;
            case FRAGMENT_SIGNUPSHOP_TAG:
                mFragment = mFragmentMap.get(SignupShopInfoFragment.FRAGMENT_TAG);

                ((SignupShopInfoFragment)mFragment).setOnSignupShopInfoListener(this);
                break;
            case FRAGMENT_SIGNUP_ADD_VEHICLE_TAG:
                mFragment = mFragmentMap.get(SignupAddVehicleFragment.FRAGMENT_TAG);

                ((SignupAddVehicleFragment)mFragment).setOnSignupAddVehicleListener(this);
            default:
                break;
        }

        if (mFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.signup_fragment, mFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentPosition == 0) {
            setResult(Activity.RESULT_CANCELED);
            super.onBackPressed();
        } else {
            if (currentPosition == (FRAGMENT_SIGNUPSHOP_TAG + 1)) {
                setResult(Activity.RESULT_CANCELED);
            } else {
                if (currentPosition == 4 || currentPosition == 5){
                    currentPosition = 2;
                }else if(currentPosition == 6){
                    currentPosition = 3;
                }
                else {
                    currentPosition--;
                }
                showFragment(currentPosition, false);
            }
        }
    }


    @Override
    public void onNexttoInfo(String email, String password) {
        mEmail = email;
        mPassword = password;
        if(userType.equals("customer")){
            showFragment(FRAGMENT_SIGNUPUSER_TAG,true);
        }else if(userType.equals("mechanic")){
            showFragment(FRAGMENT_SIGNUPMECHANCIC_TAG,true);
        }else {
            showFragment(FRAGMENT_SIGNUPSHOP_TAG,true);
        }
    }

    @Override
    public void onBackSignupEmail() {
        ((SignupEmailFragment)mFragment).initialize();
        onBackPressed();
    }

    @Override
    public void onSignupDone(final String user_name, final String phone, final Bitmap avatar, final Boolean isLocation) {
        if(signupType.equals("email")){
            showProgressHUD("");
            auth.getInstance().createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                hideProgressHUD();
                            } else {
                                final FirebaseUser user = task.getResult().getUser();
                                if (user != null) {
                                    storePhoto = FirebaseStorage.getInstance().getReference();
                                    uploadDriverImage(user.getUid(), mEmail, user_name, phone, avatar, isLocation);
                                }
                            }
                        }
                    });
        }else {
            storePhoto = FirebaseStorage.getInstance().getReference();
            uploadDriverImage(social_userId, mEmail, user_name, phone, avatar, isLocation);
        }
    }

    @Override
    public void onGotoSignupAddVehicle(String key) {
        Bundle send_key = new Bundle();
        send_key.putString("key", key);
        mFragment = mFragmentMap.get(SignupAddVehicleFragment.FRAGMENT_TAG);
        ((SignupAddVehicleFragment)mFragment).setArguments(send_key);
        showFragment(FRAGMENT_SIGNUP_ADD_VEHICLE_TAG, true);
    }

    @Override
    public void onSignupAddVehicle() {
        ((SignupAddVehicleFragment)mFragment).initialize();
        showFragment(FRAGMENT_SIGNUPUSER_TAG, true);
    }

    @Override
    public void onBacktoSignupInfo() {
        showFragment(FRAGMENT_SIGNUPUSER_TAG, true);
    }

    public void uploadDriverImage(final String userId, final String email, final String user_name, final String phone, final Bitmap avatar, final Boolean isLocation){
        showProgressHUD("");
        if(avatar!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            avatar.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            Long tsLong = System.currentTimeMillis();
            StorageReference filepath = storePhoto.child("avatars").child(userId).child(tsLong + ".jpg");
            filepath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    avatar_url = downloadUri.toString();
                    uploadUserVehicles(userId, email, user_name, phone, isLocation);
                }
            });
        }else {
            uploadUserVehicles(userId, email, user_name, phone, isLocation);
        }
    }

    public void uploadUserVehicles(final String userId, final String email, final String user_name, final String phone, final Boolean isLocation){
        if(VehicleInfoManager.getInstance().vehicleImgList.size() > 0){
            for(final Map.Entry<String, Bitmap> entry : VehicleInfoManager.getInstance().getVehicleImgList().entrySet()){
                if(entry.getValue() != null){
                    count = count + 1;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    entry.getValue().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] data = stream.toByteArray();
                    Long tsLong = System.currentTimeMillis();
                    StorageReference filepath = storePhoto.child("vehicles").child(userId).child(tsLong + ".jpg");
                    filepath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploaded_count = uploaded_count + 1;
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            vehicleUrlList.put(entry.getKey(), downloadUri.toString());
                            if(count == uploaded_count){
                                setDriverInfo(userId, email, user_name, phone, isLocation);
                            }
                        }
                    });
                }else {
                    vehicleUrlList.put(entry.getKey(), "");
                    if(count == uploaded_count){
                        setDriverInfo(userId, email, user_name, phone, isLocation);
                    }
                }
            }
        }else {
            setDriverInfo(userId, email, user_name, phone, isLocation);
        }
    }

    public void setDriverInfo(String userId, String email, String user_name, String phone, Boolean isLocation){
        Map<String, VehicleInfo> vehicleInfoMap = new HashMap<>();
        if(VehicleInfoManager.getInstance().getVehicleInfoList().size() > 0){
            for(final Map.Entry<String, SignupVehicle> entry : VehicleInfoManager.getInstance().getVehicleInfoList().entrySet()){
                SignupVehicle signup_info = entry.getValue();
                VehicleInfo info = new VehicleInfo();
                info.vehicleImageUrl = vehicleUrlList.get(entry.getKey());
                info.key = entry.getKey();
                info.model = signup_info.model;
                info.engine = signup_info.engine;
                info.year = signup_info.year;
                info.manufacturer = signup_info.manufacturer;
                info.transmission = signup_info.transmission;
                info.vin = signup_info.vin;
                vehicleInfoMap.put(entry.getKey(),info);
            }
        }
        Driver driver = new Driver();
        driver.userID = userId;
        driver.userEmail = email;
        driver.createdAt = System.currentTimeMillis();
        driver.fullName = user_name;
        driver.phone = phone;
        driver.vehicleInfo = vehicleInfoMap;
        if(!signupType.equals("email")){
            if(avatar_url.equals("")){
                driver.photoUrl = social_avatar_url;
            }else {
                driver.photoUrl = avatar_url;
            }
        }else {
            driver.photoUrl = avatar_url;
        }
        driver.locationservice = isLocation;
        FirebaseManager.getInstance().setUserType("customer");

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId, driver);
        userUpdates.put("/" + DBInfo.TBL_EMAIL + "/" + userId + "/" + DBInfo.TBL_EMAIL, mEmail);
        FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
                startActivity(new Intent(SignupActivity.this, TermandConditionsActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackSignupInfo() {
        if(signupType.equals("email")){
            ((SignupInfoFragment)mFragment).initialize();
            VehicleInfoManager.getInstance().clear();
            showFragment(FRAGMENT_SIGNUPEMAIL_TAG, true);
        }else {
            ((SignupInfoFragment)mFragment).initialize();
            VehicleInfoManager.getInstance().clear();
            showFragment(FRAGMENT_SIGNUPTYPE_TAG, true);
        }
    }

    @Override
    public void onNexttoEmail(String type) {
        userType = type;
        if(signupType.equals("email")){
            showFragment(FRAGMENT_SIGNUPEMAIL_TAG, true);
        }else {
            if(userType.equals("customer")){
                mFragment = mFragmentMap.get(SignupInfoFragment.FRAGMENT_TAG);
                ((SignupInfoFragment)mFragment).setArguments(bundle);
                showFragment(FRAGMENT_SIGNUPUSER_TAG, true);
            }else if(userType.equals("mechanic")){
                mFragment = mFragmentMap.get(SignupMechanicInfoFragment.FRAGMENT_TAG);
                ((SignupMechanicInfoFragment)mFragment).setArguments(bundle);
                showFragment(FRAGMENT_SIGNUPMECHANCIC_TAG, true);
            }else if(userType.equals("repairshop")){
                mFragment = mFragmentMap.get(SignupShopInfoFragment.FRAGMENT_TAG);
                ((SignupShopInfoFragment)mFragment).setArguments(bundle);
                showFragment(FRAGMENT_SIGNUPSHOP_TAG, true);
            }
        }
    }

    @Override
    public void onBackSignupType() {
        onBackPressed();
    }

    @Override
    public void onSignupShopDone(final String shop_name, final String shop_phone, final String shop_license, final String hourly_rate, final String offsite_rate, final String inshop_rate, final String shop_brief, final Bitmap avatar, final String address, final Double lat, final  Double lng, final HashMap time_map) {
        if(signupType.equals("email")){
            showProgressHUD("");
            auth.getInstance().createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                hideProgressHUD();
                                Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                final FirebaseUser user = task.getResult().getUser();
                                if (user != null) {
                                    hideProgressHUD();
                                    storePhoto = FirebaseStorage.getInstance().getReference();
                                    uploadShopAvatar(user.getUid(), mEmail, shop_name, shop_phone, shop_license, hourly_rate, offsite_rate, inshop_rate, shop_brief, avatar, address, lat, lng, time_map);
                                }
                            }
                        }
                    });
        }else {
            storePhoto = FirebaseStorage.getInstance().getReference();
            uploadShopAvatar(social_userId, mEmail, shop_name, shop_phone, shop_license, hourly_rate, offsite_rate, inshop_rate, shop_brief, avatar, address, lat, lng, time_map);
        }
    }

    public void uploadShopAvatar(final String userId, final String email, final String shop_name, final String shop_phone, final String shop_license, final String hourly_rate, final String offsite_rate, final String inshop_rate, final String shop_brief, Bitmap avatar, final String address, final Double lat, final Double lng, final HashMap time_map){
        showProgressHUD("");
        if(avatar!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            avatar.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            Long tsLong = System.currentTimeMillis();
            StorageReference filepath = storePhoto.child("avatars").child(userId).child(tsLong+".jpg");
            filepath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    avatar_url = downloadUri.toString();
                    setShopInfo(userId, email,shop_name,shop_phone, shop_license, hourly_rate, offsite_rate, inshop_rate, shop_brief,address,lat, lng, time_map);
                }
            });
        }else {
            setShopInfo(userId, mEmail, shop_name, shop_phone, shop_license, hourly_rate, offsite_rate, inshop_rate, shop_brief,address,lat,lng, time_map);
        }
    }

    public void setShopInfo(String userId, String email, String shop_name, String shop_phone, String shop_license, String hourly_rate, String offsite_rate, String inshop_rate, String shop_brief, String address, Double lat, Double lng, HashMap time_map){
        final Map<String, Object> shop_services = new HashMap<>();
        Map<String, Object> offsite_detail = new HashMap<>();
        offsite_detail.put("rate", Double.parseDouble(offsite_rate));
        offsite_detail.put("serviceName", "Offsite Diagnosis");

        Map<String, Object> inshop_detail = new HashMap<>();
        inshop_detail.put("rate", Double.parseDouble(inshop_rate));
        inshop_detail.put("serviceName", "In-Shop Diagnosis");

        shop_services.put("Offsite Diagnosis", offsite_detail);
        shop_services.put("In-Shop Diagnosis", inshop_detail);


        final RepairShop repair_shop =  new RepairShop();
        repair_shop.userID = userId;
        repair_shop.userEmail = email;
        if(!signupType.equals("email")){
            if(avatar_url.equals("")){
                repair_shop.photoUrl = social_avatar_url;
            }else {
                repair_shop.photoUrl = avatar_url;
            }
        }else {
            repair_shop.photoUrl = avatar_url;
        }
        repair_shop.createdAt = System.currentTimeMillis();
        repair_shop.fullName = shop_name;
        repair_shop.phone = shop_phone;
        repair_shop.licenseNumber = shop_license;
        repair_shop.description = shop_brief;
        repair_shop.address =address;
        repair_shop.hourlyRate = Double.parseDouble(hourly_rate);
        repair_shop.latitude = lat;
        repair_shop.longitude = lng;
        repair_shop.available = true;
        repair_shop.services = shop_services;
        repair_shop.businessTime = time_map;
        FirebaseManager.getInstance().setUserType("repairshop");

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId, repair_shop);
        userUpdates.put("/" + DBInfo.TBL_EMAIL + "/" + userId + "/" + DBInfo.TBL_EMAIL, mEmail);
        FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
                FirebaseManager.getInstance().setRepairShop(repair_shop);
                startActivity(new Intent(SignupActivity.this, TermandConditionsActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackSignupShopInfo() {
        if(signupType.equals("email")){
            ((SignupShopInfoFragment)mFragment).initialize();
            showFragment(FRAGMENT_SIGNUPEMAIL_TAG, true);
        }else {
            ((SignupShopInfoFragment)mFragment).initialize();
            showFragment(FRAGMENT_SIGNUPTYPE_TAG, true);
        }
    }

    @Override
    public void onSignupMechanicDone(final String name, final String phone, final String license, final String brief, final Bitmap avatar, final Boolean isLocationValid, final String shopId, final String shopName, final String signupCode) {
        if(signupType.equals("email")){
            showProgressHUD("");
            auth.getInstance().createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                hideProgressHUD();
                                Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                final FirebaseUser user = task.getResult().getUser();
                                if (user != null) {
                                    hideProgressHUD();
                                    storePhoto = FirebaseStorage.getInstance().getReference();
                                    uploadMechanicAvatar(user.getUid(), mEmail, name, phone, license, brief, avatar, isLocationValid, shopId, shopName, signupCode);
                                }
                            }
                        }
                    });
        }else {
            storePhoto = FirebaseStorage.getInstance().getReference();
            uploadMechanicAvatar(social_userId, mEmail, name, phone, license, brief, avatar, isLocationValid, shopId, shopName, signupCode);
        }
    }

    public void uploadMechanicAvatar(final String userId, final String email, final String name, final String phone, final String license, final String brief, Bitmap avatar, final Boolean isLocation, final String shopId, final String shopName, final String signupCode){
        showProgressHUD("");
        if(avatar!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            avatar.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            Long tsLong = System.currentTimeMillis();
            StorageReference filepath = storePhoto.child("avatars").child(userId).child(tsLong+".jpg");
            filepath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    avatar_url = downloadUri.toString();
                    setMechanicInfo(userId, email, name, phone, license, brief, isLocation, shopId, shopName, signupCode);
                }
            });
        }else {
            setMechanicInfo(userId, email, name, phone, license, brief, isLocation, shopId, shopName, signupCode);
        }
    }

    public void setMechanicInfo(String userId, String email, String name, String phone, String license, String brief, Boolean isLocation, String shopId, String shop_name, String signup_code){
        final Mechanic mechanic = new Mechanic();
        mechanic.createdAt = System.currentTimeMillis();
        mechanic.userID = userId;
        mechanic.userEmail = email;
        mechanic.fullName = name;
        mechanic.phone = phone;
        mechanic.licenseNumber = license;
        mechanic.description = brief;
        mechanic.available = true;
        mechanic.locationservice = isLocation;
        mechanic.shopID = shopId;
        mechanic.shopName = shop_name;
        mechanic.signupCode = signup_code;
        if(!signupType.equals("email")){
            if(avatar_url.equals("")){
                mechanic.photoUrl = social_avatar_url;
            }else {
                mechanic.photoUrl = avatar_url;
            }
        }else {
            mechanic.photoUrl = avatar_url;
        }
        FirebaseManager.getInstance().setUserType("mechanic");

        if(!shopId.isEmpty()){
            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(shopId).child("mechanics").child(userId).setValue(true);
        }
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId, mechanic);
        userUpdates.put("/" + DBInfo.TBL_EMAIL + "/" + userId + "/" + DBInfo.TBL_EMAIL, mEmail);
        FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseManager.getInstance().setMechanic(mechanic);
                hideProgressHUD();
                startActivity(new Intent(SignupActivity.this, MechanicMainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackSignupMechanicInfo() {
        if(signupType.equals("email")){
            ((SignupMechanicInfoFragment)mFragment).initialize();
            showFragment(FRAGMENT_SIGNUPEMAIL_TAG, true);
        }else {
            ((SignupMechanicInfoFragment)mFragment).initialize();
            showFragment(FRAGMENT_SIGNUPTYPE_TAG, true);
        }
    }
}
