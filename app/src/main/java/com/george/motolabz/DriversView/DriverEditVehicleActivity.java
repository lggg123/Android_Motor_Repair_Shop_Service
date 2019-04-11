package com.brainyapps.motolabz.DriversView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.VehicleModelRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.VehicleInfo;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.ImagePicker;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.walnutlabs.android.ProgressHUD;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DriverEditVehicleActivity extends AppCompatActivity implements View.OnClickListener, VehicleModelRecyclerAdapter.OnClickItemListener{

    private ImageView btnBack;
    private ImageView btnDelete;
    private EditText year;
    private EditText model;
    private TextView manufacturer;
    private EditText engine;
    private EditText transmission;
    private EditText vin;
    private RelativeLayout btnSave;

    private ImageView vehicleImg;
    private ImageView btnUploadVehicleImg;

    private Bitmap vehicleBitmap;
    private String vehicleUrl = "";

    private Dialog dlg;
    private ArrayList<String> modelList = new ArrayList<>();
    private RecyclerView modelItemRecyclerView;
    private VehicleModelRecyclerAdapter modelItemRecyclerAdapter;

    private VehicleInfo currentInfo;
    private DatabaseReference mDatabase;
    private StorageReference storePhoto;

    private String userId = "";
    private String vehicleKey = "";

    public static final int REQUEST_EDIT_VEHICLE_IMAGE_CONTENT =3015;

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
        setContentView(R.layout.activity_driver_edit_vehicle);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            userId = i.getStringExtra("userID");
            vehicleKey = i.getStringExtra("vehicleKey");
        }else {
            super.onBackPressed();
        }

        btnBack = (ImageView)findViewById(R.id.driver_edit_vehicle_back);
        btnBack.setOnClickListener(this);
        btnDelete = (ImageView)findViewById(R.id.shop_delete_vehicle_item);
        btnDelete.setOnClickListener(this);
        if(vehicleKey.isEmpty()){
            btnDelete.setVisibility(View.GONE);
        }else {
            btnDelete.setVisibility(View.VISIBLE);
        }

        vehicleImg = (ImageView)findViewById(R.id.driver_profile_vehicle_photo);
        btnUploadVehicleImg = (ImageView)findViewById(R.id.driver_profile_upload_vehicle_img);
        btnUploadVehicleImg.setOnClickListener(this);

        year = (EditText)findViewById(R.id.driver_profile_vehicle_year);
        model = (EditText)findViewById(R.id.driver_profile_vehicle_model);
        manufacturer = (TextView)findViewById(R.id.driver_profile_vehicle_manufacturer);
        engine = (EditText)findViewById(R.id.driver_profile_vehicle_engine);
        transmission = (EditText)findViewById(R.id.driver_profile_vehicle_transmission);
        vin = (EditText)findViewById(R.id.driver_profile_vin);

        btnSave = (RelativeLayout)findViewById(R.id.driver_edit_vehicle_done);
        btnSave.setOnClickListener(this);

        manufacturer.setInputType(InputType.TYPE_NULL);
        manufacturer.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    showModelDlg();
                }
                return false;
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storePhoto = FirebaseStorage.getInstance().getReference();
        if(!vehicleKey.isEmpty()){
            mDatabase.child(DBInfo.TBL_USER).child(userId).child("vehicleInfo").child(vehicleKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    hideProgressHUD();
                    currentInfo = dataSnapshot.getValue(VehicleInfo.class);
                    model.setText(currentInfo.model);
                    year.setText(currentInfo.year.toString());
                    manufacturer.setText(currentInfo.manufacturer);
                    transmission.setText(currentInfo.transmission);
                    vin.setText(currentInfo.vin);
                    if(!currentInfo.vehicleImageUrl.isEmpty()){
                        vehicleImg.setVisibility(View.VISIBLE);
                        Glide.with(getApplication()).load(currentInfo.vehicleImageUrl).into(vehicleImg);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgressHUD();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.driver_profile_upload_vehicle_img:
                Intent vehicel_intent = new Intent();
                vehicel_intent.setType("image/*");
                vehicel_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(vehicel_intent, "Select Picture"),REQUEST_EDIT_VEHICLE_IMAGE_CONTENT);
                break;
            case R.id.driver_edit_vehicle_back:
                goBack();
                break;
            case R.id.shop_delete_vehicle_item:
                if(!vehicleKey.isEmpty()){
                    FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(userId).child("vehicleInfo").child(vehicleKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            goBack();
                        }
                    });
                }
                break;
            case R.id.driver_edit_vehicle_done:
                updateVehicleInfo();
                break;
        }
    }

    public void goBack(){
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_VEHICLE_IMAGE_CONTENT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            vehicleImg.setVisibility(View.VISIBLE);
            vehicleBitmap = ImagePicker.getImageFromResult(this,resultCode,data);
            vehicleImg.setImageBitmap(vehicleBitmap);
        }
    }

    public boolean checkValidation(){
        if(year.getText().toString().isEmpty()){
            year.requestFocus();
            AlertFactory.showAlert(this, "", "Please input year of your vehicle.");
            return false;
        }
        if(model.getText().toString().isEmpty()){
            model.requestFocus();
            AlertFactory.showAlert(this, "", "Please input model of your vehicle.");
            return false;
        }
        if(engine.getText().toString().isEmpty()){
            engine.requestFocus();
            AlertFactory.showAlert(this, "", "Please input engine type of your vehicle.");
            return false;
        }
        if(manufacturer.getText().toString().isEmpty()){
            manufacturer.requestFocus();
            AlertFactory.showAlert(this, "", "Please input manufacturer of your vehicle.");
            return false;
        }
        if(transmission.getText().toString().isEmpty()){
            transmission.requestFocus();
            AlertFactory.showAlert(this, "", "Please input transmission of your vehicle.");
            return false;
        }
        return true;
    }

    public void updateVehicleInfo(){
        if(checkValidation()){
            uploadVehicleImage(vehicleBitmap);
        }
    }

    public void uploadVehicleImage(Bitmap vehicle_img){
        showProgressHUD("");
        if(vehicle_img!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            vehicle_img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            Long tsLong = System.currentTimeMillis();
            StorageReference filepath = storePhoto.child("vehicles").child(userId).child(tsLong + ".jpg");
            filepath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    vehicleUrl = downloadUri.toString();
                    uploadVehicleInfo();
                }
            });
        }else {
            uploadVehicleInfo();
        }
    }

    public void uploadVehicleInfo(){
        if(vehicleKey.isEmpty()){
            vehicleKey = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(userId).child("vehicleInfo").push().getKey();
        }

        VehicleInfo info = new VehicleInfo();
        info.key = vehicleKey;
        info.model = model.getText().toString();
        info.year = Integer.parseInt(year.getText().toString());
        info.engine = engine.getText().toString();
        info.manufacturer = manufacturer.getText().toString();
        info.transmission = transmission.getText().toString();
        info.vin = vin.getText().toString();
        info.vehicleImageUrl = vehicleUrl;

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "vehicleInfo/" + vehicleKey, info);
        FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
                goBack();
            }
        });
    }

    public void showModelDlg(){
        dlg = new Dialog(this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dlg_model_info);

        modelItemRecyclerAdapter = new VehicleModelRecyclerAdapter(modelList);
        modelItemRecyclerView = (RecyclerView)dlg.findViewById(R.id.dlg_model_recycler_view);
        modelItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        modelItemRecyclerView.setAdapter(modelItemRecyclerAdapter);
        modelItemRecyclerAdapter.setOnClickItemListener(this);

//        modelItemRecyclerView.setVisibility(View.GONE);
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
        manufacturer.setText(model_name);
        dlg.hide();
    }
}
