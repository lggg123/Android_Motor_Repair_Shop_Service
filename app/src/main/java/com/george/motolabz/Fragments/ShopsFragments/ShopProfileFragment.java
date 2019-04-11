package com.brainyapps.motolabz.Fragments.ShopsFragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.ShopsView.ShopAddServiceActivity;
import com.brainyapps.motolabz.ShopsView.ShopEditProfileActivity;
import com.brainyapps.motolabz.ShopsView.ShopInviteMechanicsActivity;
import com.brainyapps.motolabz.ShopsView.ShopMainActivity;
import com.brainyapps.motolabz.ShopsView.ShopMechanicsListActivity;
import com.brainyapps.motolabz.ShopsView.ShopModelsActivity;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.walnutlabs.android.ProgressHUD;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShopProfileFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private ImageView shop_image;
    private TextView shop_address;
    private TextView shop_business_time;
    private TextView hourly_rate;
    private TextView shop_description;

    private ImageView btnEdit;
    private RelativeLayout serviceRequests;
    private RelativeLayout listModelService;
    private RelativeLayout serviceOffered;
    private RelativeLayout mechanics;
    private RelativeLayout inviteMechanics;
    private TextView new_address;

    private SwitchCompat switch_available;
    private TextView text_available;

    private String myId = FirebaseManager.getInstance().getUserId();
    private String address;
    private Double latitude = 0.0d;
    private Double longitude = 0.0d;

    private RepairShop shop;
    private StorageReference storePhoto = FirebaseStorage.getInstance().getReference();
    public static final String FRAGMENT_TAG = "com_motolabz_shop_profile_fragment_tag";
    private static Context mContext;
    private static int PLACE_PICKER_REQUEST = 501;
    public static final int REQUEST_IMAGE_CONTENT = 2212;
    private Bitmap bitmap;
    private Boolean isValidBusinessTime = false;

    private ProgressHUD mProgressDialog;

    private void showProgressHUD(String text) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = ProgressHUD.show(getActivity(), text, true);
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

        android.app.Fragment f = new ShopProfileFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_shop_profile, container, false);

        shop_image = (ImageView)rootView.findViewById(R.id.shop_profile_image);
        shop_image.setOnClickListener(this);
        shop_address = (TextView)rootView.findViewById(R.id.shop_profile_address);
        shop_business_time = (TextView)rootView.findViewById(R.id.shop_profile_business_time);
        hourly_rate = (TextView)rootView.findViewById(R.id.shop_profile_hourly_rate);
        shop_description = (TextView)rootView.findViewById(R.id.shop_profile_description);
        switch_available = (SwitchCompat)rootView.findViewById(R.id.shop_available_switch);
        switch_available.setOnCheckedChangeListener(this);
        text_available = (TextView)rootView.findViewById(R.id.shop_available_text);

        btnEdit = (ImageView)rootView.findViewById(R.id.shop_profile_edit);
        btnEdit.setOnClickListener(this);

        serviceRequests = (RelativeLayout)rootView.findViewById(R.id.shop_profile_service_requests);
        serviceRequests.setOnClickListener(this);
        listModelService = (RelativeLayout)rootView.findViewById(R.id.shop_profile_serviced_models);
        listModelService.setOnClickListener(this);
        serviceOffered = (RelativeLayout)rootView.findViewById(R.id.shop_profile_service_offered);
        serviceOffered.setOnClickListener(this);
        mechanics = (RelativeLayout)rootView.findViewById(R.id.shop_profile_mechanics);
        mechanics.setOnClickListener(this);
        inviteMechanics = (RelativeLayout)rootView.findViewById(R.id.shop_profile_invite_mechanic);
        inviteMechanics.setOnClickListener(this);

        return rootView;
    }

    public void getInfo(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);

        shop = FirebaseManager.getInstance().getCurrentRepairShop();
        if(!shop.photoUrl.isEmpty()){
            Glide.with(getActivity()).load(shop.photoUrl).into(shop_image);
        }
        shop_address.setText(shop.address);
        shop_description.setText(shop.description);
        hourly_rate.setText("Hourly Rate: $" + shop.hourlyRate.toString());

        switch_available.setChecked(shop.available);
        if(shop.available){
            text_available.setText("(Available)");
        }else {
            text_available.setText("(Unavailable)");
        }
        ObjectMapper oMapper = new ObjectMapper();
        if(shop.businessTime.containsKey(dayOfTheWeek)){
            Map<String, Object> time_map = oMapper.convertValue(shop.businessTime.get(dayOfTheWeek), Map.class);
            if(time_map.get("status").toString().equals("true")){
                shop_business_time.setText(dayOfTheWeek+" : "+time_map.get("start").toString()+" to "+time_map.get("end").toString());
            }else {
                shop_business_time.setText(dayOfTheWeek+" : (Closed)");
            }
        }else {
            shop_business_time.setText(dayOfTheWeek+" : (Closed)");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shop_profile_edit:
                Intent edit_profile_intent = new Intent(getActivity(), ShopEditProfileActivity.class);
                startActivity(edit_profile_intent);
//                showDlg();
                break;
            case R.id.shop_profile_service_requests:
                ((ShopMainActivity)getActivity()).showFragment(1, true);
                break;
            case R.id.shop_profile_serviced_models:
                Intent serviced_models_intent = new Intent(getActivity(), ShopModelsActivity.class);
                startActivity(serviced_models_intent);
                break;
            case R.id.shop_profile_service_offered:
                Intent offered_intent = new Intent(getActivity(), ShopAddServiceActivity.class);
                startActivity(offered_intent);
                break;
            case R.id.shop_profile_mechanics:
                Intent mechanic_intent = new Intent(getActivity(), ShopMechanicsListActivity.class);
                startActivity(mechanic_intent);
                break;
            case R.id.shop_profile_invite_mechanic:
                Intent invite_intent = new Intent(getActivity(), ShopInviteMechanicsActivity.class);
                startActivity(invite_intent);
                break;
            case R.id.shop_profile_image:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_IMAGE_CONTENT);
                break;
            default:
                break;
        }
    }

    public void showDlg(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dlg_edit_profile);
        dialog.show();

        final EditText shop_name = (EditText)dialog.findViewById(R.id.dlg_shop_edit_profile_shop_name);
        new_address = (TextView) dialog.findViewById(R.id.dlg_shop_edit_profile_shop_address);
        final EditText shop_description = (EditText)dialog.findViewById(R.id.dlg_shop_edit_profile_shop_description);
        final EditText business_hours = (EditText)dialog.findViewById(R.id.dlg_shop_edit_profile_shop_business_time);
        RelativeLayout submit = (RelativeLayout) dialog.findViewById(R.id.dlg_shop_edit_profile_submit);

        shop_name.setText(shop.fullName);
        new_address.setText(shop.address);
        new_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddress();
            }
        });
        shop_description.setText(shop.description);
