package com.brainyapps.motolabz.ShopsView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.DriverVehicleDetailRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.MechanicsView.MechanicLocationMapActivity;
import com.brainyapps.motolabz.MessageActivity;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.TaskStatus;
import com.brainyapps.motolabz.Models.VehicleInfo;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopCustomerDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView onBack;
    private TextView vehicle_detail;
    private TextView additional_info;

    private CircleImageView avatarImg;
    private TextView title;
    private TextView driverName;
    private TextView requestTime;
    private LinearLayout btnCall;

    private RelativeLayout assignMechanic;
    private RelativeLayout viewLocation;
    private RelativeLayout contactClient;
    private RelativeLayout checkStatus;


    private Driver driver;
    private String taskId = "";
    private String userId = "";
    private String request_info = "";
    private String myId = FirebaseManager.getInstance().getUserId();

    private Dialog dialog;
    private ArrayList<VehicleInfo> vehicleList = new ArrayList<>();
    private RecyclerView driverVehicleItemRecyclerView;
    private DriverVehicleDetailRecyclerAdapter driverVehicleDetailRecyclerAdapter;


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
        setContentView(R.layout.activity_shop_customer_detail);
        showProgressHUD("");
        if (getIntent().getExtras() != null) {
            Intent i = getIntent();
            taskId = i.getStringExtra("requesterId");
        } else {
            super.onBackPressed();
        }
        onBack = (ImageView)findViewById(R.id.shop_customer_request_detail_back);
        onBack.setOnClickListener(this);
        vehicle_detail = (TextView)findViewById(R.id.shop_customer_request_vehicle_details);
        vehicle_detail.setOnClickListener(this);
        additional_info = (TextView)findViewById(R.id.shop_customer_request_additional_info);
        additional_info.setOnClickListener(this);

        assignMechanic = (RelativeLayout)findViewById(R.id.shop_customer_request_assign_mechanic);
        assignMechanic.setOnClickListener(this);
        viewLocation = (RelativeLayout)findViewById(R.id.shop_customer_request_view_location);
        viewLocation.setOnClickListener(this);
        contactClient = (RelativeLayout)findViewById(R.id.shop_customer_request_contact_client);
        contactClient.setOnClickListener(this);
        checkStatus = (RelativeLayout)findViewById(R.id.shop_customer_request_check_status);
        checkStatus.setOnClickListener(this);

        avatarImg = (CircleImageView)findViewById(R.id.shop_customer_detail_avatar);
        title = (TextView)findViewById(R.id.shop_customer_detail_title);
        driverName = (TextView)findViewById(R.id.shop_customer_detail_name);
        requestTime = (TextView)findViewById(R.id.shop_customer_detail_time);
        btnCall = (LinearLayout)findViewById(R.id.shop_customer_detail_call);
        btnCall.setOnClickListener(this);

        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_TASK).child(myId).child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dt : dataSnapshot.child("services").getChildren()){
                    request_info = request_info+dt.getKey().toString()+"\n";
                }
                TaskStatus task = dataSnapshot.getValue(TaskStatus.class);
                userId = task.customerID;
                requestTime.setText(Utils.converteTimestamp(task.time));
                getRequesterInfo(task.customerID);
                hideProgressHUD();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });

    }

    public void getRequesterInfo(String requesterId){
        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(requesterId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    driver = dataSnapshot.getValue(Driver.class);
                    if(!driver.photoUrl.isEmpty()){
                        Glide.with(getApplication()).load(driver.photoUrl).into(avatarImg);
                    }
                    if(driver.fullName.length()>15){
                        driverName.setText(driver.fullName.substring(0,15)+"...");
                    }else {
                        driverName.setText(driver.fullName);
                    }
                    title.setText(driver.fullName);
                }
                hideProgressHUD();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shop_customer_request_detail_back:
                super.onBackPressed();
                break;
            case R.id.shop_customer_request_vehicle_details:
                showDetailDlg();
                break;
            case R.id.shop_customer_request_additional_info:
                showAdditionalInfoDlg();
                break;
            case R.id.shop_customer_request_assign_mechanic:
                Intent assign_intent = new Intent(this, ShopAssignMechanicActivity.class);
                assign_intent.putExtra("taskId", taskId);
                assign_intent.putExtra("driverId", userId);
                startActivity(assign_intent);
                break;
            case R.id.shop_customer_request_view_location:
                Intent customer_map = new Intent(this, MechanicLocationMapActivity.class);
                customer_map.putExtra("driverId", userId);
                startActivity(customer_map);
                break;
            case R.id.shop_customer_request_contact_client:
                Intent msg_intent = new Intent(this, MessageActivity.class);
                msg_intent.putExtra("oppUserId", userId);
                startActivity(msg_intent);
                break;
            case R.id.shop_customer_request_check_status:
                Intent intent = new Intent(this, ShopCheckStatusActivity.class);
                intent.putExtra("taskId", taskId);
                startActivity(intent);
                break;
            case R.id.shop_customer_detail_call:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+driver.phone));

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                }
                startActivity(callIntent);
                break;
            default:
                break;
        }
    }

    public void showDetailDlg() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dlg_vehicle_detail);
        dialog.show();

        RelativeLayout btnSubmit = (RelativeLayout)dialog.findViewById(R.id.shop_driver_vehicle_info_btn_dlg);

        driverVehicleDetailRecyclerAdapter = new DriverVehicleDetailRecyclerAdapter(vehicleList);
        driverVehicleItemRecyclerView = (RecyclerView) dialog.findViewById(R.id.shop_driver_vehicle_recycler_viewer);
        driverVehicleItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        driverVehicleItemRecyclerView.setAdapter(driverVehicleDetailRecyclerAdapter);

        showProgressHUD("");
        Query vehicleInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(userId).child("vehicleInfo");
        vehicleInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    vehicleList.clear();
                    Map<String, Object> result = (Map<String, Object>)dataSnapshot.getValue();

                    for (Map.Entry<String, Object> entry : result.entrySet()){
                        Map singleEntry = (Map)entry.getValue();
                        VehicleInfo i = new VehicleInfo();
                        i.key = singleEntry.get("key").toString();
                        i.model = singleEntry.get("model").toString();
                        i.year = Integer.parseInt(singleEntry.get("year").toString());
                        i.engine = singleEntry.get("engine").toString();
                        i.transmission = singleEntry.get("transmission").toString();
                        i.manufacturer = singleEntry.get("manufacturer").toString();
                        i.vin = singleEntry.get("vin").toString();
                        i.vehicleImageUrl = singleEntry.get("vehicleImageUrl").toString();
                        vehicleList.add(i);
                    }
                    driverVehicleDetailRecyclerAdapter.notifyDataSetChanged();
                }
                hideProgressHUD();
                dialog.show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showAdditionalInfoDlg() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dlg_show_info);
        dialog.show();

        ImageView vehicleImg = (ImageView)dialog.findViewById(R.id.shop_info_dlg_driver_vehicle_image);
        TextView description = (TextView) dialog.findViewById(R.id.shop_info_dlg_driver_description);
        RelativeLayout btnSubmit = (RelativeLayout)dialog.findViewById(R.id.shop_driver_info_btn_dlg);
        TextView title = (TextView)dialog.findViewById(R.id.shop_info_dlg_service_title);
        title.setVisibility(View.VISIBLE);
        vehicleImg.setVisibility(View.GONE);

        description.setText(request_info);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
