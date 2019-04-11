package com.brainyapps.motolabz.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.UserAuthInfo;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.ImagePicker;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.walnutlabs.android.ProgressHUD;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupMechanicInfoFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{


    public static final String FRAGMENT_TAG = "com_mobile_signup_mechanic_info_fragment_tag";

    public static final int REQUEST_IMAGE_CONTENT = 2013;
    private static int PLACE_PICKER_REQUEST = 51;
    private static Context mContext;

    private ImageView signup_mechanic_info_back;
    private RelativeLayout signup_mechanic_done;
    private ImageView imgAvatar;
    private EditText etf_mechanic_name;
    private EditText etf_mechanic_phone;
    private EditText etf_mechanic_license;
    private EditText etf_mechanic_code;
    private EditText etf_mechanic_brief;
    private SwitchCompat mechanic_location_switch;
    private TextView btnUploadAvatar;
    private Bitmap bitmap;

    String lastChar = " ";

    private StorageReference storePhoto;
    private String avatar_url = "";
    private Boolean isLocationVisible = false;

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

        android.app.Fragment f = new SignupMechanicInfoFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_signup_mechanic_info, container, false);
        signup_mechanic_info_back = (ImageView)rootView.findViewById(R.id.signup_mechanic_info_btn_back);
        signup_mechanic_info_back.setOnClickListener(this);

        signup_mechanic_done = (RelativeLayout)rootView.findViewById(R.id.signup_mechanic_done);
        signup_mechanic_done.setOnClickListener(this);
        imgAvatar = (ImageView)rootView.findViewById(R.id.img_signup_mechanic_avatar);
        etf_mechanic_name = (EditText)rootView.findViewById(R.id.signup_mechanic_name);
        etf_mechanic_phone = (EditText)rootView.findViewById(R.id.signup_mechanic_phone);
        etf_mechanic_license = (EditText)rootView.findViewById(R.id.signup_mechanic_license);
        etf_mechanic_code = (EditText)rootView.findViewById(R.id.signup_mechanic_code);
        etf_mechanic_brief = (EditText)rootView.findViewById(R.id.signup_mechanic_brief);

        mechanic_location_switch = (SwitchCompat)rootView.findViewById(R.id.signup_location_switch);
        mechanic_location_switch.setOnCheckedChangeListener(this);

        btnUploadAvatar = (TextView)rootView.findViewById(R.id.btn_upload_signup_mechanic_avatar);
        btnUploadAvatar.setOnClickListener(this);

        etf_mechanic_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int digits = etf_mechanic_phone.getText().toString().length();
                if (digits > 1)
                    lastChar = etf_mechanic_phone.getText().toString().substring(digits-1);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int digits = etf_mechanic_phone.getText().toString().length();
                if (!lastChar.equals("-")) {
                    if (digits == 3 || digits == 7) {
                        etf_mechanic_phone.append("-");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Bundle bundle = getArguments();
        if(bundle!=null){
            UserAuthInfo info = bundle.getParcelable("userAuthInfo");
            etf_mechanic_name.setText(info.userName);
            Glide.with(getActivity()).load(info.photoUrl).into(imgAvatar);
        }
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signup_mechanic_info_btn_back:
                if (mListener != null) {
                    mListener.onBackSignupMechanicInfo();
                }
                break;
            case R.id.signup_mechanic_done:
                if(checkValidation()){
                    if(!etf_mechanic_code.getText().toString().isEmpty()){
                        showProgressHUD("");
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        Query query = databaseReference.child(DBInfo.TBL_INVITE);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    int i = 0;
                                    for (DataSnapshot invite : dataSnapshot.getChildren()){
                                        if(invite.child("signupCode").exists()){
                                            if(invite.child("signupCode").getValue().toString().equals(etf_mechanic_code.getText().toString())){
                                                i = i + 1;
                                                if (mListener != null) {
                                                    mListener.onSignupMechanicDone(etf_mechanic_name.getText().toString(), etf_mechanic_phone.getText().toString().replaceAll("-",""), etf_mechanic_license.getText().toString(),
                                                            etf_mechanic_brief.getText().toString(), bitmap, isLocationVisible, invite.child("userID").getValue().toString(), invite.child("shopName").getValue().toString(),invite.child("signupCode").getValue().toString());
                                                }
                                            }
                                        }
                                    }
                                    if(i < 1){
                                        etf_mechanic_code.requestFocus();
                                        AlertFactory.showAlert(mContext, "", "Code does not matching. Please check and try again.");
                                        hideProgressHUD();
                                        return;
                                    }

                                }else {
                                    etf_mechanic_code.requestFocus();
                                    AlertFactory.showAlert(mContext, "", "Code does not matching. Please check and try again.");
                                    hideProgressHUD();
                                    return;
                                }
                                hideProgressHUD();
                                return;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                hideProgressHUD();
                                return;
                            }
                        });
                    }else {
                        if (mListener != null) {
                            mListener.onSignupMechanicDone(etf_mechanic_name.getText().toString(), etf_mechanic_phone.getText().toString().replaceAll("-",""), etf_mechanic_license.getText().toString(),
                                    etf_mechanic_brief.getText().toString(), bitmap, isLocationVisible, "", "", "");
                        }
                    }
                }
                break;
            case R.id.btn_upload_signup_mechanic_avatar:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_IMAGE_CONTENT);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CONTENT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            bitmap = ImagePicker.getImageFromResult(getActivity(),resultCode,data);
            imgAvatar.setImageBitmap(bitmap);
        }
    }

    public boolean checkValidation(){
        if(etf_mechanic_name.getText().toString().isEmpty()){
            etf_mechanic_name.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input name. You must fill this field.");
            return false;
        }
        if(etf_mechanic_phone.getText().toString().isEmpty()){
            etf_mechanic_phone.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input phone number. You must fill this field.");
            return false;
        }
        if(etf_mechanic_brief.getText().toString().isEmpty()){
            etf_mechanic_brief.requestFocus();
            AlertFactory.showAlert(mContext, "", "You need to input shop brief about yourself.");
            return false;
        }
        return true;
    }

    public OnSignupMechanicInfoListener mListener;

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.signup_location_switch:
                isLocationVisible = b;
                break;
            default:
                break;
        }
    }

    public void initialize(){
        if(!etf_mechanic_name.getText().toString().isEmpty()){
            etf_mechanic_name.setText("");
        }
        if(!etf_mechanic_phone.getText().toString().isEmpty()){
            etf_mechanic_phone.setText("");
        }
        if(!etf_mechanic_code.getText().toString().isEmpty()){
            etf_mechanic_code.setText("");
        }
        if(!etf_mechanic_brief.getText().toString().isEmpty()){
            etf_mechanic_brief.setText("");
        }
        mechanic_location_switch.setChecked(false);
    }

    public interface OnSignupMechanicInfoListener {
        void onSignupMechanicDone(String name, String phone, String license, String brief, Bitmap avatar, Boolean isLocationValid, String shopId, String shop_name, String signup_code);
        void onBackSignupMechanicInfo();
    }

    public void setOnSignupMechanicInfoListener(OnSignupMechanicInfoListener listener) {
        mListener = listener;
    }
}
