package com.brainyapps.motolabz.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.Utils;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupEmailFragment extends Fragment implements View.OnClickListener{


    public static final String FRAGMENT_TAG = "com_mobile_signup_email_fragment_tag";

    private static Context mContext;

    private ImageView signup_email_back;
    private RelativeLayout signup_email_next;
    private EditText etf_email;
    private EditText etf_password;
    private EditText etf_confirm_password;

    private ProgressHUD mProgressDialog;

    private void showProgressHUD(String text) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = ProgressHUD.show(mContext, text, true);
        mProgressDialog.show();
    }

    private void hideProgressHUD() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public static android.app.Fragment newInstance(Context context) {
        mContext = context;

        android.app.Fragment f = new SignupEmailFragment();
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
        ViewGroup rootView =  (ViewGroup) inflater.inflate(R.layout.fragment_signup_email, container, false);
        signup_email_back = (ImageView) rootView.findViewById(R.id.signup_email_btn_back);
        signup_email_back.setOnClickListener(this);
        signup_email_next = (RelativeLayout) rootView.findViewById(R.id.signup_email_next);
        signup_email_next.setOnClickListener(this);
        etf_email = (EditText)rootView.findViewById(R.id.signup_email);
        etf_password = (EditText)rootView.findViewById(R.id.signup_password);
        etf_confirm_password = (EditText)rootView.findViewById(R.id.signup_repassword);
        return rootView;
    }

    public SignupEmailFragment.OnSignupEmailListener mListener;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signup_email_next:
                gotoNext();
                break;
            case R.id.signup_email_btn_back:
                if (mListener != null) {
                    mListener.onBackSignupEmail();
                }
                break;
            default:
                break;
        }
    }

    public void gotoNext(){
        if(checkValidation()){
            showProgressHUD("");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = databaseReference.child(DBInfo.TBL_EMAIL);
            query.orderByChild(DBInfo.TBL_EMAIL).equalTo(etf_email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        if (mListener != null) {
                            mListener.onNexttoInfo(etf_email.getText().toString(),etf_password.getText().toString());
                        }
                    }else {
                        etf_email.requestFocus();
                        AlertFactory.showAlert(mContext, "", "e-mail address is already in use.");
                        hideProgressHUD();
                        return;
                    }
                    hideProgressHUD();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgressHUD();
                    return;
                }
            });
        }
    }

    public boolean checkValidation(){
        if(!Utils.isValidEmail(etf_email.getText().toString())){
            etf_email.requestFocus();
            AlertFactory.showAlert(mContext, "", "Invalid e-mail address. Please input again");
            return false;
        }
        if(!Utils.overLength(etf_password.getText().toString()) || !Utils.containsCharacter(etf_password.getText().toString()) || !Utils.containsNumber(etf_password.getText().toString())){
            etf_password.requestFocus();
            AlertFactory.showAlert(mContext, "", "Password must contains more than 6 letters with at least one character and one number");
            return false;
        }
        if(!etf_password.getText().toString().equals(etf_confirm_password.getText().toString())){
            etf_confirm_password.requestFocus();
            AlertFactory.showAlert(mContext, "", "Password is not matching! Please try again");
            return false;
        }
        return true;
    }

    public void initialize(){
        if(!etf_email.getText().toString().isEmpty()){
            etf_email.setText("");
        }
        if(!etf_password.getText().toString().isEmpty()){
            etf_password.setText("");
        }
        if(!etf_confirm_password.getText().toString().isEmpty()){
            etf_confirm_password.setText("");
        }
    }

    public interface OnSignupEmailListener {
        void onNexttoInfo(String email, String password);
        void onBackSignupEmail();
    }

    public void setOnSignupEmailListener(OnSignupEmailListener listener) {
        mListener = listener;
    }
}
