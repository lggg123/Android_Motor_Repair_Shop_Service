package com.brainyapps.motolabz;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.MessageContentRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.Message;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView onBack;
    private TextView title;
    private ImageView phoneCall;
    private RecyclerView messageRecyclerView;
    private ImageView btnEmoji;
    private EditText messageContent;
    private ImageView btnSendMessage;

    private ArrayList<Message> messageList = new ArrayList<>();
    private MessageContentRecyclerAdapter messageRecyclerAdapter;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String oppId = "";
    private String oppAvatarUrl = "";
    private String phone_number;
    private Query messageInfo;

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
        setContentView(R.layout.activity_message);

        if (getIntent().getExtras() != null) {
            Intent i = getIntent();
            oppId = i.getStringExtra("oppUserId");
        } else {
            super.onBackPressed();
        }
        onBack = (ImageView) findViewById(R.id.message_back);
        onBack.setOnClickListener(this);
        title = (TextView) findViewById(R.id.message_title_name);
        phoneCall = (ImageView) findViewById(R.id.message_phone_call);
        phoneCall.setOnClickListener(this);
        messageRecyclerView = (RecyclerView) findViewById(R.id.message_recycler_view);
        btnEmoji = (ImageView) findViewById(R.id.message_btn_emoji);
        btnEmoji.setOnClickListener(this);
        messageContent = (EditText) findViewById(R.id.message_send_message_content);
        btnSendMessage = (ImageView) findViewById(R.id.message_btn_send_message);
        btnSendMessage.setOnClickListener(this);

        showProgressHUD("");
        Query oppInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(oppId);
        oppInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    oppAvatarUrl = dataSnapshot.child("photoUrl").getValue().toString();
                    title.setText(dataSnapshot.child("fullName").getValue().toString());
                    phone_number = dataSnapshot.child("phone").getValue().toString();
//                    if(dataSnapshot.child("fullName").getValue().toString().length() > 18){
//                        title.setText(dataSnapshot.child("fullName").getValue().toString().substring(0,18)+"...");
//                    }else {
//                        title.setText(dataSnapshot.child("fullName").getValue().toString());
//                    }
                    messageRecyclerAdapter = new MessageContentRecyclerAdapter(messageList, oppAvatarUrl);
                    messageRecyclerView = (RecyclerView) findViewById(R.id.message_recycler_view);
                    messageRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                    messageRecyclerView.setAdapter(messageRecyclerAdapter);
                    showMessage();
                }
                hideProgressHUD();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    public void showMessage() {
        messageInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_CHAT).child(myId).child(oppId).child("messages");
        messageInfo.addValueEventListener(updateMessages);
    }

    private ValueEventListener updateMessages = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_CHAT).child(myId).child(oppId).child("isRead").setValue(true);
                messageList.clear();
                for (DataSnapshot message : dataSnapshot.getChildren()) {
                    Message new_message = message.getValue(Message.class);
                    messageList.add(new_message);
                }
                messageRecyclerAdapter.notifyDataSetChanged();
                messageRecyclerView.smoothScrollToPosition(messageList.size());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.message_back:
                super.onBackPressed();
                break;
            case R.id.message_phone_call:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+phone_number));

                if (android.support.v13.app.ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                }
                startActivity(callIntent);
                break;
            case R.id.message_btn_emoji:
                break;
            case R.id.message_btn_send_message:
                sendMessage();
                break;
            default:
                break;
        }
    }

    public void sendMessage(){
        if(!messageContent.getText().toString().isEmpty()){
            showProgressHUD("");
            String myMessageId = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_CHAT).child(myId).child(oppId).child("messages").push().getKey();
            String oppMessageId = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_CHAT).child(oppId).child(myId).child("messages").push().getKey();
            Message msg = new Message();
            msg.userID = myId;
            msg.text = messageContent.getText().toString();
            msg.time = System.currentTimeMillis();
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("/" + DBInfo.TBL_CHAT + "/" + myId + "/" + oppId +"/messages/" + myMessageId, msg);
            userUpdates.put("/" + DBInfo.TBL_CHAT + "/" + oppId + "/" + myId +"/messages/" + oppMessageId, msg);
            userUpdates.put("/" + DBInfo.TBL_CHAT + "/" + myId + "/" + oppId +"/isRead", true);
            userUpdates.put("/" + DBInfo.TBL_CHAT + "/" + myId + "/" + oppId +"/lastMsg", msg.text);
            userUpdates.put("/" + DBInfo.TBL_CHAT + "/" + myId + "/" + oppId +"/time", System.currentTimeMillis());
            userUpdates.put("/" + DBInfo.TBL_CHAT + "/" + oppId + "/" + myId +"/isRead", false);
            userUpdates.put("/" + DBInfo.TBL_CHAT + "/" + oppId + "/" + myId +"/lastMsg", msg.text);
            userUpdates.put("/" + DBInfo.TBL_CHAT + "/" + oppId + "/" + myId +"/time", System.currentTimeMillis());
            userUpdates.put("/" + DBInfo.TBL_UNREAD_MESSAGES + "/" + oppId + "/" + myId, true);
//            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_UNREAD_MESSAGES).child(myId).child(oppId).removeValue();
            FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hideProgressHUD();
                    messageContent.setText("");
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_UNREAD_MESSAGES).child(myId).child(oppId).removeValue();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageInfo.removeEventListener(updateMessages);
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_UNREAD_MESSAGES).child(myId).child(oppId).removeValue();
    }
}
