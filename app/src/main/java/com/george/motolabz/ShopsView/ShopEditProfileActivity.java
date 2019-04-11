package com.brainyapps.motolabz.ShopsView;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Utils.ImagePicker;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.walnutlabs.android.ProgressHUD;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ShopEditProfileActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    public static final int REQUEST_IMAGE_CONTENT = 3412;
    private static int PLACE_PICKER_REQUEST = 3413;

    private ImageView btn_back;
    private ImageView avatar;
    private TextView upload_avatar;
    private EditText name;
    private EditText phone;
    private EditText license;
    private EditText hourly_rate;
    private EditText brief;
    private TextView location;

    private RelativeLayout btn_done;

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

    private String lastChar = " ";
    private Bitmap bitmap;
    private String address = "";
    private Double latitude = 0.0d;
    private Double longitude = 0.0d;

    private HashMap time_map;
    private RepairShop shop;

    private DatabaseReference mDatabase;
    private String avatarUrl = "";
    private StorageReference storePhoto;

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
        setContentView(R.layout.activity_shop_edit_profile);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        shop = FirebaseManager.getInstance().getCurrentRepairShop();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storePhoto = FirebaseStorage.getInstance().getReference();

        btn_back = (ImageView)findViewById(R.id.shop_edit_profile_back);
        btn_back.setOnClickListener(this);
        avatar = (ImageView)findViewById(R.id.img_shop_profile_avatar);
        if(!shop.photoUrl.isEmpty()){
            Glide.with(getApplication()).load(shop.photoUrl).into(avatar);
        }
        upload_avatar = (TextView)findViewById(R.id.shop_profile_upload_avatar);
        upload_avatar.setOnClickListener(this);
        name = (EditText)findViewById(R.id.shop_profile_name);
        name.setText(shop.fullName);
        phone = (EditText)findViewById(R.id.shop_profile_phone);
        phone.setText(changePhoneFormat(shop.phone));
        license = (EditText)findViewById(R.id.shop_profile_edit_license);
        license.setText(shop.licenseNumber);
        hourly_rate = (EditText)findViewById(R.id.shop_profile_edit_hourly_rate);
        hourly_rate.setText(shop.hourlyRate.toString());
        brief = (EditText)findViewById(R.id.shop_profile_edit_brief);
        brief.setText(shop.description);
        location = (TextView)findViewById(R.id.shop_profile_location);
        location.setOnClickListener(this);
        location.setText(shop.address);
        latitude = shop.latitude;
        longitude = shop.longitude;

        ObjectMapper oMapper = new ObjectMapper();

        business_time_mon_start = (TextView)findViewById(R.id.shop_profile_mon_start);
        business_time_mon_start.setOnClickListener(this);
        business_time_mon_end = (TextView)findViewById(R.id.shop_profile_mon_end);
        business_time_mon_end.setOnClickListener(this);
        business_time_mon_status = (SwitchCompat)findViewById(R.id.shop_profile_mon_switch);
        business_time_mon_status.setOnCheckedChangeListener(this);
        Map<String, Object> mon_map = oMapper.convertValue(shop.businessTime.get("Monday"), Map.class);
        if(shop.businessTime.containsKey("Monday")){
            if(mon_map.get("status").toString().equals("true")){
                business_time_mon_status.setChecked(true);
                mon_status = true;
            }else {
                business_time_mon_status.setChecked(false);
                mon_status = false;
            }
            business_time_mon_start.setText(mon_map.get("start").toString());
            business_time_mon_end.setText(mon_map.get("end").toString());
        }else {
            business_time_mon_status.setChecked(false);
            mon_status = false;
        }

        business_time_tue_start = (TextView)findViewById(R.id.shop_profile_tue_start);
        business_time_tue_start.setOnClickListener(this);
        business_time_tue_end = (TextView)findViewById(R.id.shop_profile_tue_end);
        business_time_tue_end.setOnClickListener(this);
        business_time_tue_status = (SwitchCompat)findViewById(R.id.shop_profile_tue_switch);
        business_time_tue_status.setOnCheckedChangeListener(this);
        if(shop.businessTime.containsKey("Tuesday")){
            Map<String, Object> tue_map = oMapper.convertValue(shop.businessTime.get("Tuesday"), Map.class);
            if(tue_map.get("status").toString().equals("true")){
                business_time_tue_status.setChecked(true);
                tue_status = true;
            }else {
                business_time_tue_status.setChecked(false);
                tue_status = false;
            }
            business_time_tue_start.setText(tue_map.get("start").toString());
            business_time_tue_end.setText(tue_map.get("end").toString());
        }else {
            business_time_tue_status.setChecked(false);
            tue_status = false;
        }

        business_time_wed_start = (TextView)findViewById(R.id.shop_profile_wed_start);
        business_time_wed_start.setOnClickListener(this);
        business_time_wed_end = (TextView)findViewById(R.id.shop_profile_wed_end);
        business_time_wed_end.setOnClickListener(this);
        business_time_wed_status = (SwitchCompat)findViewById(R.id.shop_profile_wed_switch);
        business_time_wed_status.setOnCheckedChangeListener(this);
        if(shop.businessTime.containsKey("Wednesday")){
            Map<String, Object> wed_map = oMapper.convertValue(shop.businessTime.get("Wednesday"), Map.class);
            if(wed_map.get("status").toString().equals("true")){
                business_time_wed_status.setChecked(true);
                wed_status = true;
            }else {
                business_time_wed_status.setChecked(false);
                wed_status = false;
            }
            business_time_wed_start.setText(wed_map.get("start").toString());
            business_time_wed_end.setText(wed_map.get("end").toString());
        }else {
            business_time_wed_status.setChecked(false);
            wed_status = false;
        }

        business_time_thu_start = (TextView)findViewById(R.id.shop_profile_thu_start);
        business_time_thu_start.setOnClickListener(this);
        business_time_thu_end = (TextView)findViewById(R.id.shop_profile_thu_end);
        business_time_thu_end.setOnClickListener(this);
        business_time_thu_status = (SwitchCompat)findViewById(R.id.shop_profile_thu_switch);
        business_time_thu_status.setOnCheckedChangeListener(this);
        if(shop.businessTime.containsKey("Thursday")){
            Map<String, Object> thu_map = oMapper.convertValue(shop.businessTime.get("Thursday"), Map.class);
            if(thu_map.get("status").toString().equals("true")){
                business_time_thu_status.setChecked(true);
                thu_status = true;
            }else {
                business_time_thu_status.setChecked(false);
                thu_status = false;
            }
            business_time_thu_start.setText(thu_map.get("start").toString());
            business_time_thu_end.setText(thu_map.get("end").toString());
        }else {
            business_time_thu_status.setChecked(false);
            thu_status = false;
        }

        business_time_fri_start = (TextView)findViewById(R.id.shop_profile_fri_start);
        business_time_fri_start.setOnClickListener(this);
        business_time_fri_end = (TextView)findViewById(R.id.shop_profile_fri_end);
        business_time_fri_end.setOnClickListener(this);
        business_time_fri_status = (SwitchCompat)findViewById(R.id.shop_profile_fri_switch);
        business_time_fri_status.setOnCheckedChangeListener(this);
        if(shop.businessTime.containsKey("Friday")){
            Map<String, Object> fri_map = oMapper.convertValue(shop.businessTime.get("Friday"), Map.class);
            if(fri_map.get("status").toString().equals("true")){
                business_time_fri_status.setChecked(true);
                fri_status = true;
            }else {
                business_time_fri_status.setChecked(false);
                fri_status = false;
            }
            business_time_fri_start.setText(fri_map.get("start").toString());
            business_time_fri_end.setText(fri_map.get("end").toString());
        }else {
            business_time_fri_status.setChecked(false);
            fri_status = false;
        }

        business_time_sat_start = (TextView)findViewById(R.id.shop_profile_sat_start);
        business_time_sat_start.setOnClickListener(this);
        business_time_sat_end = (TextView)findViewById(R.id.shop_profile_sat_end);
        business_time_sat_end.setOnClickListener(this);
        business_time_sat_status = (SwitchCompat)findViewById(R.id.shop_profile_sat_switch);
        business_time_sat_status.setOnCheckedChangeListener(this);
        if(shop.businessTime.containsKey("Saturday")){
            Map<String, Object> sat_map = oMapper.convertValue(shop.businessTime.get("Saturday"), Map.class);
            if(sat_map.get("status").toString().equals("true")){
                business_time_sat_status.setChecked(true);
                sat_status = true;
            }else {
                business_time_sat_status.setChecked(false);
                sat_status = false;
            }
            business_time_sat_start.setText(sat_map.get("start").toString());
            business_time_sat_end.setText(sat_map.get("end").toString());
        }else {
            business_time_sat_status.setChecked(false);
            sat_status = false;
        }

        business_time_sun_start = (TextView)findViewById(R.id.shop_profile_sun_start);
        business_time_sun_start.setOnClickListener(this);
        business_time_sun_end = (TextView)findViewById(R.id.shop_profile_sun_end);
        business_time_sun_end.setOnClickListener(this);
        business_time_sun_status = (SwitchCompat)findViewById(R.id.shop_profile_sun_switch);
        business_time_sun_status.setOnCheckedChangeListener(this);
        if(shop.businessTime.containsKey("Sunday")){
            Map<String, Object> sun_map = oMapper.convertValue(shop.businessTime.get("Sunday"), Map.class);
            if(sun_map.get("status").toString().equals("true")){
                business_time_sun_status.setChecked(true);
                sun_status = true;
            }else {
                business_time_sun_status.setChecked(false);
                sun_status = false;
            }
            business_time_sun_start.setText(sun_map.get("start").toString());
            business_time_sun_end.setText(sun_map.get("end").toString());
        }else {
            business_time_sun_status.setChecked(false);
            sun_status = false;
        }

        btn_done = (RelativeLayout)findViewById(R.id.shop_edit_profile_done);
        btn_done.setOnClickListener(this);

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int digits = phone.getText().toString().length();
                if (digits > 1)
                    lastChar = phone.getText().toString().substring(digits-1);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int digits = phone.getText().toString().length();
                if (!lastChar.equals("-")) {
                    if (digits == 3 || digits == 7) {
                        phone.append("-");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public String changePhoneFormat(String number){
        if(number.length()>3 && number.length()<7){
            return number.substring(0,3)+"-"+number.substring(3,number.length());
        }else if(number.length() > 7){
            return number.substring(0,3)+"-"+number.substring(3,6)+"-"+number.substring(6,number.length());
        }else {
            return number;
        }
    }

    public void showTimeerDlg(final String edit_type){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                if(edit_type.equals("mon_start")){
                    if(business_time_mon_end.getText().toString().isEmpty()){
                        business_time_mon_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_mon_end.getText().toString())){
                            business_time_mon_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("mon_end")){
                    if(business_time_mon_start.getText().toString().isEmpty()){
                        business_time_mon_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_mon_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_mon_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("tue_start")){
                    if(business_time_tue_end.getText().toString().isEmpty()){
                        business_time_tue_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_tue_end.getText().toString())){
                            business_time_tue_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("tue_end")){
                    if(business_time_tue_start.getText().toString().isEmpty()){
                        business_time_tue_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_tue_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_tue_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("wed_start")){
                    if(business_time_wed_end.getText().toString().isEmpty()){
                        business_time_wed_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_wed_end.getText().toString())){
                            business_time_wed_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("wed_end")){
                    if(business_time_wed_start.getText().toString().isEmpty()){
                        business_time_wed_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_wed_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_wed_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("thu_start")){
                    if(business_time_thu_end.getText().toString().isEmpty()){
                        business_time_thu_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_thu_end.getText().toString())){
                            business_time_thu_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("thu_end")){
                    if(business_time_thu_start.getText().toString().isEmpty()){
                        business_time_thu_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_thu_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_thu_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("fri_start")){
                    if(business_time_fri_end.getText().toString().isEmpty()){
                        business_time_fri_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_fri_end.getText().toString())){
                            business_time_fri_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("fri_end")){
                    if(business_time_fri_start.getText().toString().isEmpty()){
                        business_time_fri_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_fri_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_fri_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("sat_start")){
                    if(business_time_sat_end.getText().toString().isEmpty()){
                        business_time_sat_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_sat_end.getText().toString())){
                            business_time_sat_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("sat_end")){
                    if(business_time_sat_start.getText().toString().isEmpty()){
                        business_time_sat_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_sat_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_sat_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("sun_start")){
                    if(business_time_sun_end.getText().toString().isEmpty()){
                        business_time_sun_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(String.format("%02d:%02d", hourOfDay, minutes), business_time_sun_end.getText().toString())){
                            business_time_sun_start.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
                        }
                    }
                } else if(edit_type.equals("sun_end")){
                    if(business_time_sun_start.getText().toString().isEmpty()){
                        business_time_sun_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }else {
                        if(compareTime(business_time_sun_start.getText().toString(), String.format("%02d:%02d", hourOfDay, minutes))){
                            business_time_sun_end.setText(String.format("%02d:%02d", hourOfDay, minutes));
                        }else {
                            AlertFactory.showAlert(ShopEditProfileActivity.this, "", "Please input available time.");
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

    public boolean checkValidation(){
        if(name.getText().toString().isEmpty()){
            name.requestFocus();
            AlertFactory.showAlert(this, "", "Please input shop name. You must fill this field.");
            return false;
        }
        if(brief.getText().toString().isEmpty()){
            brief.requestFocus();
            AlertFactory.showAlert(this, "", "Please input brief. You must fill this field.");
            return false;
        }
        if(location.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please select location of your shop.");
            return false;
        }
        if(phone.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input phone number. You must fill this field");
            return false;
        }
        if(phone.getText().toString().length() != 12){
            AlertFactory.showAlert(this, "", "Please input correct phone number.");
            return false;
        }
        if(hourly_rate.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input hourly rate of your shop. You must fill this field");
            return false;
        }
        if(business_time_mon_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_mon_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_tue_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_tue_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_wed_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_wed_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_thu_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_thu_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_fri_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_fri_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_sat_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_sat_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_sun_start.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        if(business_time_sun_end.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input business time.");
            return false;
        }
        return true;
    }

    private void getAddress(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CONTENT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            bitmap = ImagePicker.getImageFromResult(this,resultCode,data);
            avatar.setImageBitmap(bitmap);
        }

        if (requestCode == PLACE_PICKER_REQUEST &&resultCode == Activity.RESULT_OK) {
            Place place = PlacePicker.getPlace(data,this);
            address = place.getAddress().toString();
            LatLng current_location = place.getLatLng();
            latitude = current_location.latitude;
            longitude = current_location.longitude;
            location.setText(address);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shop_edit_profile_back:
                super.onBackPressed();
                break;
            case R.id.shop_edit_profile_done:
                if(checkValidation()){
                    time_map = new HashMap();
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

                    uploadShopImage(shop.userID, name.getText().toString(),phone.getText().toString().replaceAll("-",""),
                            license.getText().toString(), hourly_rate.getText().toString(), brief.getText().toString(),location.getText().toString(),latitude, longitude, bitmap, time_map);
                }
                break;
            case R.id.shop_profile_upload_avatar:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_IMAGE_CONTENT);
                break;
            case R.id.shop_profile_location:
                getAddress();
                break;
            case R.id.shop_profile_mon_start:
                showTimeerDlg("mon_start");
                break;
            case R.id.shop_profile_mon_end:
                showTimeerDlg("mon_end");
                break;
            case R.id.shop_profile_tue_start:
                showTimeerDlg("tue_start");
                break;
            case R.id.shop_profile_tue_end:
                showTimeerDlg("tue_end");
                break;
            case R.id.shop_profile_wed_start:
                showTimeerDlg("wed_start");
                break;
            case R.id.shop_profile_wed_end:
                showTimeerDlg("wed_end");
                break;
            case R.id.shop_profile_thu_start:
                showTimeerDlg("thu_start");
                break;
            case R.id.shop_profile_thu_end:
                showTimeerDlg("thu_end");
                break;
            case R.id.shop_profile_fri_start:
                showTimeerDlg("fri_start");
                break;
            case R.id.shop_profile_fri_end:
                showTimeerDlg("fri_end");
                break;
            case R.id.shop_profile_sat_start:
                showTimeerDlg("sat_start");
                break;
            case R.id.shop_profile_sat_end:
                showTimeerDlg("sat_end");
                break;
            case R.id.shop_profile_sun_start:
                showTimeerDlg("sun_start");
                break;
            case R.id.shop_profile_sun_end:
                showTimeerDlg("sun_end");
                break;
        }
    }

    public void uploadShopImage(final String userId, final String shop_name, final String phone_number, final String license, final String hourly_rate, final String brief, final String address, final Double latitude, final Double longitude, final Bitmap avatar, final HashMap time_map){
        showProgressHUD("");
        if(avatar!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            avatar.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            Long tsLong = System.currentTimeMillis();
            StorageReference filepath = storePhoto.child("avatars").child(userId).child(tsLong + ".jpg");
            filepath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    avatarUrl = downloadUri.toString();
                    setShopInfo(userId, shop_name, phone_number, license, hourly_rate, brief,address, latitude, longitude, time_map);
                }
            });
        }else {
            setShopInfo(userId, shop_name, phone_number, license, hourly_rate, brief, address, latitude, longitude, time_map);
        }
    }

    public void setShopInfo(String userId, String shop_name, String phone_number, String license, String hourly_rate, String brief, String address, Double latitude, Double longitude, HashMap time_map){

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "fullName", shop_name);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "phone", phone_number);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "licenseNumber", license);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "description", brief);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "address", address);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "hourlyRate", Double.parseDouble(hourly_rate));
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "latitude", latitude);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "longitude", longitude);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "photoUrl", avatarUrl);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "businessTime", time_map);

        FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
                setFirebaseInfo();
            }
        });
    }

    public void setFirebaseInfo(){
        showProgressHUD("");
        mDatabase.child(DBInfo.TBL_USER).child(shop.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressHUD();
                if(dataSnapshot.exists()){
                    RepairShop repair_shop = dataSnapshot.getValue(RepairShop.class);
                    FirebaseManager.getInstance().setRepairShop(repair_shop);
                    goBack();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    public void goBack(){
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.shop_profile_mon_switch:
                mon_status = isChecked;
                break;
            case R.id.shop_profile_tue_switch:
                tue_status = isChecked;
                break;
            case R.id.shop_profile_wed_switch:
                wed_status = isChecked;
                break;
            case R.id.shop_profile_thu_switch:
                thu_status = isChecked;
                break;
            case R.id.shop_profile_fri_switch:
                fri_status = isChecked;
                break;
            case R.id.shop_profile_sat_switch:
                sat_status = isChecked;
                break;
            case R.id.shop_profile_sun_switch:
                sun_status = isChecked;
                break;
        }
    }
}
