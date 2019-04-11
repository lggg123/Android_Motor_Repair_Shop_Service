package com.brainyapps.motolabz.ShopsView;

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
import com.brainyapps.motolabz.Fragments.ShopsFragments.ShopProfileFragment;
import com.brainyapps.motolabz.Fragments.ShopsFragments.ShopServiceRequestFragment;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.NotificationActivity;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.SigninActivity;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ShopMainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int FRAGMENT_SHOP_PROFILE_TAG = 0;
    private static final int FRAGMENT_SHOP_REQUEST_TAG = 1;
    private static final int FRAGMENT_SHOP_MESSAGE_TAG = 2;
    private static final int FRAGMENT_SHOP_SETTINGS_TAG = 3;
    private Map<String, Fragment> mFragmentMap;
    private Fragment mFragment;

    private ImageView shopNotification;

    private ImageView shopProfile;
    private ImageView shopRequest;
    private ImageView shopMessage;
    private ImageView shopSetting;
    private ImageView alertDot;
    private TextView unreadMessageCount;
    private ImageView onCommunity;

    private int currentPosition = 0;
    private DatabaseReference mDatabase;

    private Query notificationInfo;
    private Query messageInfo;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
    private TextView shopTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        notificationInfo = FirebaseDatabase.getInstance().getReference().child("unread").child(myId);
        notificationInfo.addValueEventListener(checkNotification);

        messageInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_UNREAD_MESSAGES).child(myId);
        messageInfo.addValueEventListener(checkMessage);

        shopNotification = (ImageView)findViewById(R.id.shop_main_notification);
        shopNotification.setOnClickListener(this);
        alertDot = (ImageView)findViewById(R.id.shop_main_notification_dot);

        unreadMessageCount = (TextView)findViewById(R.id.shop_main_message_count);

        onCommunity = (ImageView) findViewById(R.id.shop_main_community);
        onCommunity.setOnClickListener(this);

        shopProfile = (ImageView)findViewById(R.id.shop_main_profile);
        shopProfile.setOnClickListener(this);
        shopRequest = (ImageView)findViewById(R.id.shop_main_requests);
        shopRequest.setOnClickListener(this);
        shopMessage = (ImageView) findViewById(R.id.shop_main_message);
        shopMessage.setOnClickListener(this);
        shopSetting = (ImageView)findViewById(R.id.shop_main_setting);
        shopSetting.setOnClickListener(this);

        shopTitle = (TextView)findViewById(R.id.shop_main_title);

        mFragmentMap = new HashMap<>();
        mFragmentMap.put(ShopProfileFragment.FRAGMENT_TAG, new ShopProfileFragment().newInstance(this));
        mFragmentMap.put(ShopServiceRequestFragment.FRAGMENT_TAG, new ShopServiceRequestFragment().newInstance(this));
        mFragmentMap.put(DriverMessageChannelFragment.FRAGMENT_TAG, new DriverMessageChannelFragment().newInstance(this));
        mFragmentMap.put(DriverSettingsFragment.FRAGMENT_TAG, new DriverSettingsFragment().newInstance(this));

        showFragment(FRAGMENT_SHOP_PROFILE_TAG, true);
    }

    public void showFragment(int position, Boolean isPushed) {
        mFragment = null;

        currentPosition = position;

        switch (position) {
            case FRAGMENT_SHOP_PROFILE_TAG:
                shopProfile.setImageResource(R.drawable.ic_tab_repairshop_active);
                shopRequest.setImageResource(R.drawable.ic_tab_service);
                shopMessage.setImageResource(R.drawable.ic_tab_messages);
                shopSetting.setImageResource(R.drawable.ic_tab_settings);
                mFragment = mFragmentMap.get(ShopProfileFragment.FRAGMENT_TAG);
                shopTitle.setText("Service Shop Profile");
                break;
            case FRAGMENT_SHOP_REQUEST_TAG:
                shopProfile.setImageResource(R.drawable.ic_tab_repairshop);
                shopRequest.setImageResource(R.drawable.ic_tab_service_active);
                shopMessage.setImageResource(R.drawable.ic_tab_messages);
                shopSetting.setImageResource(R.drawable.ic_tab_settings);
                mFragment = mFragmentMap.get(ShopServiceRequestFragment.FRAGMENT_TAG);
                shopTitle.setText("Services Requests");
                break;
            case FRAGMENT_SHOP_MESSAGE_TAG:
                shopProfile.setImageResource(R.drawable.ic_tab_repairshop);
                shopRequest.setImageResource(R.drawable.ic_tab_service);
                shopMessage.setImageResource(R.drawable.ic_tab_messages_active);
                shopSetting.setImageResource(R.drawable.ic_tab_settings);
                mFragment = mFragmentMap.get(DriverMessageChannelFragment.FRAGMENT_TAG);
                shopTitle.setText("Messages");
                break;
            case FRAGMENT_SHOP_SETTINGS_TAG:
                shopProfile.setImageResource(R.drawable.ic_tab_repairshop);
                shopRequest.setImageResource(R.drawable.ic_tab_service);
                shopMessage.setImageResource(R.drawable.ic_tab_messages);
                shopSetting.setImageResource(R.drawable.ic_tab_settings_active);
                mFragment = mFragmentMap.get(DriverSettingsFragment.FRAGMENT_TAG);
                shopTitle.setText("Settings");
                break;
            default:
                break;
        }

        if (mFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.shop_main_field, mFragment).commit();
        }
    }

    private ValueEventListener checkNotification = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                alertDot.setVisibility(View.VISIBLE);
            }else {
                alertDot.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            alertDot.setVisibility(View.GONE);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shop_main_profile:
                showFragment(FRAGMENT_SHOP_PROFILE_TAG, true);
                break;
            case R.id.shop_main_requests:
                showFragment(FRAGMENT_SHOP_REQUEST_TAG, true);
                break;
            case R.id.shop_main_message:
                showFragment(FRAGMENT_SHOP_MESSAGE_TAG, true);
                break;
            case R.id.shop_main_setting:
                showFragment(FRAGMENT_SHOP_SETTINGS_TAG, true);
                break;
            case R.id.shop_main_notification:
                Intent notification_intent = new Intent(this, NotificationActivity.class);
                startActivity(notification_intent);
                break;
            case R.id.shop_main_community:
                Intent driver_community_intent = new Intent(this, DriverCommunityActivity.class);
                startActivity(driver_community_intent);
                break;
        }
    }

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
