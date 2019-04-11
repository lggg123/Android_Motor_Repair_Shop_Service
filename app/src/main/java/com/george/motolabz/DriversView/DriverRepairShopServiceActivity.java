package com.brainyapps.motolabz.DriversView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.CheckableServiceRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.MessageActivity;
import com.brainyapps.motolabz.Models.Notification;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.Models.Report;
import com.brainyapps.motolabz.Models.ServiceListItem;
import com.brainyapps.motolabz.Models.TaskStatus;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Views.AlertFactory;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DriverRepairShopServiceActivity extends AppCompatActivity implements View.OnClickListener{

    final Context context = this;
    private ImageView onBack;
    private ImageView showReport;
    private LinearLayout content;
    private RelativeLayout onNext;
    private TextView shopTitle;
    private ImageView imgShop;
    private TextView shopName;
    private TextView shopDescription;
    private ImageView imgFavourite;
    private ImageView imgChat;
    private RelativeLayout waitScreen;

    private ArrayList<ServiceListItem> serviceList = new ArrayList<>();
    private RecyclerView checkableServiceRecyclerView;
    private CheckableServiceRecyclerAdapter checkableServiceRecyclerAdapter;

    private ImageView imgAvailable;
    private TextView textAvailable;

    private String shopId = "";
    private String taskKey = "";
    private RepairShop currentShop;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Boolean isFavourite = false;
    private ProgressHUD mProgressDialog;

    private Query statusInfo;

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
        setContentView(R.layout.activity_driver_repair_shop_service);

        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            shopId = i.getStringExtra("shopId");
        }else {
            super.onBackPressed();
        }

        onBack = (ImageView) findViewById(R.id.driver_repair_shop_back);
        onBack.setOnClickListener(this);
        showReport = (ImageView) findViewById(R.id.driver_repair_shop_report);
        showReport.setOnClickListener(this);
        content=(LinearLayout)findViewById(R.id.driver_repair_shop_content);
        content.setVisibility(View.GONE);
        onNext = (RelativeLayout) findViewById(R.id.driver_repair_shop_next);
        onNext.setOnClickListener(this);
        imgShop = (ImageView)findViewById(R.id.driver_repair_shop_image);
        shopTitle = (TextView)findViewById(R.id.driver_repair_shop_title);
        shopName = (TextView)findViewById(R.id.driver_repair_shop_name);
        shopDescription = (TextView)findViewById(R.id.driver_repair_shop_description);
        imgFavourite = (ImageView)findViewById(R.id.driver_repair_shop_img_favourite);
        imgFavourite.setOnClickListener(this);
        imgChat = (ImageView)findViewById(R.id.driver_repair_shop_img_chat);
        imgChat.setOnClickListener(this);

        waitScreen = (RelativeLayout)findViewById(R.id.driver_repair_shop_wait);
        waitScreen.setOnClickListener(this);

        imgAvailable = (ImageView)findViewById(R.id.driver_repair_shop_available);
        textAvailable = (TextView)findViewById(R.id.driver_repair_shop_available_text);


        imgFavourite.setImageResource(R.drawable.ic_like_post);
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(shopId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentShop = dataSnapshot.getValue(RepairShop.class);
                    if(!currentShop.photoUrl.isEmpty()){
                        Glide.with(context).load(currentShop.photoUrl).into(imgShop);
                    }
                    shopTitle.setText(currentShop.fullName);
                    shopName.setText(currentShop.fullName);
                    if (currentShop.description.length()>60){
                        shopDescription.setText(currentShop.description.substring(0,60)+"...");
                    }else {
                        shopDescription.setText(currentShop.description);
                    }
                    if(currentShop.available){
                        imgAvailable.setImageResource(R.drawable.ic_dot_green);
                        textAvailable.setText("Available");
//                        Calendar calendar = Calendar.getInstance();
//                        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
//                        String strDate = mdformat.format(calendar.getTime());
//                        int day = calendar.get(Calendar.DAY_OF_WEEK);
//
//                        switch (day) {
//                            case Calendar.SUNDAY:
//                                checkAvailability("Sunday", strDate);
//                                break;
//                            case Calendar.MONDAY:
//                                checkAvailability("Monday", strDate);
//                                break;
//                            case Calendar.TUESDAY:
//                                checkAvailability("Tuesday", strDate);
//                                break;
//                            case Calendar.WEDNESDAY:
//                                checkAvailability("Wednesday", strDate);
//                                break;
//                            case Calendar.THURSDAY:
//                                checkAvailability("Thursday", strDate);
//                                break;
//                            case Calendar.FRIDAY:
//                                checkAvailability("Friday", strDate);
//                                break;
//                            case Calendar.SATURDAY:
//                                checkAvailability("Saturday", strDate);
//                                break;
//                        }
                    }else {
                        imgAvailable.setImageResource(R.drawable.ic_dot_red);
                        textAvailable.setText("Unavailable");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot item : dataSnapshot.getChildren()){
                        if(item.getKey().toString().equals(shopId)){
                            isFavourite = true;
                            imgFavourite.setImageResource(R.drawable.ic_like_post_active);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_TASK).child(shopId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        TaskStatus task = data.getValue(TaskStatus.class);
                        if(task.customerID.equals(myId) && !task.status.equals("done")){
                            taskKey = task.key;
                        }
                    }
                    if(!taskKey.isEmpty()){
                        checkStatus();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        checkableServiceRecyclerAdapter = new CheckableServiceRecyclerAdapter(serviceList);
        checkableServiceRecyclerView = (RecyclerView) findViewById(R.id.driver_repair_shop_service_recycler_view);
        checkableServiceRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        checkableServiceRecyclerView.setAdapter(checkableServiceRecyclerAdapter);
        updateList();
    }

    public void checkAvailability(String week_day, final String current_time){
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(shopId).child("businessTime").child(week_day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(dataSnapshot.child("status").getValue().toString().equals("false")){
                        imgAvailable.setImageResource(R.drawable.ic_dot_red);
                        textAvailable.setText("Unavailable");
                    }else {
                        String start_time = dataSnapshot.child("start").getValue().toString();
                        String end_time = dataSnapshot.child("end").getValue().toString();
                        if(Integer.parseInt(current_time.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(current_time.replaceAll(":",""))){
                            imgAvailable.setImageResource(R.drawable.ic_dot_green);
                            textAvailable.setText("Available");
                        }else {
                            imgAvailable.setImageResource(R.drawable.ic_dot_red);
                            textAvailable.setText("Unavailable");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateList(){
        showProgressHUD("");
        Query userInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(shopId).child("services");
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    serviceList.clear();
                    Map<String,Object> result = (Map<String,Object>)dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : result.entrySet()){
                        Map singleEntry = (Map) entry.getValue();
                        ServiceListItem new_result = new ServiceListItem();
                        new_result.service_name = singleEntry.get("serviceName").toString();
                        if(singleEntry.containsKey("rate")){
                            new_result.service_rate = singleEntry.get("rate").toString();
                        }
                        if(!new_result.service_rate.isEmpty()){
                            serviceList.add(0,new_result);
                        }else {
                            serviceList.add(new_result);
                        }
                    }
                    checkableServiceRecyclerAdapter.notifyDataSetChanged();
                }
                hideProgressHUD();
                content.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
                content.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_repair_shop_back:
                super.onBackPressed();
                break;
            case R.id.driver_repair_shop_report:
                showReportDlg();
                break;
            case R.id.driver_repair_shop_next:
                sendRequest();
                break;
            case R.id.driver_repair_shop_wait:
                break;
            case R.id.driver_repair_shop_img_favourite:
                if(isFavourite){
                    imgFavourite.setImageResource(R.drawable.ic_like_post);
                    FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("likes").child(shopId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            isFavourite = false;
                        }
                    });
                }else {
                    imgFavourite.setImageResource(R.drawable.ic_like_post_active);
                    FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("likes").child(shopId).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            isFavourite = true;
                        }
                    });
                    isFavourite = true;
                }
                break;
            case R.id.driver_repair_shop_img_chat:
                Intent msg_intent = new Intent(this, MessageActivity.class);
                msg_intent.putExtra("oppUserId", shopId);
                startActivity(msg_intent);
                break;
            default:
                break;
        }
    }

    public void sendRequest(){
        final ArrayList<String> list  = checkableServiceRecyclerAdapter.getSelectedService();
        final Map<String, Object> serviceList = new HashMap<>();
        if(list.size() == 0){
            AlertFactory.showAlert(this,"","Please select services you need.");
        }else {
            showProgressHUD("");
            taskKey = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_TASK).child(shopId).push().getKey();
            final TaskStatus task = new TaskStatus();
            task.customerID = myId;
            task.shopID = shopId;
            task.status = "pending";
            task.time = System.currentTimeMillis();
            task.key = taskKey;

            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(shopId).child("services").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for (DataSnapshot service : dataSnapshot.getChildren()){
                            if(list.contains(service.getKey())){
                                serviceList.put(service.getKey(), service.getValue());
                            }
                        }
                        task.services = serviceList;
                        setReguestOnFirebase(task);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void setReguestOnFirebase(TaskStatus task){
        String notificationKey = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_NOTIFICATION).child(shopId).push().getKey();
        Notification notification = new Notification();
        notification.postKey = "Please assign a mechanic";
        notification.read = false;
        notification.receiverID = shopId;
        notification.type = 2;
        notification.senderID = myId;
        notification.time = System.currentTimeMillis();

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/" + DBInfo.TBL_TASK + "/" + shopId + "/" + task.key, task);
        userUpdates.put("/" + DBInfo.TBL_TASK + "/" + myId + "/" + task.key, task);
        userUpdates.put("/" + DBInfo.TBL_NOTIFICATION + "/" + shopId + "/" + notificationKey, notification);
        userUpdates.put("/unread/" + shopId + "/" + notificationKey, true);
        FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
                checkStatus();
            }
        });
    }

    public void checkStatus(){
        statusInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_TASK).child(shopId).child(taskKey);
        statusInfo.addValueEventListener(checkTask);
    }

    private ValueEventListener checkTask = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
