package com.brainyapps.motolabz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.brainyapps.motolabz.Adapters.NotificationRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Constants.NotificationType;
import com.brainyapps.motolabz.DriversView.DriverDispatchTeamActivity;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.Models.Notification;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.firebase.client.Firebase;
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

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener, NotificationRecyclerAdapter.OnClickItemListener{

    private ImageView onBack;
    private RecyclerView notificationRecyclerView;
    private ArrayList<Notification> notificationList = new ArrayList<>();
    private NotificationRecyclerAdapter notificationRecyclerAdapter;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String myName;

    private Query notificationInfo;

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
        setContentView(R.layout.activity_notification);

        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child("unread").child(myId).removeValue();
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myName = dataSnapshot.child("fullName").getValue().toString();
                }
                hideProgressHUD();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        onBack = (ImageView) findViewById(R.id.notification_btn_back);
        onBack.setOnClickListener(this);
        notificationRecyclerView = (RecyclerView)findViewById(R.id.notification_recycler_view);
        notificationRecyclerAdapter = new NotificationRecyclerAdapter(notificationList);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        notificationRecyclerView.setAdapter(notificationRecyclerAdapter);
        notificationRecyclerAdapter.setOnClickItemListener(this);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                readNotification(notificationRecyclerAdapter.notificationList.get(viewHolder.getAdapterPosition()));
                notificationList.remove(viewHolder.getAdapterPosition());
                notificationRecyclerAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchhelper.attachToRecyclerView(notificationRecyclerView);

        notificationInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_NOTIFICATION).child(myId);
        notificationInfo.addValueEventListener(showNotification);
    }

    private ValueEventListener showNotification = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                notificationList.clear();
                for (DataSnapshot notification : dataSnapshot.getChildren()){
                    Notification new_notification = new Notification();
                    new_notification.notificationID = notification.getKey().toString();
                    new_notification.postKey = notification.child("postKey").getValue().toString();
                    new_notification.senderID = notification.child("senderID").getValue().toString();
                    new_notification.receiverID = notification.child("receiverID").getValue().toString();
                    new_notification.read = Boolean.parseBoolean(notification.child("read").getValue().toString());
                    new_notification.time = Long.parseLong(notification.child("time").getValue().toString());
                    new_notification.type = Integer.parseInt(notification.child("type").getValue().toString());
                    if(!new_notification.read){
                        notificationList.add(new_notification);
                    }
                }
                notificationRecyclerAdapter.notifyDataSetChanged();
                notificationRecyclerView.smoothScrollToPosition(notificationList.size());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void readNotification(Notification notification){
        String notificationId = notification.notificationID;
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_NOTIFICATION).child(myId).child(notificationId).child("read").setValue(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.notification_btn_back:
                super.onBackPressed();
                break;
            default:
                break;
        }
    }

    public void dispatch(){
        Intent dispatch_intent = new Intent(this, DriverDispatchTeamActivity.class);
        startActivity(dispatch_intent);
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
        notificationInfo.removeEventListener(showNotification);
    }

    @Override
    public void clickNotification(int index, String notificationID, int notificationType) {

    }

    @Override
    public void clickBtnAccept(int index, final String notificationID, final String senderID) {
        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(senderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressHUD();
                if(dataSnapshot.exists()){
                    RepairShop shop = dataSnapshot.getValue(RepairShop.class);
                    FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(senderID).child("mechanics").child(myId).setValue(true);
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("/" + DBInfo.TBL_USER + "/" + myId + "/" + "shopID", senderID);
                    userUpdates.put("/" + DBInfo.TBL_USER + "/" + myId + "/" + "shopName", shop.fullName);
                    FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            setFirebaseInfo();
                            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_NOTIFICATION).child(myId).child(notificationID).child("read").setValue(true);
                            sendNotification("accept", senderID);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    public void setFirebaseInfo(){
        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressHUD();
                if(dataSnapshot.exists()){
                    Mechanic mechanic = dataSnapshot.getValue(Mechanic.class);
                    FirebaseManager.getInstance().setMechanic(mechanic);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    @Override
    public void clickBtnDecline(int index, String notificationID, String senderID) {
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_NOTIFICATION).child(myId).child(notificationID).child("read").setValue(true);
        sendNotification("decline", senderID);
    }

    public void sendNotification(String type, String receiverID){
        Notification notification = new Notification();
        notification.senderID = myId;
        notification.receiverID = receiverID;
        notification.time = System.currentTimeMillis();
        notification.read = false;
        if(type.equals("accept")){
            notification.postKey = myName + " accepted your invitation";
            notification.type = NotificationType.accept;
        }else if(type.equals("decline")){
            notification.postKey = myName + " declined your invitation";
            notification.type = NotificationType.decline;
        }
        String notificationId = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_NOTIFICATION).child(receiverID).push().getKey();
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_NOTIFICATION).child(receiverID).child(notificationId).setValue(notification);
        FirebaseDatabase.getInstance().getReference().child("unread").child(receiverID).child(notificationId).setValue(true);
    }
}
