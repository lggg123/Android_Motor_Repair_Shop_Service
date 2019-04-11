
package com.brainyapps.motolabz.MechanicsView;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.DriverVehicleDetailRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.MessageActivity;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.TaskStatus;
import com.brainyapps.motolabz.Models.VehicleInfo;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MechanicRequestDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView onBack;
    private TextView vehicle_detail;
    private TextView additional_info;

    private CircleImageView avatarImg;
    private TextView title;
    private TextView driverName;
    private TextView requestTime;

    private RelativeLayout viewLocation;
    private RelativeLayout contactClient;
    private RelativeLayout updateShop;

    private RelativeLayout btnAccept;
    private RelativeLayout btnDecline;

    private Driver driver;
    private String taskId = "";
    private String shopId = "";
    private String userId = "";
    private String requestStatus = "";
    private String request_info = "";
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        setContentView(R.layout.activity_mechanic_request_details);

        if (getIntent().getExtras() != null) {
            Intent i = getIntent();
            taskId = i.getStringExtra("taskId");
            shopId = i.getStringExtra("shopId");
            requestStatus = i.getStringExtra("requestStatus");
        } else {
            super.onBackPressed();
        }

        onBack = (ImageView) findViewById(R.id.mechanic_request_back);
        onBack.setOnClickListener(this);
        vehicle_detail = (TextView) findViewById(R.id.mechanic_request_vehicle_details);
        vehicle_detail.setOnClickListener(this);
        additional_info = (TextView) findViewById(R.id.mechanic_request_admin_info);
        additional_info.setOnClickListener(this);

        btnAccept = (RelativeLayout)findViewById(R.id.mechanic_request_accept);
        btnAccept.setOnClickListener(this);
        btnDecline = (RelativeLayout)findViewById(R.id.mechanic_request_decline);
        btnDecline.setOnClickListener(this);

        if(requestStatus.equals("assigning")){
            btnAccept.setVisibility(View.VISIBLE);
            btnDecline.setVisibility(View.VISIBLE);
        }else {
            btnAccept.setVisibility(View.GONE);
            btnDecline.setVisibility(View.GONE);
        }

        viewLocation = (RelativeLayout)findViewById(R.id.mechanic_request_location);
        viewLocation.setOnClickListener(this);
        contactClient = (RelativeLayout)findViewById(R.id.mechanic_request_contact_client);
        contactClient.setOnClickListener(this);
        updateShop = (RelativeLayout) findViewById(R.id.mechanic_request_update_shop);
        updateShop.setOnClickListener(this);

        avatarImg = (CircleImageView)findViewById(R.id.mechanic_request_detail_avatar);
        title = (TextView)findViewById(R.id.mechanic_request_detail_title);
        driverName = (TextView)findViewById(R.id.mechanic_request_detail_name);
        requestTime = (TextView)findViewById(R.id.mechanic_request_detail_time);

        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_TASK).child(shopId).child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
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
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(requesterId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    driver = dataSnapshot.getValue(Driver.class);
                    if(!driver.photoUrl.isEmpty()){
                        Glide.with(getApplication()).load(driver.photoUrl).into(avatarImg);
                    }
                    driverName.setText(driver.fullName);
//                    if(driver.fullName.length()>15){
//                        driverName.setText(driver.fullName.substring(0,15)+"...");
//                    }else {
//                        driverName.setText(driver.fullName);
//                    }
                    title.setText(driver.fullName);
                }
//                hideProgressHUD();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                hideProgressHUD();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mechanic_request_back:
                goBack();
                break;
            case R.id.mechanic_request_vehicle_details:
                showDetailDlg();
                break;
            case R.id.mechanic_request_admin_info:
                showAdditionalInfoDlg();
                break;
            case R.id.mechanic_request_location:
                Intent customer_map = new Intent(this, MechanicLocationMapActivity.class);
                customer_map.putExtra("driverId", userId);
                startActivity(customer_map);
                break;
            case R.id.mechanic_request_contact_client:
                gotoMessageBox(userId);
                break;
            case R.id.mechanic_request_update_shop:
                gotoMessageBox(shopId);
                break;
            case R.id.mechanic_request_accept:
                acceptAssignRequest();
                break;
            case R.id.mechanic_request_decline:
                declineAssignRequest();
                break;
            default:
                break;
        }
    }

    public void acceptAssignRequest(){
        showProgressHUD("");
        Map<String, Object> taskUpdates = new HashMap<>();
        taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + userId + "/" + taskId + "/status" , "working");
        taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + shopId + "/" + taskId + "/status" , "working");
        FirebaseDatabase.getInstance().getReference().updateChildren(taskUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
                goBack();
            }
        });
    }

    public void declineAssignRequest(){
        showProgressHUD("");
        Map<String, Object> taskUpdates = new HashMap<>();
        taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + userId + "/" + taskId + "/mechanicID" , "");
        taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + userId + "/" + taskId + "/status" , "pending");
        taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + shopId + "/" + taskId + "/mechanicID" , "");
        taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + shopId + "/" + taskId + "/status" , "pending");
        FirebaseDatabase.getInstance().getReference().updateChildren(taskUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void gotoMessageBox(String target){
        Intent msg_intent = new Intent(this, MessageActivity.class);
        msg_intent.putExtra("oppUserId", target);
        startActivity(msg_intent);
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
                if (dataSnapshot.exists()) {
                    vehicleList.clear();
                    Map<String, Object> result = (Map<String, Object>) dataSnapshot.getValue();

                    for (Map.Entry<String, Object> entry : result.entrySet()) {
                        Map singleEntry = (Map) entry.getValue();
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
