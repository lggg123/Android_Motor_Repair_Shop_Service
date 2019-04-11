package com.brainyapps.motolabz.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainyapps.motolabz.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Onboarding1Fragment extends Fragment {

    public static final String FRAGMENT_TAG = "com_motolabz_onboarding_first_fragment_tag";
    private static Context mContext;

    public static android.app.Fragment newInstance(Context context) {
        mContext = context;

        android.app.Fragment f = new Onboarding1Fragment();
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
        return inflater.inflate(R.layout.fragment_onboarding1, container, false);
    }
}
