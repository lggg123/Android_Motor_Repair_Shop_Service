package com.brainyapps.motolabz.ShopsView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brainyapps.motolabz.Adapters.ServiceRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.ServiceListItem;
import com.brainyapps.motolabz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopAddServiceActivity extends AppCompatActivity implements View.OnClickListener, ServiceRecyclerAdapter.OnClickItemListener{

    private RelativeLayout addService;
    private ImageView onBack;
    private ArrayList<ServiceListItem> serviceList = new ArrayList<>();
    private ArrayList<String> serviceStringList = new ArrayList<>();
    private RecyclerView serviceItemRecyclerView;
    private ServiceRecyclerAdapter serviceItemRecyclerAdapter;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private DatabaseReference mDatabase;
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
        setContentView(R.layout.activity_shop_add_service);

        onBack = (ImageView)findViewById(R.id.shop_service_offered_back);
        onBack.setOnClickListener(this);
        addService = (RelativeLayout)findViewById(R.id.shop_goto_add_service);
        addService.setOnClickListener(this);

        serviceItemRecyclerAdapter = new ServiceRecyclerAdapter(serviceList);
        serviceItemRecyclerView = (RecyclerView) findViewById(R.id.shop_service_list_recycler_viewer);
        serviceItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        serviceItemRecyclerView.setAdapter(serviceItemRecyclerAdapter);
        serviceItemRecyclerAdapter.setOnClickItemListener(this);

    }

    public void updateList(){
        showProgressHUD("");
        Query userInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("services");
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    serviceList.clear();
                    serviceStringList.clear();
                    Map<String,Object> result = (Map<String,Object>)dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : result.entrySet()){
                        Map singleEntry = (Map) entry.getValue();
                        ServiceListItem new_result = new ServiceListItem();
                        new_result.service_name = singleEntry.get("serviceName").toString();
                        if(singleEntry.containsKey("rate")){
                            new_result.service_rate = singleEntry.get("rate").toString();
                        }
                        if(!new_result.service_rate.isEmpty()){
                            serviceList.add(0,new_result);
                        }else {
                            serviceList.add(new_result);
                        }
                        serviceStringList.add(new_result.service_name);
                    }
                    serviceItemRecyclerAdapter.notifyDataSetChanged();
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
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shop_service_offered_back:
                super.onBackPressed();
                break;
            case R.id.shop_goto_add_service:
                Intent add_service_intent = new Intent(this, ShopAddNewServiceActivity.class);
                add_service_intent.putExtra("ServiceAction", "add_service");
                add_service_intent.putStringArrayListExtra("exist_services", serviceStringList);
                startActivity(add_service_intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void clickServiceItem(int index, String service_name, String service_rate) {
        Intent add_service_intent = new Intent(this, ShopAddNewServiceActivity.class);
        add_service_intent.putExtra("ServiceAction", "edit_service");
        add_service_intent.putExtra("myServiceName", service_name);
        add_service_intent.putExtra("myServiceRate", service_rate);
        startActivity(add_service_intent);
    }
}