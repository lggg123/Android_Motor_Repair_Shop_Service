package com.brainyapps.motolabz.ShopsView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.MessageActivity;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.ShowRatingAndReviewActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopMechanicDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView onBack;
    private TextView title;
    private TextView name;
    private CircleImageView avatarImg;
    private TextView description;
    private RelativeLayout ratings;
    private RelativeLayout sendMessage;
    private String mechanicId;

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
        setContentView(R.layout.activity_shop_mechanic_detail);
        showProgressHUD("");
        if (getIntent().getExtras() != null) {
            Intent i = getIntent();
            mechanicId = i.getStringExtra("mechanicId");
        } else {
            super.onBackPressed();
        }

        title = (TextView)findViewById(R.id.shop_mechanic_detail_title);
        name = (TextView)findViewById(R.id.shop_mechanic_detail_name);
        avatarImg = (CircleImageView)findViewById(R.id.shop_mechanic_detail_avatar);
        description = (TextView)findViewById(R.id.shop_mechanic_detail_description);
        onBack = (ImageView)findViewById(R.id.shop_mechanic_detail_back);
        onBack.setOnClickListener(this);
        ratings = (RelativeLayout) findViewById(R.id.shop_mechanic_detail_goto_review);
        ratings.setOnClickListener(this);
        sendMessage = (RelativeLayout)findViewById(R.id.shop_mechanic_detail_send_message);
        sendMessage.setOnClickListener(this);
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(mechanicId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Mechanic mechanic = dataSnapshot.getValue(Mechanic.class);
                    if(mechanic.fullName.length() > 20){
                        title.setText(mechanic.fullName.substring(0,20)+"...");
                        name.setText(mechanic.fullName.substring(0,20)+"...");
                    }else {
                        title.setText(mechanic.fullName);
                        name.setText(mechanic.fullName);
                    }
                    if(!mechanic.photoUrl.isEmpty()){
                        Glide.with(getApplication()).load(mechanic.photoUrl).into(avatarImg);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shop_mechanic_detail_back:
                super.onBackPressed();
                break;
            case R.id.shop_mechanic_detail_goto_review:
                Intent review_intent = new Intent(this, ShowRatingAndReviewActivity.class);
                review_intent.putExtra("mechanicId", mechanicId);
                startActivity(review_intent);
                break;
            case R.id.shop_mechanic_detail_send_message:
                Intent msg_intent = new Intent(this, MessageActivity.class);
                msg_intent.putExtra("oppUserId", mechanicId);
                startActivity(msg_intent);
                break;
            default:
                break;
        }
    }
}