//                Log.e("MECHANIC_ID", "HELLO WORLD!!!!!!");
                if(dataSnapshot.hasChild("mechanicID")){
                    String mechanicId = dataSnapshot.child("mechanicID").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    if(status.equals("pending") || status.equals("assigning")){
                        waitScreen.setVisibility(View.VISIBLE);
                    }else {
                        getMechanic(mechanicId);
                    }
                }else {
                    waitScreen.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void getMechanic(String mechanicId){
        Intent mechanic_intent = new Intent(this, DriverMechanicProfileActivity.class);
        mechanic_intent.putExtra("mechanicId", mechanicId);
        startActivity(mechanic_intent);
        finish();
    }

    public void showReportDlg() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dlg_report);
        dialog.show();

        final EditText description = (EditText) dialog.findViewById(R.id.driver_report_description);
        RelativeLayout btnSubmit = (RelativeLayout)dialog.findViewById(R.id.driver_report_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(description.getText().toString().isEmpty()){
                    showAlert("Please write post description.");
                }else {
                    reportShop(description.getText().toString());
                    dialog.dismiss();
                }
            }
        });
    }

    public void showAlert(String content){
        AlertFactory.showAlert(this, "", content);
    }

    public void reportShop(String description){
        showProgressHUD("");
        String reportId = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_REPORT).push().getKey();
        Report report = new Report();
        report.userReportID = reportId;
        report.reporterID = myId;
        report.userID = shopId;
        report.time = System.currentTimeMillis();
        report.reason = description;
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_REPORT).child(reportId).setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
                showAlert("Successfully Reported!");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        statusInfo.removeEventListener(checkTask);
    }
}
