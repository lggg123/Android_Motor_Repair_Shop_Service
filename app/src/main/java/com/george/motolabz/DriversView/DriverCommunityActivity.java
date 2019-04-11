package com.brainyapps.motolabz.DriversView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.VehicleModelRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.ServiceListItem;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;
import java.util.Map;

public class DriverCommunityActivity extends AppCompatActivity implements View.OnClickListener, VehicleModelRecyclerAdapter.OnClickItemListener{

    private ImageView btnBack;
    private RelativeLayout joinToCommunity;
    private TextView selectedModel;
    private ImageView selectImg;
    private boolean isOpen = false;
    private String modelName = "";

    private ArrayList<String> modelList = new ArrayList<>();
    private RecyclerView modelItemRecyclerView;
    private VehicleModelRecyclerAdapter modelItemRecyclerAdapter;

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
        setContentView(R.layout.activity_driver_community);

        btnBack = (ImageView) findViewById(R.id.driver_main_community_back);
        btnBack.setOnClickListener(this);
        joinToCommunity = (RelativeLayout) findViewById(R.id.driver_main_join_to_community);
        joinToCommunity.setOnClickListener(this);
        selectedModel = (TextView)findViewById(R.id.driver_community_select);
        selectImg = (ImageView)findViewById(R.id.driver_community_select_img);
        selectImg.setOnClickListener(this);

        modelItemRecyclerAdapter = new VehicleModelRecyclerAdapter(modelList);
        modelItemRecyclerView = (RecyclerView) findViewById(R.id.driver_community_model_recycler_view);
        modelItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        modelItemRecyclerView.setAdapter(modelItemRecyclerAdapter);
        modelItemRecyclerAdapter.setOnClickItemListener(this);

        modelItemRecyclerView.setVisibility(View.GONE);
        showProgressHUD("");
        Query userInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_MODELS);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    modelList.clear();
                    Map<String,Object> result = (Map<String,Object>)dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : result.entrySet()){
                        modelList.add(entry.getKey().toString());
                    }
                    modelItemRecyclerAdapter.notifyDataSetChanged();
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
            case R.id.driver_main_community_back:
                super.onBackPressed();
                break;
            case R.id.driver_main_join_to_community:
                if(modelName.isEmpty()){
                    AlertFactory.showAlert(this, "", "Please select model to join community.");
                }else {
                    Intent post_community_intent = new Intent(this, DriverPostOnCommunityActivity.class);
                    post_community_intent.putExtra("ModelName", modelName);
                    startActivity(post_community_intent);
                }
                break;
            case R.id.driver_community_select_img:
                if(isOpen){
                    isOpen = false;
                    modelItemRecyclerView.setVisibility(View.GONE);
                    selectImg.setImageResource(R.drawable.ic_arrow_down);
                }else {
                    isOpen = true;
                    modelItemRecyclerView.setVisibility(View.VISIBLE);
                    selectImg.setImageResource(R.drawable.ic_arrow_up);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void clickModelItem(int index, String model_name) {
        selectedModel.setText(model_name);
        modelName = model_name;
        isOpen = false;
        modelItemRecyclerView.setVisibility(View.GONE);
        selectImg.setImageResource(R.drawable.ic_arrow_down);
    }
}
