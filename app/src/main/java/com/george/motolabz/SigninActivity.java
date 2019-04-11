package com.brainyapps.motolabz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.DriversView.DriverMainActivity;
import com.brainyapps.motolabz.MechanicsView.MechanicMainActivity;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.Models.UserAuthInfo;
import com.brainyapps.motolabz.ShopsView.ShopMainActivity;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Utils.Utils;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.brainyapps.motolabz.Views.AlertFactoryClickListener;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SigninActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private enum SignInStatus {
        Connecting,
        Ready,
        Success
    }

    public static final int RC_GOOGLE_LOGIN = 1001;

    private TextView reset_password;
    private TextView sign_up;
    private RelativeLayout sign_in;
    private ImageView signup_facebook;
    private ImageView signup_google;

    private EditText signin_email;
    private EditText signin_password;

    private boolean mGoogleIntentInProgress;
    private boolean mGoogleLoginClicked;
    private SignInStatus signInStatus;
    GoogleApiClient googleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private CallbackManager mFacebookCallbackManager;
    private LoginButton mFacebookButton;

    private ProgressHUD mProgressDialog;
    private void showProgressHUD(String text) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = ProgressHUD.show(SigninActivity.this, text, true);
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
        setContentView(R.layout.activity_signin);

        reset_password = (TextView)findViewById(R.id.signin_btn_forgot);
        reset_password.setOnClickListener(this);
        sign_up = (TextView)findViewById(R.id.signin_btn_signup);
        sign_up.setOnClickListener(this);
        sign_in = (RelativeLayout)findViewById(R.id.signin_btn_login);
        sign_in.setOnClickListener(this);
        signup_google = (ImageView)findViewById(R.id.signin_btn_google);
        signup_google.setOnClickListener(this);
        signup_facebook = (ImageView)findViewById(R.id.signin_btn_facebook);
        signup_facebook.setOnClickListener(this);

        signin_email = (EditText) findViewById(R.id.signin_edit_email);
        signin_password = (EditText) findViewById(R.id.signin_edit_password);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookButton = (LoginButton) findViewById(R.id.signin_fb_button);
        mFacebookButton.setReadPermissions("email", "public_profile");
        mFacebookButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginRes) {
                handleFacebookAccessToken(loginRes.getAccessToken());
            }

            @Override
            public void onCancel() {
                // ...
                LoginManager.getInstance().logOut();
                signInStatus = SignInStatus.Ready;
                signup_facebook.setEnabled(true);
            }

            @Override
            public void onError(FacebookException error) {
                // ...
                LoginManager.getInstance().logOut();
                signInStatus = SignInStatus.Ready;
                signup_facebook.setEnabled(true);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("347133481774-d9nlbkdt4pe6bpplr26hvrjn5c59i881.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInStatus = SignInStatus.Success;

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                hideProgressHUD();

                if (!Utils.isNetworkAvailable(SigninActivity.this)) {
                    return;
                }

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    FirebaseManager.getInstance().setFirebaseAuth(firebaseAuth);
                    firebaseAuth.getCurrentUser().getToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult getTokenResult) {
                            FirebaseManager.getInstance().setAuthToken(getTokenResult.getToken());
                        }
                    });

                    if (!user.isEmailVerified()) {
                    }

                    FirebaseManager.getInstance().setCurrentuser(user);
                    FirebaseManager.getInstance().setDataBaseRef(FirebaseDatabase.getInstance().getReference());

                    if (signInStatus == SignInStatus.Success) {
                        signInStatus = SignInStatus.Ready;

                        showProgressHUD("");

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        Query query = databaseReference.child(DBInfo.TBL_USER + "/" + user.getUid());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                hideProgressHUD();

                                if (!dataSnapshot.exists()) {
                                    FirebaseAuth.getInstance().signOut();

                                    return;
                                }
                                gotoMain();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                hideProgressHUD();
                                signout();
                            }
                        });
                    }
                } else {
                    hideProgressHUD();
                    signInStatus = SignInStatus.Ready;
//                    mFBButton.setEnabled(true);
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void handleFacebookAccessToken(final AccessToken token) {

        showProgressHUD("");
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        signInStatus = SignInStatus.Ready;

                        if (!task.isSuccessful()) {
                            signup_facebook.setEnabled(true);

                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {

                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        if (object.has("email")) {
                                            String email = null;
                                            try {
                                                email = object.getString("email");
                                                handleFetchProvider(email);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        LoginManager.getInstance().logOut();
                                    }
                                });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "email"); // Parámetros que pedimos a facebook
                                request.setParameters(parameters);
                                request.executeAsync();
                                return;
                            } else {
                            }
                            hideProgressHUD();
                            LoginManager.getInstance().logOut();
                            return;
                        }
                        String name = task.getResult().getUser().getDisplayName();

                        UserAuthInfo user = new UserAuthInfo();
                        user.userId = task.getResult().getUser().getUid();
                        user.userName = name;
                        user.userEmail = task.getResult().getUser().getEmail();
                        user.photoUrl = String.valueOf(task.getResult().getUser().getPhotoUrl());
                        socialSignin(user);
                        hideProgressHUD();
                    }
                });
    }

    public void gotoMain(){
        mDatabase.child(DBInfo.TBL_USER).child(mAuth.getCurrentUser().getUid()).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressHUD();
                FirebaseManager.getInstance().setUserId(mAuth.getCurrentUser().getUid());
                if(dataSnapshot.getValue().toString().equals("customer")){
                    FirebaseManager.getInstance().setUserType("customer");
                    Intent main_intent = new Intent(getApplication(), DriverMainActivity.class);
                    startActivity(main_intent);
                    finish();
                }else if(dataSnapshot.getValue().toString().equals("mechanic")){
                    FirebaseManager.getInstance().setUserType("mechanic");
                    setUserInfo("mechanic");
                }else if(dataSnapshot.getValue().toString().equals("repairshop")){
                    FirebaseManager.getInstance().setUserType("repairshop");
                    setUserInfo("repairshop");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    public void setUserInfo(final String type){
        showProgressHUD("");
        mDatabase.child(DBInfo.TBL_USER).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressHUD();
                if(dataSnapshot.exists()){
                    if(type.equals("mechanic")){
                        Mechanic mechanic = dataSnapshot.getValue(Mechanic.class);
                        FirebaseManager.getInstance().setMechanic(mechanic);
                        Intent main_intent = new Intent(getApplication(), MechanicMainActivity.class);
                        startActivity(main_intent);
                    }else if(type.equals("repairshop")){
                        RepairShop repairShop = dataSnapshot.getValue(RepairShop.class);
                        FirebaseManager.getInstance().setRepairShop(repairShop);
                        setShopStatus(repairShop);
                        Intent main_intent = new Intent(getApplication(), ShopMainActivity.class);
                        startActivity(main_intent);
                    }
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    public void gotoSignup(){
        Intent signup_intent = new Intent(this, SignupActivity.class);
        startActivity(signup_intent);
    }

    public void loginwithGoogle(){
        if (!Utils.checkConnection(this)) {
            return;
        }
        mGoogleLoginClicked = true;
        showProgressHUD("");
        Intent signinGoogle = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signinGoogle,RC_GOOGLE_LOGIN);
    }

    public void loginwithFacebook(){
        if (!Utils.checkConnection(this)) {
            return;
        }
        LoginManager.getInstance().logOut();
        mFacebookButton.performClick();
    }

    public void reset_password(){
        Intent reset_password_intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(reset_password_intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signin_btn_login:
                loginwithEmail();
                break;
            case R.id.signin_btn_forgot:
                reset_password();
                break;
            case R.id.signin_btn_facebook:
                loginwithFacebook();
                break;
            case R.id.signin_btn_google:
                loginwithGoogle();
                break;
            case R.id.signin_btn_signup:
                gotoSignup();
                break;
            default:
                break;
        }
    }

    public void loginwithEmail(){
        final String email = signin_email.getText().toString();
        final String password = signin_password.getText().toString();
//        Toast.makeText(SigninActivity.this, "email: " + email + " password: " + password,
//                Toast.LENGTH_LONG).show();
        if (!Utils.checkConnection(this)) {
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            signin_email.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            signin_password.requestFocus();
            return;
        }
        showProgressHUD("");
        Query query = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_EMAIL);
        query.orderByChild(DBInfo.TBL_EMAIL).equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    hideProgressHUD();
                    AlertFactory.showAlert(SigninActivity.this, "", "e-mail does not exist.");
                }else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    hideProgressHUD();
                                    if (!task.isSuccessful()) {
                                        hideProgressHUD();
                                        // there was an error
                                        AlertFactory.showAlert(SigninActivity.this, "Sign In", "Email or Password is incorrect.", "OKAY", "", new AlertFactoryClickListener() {
                                            @Override
                                            public void onClickYes(AlertDialog dialog) {

                                            }
                                            @Override
                                            public void onClickNo(AlertDialog dialog) {

                                            }
                                            @Override
                                            public void onClickDone(AlertDialog dialog) {
                                                dialog.dismiss();
                                            }
                                        });
                                    } else {
                                        gotoMain();
                                    }
                                }
                            });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
                return;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_GOOGLE_LOGIN){
            if (resultCode != RESULT_OK) {
                hideProgressHUD();
                mGoogleLoginClicked = false;
                signInStatus = SignInStatus.Ready;
                Toast.makeText(SigninActivity.this, "Google login failed", Toast.LENGTH_SHORT).show();
                return;
            }

            signInStatus = SignInStatus.Connecting;

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                hideProgressHUD();
                Toast.makeText(SigninActivity.this,"Signin Failed",Toast.LENGTH_LONG).show();
                // Google Sign In failed, update UI appropriately
                // ...
            }
            mGoogleIntentInProgress = false;
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account){

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            String name = task.getResult().getUser().getDisplayName();
                            // ...
                            UserAuthInfo user = new UserAuthInfo();
                            user.userId = task.getResult().getUser().getUid();
                            user.userName = name;
                            user.userEmail = task.getResult().getUser().getEmail();
                            user.photoUrl = String.valueOf(account.getPhotoUrl());
                            socialSignin(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                if (account != null)
                                    handleFetchProvider(account.getEmail());
                            }
                            return;
                        }
                        // ...
                    }
                });
    }

    private void socialSignin(final UserAuthInfo user){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child(DBInfo.TBL_USER + "/" + user.userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    socialSignUp(user);
                    return;
                }
                gotoMain();
                hideProgressHUD();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
                signout();
            }
        });
    }

    private void socialSignUp(final UserAuthInfo user){
        Bundle bundle = new Bundle();
        bundle.putParcelable("userAuthInfo", user);
        Intent signup_intent = new Intent(this, SignupActivity.class);
        signup_intent.putExtras(bundle);
        startActivity(signup_intent);
    }

    private void handleFetchProvider(final String email) {
        showProgressHUD("");
        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(this, new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                if (!task.isSuccessful()) {
//                    Log.w(TAG, "signInWithCredential", task.getException());
                    signInStatus = SignInStatus.Ready;
                    signup_facebook.setEnabled(true);

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    }
                    return;
                }

                ProviderQueryResult result = task.getResult();
                if (result == null)
                    return;

                List<String> providers = result.getProviders();
                if (providers == null)
                    return;

                String provider = "";
                int index = 0;
                while (index < providers.size()) {
                    if (providers.get(index).contains("google")) {
                        provider = provider + "\nGoogle account";
                    } else if (providers.get(index).contains("facebook")) {
                        provider = provider + "\nFacebook account";
                    } else {
                        provider = provider + "\nEmail account";
                    }

                    index++;
                }
                hideProgressHUD();
                AlertFactory.showAlert(SigninActivity.this, "Warning", "You have already registered account(s):" + provider);
            }
        });
    }

    public void setShopStatus(RepairShop currentShop){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String strDate = mdformat.format(calendar.getTime());
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                checkAvailability("Sunday", strDate, currentShop);
                break;
            case Calendar.MONDAY:
                checkAvailability("Monday", strDate, currentShop);
                break;
            case Calendar.TUESDAY:
                checkAvailability("Tuesday", strDate, currentShop);
                break;
            case Calendar.WEDNESDAY:
                checkAvailability("Wednesday", strDate, currentShop);
                break;
            case Calendar.THURSDAY:
                checkAvailability("Thursday", strDate, currentShop);
                break;
            case Calendar.FRIDAY:
                checkAvailability("Friday", strDate, currentShop);
                break;
            case Calendar.SATURDAY:
                checkAvailability("Saturday", strDate, currentShop);
                break;
        }
    }

    public void checkAvailability(String week_day, final String current_time, final RepairShop currentShop){
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(currentShop.userID).child("businessTime").child(week_day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(dataSnapshot.child("status").getValue().toString().equals("false")){
                        setAvailableValue(false, currentShop);
                    }else {
                        String start_time = dataSnapshot.child("start").getValue().toString();
                        String end_time = dataSnapshot.child("end").getValue().toString();
                        if(Integer.parseInt(current_time.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(current_time.replaceAll(":",""))){
                            if(!currentShop.available){
                                setAvailableValue(false, currentShop);
                            }else {
                                setAvailableValue(true, currentShop);
                            }
                        }else {
                            setAvailableValue(false, currentShop);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setAvailableValue(Boolean isChecked, RepairShop currentShop){
        currentShop.available = isChecked;
        FirebaseManager.getInstance().setRepairShop(currentShop);
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(currentShop.userID).child("available").setValue(isChecked);
    }

    public void signout(){
        if (FirebaseAuth.getInstance() != null) {
            FirebaseAuth.getInstance().signOut();
            Intent logout_intent = new Intent(this, SigninActivity.class);
            startActivity(logout_intent);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
