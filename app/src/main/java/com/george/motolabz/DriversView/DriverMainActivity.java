package com.brainyapps.motolabz.DriversView;

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
import com.brainyapps.motolabz.Fragments.DriversFragments.DriverFavoriteFragment;
import com.brainyapps.motolabz.Fragments.DriversFragments.DriverMessageChannelFragment;
import com.brainyapps.motolabz.Fragments.DriversFragments.DriverSettingsFragment;
import com.brainyapps.motolabz.Fragments.DriversFragments.DriverShopListFragment;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.Notification;
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

public class DriverMainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int FRAGMENT_DRIVER_SHOP_LIST_TAG = 0;
    private static final int FRAGMENT_DRIVER_FAVORITE_TAG = 1;
    private static final int FRAGMENT_DRIVER_MESSAGE_TAG = 2;
    private static final int FRAGMENT_DRIVER_SETTINGS_TAG = 3;
    private Map<String, Fragment> mFragmentMap;
    private Fragment mFragment;

    private ImageView driver_shop_list;
    private ImageView driver_favorite;
    private ImageView driver_message;
    private ImageView driver_settings;
    private TextView pageTitle;
    private TextView unreadMessageCount;

    private ImageView onCommunity;
    private ImageView onNotification;
    private ImageView alertDot;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        driver_shop_list = (ImageView) findViewById(R.id.driver_main_shop_list);
        driver_shop_list.setOnClickListener(this);
        driver_favorite = (ImageView) findViewById(R.id.driver_main_favor);
        driver_favorite.setOnClickListener(this);
        driver_message = (ImageView) findViewById(R.id.driver_main_message);
        driver_message.setOnClickListener(this);
        driver_settings = (ImageView) findViewById(R.id.driver_main_setting);
        driver_settings.setOnClickListener(this);

        unreadMessageCount = (TextView)findViewById(R.id.driver_main_message_count);

        onCommunity = (ImageView) findViewById(R.id.driver_main_community);
        onCommunity.setOnClickListener(this);
        onNotification = (ImageView) findViewById(R.id.driver_main_notification);
        onNotification.setOnClickListener(this);
        alertDot = (ImageView)findViewById(R.id.driver_main_notification_dot);

        notificationInfo = FirebaseDatabase.getInstance().getReference().child("unread").child(myId);
        notificationInfo.addValueEventListener(checkNotification);

        messageInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_UNREAD_MESSAGES).child(myId);
        messageInfo.addValueEventListener(checkMessage);

        pageTitle = (TextView)findViewById(R.id.diver_main_title);
        showProgressHUD("");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(DBInfo.TBL_USER).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressHUD();
                FirebaseManager.getInstance().setDriver(dataSnapshot.getValue(Driver.class));
                if (FirebaseManager.getInstance().getCurrentDriver()!= null) {
                } else {
                    signout();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });

        mFragmentMap = new HashMap<>();
        mFragmentMap.put(DriverShopListFragment.FRAGMENT_TAG, new DriverShopListFragment().newInstance(this));
        mFragmentMap.put(DriverFavoriteFragment.FRAGMENT_TAG, new DriverFavoriteFragment().newInstance(this));
        mFragmentMap.put(DriverMessageChannelFragment.FRAGMENT_TAG, new DriverMessageChannelFragment().newInstance(this));
        mFragmentMap.put(DriverSettingsFragment.FRAGMENT_TAG, new DriverSettingsFragment().newInstance(this));

        showFragment(FRAGMENT_DRIVER_SHOP_LIST_TAG, true);
    }


    public void showFragment(int position, Boolean isPushed) {
        mFragment = null;

        currentPosition = position;

        switch (position) {
            case FRAGMENT_DRIVER_SHOP_LIST_TAG:
                driver_shop_list.setImageResource(R.drawable.ic_tab_list_active);
                driver_favorite.setImageResource(R.drawable.ic_tab_favorite);
                driver_message.setImageResource(R.drawable.ic_tab_messages);
                driver_settings.setImageResource(R.drawable.ic_tab_settings);
                mFragment = mFragmentMap.get(DriverShopListFragment.FRAGMENT_TAG);
                pageTitle.setText("MotoLabz");
                break;
            case FRAGMENT_DRIVER_FAVORITE_TAG:
                driver_shop_list.setImageResource(R.drawable.ic_tab_list);
                driver_favorite.setImageResource(R.drawable.ic_tab_favorite_active);
                driver_message.setImageResource(R.drawable.ic_tab_messages);
                driver_settings.setImageResource(R.drawable.ic_tab_settings);
                mFragment = mFragmentMap.get(DriverFavoriteFragment.FRAGMENT_TAG);
                pageTitle.setText("Favourites");
                break;
            case FRAGMENT_DRIVER_MESSAGE_TAG:
                driver_shop_list.setImageResource(R.drawable.ic_tab_list);
                driver_favorite.setImageResource(R.drawable.ic_tab_favorite);
                driver_message.setImageResource(R.drawable.ic_tab_messages_active);
                driver_settings.setImageResource(R.drawable.ic_tab_settings);
                mFragment = mFragmentMap.get(DriverMessageChannelFragment.FRAGMENT_TAG);
                pageTitle.setText("Messages");
                break;
            case FRAGMENT_DRIVER_SETTINGS_TAG:
                driver_shop_list.setImageResource(R.drawable.ic_tab_list);
                driver_favorite.setImageResource(R.drawable.ic_tab_favorite);
                driver_message.setImageResource(R.drawable.ic_tab_messages);
                driver_settings.setImageResource(R.drawable.ic_tab_settings_active);
                mFragment = mFragmentMap.get(DriverSettingsFragment.FRAGMENT_TAG);
                pageTitle.setText("Settings");
                break;
            default:
                break;
        }

        if (mFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.driver_main_field, mFragment).commit();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_main_shop_list:
                showFragment(FRAGMENT_DRIVER_SHOP_LIST_TAG, true);
                break;
            case R.id.driver_main_favor:
                showFragment(FRAGMENT_DRIVER_FAVORITE_TAG, true);
                break;
            case R.id.driver_main_message:
                showFragment(FRAGMENT_DRIVER_MESSAGE_TAG, true);
                break;
            case R.id.driver_main_setting:
                showFragment(FRAGMENT_DRIVER_SETTINGS_TAG, true);
                break;
            case R.id.driver_main_community:
                Intent driver_community_intent = new Intent(this, DriverCommunityActivity.class);
                startActivity(driver_community_intent);
                break;
            case R.id.driver_main_notification:
                Intent driver_notification_intent = new Intent(this, NotificationActivity.class);
                startActivity(driver_notification_intent);
                break;
            default:
                break;
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

    public void signout(){
        if (FirebaseAuth.getInstance() != null) {
            FirebaseAuth.getInstance().signOut();
            Intent logout_intent = new Intent(this, SigninActivity.class);
            startActivity(logout_intent);
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
