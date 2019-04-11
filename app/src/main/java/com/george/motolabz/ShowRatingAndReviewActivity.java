package com.brainyapps.motolabz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.brainyapps.motolabz.Adapters.RatingRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.RateReview;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowRatingAndReviewActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView onBack;
    private ImageView gotoReview;

    private ArrayList<RateReview> rateList = new ArrayList<>();
    private RatingRecyclerAdapter ratingRecyclerAdapter;
    private RecyclerView ratingRecyclerView;

    private String mechanicId = "";
    private Query getRatingList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_rating_and_review);
        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            mechanicId = i.getStringExtra("mechanicId");
        }else {
            super.onBackPressed();
        }
        onBack = (ImageView) findViewById(R.id.driver_rate_and_review_btn_back);
        onBack.setOnClickListener(this);
        gotoReview = (ImageView) findViewById(R.id.driver_rate_view_post);
        gotoReview.setOnClickListener(this);
        if(FirebaseManager.getInstance().getUserType().equals("customer")){
            gotoReview.setVisibility(View.VISIBLE);
        }else {
            gotoReview.setVisibility(View.GONE);
        }
        ratingRecyclerAdapter = new RatingRecyclerAdapter(rateList);
        ratingRecyclerView = (RecyclerView) findViewById(R.id.driver_rating_recycler_viewer);
        ratingRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ratingRecyclerView.setAdapter(ratingRecyclerAdapter);

        getRatingList = FirebaseDatabase.getInstance().getReference().child(RateReview.TABLE_NAME).child(mechanicId);
        getRatingList.addValueEventListener(getList);
    }

    private ValueEventListener getList = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for (DataSnapshot rating : dataSnapshot.getChildren()){
                    RateReview item = rating.getValue(RateReview.class);
                    rateList.add(item);
                }
                ratingRecyclerAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

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
        getRatingList.removeEventListener(getList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_rate_and_review_btn_back:
                super.onBackPressed();
                break;
            case R.id.driver_rate_view_post:
                Intent review_intent = new Intent(this, RatingReviewActivity.class);
                review_intent.putExtra("mechanicId", mechanicId);
                startActivity(review_intent);
                break;
            default:
                break;
        }
    }
}
