package com.brainyapps.motolabz.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.VehicleModelRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.SignupVehicle;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.ImagePicker;
import com.brainyapps.motolabz.Utils.Utils;
import com.brainyapps.motolabz.Utils.VehicleInfoManager;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupAddVehicleFragment extends Fragment implements View.OnClickListener, VehicleModelRecyclerAdapter.OnClickItemListener{

    public static final String FRAGMENT_TAG = "com_mobile_signup_add_vehicle_fragment_tag";
    public static final int REQUEST_VEHICLE_IMAGE_CONTENT = 20140;

    private static Context mContext;

    private Dialog dlg;
    private ArrayList<String> modelList = new ArrayList<>();
    private RecyclerView modelItemRecyclerView;
    private VehicleModelRecyclerAdapter modelItemRecyclerAdapter;

    private ImageView btnBack;
    private RelativeLayout btnAdd;
    private ImageView btnRemove;

    private EditText etf_vehicle_year;
    private EditText etf_vehicle_model;
    private TextView etf_vehicle_manufacturer;
    private EditText etf_vehicle_engine;
    private EditText etf_vehicle_transmission;
    private EditText etf_vehicle_vin;
    private ImageView btnUploadVehicleImg;
    private ImageView vehicleImg;

    private Bitmap vehicleBitmap = null;

    private String currnet_key = "";

    private ViewGroup rootView;
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

        android.app.Fragment f = new SignupAddVehicleFragment();
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
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_signup_add_vehicle, container, false);
        Bundle b = this.getArguments();
        currnet_key = b.getString("key");

        btnBack = (ImageView)rootView.findViewById(R.id.signup_add_vehicle_btn_back);
        btnBack.setOnClickListener(this);
        btnAdd = (RelativeLayout)rootView.findViewById(R.id.signup_add_vehicle);
        btnAdd.setOnClickListener(this);
        btnRemove = (ImageView)rootView.findViewById(R.id.signup_add_vehicle_btn_remove);
        btnRemove.setOnClickListener(this);

        etf_vehicle_year = (EditText)rootView.findViewById(R.id.signup_vehicle_year);
        etf_vehicle_model = (EditText) rootView.findViewById(R.id.signup_vehicle_model);
        etf_vehicle_manufacturer = (TextView)rootView.findViewById(R.id.signup_vehicle_manufacturer);
        etf_vehicle_engine = (EditText)rootView.findViewById(R.id.signup_vehicle_engine);
        etf_vehicle_transmission = (EditText)rootView.findViewById(R.id.signup_vehicle_transmission);
        etf_vehicle_vin = (EditText)rootView.findViewById(R.id.signup_vehicle_vin);
        btnUploadVehicleImg = (ImageView)rootView.findViewById(R.id.signup_upload_vehicle_img);
        btnUploadVehicleImg.setOnClickListener(this);
        vehicleImg = (ImageView)rootView.findViewById(R.id.signup_vehicle_photo);

        etf_vehicle_manufacturer.setInputType(InputType.TYPE_NULL);
        etf_vehicle_manufacturer.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    showModelDlg();
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!currnet_key.isEmpty()){
            if(VehicleInfoManager.getInstance().getVehicleImgList().get(currnet_key)!=null){
                vehicleImg.setVisibility(View.VISIBLE);
                vehicleImg.setImageBitmap(VehicleInfoManager.getInstance().getVehicleImgList().get(currnet_key));
            }
            btnRemove.setVisibility(View.VISIBLE);
            SignupVehicle info = VehicleInfoManager.getInstance().getVehicleInfoList().get(currnet_key);
            setSelectedValue(info);

        }else {
            btnRemove.setVisibility(View.GONE);
            if(!etf_vehicle_year.getText().toString().isEmpty()){
                etf_vehicle_year.setText("");
            }
            if(!etf_vehicle_model.getText().toString().isEmpty()){
                etf_vehicle_model.setText("");
            }
            if(!etf_vehicle_engine.getText().toString().isEmpty()){
                etf_vehicle_engine.setText("");
            }
            if(!etf_vehicle_manufacturer.getText().toString().isEmpty()){
                etf_vehicle_manufacturer.setText("");
            }
            if(!etf_vehicle_transmission.getText().toString().isEmpty()){
                etf_vehicle_transmission.setText("");
            }
            if(!etf_vehicle_vin.getText().toString().isEmpty()){
                etf_vehicle_vin.setText("");
            }
        }
    }

    public void setSelectedValue(SignupVehicle info){
        etf_vehicle_model.setText(info.model);
        etf_vehicle_engine.setText(info.engine);
        etf_vehicle_manufacturer.setText(info.manufacturer);
        etf_vehicle_transmission.setText(info.transmission);
        etf_vehicle_vin.setText(info.vin);
        etf_vehicle_year.setText(info.year.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VEHICLE_IMAGE_CONTENT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            vehicleImg.setVisibility(View.VISIBLE);
            vehicleBitmap = ImagePicker.getImageFromResult(getActivity(),resultCode,data);
            vehicleImg.setImageBitmap(vehicleBitmap);
        }
    }

    public boolean checkValidation(){
        if(etf_vehicle_year.getText().toString().isEmpty()){
            etf_vehicle_year.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input year of your vehicle.");
            return false;
        }
        if(!Utils.isInterger(etf_vehicle_year.getText().toString())){
            etf_vehicle_year.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input invalid year. Please input again.");
            return false;
        }
        if(etf_vehicle_model.getText().toString().isEmpty()){
            etf_vehicle_model.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input manufacturer of your vehicle.");
            return false;
        }
        if(etf_vehicle_engine.getText().toString().isEmpty()){
            etf_vehicle_engine.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input engine type of your vehicle.");
            return false;
        }
        if(etf_vehicle_manufacturer.getText().toString().isEmpty()){
            etf_vehicle_manufacturer.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input manufacturer of your vehicle.");
            return false;
        }
        if(etf_vehicle_transmission.getText().toString().isEmpty()){
            etf_vehicle_transmission.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input transmission of your vehicle.");
            return false;
        }
        if(etf_vehicle_vin.getText().toString().isEmpty()){
            etf_vehicle_vin.requestFocus();
            AlertFactory.showAlert(mContext, "", "Please input VIN.");
            return false;
        }
        return true;
    }

    public void initialize(){
        vehicleImg.setVisibility(View.GONE);
        vehicleBitmap = null;
        if(!etf_vehicle_year.getText().toString().isEmpty()){
            etf_vehicle_year.setText("");
        }
        if(!etf_vehicle_model.getText().toString().isEmpty()){
            etf_vehicle_model.setText("");
        }
        if(!etf_vehicle_engine.getText().toString().isEmpty()){
            etf_vehicle_engine.setText("");
        }
        if(!etf_vehicle_manufacturer.getText().toString().isEmpty()){
            etf_vehicle_manufacturer.setText("");
        }
        if(!etf_vehicle_transmission.getText().toString().isEmpty()){
            etf_vehicle_transmission.setText("");
        }
        if(!etf_vehicle_vin.getText().toString().isEmpty()){
            etf_vehicle_vin.setText("");
        }
    }

    public void showModelDlg(){
        dlg = new Dialog(getActivity());
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dlg_model_info);

        modelItemRecyclerAdapter = new VehicleModelRecyclerAdapter(modelList);
        modelItemRecyclerView = (RecyclerView)dlg.findViewById(R.id.dlg_model_recycler_view);
        modelItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        modelItemRecyclerView.setAdapter(modelItemRecyclerAdapter);
        modelItemRecyclerAdapter.setOnClickItemListener(this);

        showProgressHUD("");
        Query userInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_MODELS);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    modelList.clear();
                    Map<String,Object> result = (Map<String,Object>)dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : result.entrySet()){
                        modelList.add(entry.getKey().toString());
                    }
                    modelItemRecyclerAdapter.notifyDataSetChanged();
                }
                hideProgressHUD();
                dlg.show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    @Override
    public void clickModelItem(int index, String model_name) {
        etf_vehicle_manufacturer.setText(model_name);
        dlg.hide();
    }

    public OnSignupAddVehicleListener mListener;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup_upload_vehicle_img:
                Intent vehicel_intent = new Intent();
                vehicel_intent.setType("image/*");
                vehicel_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(vehicel_intent, "Select Picture"),REQUEST_VEHICLE_IMAGE_CONTENT);
                break;
            case R.id.signup_add_vehicle:
                if(checkValidation()){
                    if(!currnet_key.isEmpty()){
                        VehicleInfoManager.getInstance().removeVehicleInfo(currnet_key);
                        VehicleInfoManager.getInstance().removeVehicleImg(currnet_key);
                        SignupVehicle info = new SignupVehicle();
                        info.model = etf_vehicle_model.getText().toString();
                        info.engine = etf_vehicle_engine.getText().toString();
                        info.year = Integer.parseInt(etf_vehicle_year.getText().toString());
                        info.manufacturer = etf_vehicle_manufacturer.getText().toString();
                        info.transmission = etf_vehicle_transmission.getText().toString();
                        info.vin = etf_vehicle_vin.getText().toString();
                        info.key = currnet_key;
                        VehicleInfoManager.getInstance().addVehicleInfo(currnet_key,info);
                        VehicleInfoManager.getInstance().addVehicleImg(currnet_key,vehicleBitmap);
                    }else {
                        String key = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_EMAIL).push().getKey();
                        SignupVehicle info = new SignupVehicle();
                        info.model = etf_vehicle_model.getText().toString();
                        info.engine = etf_vehicle_engine.getText().toString();
                        info.year = Integer.parseInt(etf_vehicle_year.getText().toString());
                        info.manufacturer = etf_vehicle_manufacturer.getText().toString();
                        info.transmission = etf_vehicle_transmission.getText().toString();
                        info.vin = etf_vehicle_vin.getText().toString();
                        info.key = key;
                        VehicleInfoManager.getInstance().addVehicleInfo(key,info);
                        VehicleInfoManager.getInstance().addVehicleImg(key,vehicleBitmap);
                    }
                    if (mListener != null) {
                        mListener.onSignupAddVehicle();
                    }
                }
                break;
            case R.id.signup_add_vehicle_btn_back:
                if (mListener != null) {
                    mListener.onBacktoSignupInfo();
                }
                break;
            case R.id.signup_add_vehicle_btn_remove:
                VehicleInfoManager.getInstance().removeVehicleImg(currnet_key);
                VehicleInfoManager.getInstance().removeVehicleInfo(currnet_key);
                if (mListener != null) {
                    mListener.onBacktoSignupInfo();
                }
                break;
        }
    }

    public interface OnSignupAddVehicleListener {
        void onSignupAddVehicle();
        void onBacktoSignupInfo();
    }

    public void setOnSignupAddVehicleListener(OnSignupAddVehicleListener listener) {
        mListener = listener;
    }
}
