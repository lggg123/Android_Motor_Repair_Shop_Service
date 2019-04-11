package com.brainyapps.motolabz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.RateReview;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RatingReviewActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView onBack;
    private RelativeLayout submit;

    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;
    private EditText reviewText;

    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String mechanicId = "";
    private int rateScore = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_review);
        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            mechanicId = i.getStringExtra("mechanicId");
        }else {
            super.onBackPressed();
        }
        onBack = (ImageView) findViewById(R.id.rate_and_review_btn_back);
        onBack.setOnClickListener(this);
        submit = (RelativeLayout) findViewById(R.id.rate_and_review_submit);
        submit.setOnClickListener(this);
        reviewText = (EditText)findViewById(R.id.write_review_edit);
        star1 = (ImageView)findViewById(R.id.driver_write_review_star1);
        star1.setOnClickListener(this);
        star2 = (ImageView)findViewById(R.id.driver_write_review_star2);
        star2.setOnClickListener(this);
        star3 = (ImageView)findViewById(R.id.driver_write_review_star3);
        star3.setOnClickListener(this);
        star4 = (ImageView)findViewById(R.id.driver_write_review_star4);
        star4.setOnClickListener(this);
        star5 = (ImageView)findViewById(R.id.driver_write_review_star5);
        star5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rate_and_review_btn_back:
                goBack();
                break;
            case R.id.rate_and_review_submit:
                sendRate();
                break;
            case R.id.driver_write_review_star1:
                star1.setImageResource(R.drawable.ic_star_on);
                star2.setImageResource(R.drawable.ic_star_off);
                star3.setImageResource(R.drawable.ic_star_off);
                star4.setImageResource(R.drawable.ic_star_off);
                star5.setImageResource(R.drawable.ic_star_off);
                rateScore = 1;
                break;
            case R.id.driver_write_review_star2:
                star1.setImageResource(R.drawable.ic_star_on);
                star2.setImageResource(R.drawable.ic_star_on);
                star3.setImageResource(R.drawable.ic_star_off);
                star4.setImageResource(R.drawable.ic_star_off);
                star5.setImageResource(R.drawable.ic_star_off);
                rateScore = 2;
                break;
            case R.id.driver_write_review_star3:
                star1.setImageResource(R.drawable.ic_star_on);
                star2.setImageResource(R.drawable.ic_star_on);
                star3.setImageResource(R.drawable.ic_star_on);
                star4.setImageResource(R.drawable.ic_star_off);
                star5.setImageResource(R.drawable.ic_star_off);
                rateScore = 3;
                break;
            case R.id.driver_write_review_star4:
                star1.setImageResource(R.drawable.ic_star_on);
                star2.setImageResource(R.drawable.ic_star_on);
                star3.setImageResource(R.drawable.ic_star_on);
                star4.setImageResource(R.drawable.ic_star_on);
                star5.setImageResource(R.drawable.ic_star_off);
                rateScore = 4;
                break;
            case R.id.driver_write_review_star5:
                star1.setImageResource(R.drawable.ic_star_on);
                star2.setImageResource(R.drawable.ic_star_on);
                star3.setImageResource(R.drawable.ic_star_on);
                star4.setImageResource(R.drawable.ic_star_on);
                star5.setImageResource(R.drawable.ic_star_on);
                rateScore = 5;
                break;
            default:
                break;
        }
    }

    public void sendRate(){
        if(reviewText.getText().toString().isEmpty()){
            reviewText.requestFocus();
            AlertFactory.showAlert(this,"Rate and Review","Please type review about this mechanic.");
        }else {
            String rateKey = FirebaseDatabase.getInstance().getReference().child(RateReview.TABLE_NAME).child(mechanicId).push().getKey();
            RateReview rate  = new RateReview();
            rate.userID = myId;
            rate.time = System.currentTimeMillis();
            rate.rate = rateScore;
            rate.rateContent = reviewText.getText().toString();
            FirebaseDatabase.getInstance().getReference().child(RateReview.TABLE_NAME).child(mechanicId).child(rateKey).setValue(rate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    goBack();
                }
            });
        }
    }

    public void goBack(){
        super.onBackPressed();
    }
}
