package com.brainyapps.motolabz.Fragments.DriversFragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.AboutAppActivity;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.DriversView.DriverEditProfileActivity;
import com.brainyapps.motolabz.DriversView.DriverMainActivity;
import com.brainyapps.motolabz.PrivacyPolicyActivity;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.SecuritySettingActivity;
import com.brainyapps.motolabz.SigninActivity;
import com.brainyapps.motolabz.TermandConditionsActivity;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.brainyapps.motolabz.Views.AlertFactoryClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DriverSettingsFragment extends Fragment implements View.OnClickListener{

    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private TextView gotoProfile;
    private TextView aboutApp;
    private TextView rateApp;
    private TextView sendFeedback;
    private TextView securitySetting;
    private TextView termAndcondition;
    private TextView privacyPolicy;
    private RelativeLayout signOut;

    public static final String FRAGMENT_TAG = "com_motolabz_driver_setting_fragment_tag";
    private static Context mContext;

    public static android.app.Fragment newInstance(Context context) {
        mContext = context;

        android.app.Fragment f = new DriverSettingsFragment();
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_driver_settings, container, false);

        gotoProfile = (TextView)rootView.findViewById(R.id.driver_edit_profile);
        if(!FirebaseManager.getInstance().getUserType().equals("customer")){
            gotoProfile.setVisibility(View.GONE);
        }else {
            gotoProfile.setOnClickListener(this);
        }
        aboutApp = (TextView)rootView.findViewById(R.id.settings_about_app);
        aboutApp.setOnClickListener(this);
        rateApp = (TextView)rootView.findViewById(R.id.settings_rate_app);
        rateApp.setOnClickListener(this);
        sendFeedback = (TextView)rootView.findViewById(R.id.settings_send_feedback);
        sendFeedback.setOnClickListener(this);
        securitySetting = (TextView)rootView.findViewById(R.id.settings_security_setting);
        securitySetting.setOnClickListener(this);
        termAndcondition = (TextView)rootView.findViewById(R.id.settings_term_and_condition);
        termAndcondition.setOnClickListener(this);
        privacyPolicy = (TextView)rootView.findViewById(R.id.settings_privacy_policy);
        privacyPolicy.setOnClickListener(this);
        signOut = (RelativeLayout)rootView.findViewById(R.id.driver_main_setting_logout);
        signOut.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_edit_profile:
                Intent profile_intent = new Intent(getActivity(), DriverEditProfileActivity.class);
                startActivity(profile_intent);
                break;
            case R.id.settings_about_app:
                Intent about_app = new Intent(getActivity(), AboutAppActivity.class);
                startActivity(about_app);
                break;
            case R.id.settings_rate_app:
                break;
            case R.id.settings_send_feedback:
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                /* Fill it with Data */
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"motolabz@hotmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;
            case R.id.settings_security_setting:
                Intent security_settings = new Intent(getActivity(), SecuritySettingActivity.class);
                startActivity(security_settings);
                break;
            case R.id.settings_term_and_condition:
                Intent terms_condition = new Intent(getActivity(), TermandConditionsActivity.class);
                terms_condition.putExtra("terms_and_conditions", "from_settings");
                startActivity(terms_condition);
                break;
            case R.id.settings_privacy_policy:
                Intent privacy_policy = new Intent(getActivity(), PrivacyPolicyActivity.class);
                startActivity(privacy_policy);
                break;
            case R.id.driver_main_setting_logout:
                AlertFactory.showAlert(getActivity(), "", "Are you sure want to logout?", "Yes", "No", new AlertFactoryClickListener() {
                    @Override
                    public void onClickYes(AlertDialog dialog) {
                        dialog.dismiss();
                        logout();
                    }
                    @Override
                    public void onClickNo(AlertDialog dialog) {
                        dialog.dismiss();
                    }
                    @Override
                    public void onClickDone(AlertDialog dialog) {

                    }
                });
                break;
            default:
                break;
        }
    }

    public void logout(){
        if (FirebaseAuth.getInstance() != null) {
            FirebaseAuth.getInstance().signOut();
            Intent logout_intent = new Intent(getActivity(), SigninActivity.class);
            startActivity(logout_intent);
        }
    }
}
