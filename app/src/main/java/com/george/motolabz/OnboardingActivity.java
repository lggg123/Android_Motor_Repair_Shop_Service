package com.brainyapps.motolabz;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Fragments.Onboarding1Fragment;
import com.brainyapps.motolabz.Fragments.Onboarding2Fragment;
import com.brainyapps.motolabz.Fragments.Onboarding3Fragment;
import com.brainyapps.motolabz.Utils.PrefUtils;
import com.brainyapps.motolabz.Views.CustomViewPager;

import java.util.HashMap;
import java.util.Map;

public class OnboardingActivity extends AppCompatActivity implements CustomViewPager.OnSwipeLeftRightListener, View.OnClickListener{

    private static final int NUM_PAGES = 3;

    private CustomViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private Map<String, Fragment> mFragmentMap;

    private TextView onboard_skip;
    private RelativeLayout onboard_next;
    private RelativeLayout onboard_done;
    private ImageView first_dot;
    private ImageView second_dot;
    private ImageView third_dot;

    int selectedIndex = 0;

    private boolean isFromMain = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        if (getIntent() != null) {
            isFromMain = getIntent().getBooleanExtra("tutorial", false);
        }

        mFragmentMap = new HashMap<>();
        mFragmentMap.put("0", Onboarding1Fragment.newInstance(getApplicationContext()));
        mFragmentMap.put("1", Onboarding2Fragment.newInstance(getApplicationContext()));
        mFragmentMap.put("2", Onboarding3Fragment.newInstance(getApplicationContext()));

        onboard_skip = (TextView)findViewById(R.id.onboarding_skip);
        onboard_skip.setOnClickListener(this);
        onboard_next = (RelativeLayout) findViewById(R.id.onboarding_first_next);
        onboard_next.setOnClickListener(this);
        onboard_done = (RelativeLayout)findViewById(R.id.onboarding_first_done);
        onboard_done.setOnClickListener(this);

        first_dot = (ImageView) findViewById(R.id.onboarding_first_dot);
        second_dot = (ImageView) findViewById(R.id.onboarding_second_dot);
        third_dot = (ImageView) findViewById(R.id.onboarding_third_dot);

        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                selectedIndex = position;

                if (position == NUM_PAGES - 2) {
                    onboard_next.setVisibility(View.VISIBLE);
                    onboard_done.setVisibility(View.GONE);
                    first_dot.setImageResource(R.drawable.img_onboarding_dot_off);
                    second_dot.setImageResource(R.drawable.img_onboarding_dot_on);
                    third_dot.setImageResource(R.drawable.img_onboarding_dot_off);
                }else if (position == NUM_PAGES - 1) {
                    onboard_next.setVisibility(View.GONE);
                    onboard_done.setVisibility(View.VISIBLE);
                    first_dot.setImageResource(R.drawable.img_onboarding_dot_off);
                    second_dot.setImageResource(R.drawable.img_onboarding_dot_off);
                    third_dot.setImageResource(R.drawable.img_onboarding_dot_on);
                } else {
                    onboard_next.setVisibility(View.VISIBLE);
                    onboard_done.setVisibility(View.GONE);
                    first_dot.setImageResource(R.drawable.img_onboarding_dot_on);
                    second_dot.setImageResource(R.drawable.img_onboarding_dot_off);
                    third_dot.setImageResource(R.drawable.img_onboarding_dot_off);
                }
            }
        });
        mPager.setOnSwipeLeftRightListener(this);
    }

    @Override
    public void onSwipeLeft() {

    }

    @Override
    public void onSwipeRight() {
        if (selectedIndex == NUM_PAGES - 1) {
            gotoSigninPage();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.onboarding_skip:
                gotoSigninPage();
                break;
            case R.id.onboarding_first_next:
                gotoNextPage();
                break;
            case R.id.onboarding_first_done:
                gotoSigninPage();
                break;
            default:
                break;
        }
    }

    public void gotoPreviousPage(){

    }

    public void gotoNextPage(){
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);

        if (selectedIndex == NUM_PAGES - 2) {
            onboard_next.setVisibility(View.VISIBLE);
            onboard_done.setVisibility(View.GONE);
            first_dot.setImageResource(R.drawable.img_onboarding_dot_off);
            second_dot.setImageResource(R.drawable.img_onboarding_dot_on);
            third_dot.setImageResource(R.drawable.img_onboarding_dot_off);
        }else if (selectedIndex == NUM_PAGES - 1) {
            onboard_next.setVisibility(View.GONE);
            onboard_done.setVisibility(View.VISIBLE);
            first_dot.setImageResource(R.drawable.img_onboarding_dot_off);
            second_dot.setImageResource(R.drawable.img_onboarding_dot_off);
            third_dot.setImageResource(R.drawable.img_onboarding_dot_on);
        }
    }

    public void gotoSigninPage(){
        PrefUtils.getInstance().putBoolean(PrefUtils.PREF_TUTORIAL_ON, false);
        Intent signin_intent = new Intent(this, SigninActivity.class);
        startActivity(signin_intent);
        finish();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mFragmentMap.get(String.valueOf(position));
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}