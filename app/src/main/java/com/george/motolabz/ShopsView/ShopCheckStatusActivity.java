package com.brainyapps.motolabz.ShopsView;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.DriversView.DriverArriavalMapActivity;
import com.brainyapps.motolabz.MessageActivity;
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

import java.util.HashMap;
import java.util.Map;

public class ShopCheckStatusActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView onBack;
    private RelativeLayout contactMechanic;
    private RelativeLayout arrivalStatus;
    private RelativeLayout done;
    private TaskStatus task;

    private String taskId = "";
    private String myId = FirebaseManager.getInstance().getUserId();
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
        setContentView(R.layout.activity_shop_check_status);
        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            taskId = i.getStringExtra("taskId");
        }else {
            super.onBackPressed();
        }
        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_TASK).child(myId).child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    task = dataSnapshot.getValue(TaskStatus.class);
                    if(dataSnapshot.hasChild("mechanicID")){
                        if(task.status.equals("done")){
                            notifyAlert("This user's request is already completed.");
                        }else {
                            if(task.mechanicID.isEmpty()){
                                notifyAlert("You didn't assign Mechanic. Please assign mechanic first.");
                            }
                        }
                    }else {
                        notifyAlert("You didn't assign Mechanic. Please assign mechanic first.");
                    }
                }
                hideProgressHUD();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });

        onBack = (ImageView)findViewById(R.id.shop_check_status_back);
        onBack.setOnClickListener(this);

        contactMechanic = (RelativeLayout)findViewById(R.id.shop_check_status_contact_mechanic);
        contactMechanic.setOnClickListener(this);
        arrivalStatus = (RelativeLayout)findViewById(R.id.shop_check_status_arrival_status);
        arrivalStatus.setOnClickListener(this);
        done = (RelativeLayout)findViewById(R.id.shop_check_status_done);
        done.setOnClickListener(this);
    }

    public void notifyAlert(String alert){
        AlertFactory.showAlert(this, "Check Status", alert, "OKAY", "", new AlertFactoryClickListener() {
            @Override
            public void onClickYes(AlertDialog dialog) {

            }
            @Override
            public void onClickNo(AlertDialog dialog) {

            }
            @Override
            public void onClickDone(AlertDialog dialog) {
                dialog.dismiss();
                onGoback();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shop_check_status_back:
                onGoback();
                break;
            case R.id.shop_check_status_contact_mechanic:
                Intent msg_intent = new Intent(this, MessageActivity.class);
                msg_intent.putExtra("oppUserId", task.mechanicID);
                startActivity(msg_intent);
                break;
            case R.id.shop_check_status_arrival_status:
                Intent arrival_map = new Intent(this, ShopArrivalMapActivity.class);
                arrival_map.putExtra("driverId", task.customerID);
                arrival_map.putExtra("mechanicId", task.mechanicID);
                startActivity(arrival_map);
                break;
            case R.id.shop_check_status_done:
                showProgressHUD("");
                Map<String, Object> taskUpdates = new HashMap<>();
                taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + task.shopID + "/" + taskId + "/" + "status", "done");
                taskUpdates.put("/" + DBInfo.TBL_TASK + "/" + task.customerID + "/" + taskId + "/" + "status", "done");
                FirebaseDatabase.getInstance().getReference().updateChildren(taskUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressHUD();
                        onGoback();
                        finish();
                    }
                });
                break;
            default:
                break;
        }
    }

    public void onGoback(){
        super.onBackPressed();
    }
}
