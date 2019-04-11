package com.brainyapps.motolabz.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.brainyapps.motolabz.Models.UserAuthInfo;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.ImagePicker;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupShopInfoFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{


    public static final String FRAGMENT_TAG = "com_mobile_signup_shop_info_fragment_tag";
    public static final int REQUEST_IMAGE_CONTENT = 2012;
    private static int PLACE_PICKER_REQUEST = 50;

    private static Context mContext;
    private ImageView signup_shop_info_back;
    private RelativeLayout signup_done;
    private EditText etf_shop_name;
    private EditText etf_shop_phone;
    private EditText etf_shop_license;
    private EditText etf_shop_hourly_rate;
    private EditText etf_shop_offsite_rate;
    private EditText etf_shop_inshop_rate;

    private TextView business_time_mon_start;
    private TextView business_time_mon_end;
    private SwitchCompat business_time_mon_status;
    private Boolean mon_status = false;

    private TextView business_time_tue_start;
    private TextView business_time_tue_end;
    private SwitchCompat business_time_tue_status;
    private Boolean tue_status = false;

    private TextView business_time_wed_start;
    private TextView business_time_wed_end;
    private SwitchCompat business_time_wed_status;
    private Boolean wed_status = false;

    private TextView business_time_thu_start;
    private TextView business_time_thu_end;
    private SwitchCompat business_time_thu_status;
    private Boolean thu_status = false;

    private TextView business_time_fri_start;
    private TextView business_time_fri_end;
    private SwitchCompat business_time_fri_status;
    private Boolean fri_status = false;

    private TextView business_time_sat_start;
    private TextView business_time_sat_end;
    private SwitchCompat business_time_sat_status;
    private Boolean sat_status = false;

    private TextView business_time_sun_start;
    private TextView business_time_sun_end;
    private SwitchCompat business_time_sun_status;
    private Boolean sun_status = false;

    private EditText etf_shop_brief;
    private TextView tv_shop_location;
    private ImageView imgAvatar;
    private TextView btnUploadAvatar;
    private Bitmap bitmap;

    private StorageReference storePhoto;
    private String avatar_url = "";
    private String address = "";
    private Double latitude = 0.0d;
    private Double longitude = 0.0d;

    public String lastChar = " ";
    private Dialog dlg;

    public static android.app.Fragment newInstance(Context context) {
        mContext = context;

        android.app.Fragment f = new SignupShopInfoFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_signup_shop_info, container, false);
        signup_shop_info_back = (ImageView) rootView.findViewById(R.id.signup_shop_info_btn_back);
        signup_shop_info_back.setOnClickListener(this);
        signup_done = (RelativeLayout) rootView.findViewById(R.id.signup_shop_done);
        signup_done.setOnClickListener(this);
        etf_shop_name = (EditText)rootView.findViewById(R.id.signup_shop_name);
        etf_shop_hourly_rate = (EditText)rootView.findViewById(R.id.signup_shop_hourly_rate);
        etf_shop_brief = (EditText)rootView.findViewById(R.id.signup_shop_brief);
        tv_shop_location = (TextView)rootView.findViewById(R.id.signup_shop_location);
        tv_shop_location.setOnClickListener(this);
        imgAvatar = (ImageView)rootView.findViewById(R.id.img_signup_shop_avatar);
        btnUploadAvatar = (TextView)rootView.findViewById(R.id.btn_upload_signup_shop_avatar);
        btnUploadAvatar.setOnClickListener(this);

        etf_shop_phone = (EditText)rootView.findViewById(R.id.signup_shop_phone);
        etf_shop_license = (EditText)rootView.findViewById(R.id.signup_shop_license);

        etf_shop_offsite_rate = (EditText)rootView.findViewById(R.id.signup_shop_offsite_rate);
        etf_shop_inshop_rate = (EditText)rootView.findViewById(R.id.signup_shop_inshop_rate);

        business_time_mon_start = (TextView)rootView.findViewById(R.id.signup_shop_mon_start);
        business_time_mon_start.setOnClickListener(this);
        business_time_mon_end = (TextView)rootView.findViewById(R.id.signup_shop_mon_end);
        business_time_mon_end.setOnClickListener(this);
        business_time_mon_status = (SwitchCompat) rootView.findViewById(R.id.signup_shop_mon_switch);
        business_time_mon_status.setOnCheckedChangeListener(this);

        business_time_tue_start = (TextView)rootView.findViewById(R.id.signup_shop_tue_start);
        business_time_tue_start.setOnClickListener(this);
        business_time_tue_end = (TextView)rootView.findViewById(R.id.signup_shop_tue_end);
        business_time_tue_end.setOnClickListener(this);
        business_time_tue_status = (SwitchCompat) rootView.findViewById(R.id.signup_shop_tue_switch);
        business_time_tue_status.setOnCheckedChangeListener(this);

        business_time_wed_start = (TextView)rootView.findViewById(R.id.signup_shop_wed_start);
        business_time_wed_start.setOnClickListener(this);
        business_time_wed_end = (TextView)rootView.findViewById(R.id.signup_shop_wed_end);
        business_time_wed_end.setOnClickListener(this);
        business_time_wed_status = (SwitchCompat) rootView.findViewById(R.id.signup_shop_wed_switch);
        business_time_wed_status.setOnCheckedChangeListener(this);

        business_time_thu_start = (TextView)rootView.findViewById(R.id.signup_shop_thu_start);
        business_time_thu_start.setOnClickListener(this);
        business_time_thu_end = (TextView)rootView.findViewById(R.id.signup_shop_thu_end);
        business_time_thu_end.setOnClickListener(this);
        business_time_thu_status = (SwitchCompat) rootView.findViewById(R.id.signup_shop_thu_switch);
        business_time_thu_status.setOnCheckedChangeListener(this);

        business_time_fri_start = (TextView)rootView.findViewById(R.id.signup_shop_fri_start);
        business_time_fri_start.setOnClickListener(this);
        business_time_fri_end = (TextView)rootView.findViewById(R.id.signup_shop_fri_end);
        business_time_fri_end.setOnClickListener(this);
        business_time_fri_status = (SwitchCompat) rootView.findViewById(R.id.signup_shop_fri_switch);
        business_time_fri_status.setOnCheckedChangeListener(this);

        business_time_sat_start = (TextView)rootView.findViewById(R.id.signup_shop_sat_start);
        business_time_sat_start.setOnClickListener(this);
        business_time_sat_end = (TextView)rootView.findViewById(R.id.signup_shop_sat_end);
        business_time_sat_end.setOnClickListener(this);
        business_time_sat_status = (SwitchCompat) rootView.findViewById(R.id.signup_shop_sat_switch);
        business_time_sat_status.setOnCheckedChangeListener(this);

        business_time_sun_start = (TextView)rootView.findViewById(R.id.signup_shop_sun_start);
        business_time_sun_start.setOnClickListener(this);
        business_time_sun_end = (TextView)rootView.findViewById(R.id.signup_shop_sun_end);
        business_time_sun_end.setOnClickListener(this);
        business_time_sun_status = (SwitchCompat) rootView.findViewById(R.id.signup_shop_sun_switch);
        business_time_sun_status.setOnCheckedChangeListener(this);


        etf_shop_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int digits = etf_shop_phone.getText().toString().length();
                if (digits > 1)
                    lastChar = etf_shop_phone.getText().toString().substring(digits-1);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int digits = etf_shop_phone.getText().toString().length();
                if (!lastChar.equals("-")) {
                    if (digits == 3 || digits == 7) {
                        etf_shop_phone.append("-");
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
            Glide.with(getActivity()).load(info.photoUrl).into(imgAvatar);
        }
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signup_shop_info_btn_back:
                if (mListener != null) {
                    mListener.onBackSignupShopInfo();
                }
                break;
            case R.id.signup_shop_done:
                if(checkValidation()){
                    HashMap time_map = new HashMap();
                    HashMap time_map_detail_mon = new HashMap();
                    HashMap time_map_detail_tue = new HashMap();
                    HashMap time_map_detail_wed = new HashMap();
                    HashMap time_map_detail_thu = new HashMap();
                    HashMap time_map_detail_fri = new HashMap();
                    HashMap time_map_detail_sat = new HashMap();
                    HashMap time_map_detail_sun = new HashMap();
                    time_map_detail_mon.put("start", business_time_mon_start.getText().toString());
                    time_map_detail_mon.put("end", business_time_mon_end.getText().toString());
                    time_map_detail_mon.put("status", mon_status);
                    time_map.put("Monday", time_map_detail_mon);

                    time_map_detail_tue.put("start", business_time_tue_start.getText().toString());
                    time_map_detail_tue.put("end", business_time_tue_end.getText().toString());
                    time_map_detail_tue.put("status", tue_status);
                    time_map.put("Tuesday", time_map_detail_tue);

                    time_map_detail_wed.put("start", business_time_wed_start.getText().toString());
                    time_map_detail_wed.put("end", business_time_wed_end.getText().toString());
                    time_map_detail_wed.put("status", wed_status);
                    time_map.put("Wednesday", time_map_detail_wed);

                    time_map_detail_thu.put("start", business_time_thu_start.getText().toString());
                    time_map_detail_thu.put("end", business_time_thu_end.getText().toString());
                    time_map_detail_thu.put("status", thu_status);
                    time_map.put("Thursday", time_map_detail_thu);

                    time_map_detail_fri.put("start", business_time_fri_start.getText().toString());
                    time_map_detail_fri.put("end", business_time_fri_end.getText().toString());
                    time_map_detail_fri.put("status", fri_status);
                    time_map.put("Friday", time_map_detail_fri);

                    time_map_detail_sat.put("start", business_time_sat_start.getText().toString());
                    time_map_detail_sat.put("end", business_time_sat_end.getText().toString());
                    time_map_detail_sat.put("status", sat_status);
                    time_map.put("Saturday", time_map_detail_sat);

                    time_map_detail_sun.put("start", business_time_sun_start.getText().toString());
                    time_map_detail_sun.put("end", business_time_sun_end.getText().toString());
                    time_map_detail_sun.put("status", sun_status);
                    time_map.put("Sunday", time_map_detail_sun);

                    mListener.onSignupShopDone(etf_shop_name.getText().toString(), etf_shop_phone.getText().toString().replaceAll("-",""), etf_shop_license.getText().toString(),
                            etf_shop_hourly_rate.getText().toString(), etf_shop_offsite_rate.getText().toString(), etf_shop_inshop_rate.getText().toString(),
                            etf_shop_brief.getText().toString(), bitmap, address, latitude, longitude, time_map);
                }
                break;
            case R.id.btn_upload_signup_shop_avatar:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_IMAGE_CONTENT);
                break;
            case R.id.signup_shop_location:
                getAddress();
                break;
            case R.id.signup_shop_mon_start:
                showTimeerDlg("mon_start");
                break;
            case R.id.signup_shop_mon_end:
                showTimeerDlg("mon_end");
                break;
            case R.id.signup_shop_tue_start:
                showTimeerDlg("tue_start");
                break;
            case R.id.signup_shop_tue_end:
                showTimeerDlg("tue_end");
                break;
            case R.id.signup_shop_wed_start:
                showTimeerDlg("wed_start");
                break;
            case R.id.signup_shop_wed_end:
                showTimeerDlg("wed_end");
                break;
            case R.id.signup_shop_thu_start:
                showTimeerDlg("thu_start");
                break;
            case R.id.signup_shop_thu_end:
                showTimeerDlg("thu_end");
                break;
            case R.id.signup_shop_fri_start:
                showTimeerDlg("fri_start");
                break;
            case R.id.signup_shop_fri_end:
                showTimeerDlg("fri_end");
                break;
            case R.id.signup_shop_sat_start:
                showTimeerDlg("sat_start");
                break;
            case R.id.signup_shop_sat_end:
                showTimeerDlg("sat_end");
                break;
            case R.id.signup_shop_sun_start:
                showTimeerDlg("sun_start");
                break;
            case R.id.signup_shop_sun_end:
                showTimeerDlg("sun_end");
                break;
        }
    }

    private void getAddress(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public boolean checkValidation(){
        if(etf_shop_name.getText().toString().isEmpty()){
            etf_shop_name.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input shop name. You must fill this field.");
            return false;
        }
        if(etf_shop_hourly_rate.getText().toString().isEmpty()){
            etf_shop_hourly_rate.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input hourly rate of your shop. You must fill this field.");
            return false;
        }
        if(etf_shop_offsite_rate.getText().toString().isEmpty()){
            etf_shop_offsite_rate.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input offsite diagnosis hourly rate of your shop. You must fill this field.");
            return false;
        }
        if(etf_shop_inshop_rate.getText().toString().isEmpty()){
            etf_shop_inshop_rate.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input in-shop diagnosis hourly rate of your shop. You must fill this field.");
            return false;
        }
        if(etf_shop_brief.getText().toString().isEmpty()){
            etf_shop_brief.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input brief. You must fill this field.");
            return false;
        }
        if(tv_shop_location.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please select location of your shop.");
            return false;
        }
        if(etf_shop_phone.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input phone number. You must fill this field");
            return false;
        }
        if(etf_shop_phone.getText().toString().length() != 12){
            AlertFactory.showAlert(mContext, "", "Please input correct phone number.");
            return false;
        }
        if(business_time_mon_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_mon_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_tue_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_tue_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_wed_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_wed_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_thu_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_thu_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_fri_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_fri_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_sat_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_sat_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_sun_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        if(business_time_sun_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(mContext, "", "Please input business time.");
            return false;
        }
        return true;
    }

    public void showTimeerDlg(final String edit_type){
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                if(edit_type.equals("mon_start")){
                    if(business_time_mon_end.getText().toString().isEmpty()){
                        business_time_mon_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_mon_end.getText().toString())){
                            business_time_mon_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("mon_end")){
                    if(business_time_mon_start.getText().toString().isEmpty()){
                        business_time_mon_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_mon_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_mon_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("tue_start")){
                    if(business_time_tue_end.getText().toString().isEmpty()){
                        business_time_tue_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_tue_end.getText().toString())){
                            business_time_tue_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("tue_end")){
                    if(business_time_tue_start.getText().toString().isEmpty()){
                        business_time_tue_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_tue_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_tue_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("wed_start")){
                    if(business_time_wed_end.getText().toString().isEmpty()){
                        business_time_wed_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_wed_end.getText().toString())){
                            business_time_wed_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("wed_end")){
                    if(business_time_wed_start.getText().toString().isEmpty()){
                        business_time_wed_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_wed_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_wed_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("thu_start")){
                    if(business_time_thu_end.getText().toString().isEmpty()){
                        business_time_thu_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_thu_end.getText().toString())){
                            business_time_thu_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("thu_end")){
                    if(business_time_thu_start.getText().toString().isEmpty()){
                        business_time_thu_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_thu_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_thu_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("fri_start")){
                    if(business_time_fri_end.getText().toString().isEmpty()){
                        business_time_fri_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_fri_end.getText().toString())){
                            business_time_fri_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("fri_end")){
                    if(business_time_fri_start.getText().toString().isEmpty()){
                        business_time_fri_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_fri_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_fri_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("sat_start")){
                    if(business_time_sat_end.getText().toString().isEmpty()){
                        business_time_sat_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_sat_end.getText().toString())){
                            business_time_sat_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("sat_end")){
                    if(business_time_sat_start.getText().toString().isEmpty()){
                        business_time_sat_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_sat_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_sat_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("sun_start")){
                    if(business_time_sun_end.getText().toString().isEmpty()){
                        business_time_sun_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_sun_end.getText().toString())){
                            business_time_sun_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("sun_end")){
                    if(business_time_sun_start.getText().toString().isEmpty()){
                        business_time_sun_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_sun_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_sun_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(mContext, "", "Please input available time.");
                        }
                    }
                }
            }
        }, 0, 0, false);

        timePickerDialog.show();
    }

    public boolean compareTime(String start, String end){
        if(Integer.parseInt(start.replaceAll(":",""))<Integer.parseInt(end.replaceAll(":",""))){
            return true;
        }else {
            return false;
        }
    }

    public void initialize(){
        if(!etf_shop_name.getText().toString().isEmpty()){
            etf_shop_name.setText("");
        }
        if(!etf_shop_hourly_rate.getText().toString().isEmpty()){
            etf_shop_hourly_rate.setText("");
        }
        if(!etf_shop_offsite_rate.getText().toString().isEmpty()){
            etf_shop_offsite_rate.setText("");
        }
        if(!etf_shop_inshop_rate.getText().toString().isEmpty()){
            etf_shop_inshop_rate.setText("");
        }
        if(!etf_shop_brief.getText().toString().isEmpty()){
            etf_shop_brief.setText("");
        }
        if(!etf_shop_phone.getText().toString().isEmpty()){
            etf_shop_phone.setText("");
        }
        if(!etf_shop_license.getText().toString().isEmpty()){
            etf_shop_license.setText("");
        }

        if(!business_time_mon_start.getText().toString().isEmpty()){
            business_time_mon_start.setText("");
        }
        if(!business_time_mon_end.getText().toString().isEmpty()){
            business_time_mon_end.setText("");
        }
        if(!business_time_tue_start.getText().toString().isEmpty()){
            business_time_tue_start.setText("");
        }
        if(!business_time_tue_end.getText().toString().isEmpty()){
            business_time_tue_end.setText("");
        }
        if(!business_time_wed_start.getText().toString().isEmpty()){
            business_time_wed_start.setText("");
        }
        if(!business_time_wed_end.getText().toString().isEmpty()){
            business_time_wed_end.setText("");
        }
        if(!business_time_thu_start.getText().toString().isEmpty()){
            business_time_thu_start.setText("");
        }
        if(!business_time_thu_end.getText().toString().isEmpty()){
            business_time_thu_end.setText("");
        }
        if(!business_time_fri_start.getText().toString().isEmpty()){
            business_time_fri_start.setText("");
        }
        if(!business_time_fri_end.getText().toString().isEmpty()){
            business_time_fri_end.setText("");
        }
        if(!business_time_sat_start.getText().toString().isEmpty()){
            business_time_sat_start.setText("");
        }
        if(!business_time_sat_end.getText().toString().isEmpty()){
            business_time_sat_end.setText("");
        }
        if(!business_time_sun_start.getText().toString().isEmpty()){
            business_time_sun_start.setText("");
        }
        if(!business_time_sun_end.getText().toString().isEmpty()){
            business_time_sun_end.setText("");
        }

        tv_shop_location.setText("");
        address = "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CONTENT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            bitmap = ImagePicker.getImageFromResult(getActivity(),resultCode,data);
            imgAvatar.setImageBitmap(bitmap);
        }

        if (requestCode == PLACE_PICKER_REQUEST &&resultCode == Activity.RESULT_OK) {
            Place place = PlacePicker.getPlace(data,getActivity());
            address = place.getAddress().toString();
            LatLng current_location = place.getLatLng();
            latitude = current_location.latitude;
            longitude = current_location.longitude;
            tv_shop_location.setText(address);
        }
    }

    public OnSignupShopInfoListener mListener;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.signup_shop_mon_switch:
                mon_status = isChecked;
                break;
            case R.id.signup_shop_tue_switch:
                tue_status = isChecked;
                break;
            case R.id.signup_shop_wed_switch:
                wed_status = isChecked;
                break;
            case R.id.signup_shop_thu_switch:
                thu_status = isChecked;
                break;
            case R.id.signup_shop_fri_switch:
                fri_status = isChecked;
                break;
            case R.id.signup_shop_sat_switch:
                sat_status = isChecked;
                break;
            case R.id.signup_shop_sun_switch:
                sun_status = isChecked;
                break;
        }
    }

    public interface OnSignupShopInfoListener {
        void onSignupShopDone(String shop_name, String shop_phone, String shop_license, String hourly_rate, String offsite_rate, String inshop_rate, String shop_brief, Bitmap avatar, String address, Double lat, Double lng, HashMap time_map);
        void onBackSignupShopInfo();
    }

    public void setOnSignupShopInfoListener(OnSignupShopInfoListener listener) {
        mListener = listener;
    }
}
