package com.brainyapps.motolabz.Utils;

import android.content.Context;
import android.text.TextUtils;

import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.Models.UserAuthInfo;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

/**
 * Created by HappyBear on 7/6/2018.
 */

public class FirebaseManager {
    private static FirebaseManager instance;

    public Context mContext;
    public Driver currentDriver;
    public Mechanic currentMechanic;
    public RepairShop currentRepairshop;
    public Map<String, UserAuthInfo> userList;

    public String currentUserId;
    public String currentUserType;
    public float mScale;

    private Firebase mFirebaseRef;

    private FirebaseUser mCurrentUser;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    private String mAuthToken;

    public GoogleApiClient mGoogleApiClient;

    private AuthData mAuthData;

    public boolean isLoaded;

    private FirebaseManager(Context context) {
        mContext = context;
        currentDriver = new Driver();
        currentMechanic = new Mechanic();
        currentRepairshop = new RepairShop();
        currentUserId = "";
        currentUserType = "";

        isLoaded = false;
    }

    public static void init(Context context) {
        instance = new FirebaseManager(context);
    }

    public static FirebaseManager getInstance() {
        return instance;
    }
    public void setUserId(String userId){
        currentUserId = userId;
    }
    public String getUserId(){
        return currentUserId;
    }
    public void setUserType(String userType){
        currentUserType = userType;
    }
    public String getUserType(){
        return currentUserType;
    }

    public void setFirebaseRef(Firebase firebaseRef) {
        mFirebaseRef = firebaseRef;
    }

    public Firebase getFirebaseRef() {
        return mFirebaseRef;
    }

    public void setAuthToken(String token) {
        mAuthToken = token;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public void setCurrentuser(FirebaseUser user) {
        mCurrentUser = user;
    }

    public void setFirebaseAuth(FirebaseAuth auth) {
        mAuth = auth;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public FirebaseUser getCurrentUser() {
        return mCurrentUser;
    }

    public void setDataBaseRef(DatabaseReference dataBaseRef) {
        mDatabase = dataBaseRef;
    }

    public DatabaseReference getDataBaseRef() {
        return mDatabase;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setAuthData(AuthData authData) {
        mAuthData = authData;
    }

    public AuthData getAuthData() {
        return mAuthData;
    }

    public String getMyUserId() {
        String userId = "";
        if (TextUtils.isEmpty(userId) && FirebaseAuth.getInstance().getCurrentUser() != null)
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (TextUtils.isEmpty(userId))
            userId = "";

        return userId;
    }

    public void setDriver(Driver driver){
        currentDriver = driver;
    }

    public Driver getCurrentDriver(){
        return currentDriver;
    }

    public void setMechanic(Mechanic mechanic){
        currentMechanic = mechanic;
    }

    public Mechanic getCurrentMechanic(){
        return currentMechanic;
    }

    public void setRepairShop(RepairShop repairShop){
        currentRepairshop = repairShop;
    }

    public RepairShop getCurrentRepairShop(){
        return currentRepairshop;
    }

    public void clear() {
        currentDriver = new Driver();
        currentMechanic = new Mechanic();
        currentRepairshop = new RepairShop();
        mCurrentUser = null;
    }
}
