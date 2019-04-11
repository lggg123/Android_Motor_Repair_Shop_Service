package com.brainyapps.motolabz.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.brainyapps.motolabz.Adapters.VehicleSignupRecyclerAdapter;
import com.brainyapps.motolabz.Models.SignupVehicle;
import com.brainyapps.motolabz.Models.UserAuthInfo;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.ImagePicker;
import com.brainyapps.motolabz.Utils.VehicleInfoManager;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.bumptech.glide.Glide;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupInfoFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener ,VehicleSignupRecyclerAdapter.OnClickItemListener{


    public static final String FRAGMENT_TAG = "com_mobile_signup_info_fragment_tag";
    public static final int REQUEST_AVATAR_IMAGE_CONTENT = 2014;

    private static Context mContext;

    private ImageView signup_info_back;
    private RelativeLayout signup_done;
    private RelativeLayout add_vehicle;
    private TextView btnUploadAvatar;
    private ImageView imgAvatar;
    private SwitchCompat isLocationService;
    private EditText etf_user_name;
    private EditText etf_phone_number;

    private RecyclerView vehicleRecyclerView;
    private VehicleSignupRecyclerAdapter vehicleSignupRecyclerAdapter;
    private ArrayList<SignupVehicle> vehicleList = new ArrayList<>();

    private Bitmap bitmap;

    private Boolean isLocation = false;
    String lastChar = " ";

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

        android.app.Fragment f = new SignupInfoFragment();
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
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_signup_info, container, false);
        signup_done = (RelativeLayout) rootView.findViewById(R.id.signup_done);
        signup_done.setOnClickListener(this);
        signup_info_back = (ImageView)rootView.findViewById(R.id.signup_info_btn_back);
        signup_info_back.setOnClickListener(this);
        btnUploadAvatar = (TextView)rootView.findViewById(R.id.img_signup_upload_avatar);
        btnUploadAvatar.setOnClickListener(this);
        add_vehicle = (RelativeLayout) rootView.findViewById(R.id.signup_add_vehicle);
        add_vehicle.setOnClickListener(this);
        etf_user_name = (EditText)rootView.findViewById(R.id.signup_name);
        etf_phone_number = (EditText)rootView.findViewById(R.id.signup_phone);
        isLocationService = (SwitchCompat)rootView.findViewById(R.id.signup_location_switch);
        isLocationService.setOnCheckedChangeListener(this);
        imgAvatar = (CircleImageView)rootView.findViewById(R.id.img_signup_avatar);

        vehicleRecyclerView = (RecyclerView)rootView.findViewById(R.id.signup_add_vehicle_recycler_view);
        vehicleSignupRecyclerAdapter = new VehicleSignupRecyclerAdapter(vehicleList);
        vehicleRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        vehicleRecyclerView.setAdapter(vehicleSignupRecyclerAdapter);
        vehicleSignupRecyclerAdapter.setOnClickItemListener(this);

        etf_phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int digits = etf_phone_number.getText().toString().length();
                if (digits > 1)
                    lastChar = etf_phone_number.getText().toString().substring(digits-1);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int digits = etf_phone_number.getText().toString().length();
                if (!lastChar.equals("-")) {
                    if (digits == 3 || digits == 7) {
                        etf_phone_number.append("-");
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
            etf_user_name.setText(info.userName);
            Glide.with(getActivity()).load(info.photoUrl).into(imgAvatar);
        }
        return rootView;
    }

    public OnSignupInfoListener mListener;

    @Override
    public void onResume() {
        super.onResume();
        updateVehicleList();
    }

    public void updateVehicleList(){
        vehicleList.clear();
        Map<String, SignupVehicle> info = VehicleInfoManager.getInstance().getVehicleInfoList();
        Iterator myIterator = info.keySet().iterator();
        while(myIterator.hasNext()) {
            String key=(String)myIterator.next();
            vehicleList.add((SignupVehicle)info.get(key));
        }
        vehicleSignupRecyclerAdapter.notifyDataSetChanged();
        vehicleRecyclerView.smoothScrollToPosition(vehicleList.size());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signup_done:
                if(checkValidation()){
                    if (mListener != null) {
                        String phone = etf_phone_number.getText().toString().replaceAll("-","");
                        mListener.onSignupDone(etf_user_name.getText().toString(), phone, bitmap, isLocation);
                    }
                }
                break;
            case R.id.signup_info_btn_back:
                if (mListener != null) {
                    mListener.onBackSignupInfo();
                }
                break;
            case R.id.img_signup_upload_avatar:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_AVATAR_IMAGE_CONTENT);
                break;
            case R.id.signup_add_vehicle:
                if (mListener != null) {
                    mListener.onGotoSignupAddVehicle("");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_AVATAR_IMAGE_CONTENT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            bitmap = ImagePicker.getImageFromResult(getActivity(),resultCode,data);
            imgAvatar.setImageBitmap(bitmap);
        }
    }

    public boolean checkValidation(){
        if(etf_user_name.getText().toString().isEmpty()){
            etf_user_name.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input your name.");
            return false;
        }
        if(etf_phone_number.getText().toString().isEmpty()){
            etf_phone_number.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input your phone number.");
            return false;
        }
        if(VehicleInfoManager.getInstance().getVehicleInfoList().size() < 1){
            AlertFactory.showAlert(mContext, "", "Please add Vehicle information.");
            return false;
        }
        return true;
    }

    public void initialize(){
        if(!etf_user_name.getText().toString().isEmpty()){
            etf_user_name.setText("");
        }
        if(!etf_phone_number.getText().toString().isEmpty()){
            etf_phone_number.setText("");
        }
        isLocationService.setChecked(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.signup_location_switch:
                isLocation = b;
                break;
            default:
                break;
        }
    }

    @Override
    public void clickModelItem(int index, String vehicle_key) {
        if (mListener != null) {
            mListener.onGotoSignupAddVehicle(vehicle_key);
        }
    }

    public interface OnSignupInfoListener {
        void onSignupDone(String user_name, String phone, Bitmap avatar, Boolean isLocation);
        void onGotoSignupAddVehicle(String key);
        void onBackSignupInfo();
    }

    public void setOnSignupInfoListener(OnSignupInfoListener listener) {
        mListener = listener;
    }
}
