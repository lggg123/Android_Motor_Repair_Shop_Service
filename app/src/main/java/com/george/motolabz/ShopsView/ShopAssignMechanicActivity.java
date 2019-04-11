package com.brainyapps.motolabz.ShopsView;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brainyapps.motolabz.Adapters.AssignMechanicRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Constants.NotificationType;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.Models.Notification;
import com.brainyapps.motolabz.Models.TaskStatus;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.brainyapps.motolabz.Views.AlertFactoryClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopAssignMechanicActivity extends AppCompatActivity implements View.OnClickListener, AssignMechanicRecyclerAdapter.OnClickItemListener{

    private ImageView onBack;
    private String taskId = "";
    private String driverId = "";
    private String mechanicId = "";
    private String myId = FirebaseManager.getInstance().getUserId();
    private TaskStatus task;

    private RelativeLayout btnAssign;

    private AssignMechanicRecyclerAdapter assignMechanicRecyclerAdapter;
    private RecyclerView assignMechanicRecyclerView;
    private ArrayList<String> myMechanics = new ArrayList<>();
    private ArrayList<Mechanic> mechanicList = new ArrayList<>();

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
        setContentView(R.layout.activity_shop_assign_mechanic);
        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            taskId = i.getStringExtra("taskId");
            driverId = i.getStringExtra("driverId");

        }else {
            super.onBackPressed();
        }
        assignMechanicRecyclerAdapter = new AssignMechanicRecyclerAdapter(mechanicList);
        assignMechanicRecyclerView = (RecyclerView)findViewById(R.id.shop_assign_mechanic_assign_recycler_view);
        assignMechanicRecyclerView.setAdapter(assignMechanicRecyclerAdapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        assignMechanicRecyclerView.setLayoutManager(mLayoutManager);
        assignMechanicRecyclerAdapter.setOnClickItemListener(this);

        onBack = (ImageView)findViewById(R.id.shop_assign_mechanic_back);
        onBack.setOnClickListener(this);
        btnAssign = (RelativeLayout)findViewById(R.id.shop_assign_mechanic_assign);
        btnAssign.setOnClickListener(this);

        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_TASK).child(myId).child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    task = dataSnapshot.getValue(TaskStatus.class);
                    if(dataSnapshot.hasChild("mechanicID")){
                        if(!task.mechanicID.isEmpty()){
                            notifyAlert("You have already assigned Mechanic.");
                        }else {
                            getMechanic();
                        }
                    }else {
                        getMechanic();
                    }
                }
                hideProgressHUD();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    public void notifyAlert(String alert){
        AlertFactory.showAlert(this, "Assign Mechanic", alert, "OKAY", "", new AlertFactoryClickListener() {
            @Override
            public void onClickYes(AlertDialog dialog) {

            }
            @Override
            public void onClickNo(AlertDialog dialog) {

            }
            @Override
            public void onClickDone(AlertDialog dialog) {
                dialog.dismiss();
                goBack();
            }
        });
    }

    public void getMechanic(){
        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("mechanics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot mt : dataSnapshot.getChildren()){
                        myMechanics.add(mt.getKey().toString());
                    }
                    getMechanicList();
                }
                hideProgressHUD();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    public void getMechanicList(){
        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mechanicList.clear();
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        if(myMechanics.contains(data.getKey().toString())){
                            Mechanic mec = data.getValue(Mechanic.class);
                            mechanicList.add(mec);
                        }
                    }
                    assignMechanicRecyclerAdapter.notifyDataSetChanged();
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
            case R.id.shop_assign_mechanic_back:
                goBack();
                break;
            case R.id.shop_assign_mechanic_assign:
                if(mechanicId.isEmpty()){
                    AlertFactory.showAlert(this,"Assign","Please select a mechanic first.");
                }else {
                    showProgressHUD("");
                    Notification assign_notification = new Notification();
                    assign_notification.senderID = myId;
                    assign_notification.receiverID = mechanicId;
                    assign_notification.type = NotificationType.invite;
                    assign_notification.time = System.currentTimeMillis();
                    assign_notification.read = false;
                    assign_notification.postKey = "The shop assigned you for work";
                    String notificationId = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_NOTIFICATION).child(mechanicId).push().getKey();

                    Map<String, Object> taskUpdates = new HashMap<>();
                    taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + driverId + "/" + taskId + "/mechanicID" , mechanicId);
                    taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + driverId + "/" + taskId + "/status" , "assigning");
                    taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + myId + "/" + taskId + "/mechanicID" , mechanicId);
                    taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + myId + "/" + taskId + "/status" , "assigning");
                    taskUpdates.put("/" + DBInfo.TBL_NOTIFICATION + "/" + mechanicId + "/" + notificationId , assign_notification);
                    taskUpdates.put("/unread/" + mechanicId + "/" + notificationId, true);
                    FirebaseDatabase.getInstance().getReference().updateChildren(taskUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgressHUD();
                            showAlert();
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    public void showAlert(){
        AlertFactory.showAlert(this, "Assign", "You successfully assigned mechanic.", "OKAY", "", new AlertFactoryClickListener() {
            @Override
            public void onClickYes(AlertDialog dialog) {

            }
            @Override
            public void onClickNo(AlertDialog dialog) {

            }
            @Override
            public void onClickDone(AlertDialog dialog) {
                dialog.dismiss();
                goBack();
            }
        });
    }

    @Override
    public void clickMechanicItem(int index, String mechanicId) {
        this.mechanicId = mechanicId;
    }

    public void goBack(){
        super.onBackPressed();
    }
}
