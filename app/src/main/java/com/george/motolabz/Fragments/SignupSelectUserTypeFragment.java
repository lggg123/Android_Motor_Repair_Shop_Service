package com.brainyapps.motolabz.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brainyapps.motolabz.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupSelectUserTypeFragment extends Fragment implements View.OnClickListener{


    public static final String FRAGMENT_TAG = "com_mobile_signup_type_fragment_tag";

    private static Context mContext;

    private ImageView signup_type_back;
    private ImageView signup_type_driver;
    private ImageView signup_type_repair_shop;
    private ImageView signup_type_mechanic;

    public static android.app.Fragment newInstance(Context context) {
        mContext = context;

        android.app.Fragment f = new SignupSelectUserTypeFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_signup_select_user_type, container, false);

        signup_type_driver = (ImageView) rootView.findViewById(R.id.signup_type_driver);
        signup_type_driver.setOnClickListener(this);
        signup_type_repair_shop = (ImageView) rootView.findViewById(R.id.signup_type_repair_shop);
        signup_type_repair_shop.setOnClickListener(this);
        signup_type_mechanic = (ImageView) rootView.findViewById(R.id.signup_type_mechanic);
        signup_type_mechanic.setOnClickListener(this);
        signup_type_back = (ImageView)rootView.findViewById(R.id.signup_type_btn_back);
        signup_type_back.setOnClickListener(this);

        return rootView;
    }

    public OnSignupTypeListener mListener;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signup_type_driver:
                if (mListener != null) {
                    mListener.onNexttoEmail("customer");
                }
                break;
            case R.id.signup_type_repair_shop:
                if (mListener != null) {
                    mListener.onNexttoEmail("repairshop");
                }
                break;
            case R.id.signup_type_mechanic:
                if (mListener != null) {
                    mListener.onNexttoEmail("mechanic");
                }
                break;
            case R.id.signup_type_btn_back:
                if (mListener != null) {
                    mListener.onBackSignupType();
                }
                break;
            default:
                break;
        }
    }

    public interface OnSignupTypeListener {
        void onNexttoEmail(String type);
        void onBackSignupType();
    }

    public void setOnSignupTypeListener(OnSignupTypeListener listener) {
        mListener = listener;
    }
}