//        business_hours.setText(shop.businessTime);

        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!shop_name.getText().toString().isEmpty()){
                    if(!new_address.getText().toString().isEmpty()){
                        if(!business_hours.getText().toString().isEmpty()){
                            if(!shop_description.getText().toString().isEmpty()){
                                updateProfile(shop_name.getText().toString(), business_hours.getText().toString(), shop_description.getText().toString());
                                dialog.dismiss();
                            }else {
                                shop_description.requestFocus();
                                showAlert("Please input shop description please!");
                            }
                        }else {
                            business_hours.requestFocus();
                            showAlert("Please input business hours of your shop please!");
                        }
                    }else {
                        showAlert("Please input shop address please!");
                    }
                }else {
                    shop_name.requestFocus();
                    showAlert("Please input shop name please!");
                }
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CONTENT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            showProgressHUD("");
            bitmap = ImagePicker.getImageFromResult(getActivity(),resultCode,data);
            shop_image.setImageBitmap(bitmap);
            if(bitmap!=null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] new_data = stream.toByteArray();
                Long tsLong = System.currentTimeMillis();
                StorageReference filepath = storePhoto.child("attaches").child(myId).child(tsLong + ".jpg");
                filepath.putBytes(new_data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        String image_url = downloadUri.toString();
                        setPhotoUrl(image_url);
                    }
                });
            }
        }

        if (requestCode == PLACE_PICKER_REQUEST &&resultCode == Activity.RESULT_OK) {
            Place place = PlacePicker.getPlace(data,getActivity());
            address = place.getAddress().toString();
            LatLng current_location = place.getLatLng();
            latitude = current_location.latitude;
            longitude = current_location.longitude;
            new_address.setText(address);
        }
    }

    private void setPhotoUrl(String url){
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("photoUrl").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
            }
        });
    }

    public void updateProfile(String name, String b_hour, String description){
        if(address.length()>27){
            shop_address.setText(address.substring(0,26)+"...");
        }else {
            shop_address.setText(address);
        }
        shop_business_time.setText(b_hour);
        if(description.length() > 90){
            shop_description.setText(description.substring(0,90)+"...");
        }else {
            shop_description.setText(description);
        }

        showProgressHUD("");
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + myId + "/fullName", name);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + myId + "/businessTime", b_hour);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + myId + "/address", address);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + myId + "/latitude", latitude);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + myId + "/longitude", longitude);
        userUpdates.put("/" + DBInfo.TBL_USER + "/" + myId + "/description", description);
        FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
                showAlert("Successfully Saved!");
            }
        });
    }

    public void showAlert(String message){
        AlertFactory.showAlert(getActivity(),"Edit Profile", message);
    }

    @Override
    public void onResume() {
        super.onResume();
        getInfo();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.shop_available_switch:
                if(isChecked){
                    text_available.setText("(Available)");
                }else {
                    text_available.setText("(Unavailable)");
                }
                setAvailableValue(isChecked);
//                if(isValidBusinessTime){
//                    setAvailableValue(isChecked);
//                    if(isChecked){
//                        text_available.setText("(Available)");
//                    }else {
//                        text_available.setText("(Unavailable)");
//                    }
//                }else {
//                    AlertFactory.showAlert(getActivity(),"","Your shop closed. It's not your business time. To open your shop, please change business time.");
//                    switch_available.setChecked(false);
//                }
                break;
        }
    }

    public void setAvailableValue(Boolean isChecked){
        shop.available = isChecked;
        FirebaseManager.getInstance().setRepairShop(shop);
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(shop.userID).child("available").setValue(isChecked);
    }
}
