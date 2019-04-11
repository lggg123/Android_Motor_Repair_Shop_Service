package com.brainyapps.motolabz.MechanicsView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.FirebaseManager;
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
import java.util.HashMap;
import java.util.Map;

public class MechanicEditProfileActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private ImageView btn_back;
    private ImageView avatar;
    private TextView btn_upload;
    private EditText name;
    private EditText phone;
    private EditText license;
    private EditText brief;
    private SwitchCompat location_service;
    private RelativeLayout btn_done;

    private Boolean islocation = false;
    String lastChar = " ";

    private Bitmap bitmap;

    private Mechanic myInfo;
    private DatabaseReference mDatabase;
    private String avatarUrl = "";
    private StorageReference storePhoto;

    public static final int REQUEST_EDIT_AVATAR_IMAGE_CONTENT = 3214;

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
        setContentView(R.layout.activity_mechanic_edit_profile);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        btn_back = (ImageView)findViewById(R.id.mechanic_edit_profile_back);
        btn_back.setOnClickListener(this);
        avatar = (ImageView)findViewById(R.id.img_mechanic_profile_avatar);
        btn_upload = (TextView)findViewById(R.id.mechanic_profile_upload_avatar);
        btn_upload.setOnClickListener(this);
        name = (EditText)findViewById(R.id.mechanic_profile_name);
        phone = (EditText)findViewById(R.id.mechanic_profile_phone);
        location_service = (SwitchCompat)findViewById(R.id.driver_profile_location_switch);
        location_service.setOnCheckedChangeListener(this);
        license = (EditText)findViewById(R.id.mechanic_profile_edit_license);
        brief = (EditText)findViewById(R.id.mechanic_profile_edit_brief);
        btn_done = (RelativeLayout)findViewById(R.id.mechanic_edit_profile_done);
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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storePhoto = FirebaseStorage.getInstance().getReference();
        myInfo = FirebaseManager.getInstance().getCurrentMechanic();
        if(!myInfo.photoUrl.isEmpty()){
            Glide.with(getApplication()).load(myInfo.photoUrl).into(avatar);
        }
        name.setText(myInfo.fullName);
        phone.setText(changePhoneFormat(myInfo.phone));

        if(myInfo.locationservice){
            location_service.setChecked(true);
            islocation = true;
        }else {
            location_service.setChecked(false);
            islocation = false;
        }
        license.setText(myInfo.licenseNumber.toString());
        brief.setText(myInfo.description);

        avatarUrl = myInfo.photoUrl;
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mechanic_edit_profile_back:
                super.onBackPressed();
                break;
            case R.id.mechanic_profile_upload_avatar:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_EDIT_AVATAR_IMAGE_CONTENT);
                break;
            case R.id.mechanic_edit_profile_done:
                if(checkValidation()){
                    uploadMechanicImage(myInfo.userID, name.getText().toString(), phone.getText().toString().replaceAll("-",""),
                            license.getText().toString(), brief.getText().toString(), bitmap, islocation);
                }
                break;
        }
    }

    public void uploadMechanicImage(final String userId, final String user_name, final String phone_number, final String license, final String brief, final Bitmap avatar, final Boolean isLocation){
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
                    setMechanicInfo(userId, user_name, phone_number, license, brief, isLocation);
                }
            });
        }else {
            setMechanicInfo(userId, user_name, phone_number, license, brief, isLocation);
        }
    }

    public void setMechanicInfo(String userId, String user_name, String phone_number, String license, String brief, Boolean isLocation){

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "fullName", user_name);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "phone", phone_number);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "licenseNumber", license);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "description", brief);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "photoUrl", avatarUrl);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + userId + "/" + "locationservice", isLocation);

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
        mDatabase.child(DBInfo.TBL_USER).child(myInfo.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressHUD();
                if(dataSnapshot.exists()){
                    Mechanic mechanic = dataSnapshot.getValue(Mechanic.class);
                    FirebaseManager.getInstance().setMechanic(mechanic);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_AVATAR_IMAGE_CONTENT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
            avatar.setImageBitmap(bitmap);
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
        if(phone.getText().toString().length()!=12){
            phone.requestFocus();
            AlertFactory.showAlert(this, "", "Please input correct phone number.");
            return false;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        islocation = isChecked;
    }
}
