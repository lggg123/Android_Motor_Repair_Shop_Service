package com.brainyapps.motolabz.ShopsView;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Constants.NotificationType;
import com.brainyapps.motolabz.Models.Notification;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.ApiInterface;
import com.brainyapps.motolabz.Utils.ApiUtils;
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
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopInviteMechanicsActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout submitInvite;
    private ImageView onBack;
    private EditText email_address;
    private EditText description;

    private ApiInterface mAPIService;

    private String shopId = FirebaseManager.getInstance().getUserId();
    private RepairShop myShop;
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
        setContentView(R.layout.activity_shop_invite_mechanics);

        onBack = (ImageView) findViewById(R.id.shop_invite_mechanics_back);
        onBack.setOnClickListener(this);
        submitInvite = (RelativeLayout)findViewById(R.id.shop_invite_mechanics_submit_invite);
        submitInvite.setOnClickListener(this);
        email_address = (EditText) findViewById(R.id.shop_invite_mechanics_email);
        description = (EditText)findViewById(R.id.shop_invite_mechanics_description);

        myShop = FirebaseManager.getInstance().getCurrentRepairShop();
//        showProgressHUD("");
//        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(shopId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                myShop = dataSnapshot.getValue(RepairShop.class);
//                hideProgressHUD();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                hideProgressHUD();
//            }
//        });
        mAPIService = ApiUtils.getAPIService();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shop_invite_mechanics_back:
                super.onBackPressed();
                break;
            case R.id.shop_invite_mechanics_submit_invite:
                if(checkValidation()){
                    sendInvite();
                }
                break;
        }
    }

    public boolean checkValidation(){
        if(email_address.getText().toString().isEmpty()){
            AlertFactory.showAlert(this, "", "Please input email address!");
            return false;
        }
        return true;
    }

    public void sendInvite(){
        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).orderByChild("userEmail").equalTo(email_address.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String,Object> result = (Map<String,Object>)dataSnapshot.getValue();
                    Map.Entry<String,Object> entry = result.entrySet().iterator().next();
                    String mechanicID= entry.getKey().toString();
                    sendInviteNotification(mechanicID);
                    hideProgressHUD();
                    showConfirmDlg();
                }else {
                    final int min = 10000000;
                    final int max = 99999999;
                    final int random = new Random().nextInt((max - min) + 1) + min;
                    sendPost(myShop.userID, email_address.getText().toString(), description.getText().toString(), String.valueOf(random));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    public void sendInviteNotification(String mechanicID){
        String notificationId = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_NOTIFICATION).child(mechanicID).push().getKey();
        Notification invite_notification = new Notification();
        invite_notification.senderID = myShop.userID;
        invite_notification.receiverID = mechanicID;
        invite_notification.type = NotificationType.invite;
        invite_notification.time = System.currentTimeMillis();
        invite_notification.read = false;
        invite_notification.postKey = myShop.fullName + " send invite Request";

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/" + DBInfo.TBL_NOTIFICATION + "/" + mechanicID + "/" + notificationId, invite_notification);
        userUpdates.put("/unread/" + mechanicID + "/" + notificationId, true);
        FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public void sendPost(String shop_id, String mechanic_email, String message, final String signup_code) {
        mAPIService.sendEmail(shop_id, mechanic_email, message, signup_code).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    String inviteId = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_INVITE).push().getKey();
                    Long tsLong = System.currentTimeMillis();
                    Map<String, Object> result = new HashMap<>();
                    result.put("email", email_address.getText().toString());
                    result.put("key", inviteId);
                    result.put("message", description.getText().toString());
                    result.put("shopName",myShop.fullName);
                    result.put("signupCode", signup_code);
                    result.put("time",tsLong.toString(tsLong));
                    result.put("userID",myShop.userID);
                    FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_INVITE).child(inviteId).setValue(result).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgressHUD();
                            showConfirmDlg();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
//                AlertFactory.showAlert(ShopInviteMechanicsActivity.this, "", t.getLocalizedMessage());
                hideProgressHUD();
            }
        });
    }

    public void showConfirmDlg(){
        AlertFactory.showAlert(this, "Invite", "You have sent invitation successfully.", "OKAY", "", new AlertFactoryClickListener() {
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

    public void testDlg(String str){
        AlertFactory.showAlert(this, "Invite", str, "OKAY", "", new AlertFactoryClickListener() {
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

    public void goBack(){
        super.onBackPressed();
    }
}
