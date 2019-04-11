package com.brainyapps.motolabz.MechanicsView;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.DriversView.DriverCommunityActivity;
import com.brainyapps.motolabz.Fragments.DriversFragments.DriverMessageChannelFragment;
import com.brainyapps.motolabz.Fragments.DriversFragments.DriverSettingsFragment;
import com.brainyapps.motolabz.Fragments.MechanicsFragments.MechanicProfileFragment;
import com.brainyapps.motolabz.Fragments.MechanicsFragments.MechanicServiceRequestFragment;
import com.brainyapps.motolabz.NotificationActivity;
import com.brainyapps.motolabz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MechanicMainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int FRAGMENT_MECHANIC_PROFILE_TAG = 0;
    private static final int FRAGMENT_MECHANIC_REQUEST_TAG = 1;
    private static final int FRAGMENT_MECHANIC_MESSAGE_TAG = 2;
    private static final int FRAGMENT_MECHANIC_SETTINGS_TAG = 3;
    private Map<String, Fragment> mFragmentMap;
    private Fragment mFragment;

    private ImageView mechanic_profile;
    private ImageView mechanic_request;
    private ImageView mechanic_message;
    private ImageView mechanic_settings;
    private ImageView mechanic_notification;
    private ImageView mechanic_alert_dot;
    private TextView mechanicTitle;
    private TextView unreadMessageCount;
    private ImageView onCommunity;

    private int currentPosition = 0;
    private Query notificationInfo;
    private Query messageInfo;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_main);

        mechanic_profile = (ImageView)findViewById(R.id.mechanic_main_profile);
        mechanic_profile.setOnClickListener(this);
        mechanic_request = (ImageView)findViewById(R.id.mechanic_main_requests);
        mechanic_request.setOnClickListener(this);
        mechanic_message = (ImageView)findViewById(R.id.mechanic_main_message);
        mechanic_message.setOnClickListener(this);
        mechanic_settings = (ImageView) findViewById(R.id.mechanic_main_setting);
        mechanic_settings.setOnClickListener(this);
        mechanic_notification = (ImageView)findViewById(R.id.mechanic_main_notification);
        mechanic_notification.setOnClickListener(this);
        mechanic_alert_dot = (ImageView)findViewById(R.id.mechanic_main_notification_dot);
        unreadMessageCount = (TextView)findViewById(R.id.mechanic_main_message_count);

        onCommunity = (ImageView) findViewById(R.id.mechanic_main_community);
        onCommunity.setOnClickListener(this);

        mechanicTitle = (TextView)findViewById(R.id.mechanic_main_title);

        notificationInfo = FirebaseDatabase.getInstance().getReference().child("unread").child(myId);
        notificationInfo.addValueEventListener(checkNotification);

        messageInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_UNREAD_MESSAGES).child(myId);
        messageInfo.addValueEventListener(checkMessage);

        mFragmentMap = new HashMap<>();
        mFragmentMap.put(MechanicProfileFragment.FRAGMENT_TAG, new MechanicProfileFragment().newInstance(this));
        mFragmentMap.put(MechanicServiceRequestFragment.FRAGMENT_TAG, new MechanicServiceRequestFragment().newInstance(this));
        mFragmentMap.put(DriverMessageChannelFragment.FRAGMENT_TAG, new DriverMessageChannelFragment().newInstance(this));
        mFragmentMap.put(DriverSettingsFragment.FRAGMENT_TAG, new DriverSettingsFragment().newInstance(this));

        showFragment(FRAGMENT_MECHANIC_PROFILE_TAG, true);
    }

    public void setTitle(String title){
        mechanicTitle.setText(title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mechanic_main_profile:
                showFragment(FRAGMENT_MECHANIC_PROFILE_TAG, true);
                break;
            case R.id.mechanic_main_requests:
                showFragment(FRAGMENT_MECHANIC_REQUEST_TAG, true);
                break;
            case R.id.mechanic_main_message:
                showFragment(FRAGMENT_MECHANIC_MESSAGE_TAG, true);
                break;
            case R.id.mechanic_main_setting:
                showFragment(FRAGMENT_MECHANIC_SETTINGS_TAG, true);
                break;
            case R.id.mechanic_main_notification:
                Intent notification_intent = new Intent(this, NotificationActivity.class);
                startActivity(notification_intent);
                break;
            case R.id.mechanic_main_community:
                Intent driver_community_intent = new Intent(this, DriverCommunityActivity.class);
                startActivity(driver_community_intent);
                break;
            default:
                break;
        }
    }

    public void showFragment(int position, Boolean isPushed) {
        mFragment = null;

        currentPosition = position;

        switch (position) {
            case FRAGMENT_MECHANIC_PROFILE_TAG:
                mechanic_profile.setImageResource(R.drawable.ic_tab_driver_active);
                mechanic_request.setImageResource(R.drawable.ic_tab_service);
                mechanic_message.setImageResource(R.drawable.ic_tab_messages);
                mechanic_settings.setImageResource(R.drawable.ic_tab_settings);
                mFragment = mFragmentMap.get(MechanicProfileFragment.FRAGMENT_TAG);
                mechanicTitle.setText("Profile");
                break;
            case FRAGMENT_MECHANIC_REQUEST_TAG:
                mechanic_profile.setImageResource(R.drawable.ic_tab_driver);
                mechanic_request.setImageResource(R.drawable.ic_tab_service_active);
                mechanic_message.setImageResource(R.drawable.ic_tab_messages);
                mechanic_settings.setImageResource(R.drawable.ic_tab_settings);
                mFragment = mFragmentMap.get(MechanicServiceRequestFragment.FRAGMENT_TAG);
                mechanicTitle.setText("Services Requests");
                break;
            case FRAGMENT_MECHANIC_MESSAGE_TAG:
                mechanic_profile.setImageResource(R.drawable.ic_tab_driver);
                mechanic_request.setImageResource(R.drawable.ic_tab_service);
                mechanic_message.setImageResource(R.drawable.ic_tab_messages_active);
                mechanic_settings.setImageResource(R.drawable.ic_tab_settings);
                mFragment = mFragmentMap.get(DriverMessageChannelFragment.FRAGMENT_TAG);
                mechanicTitle.setText("Messages");
                break;
            case FRAGMENT_MECHANIC_SETTINGS_TAG:
                mechanic_profile.setImageResource(R.drawable.ic_tab_driver);
                mechanic_request.setImageResource(R.drawable.ic_tab_service);
                mechanic_message.setImageResource(R.drawable.ic_tab_messages);
                mechanic_settings.setImageResource(R.drawable.ic_tab_settings_active);
                mFragment = mFragmentMap.get(DriverSettingsFragment.FRAGMENT_TAG);
                mechanicTitle.setText("Settings");
                break;
            default:
                break;
        }

        if (mFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.mechanic_main_field, mFragment).commit();
        }
    }

    private ValueEventListener checkNotification = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                mechanic_alert_dot.setVisibility(View.VISIBLE);
            }else {
                mechanic_alert_dot.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            mechanic_alert_dot.setVisibility(View.GONE);
        }
    };

    private ValueEventListener checkMessage = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                unreadMessageCount.setText(dataSnapshot.getChildrenCount()+"");
                unreadMessageCount.setVisibility(View.VISIBLE);
            }else {
                unreadMessageCount.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            unreadMessageCount.setVisibility(View.GONE);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationInfo.removeEventListener(checkNotification);
        messageInfo.removeEventListener(checkMessage);
    }
}

