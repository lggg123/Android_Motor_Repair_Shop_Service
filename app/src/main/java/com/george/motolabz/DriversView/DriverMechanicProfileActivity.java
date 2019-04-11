package com.brainyapps.motolabz.DriversView;

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
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.ShowRatingAndReviewActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DriverMechanicProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView onBack;
    private RelativeLayout arrival_map;
    private RelativeLayout rate_review;
    private RelativeLayout contact;

    private ImageView mechanicImg;
    private TextView mechanicName;
    private TextView mechanicDescription;

    private ImageView availableImg;
    private TextView availableText;
    private String mechanicId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_mechanic_profile);
        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            mechanicId = i.getStringExtra("mechanicId");
        }else {
            super.onBackPressed();
        }

        onBack = (ImageView) findViewById(R.id.driver_mechanic_profile_back);
        onBack.setOnClickListener(this);
        arrival_map = (RelativeLayout) findViewById(R.id.driver_mechanic_profile_arrival_map);
        arrival_map.setOnClickListener(this);
        rate_review = (RelativeLayout) findViewById(R.id.driver_mechanic_profile_rating_review);
        rate_review.setOnClickListener(this);
        contact = (RelativeLayout) findViewById(R.id.driver_mechanic_profile_contact);
        contact.setOnClickListener(this);

        mechanicName = (TextView)findViewById(R.id.driver_mechanic_title_name);
        mechanicImg = (ImageView)findViewById(R.id.driver_mechanic_img_avatar);
        mechanicDescription = (TextView)findViewById(R.id.driver_mechanic_description);

        availableImg = (ImageView)findViewById(R.id.driver_mechanic_img_available);
        availableText = (TextView)findViewById(R.id.driver_mechanic_text_available);

        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(mechanicId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Mechanic mechanic = dataSnapshot.getValue(Mechanic.class);
                    mechanicName.setText(mechanic.fullName);
                    mechanicDescription.setText(mechanic.description);
                    if(!mechanic.photoUrl.isEmpty()){
                        Glide.with(getApplication()).load(mechanic.photoUrl).into(mechanicImg);
                    }
                    if(mechanic.available){
                        if(!mechanic.shopID.isEmpty()){
                            checkStatus(mechanic.shopID);
                        }else {
                            availableText.setText("Available");
                            availableImg.setImageResource(R.drawable.ic_dot_green);
                        }
                    }else {
                        availableText.setText("Unavailable");
                        availableImg.setImageResource(R.drawable.ic_dot_red);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkStatus(String shopID){
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(shopID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    RepairShop shop = dataSnapshot.getValue(RepairShop.class);
                    if(shop.available){
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
                        String strDate = mdformat.format(calendar.getTime());
                        int day = calendar.get(Calendar.DAY_OF_WEEK);
                        switch (day) {
                            case Calendar.SUNDAY:
                                if(dataSnapshot.child("businessTime").child("Sunday").child("status").getValue().toString().equals("false")){
                                    availableText.setText("Unavailable");
                                    availableImg.setImageResource(R.drawable.ic_dot_red);
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        availableText.setText("Available");
                                        availableImg.setImageResource(R.drawable.ic_dot_green);
                                    }else {
                                        availableText.setText("Unavailable");
                                        availableImg.setImageResource(R.drawable.ic_dot_red);
                                    }
                                }
                                break;
                            case Calendar.MONDAY:
                                if(dataSnapshot.child("businessTime").child("Monday").child("status").getValue().toString().equals("false")){
                                    availableText.setText("Unavailable");
                                    availableImg.setImageResource(R.drawable.ic_dot_red);
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        availableText.setText("Available");
                                        availableImg.setImageResource(R.drawable.ic_dot_green);
                                    }else {
                                        availableText.setText("Unavailable");
                                        availableImg.setImageResource(R.drawable.ic_dot_red);
                                    }
                                }
                                break;
                            case Calendar.TUESDAY:
                                if(dataSnapshot.child("businessTime").child("Tuesday").child("status").getValue().toString().equals("false")){
                                    availableText.setText("Unavailable");
                                    availableImg.setImageResource(R.drawable.ic_dot_red);
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        availableText.setText("Available");
                                        availableImg.setImageResource(R.drawable.ic_dot_green);
                                    }else {
                                        availableText.setText("Unavailable");
                                        availableImg.setImageResource(R.drawable.ic_dot_red);
                                    }
                                }
                                break;
                            case Calendar.WEDNESDAY:
                                if(dataSnapshot.child("businessTime").child("Wednesday").child("status").getValue().toString().equals("false")){
                                    availableText.setText("Unavailable");
                                    availableImg.setImageResource(R.drawable.ic_dot_red);
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        availableText.setText("Available");
                                        availableImg.setImageResource(R.drawable.ic_dot_green);
                                    }else {
                                        availableText.setText("Unavailable");
                                        availableImg.setImageResource(R.drawable.ic_dot_red);
                                    }
                                }
                                break;
                            case Calendar.THURSDAY:
                                if(dataSnapshot.child("businessTime").child("Thursday").child("status").getValue().toString().equals("false")){
                                    availableText.setText("Unavailable");
                                    availableImg.setImageResource(R.drawable.ic_dot_red);
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        availableText.setText("Available");
                                        availableImg.setImageResource(R.drawable.ic_dot_green);
                                    }else {
                                        availableText.setText("Unavailable");
                                        availableImg.setImageResource(R.drawable.ic_dot_red);
                                    }
                                }
                                break;
                            case Calendar.FRIDAY:
                                if(dataSnapshot.child("businessTime").child("Friday").child("status").getValue().toString().equals("false")){
                                    availableText.setText("Unavailable");
                                    availableImg.setImageResource(R.drawable.ic_dot_red);
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        availableText.setText("Available");
                                        availableImg.setImageResource(R.drawable.ic_dot_green);
                                    }else {
                                        availableText.setText("Unavailable");
                                        availableImg.setImageResource(R.drawable.ic_dot_red);
                                    }
                                }
                                break;
                            case Calendar.SATURDAY:
                                if(dataSnapshot.child("businessTime").child("Saturday").child("status").getValue().toString().equals("false")){
                                    availableText.setText("Unavailable");
                                    availableImg.setImageResource(R.drawable.ic_dot_red);
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        availableText.setText("Available");
                                        availableImg.setImageResource(R.drawable.ic_dot_green);
                                    }else {
                                        availableText.setText("Unavailable");
                                        availableImg.setImageResource(R.drawable.ic_dot_red);
                                    }
                                }
                                break;
                        }
                    }else {
                        availableText.setText("Unavailable");
                        availableImg.setImageResource(R.drawable.ic_dot_red);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_mechanic_profile_back:
                super.onBackPressed();
                break;
            case R.id.driver_mechanic_profile_arrival_map:
                Intent map_intent = new Intent(this, DriverArriavalMapActivity.class);
                map_intent.putExtra("mechanicId", mechanicId);
                startActivity(map_intent);
                break;
            case R.id.driver_mechanic_profile_rating_review:
                Intent review_intent = new Intent(this, ShowRatingAndReviewActivity.class);
                review_intent.putExtra("mechanicId", mechanicId);
                startActivity(review_intent);
                break;
            case R.id.driver_mechanic_profile_contact:
                Intent msg_intent = new Intent(this, MessageActivity.class);
                msg_intent.putExtra("oppUserId", mechanicId);
                startActivity(msg_intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent main_intent = new Intent(this, DriverMainActivity.class);
        startActivity(main_intent);
        finish();
    }
}
