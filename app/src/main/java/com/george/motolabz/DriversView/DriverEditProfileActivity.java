package com.brainyapps.motolabz.DriversView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.brainyapps.motolabz.Adapters.VehicleInfoRecyclerAdapter;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.walnutlabs.android.ProgressHUD;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DriverEditProfileActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, VehicleInfoRecyclerAdapter.OnClickItemListener{

    private ImageView onBack;
    private RelativeLayout addVehicle;
    private TextView title;
    private TextView btnSave;
    private ImageView avatarImg;
    private TextView btnUploadAvatar;
    private EditText name;
    private EditText phone;
    private SwitchCompat locationService;
    private Boolean isLocationService = false;

    private RecyclerView vehicleRecyclerView;
    private VehicleInfoRecyclerAdapter vehicleInfoRecyclerAdapter;
    private ArrayList<VehicleInfo> vehicleList = new ArrayList<>();

    private Bitmap bitmap;

    private Driver myInfo;
    private DatabaseReference mDatabase;
    private String avatarUrl = "";
    private int count = 0;
    String lastChar = " ";

    private StorageReference storePhoto;

    public static final int REQUEST_EDIT_AVATAR_IMAGE_CONTENT = 3014;
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
        setContentView(R.layout.activity_driver_edit_profile);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        onBack = (ImageView) findViewById(R.id.driver_edit_profile_back);
        onBack.setOnClickListener(this);
        btnSave = (TextView) findViewById(R.id.shop_save_driver_profile);
        btnSave.setOnClickListener(this);
        addVehicle = (RelativeLayout) findViewById(R.id.driver_edit_profile_add_vehicle);
        addVehicle.setOnClickListener(this);
        title = (TextView)findViewById(R.id.driver_edit_profile_title);
        avatarImg = (ImageView)findViewById(R.id.img_driver_profile_avatar);
        btnUploadAvatar = (TextView)findViewById(R.id.driver_profile_upload_avatar);
        btnUploadAvatar.setOnClickListener(this);
        name = (EditText)findViewById(R.id.driver_profile_name);
        phone = (EditText)findViewById(R.id.driver_profile_phone);
        locationService = (SwitchCompat) findViewById(R.id.driver_profile_location_switch);
        locationService.setOnCheckedChangeListener(this);

        vehicleRecyclerView = (RecyclerView)findViewById(R.id.driver_profile_vehicle_recycler_view);
        vehicleInfoRecyclerAdapter = new VehicleInfoRecyclerAdapter(vehicleList);
        vehicleRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        vehicleRecyclerView.setAdapter(vehicleInfoRecyclerAdapter);
        vehicleInfoRecyclerAdapter.setOnClickItemListener(this);

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


        mDatabase = FirebaseDatabase.getInstance().getReference();
        storePhoto = FirebaseStorage.getInstance().getReference();
        mDatabase.child(DBInfo.TBL_USER).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressHUD();
                myInfo = dataSnapshot.getValue(Driver.class);
                if(!myInfo.photoUrl.isEmpty()){
                    Glide.with(getApplication()).load(myInfo.photoUrl).into(avatarImg);
                }
                name.setText(myInfo.fullName);
                phone.setText(changePhoneFormat(myInfo.phone));
                title.setText(myInfo.fullName);

                if(myInfo.locationservice){
                    locationService.setChecked(true);
                    isLocationService = true;
                }else {
                    locationService.setChecked(false);
                    isLocationService = false;
                }
                avatarUrl = myInfo.photoUrl;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    public void updateList(){
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("vehicleInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vehicleList.clear();
                for(DataSnapshot task : dataSnapshot.getChildren()){
                    VehicleInfo vi = task.getValue(VehicleInfo.class);
                    vehicleList.add(vi);
                }
                vehicleInfoRecyclerAdapter.notifyDataSetChanged();
                vehicleRecyclerView.smoothScrollToPosition(vehicleList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_edit_profile_back:
                super.onBackPressed();
                break;
            case R.id.shop_save_driver_profile:
                updateProfile();
                break;
            case R.id.driver_profile_upload_avatar:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_EDIT_AVATAR_IMAGE_CONTENT);
                break;
            case R.id.driver_edit_profile_add_vehicle:
                Intent add_vehicle = new Intent(this, DriverEditVehicleActivity.class);
                add_vehicle.putExtra("userID", myInfo.userID);
                add_vehicle.putExtra("vehicleKey", "");
                startActivity(add_vehicle);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_AVATAR_IMAGE_CONTENT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            bitmap = ImagePicker.getImageFromResult(this,resultCode,data);
            avatarImg.setImageBitmap(bitmap);
        }
    }

    public boolean checkValidation(){
        if(name.getText().toString().isEmpty()){
            name.requestFocus();
            AlertFactory.showAlert(this, "", "Please input your name.");
            return false;
        }
        if(phone.getText().toString().isEmpty()){
            phone.requestFocus();
            AlertFactory.showAlert(this, "", "Please input your phone number.");
            return false;
        }
        return true;
    }

    public void updateProfile(){
        if(checkValidation()){
            uploadDriverImage(myInfo.userID, name.getText().toString(), phone.getText().toString().replaceAll("-",""), bitmap, isLocationService);
        }
    }

    public void uploadDriverImage(final String userId, final String user_name, final String phone_number, final Bitmap avatar, final Boolean isLocation){
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
                    count = count + 1;
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    avatarUrl = downloadUri.toString();
                    setDriverInfo(userId, user_name, phone_number, isLocation);
                }
            });
        }else {
            setDriverInfo(userId, user_name, phone_number, isLocation);
        }
    }

    public void setDriverInfo(String userId, String user_name, String phone_number, Boolean isLocation){

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "fullName", user_name);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "phone", phone_number);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "photoUrl", avatarUrl);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "locationservice", isLocation);

        FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
                goBack();
            }
        });
    }

    public void goBack(){
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.driver_profile_location_switch:
                isLocationService = b;
                break;
            default:
                break;
        }
    }

    @Override
    public void clickModelItem(int index, String vehicle_key) {
        Intent add_vehicle = new Intent(this, DriverEditVehicleActivity.class);
        add_vehicle.putExtra("userID", myInfo.userID);
        add_vehicle.putExtra("vehicleKey", vehicle_key);
        startActivity(add_vehicle);
    }
}
