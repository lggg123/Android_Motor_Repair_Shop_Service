package com.brainyapps.motolabz.DriversView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.ShopServiceRecyclerAdapter;
import com.brainyapps.motolabz.Adapters.VehicleModelRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.PrefUtils;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class DriverFilterSearchActivity extends AppCompatActivity implements View.OnClickListener, VehicleModelRecyclerAdapter.OnClickItemListener, ShopServiceRecyclerAdapter.OnClickItemListener{

    private RelativeLayout btn_submit;
    private ImageView btn_back;

    private ImageView dropdown_model;
    private ImageView dropdown_service;
    private EditText filter_desc;

    private TextView selected_model;
    private TextView selected_service;

    private ArrayList<String> modelList = new ArrayList<>();
    private RecyclerView modelItemRecyclerView;
    private VehicleModelRecyclerAdapter modelItemRecyclerAdapter;

    private ArrayList<String> serviceList = new ArrayList<>();
    private RecyclerView serviceItemRecyclerView;
    private ShopServiceRecyclerAdapter serviceItemRecyclerAdapter;

    boolean model_dropdown = false;
    boolean service_dropdown = false;
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
        setContentView(R.layout.activity_driver_filter_search);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        btn_submit = (RelativeLayout)findViewById(R.id.search_for_shop_submit);
        btn_submit.setOnClickListener(this);

        btn_back = (ImageView)findViewById(R.id.filter_vehicle_back);
        btn_back.setOnClickListener(this);

        selected_model = (TextView)findViewById(R.id.filter_selected_model);
        selected_model.setText(PrefUtils.getInstance().getSearchModel(PrefUtils.PREF_SEARCH_MODEL, "Select Vehicle"));
        selected_service = (TextView)findViewById(R.id.filter_vehicle_selected_services);
        selected_service.setText(PrefUtils.getInstance().getSearchService(PrefUtils.PREF_SEARCH_SERVICE, "Service Needed/ Nature of Problem"));

        filter_desc = (EditText)findViewById(R.id.filter_option_description);
        filter_desc.setText(PrefUtils.getInstance().getDescService(PrefUtils.PREF_SEARCH_DESCRIPTION,""));

        dropdown_model = (ImageView)findViewById(R.id.filter_model_dropdown);
        dropdown_model.setOnClickListener(this);
        dropdown_service = (ImageView)findViewById(R.id.filter_service_dropdown);
        dropdown_service.setOnClickListener(this);

        showProgressHUD("");
        modelItemRecyclerAdapter = new VehicleModelRecyclerAdapter(modelList);
        modelItemRecyclerView = (RecyclerView)findViewById(R.id.filter_vehicle_recycler_model);
        modelItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        modelItemRecyclerView.setAdapter(modelItemRecyclerAdapter);
        modelItemRecyclerAdapter.setOnClickItemListener(this);

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

        serviceItemRecyclerAdapter = new ShopServiceRecyclerAdapter(serviceList);
        serviceItemRecyclerView = (RecyclerView)findViewById(R.id.filter_vehicle_recycler_services);
        serviceItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        serviceItemRecyclerView.setAdapter(serviceItemRecyclerAdapter);
        serviceItemRecyclerAdapter.setOnClickItemListener(this);

        Query serviceInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_SERVICE);
        serviceInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    serviceList.clear();
                    Map<String,Object> result = (Map<String,Object>)dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : result.entrySet()){
                        serviceList.add(entry.getKey().toString());
                    }
                    serviceItemRecyclerAdapter.notifyDataSetChanged();
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
            case R.id.search_for_shop_submit:
                setFilter();
                break;
            case R.id.filter_vehicle_back:
                super.onBackPressed();
                break;
            case R.id.filter_model_dropdown:
                if(model_dropdown){
                    model_dropdown = false;
                    modelItemRecyclerView.setVisibility(View.GONE);
                }else {
                    model_dropdown = true;
                    modelItemRecyclerView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.filter_service_dropdown:
                if(service_dropdown){
                    service_dropdown = false;
                    serviceItemRecyclerView.setVisibility(View.GONE);
                }else {
                    service_dropdown = true;
                    serviceItemRecyclerView.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    public void setFilter(){
        if(!selected_model.getText().toString().equals("Select Vehicle")){
            PrefUtils.getInstance().setSearchModel(PrefUtils.PREF_SEARCH_MODEL, selected_model.getText().toString());
        }
        if(!selected_service.getText().toString().equals("Service Needed/ Nature of Problem")){
            PrefUtils.getInstance().setSearchService(PrefUtils.PREF_SEARCH_SERVICE, selected_service.getText().toString());
        }
        if(!selected_service.getText().toString().equals("Service Needed/ Nature of Problem")){
            PrefUtils.getInstance().setSearchService(PrefUtils.PREF_SEARCH_SERVICE, selected_service.getText().toString());
        }
        PrefUtils.getInstance().setDescService(PrefUtils.PREF_SEARCH_DESCRIPTION, filter_desc.getText().toString());
        super.onBackPressed();
    }

    @Override
    public void clickModelItem(int index, String model_name) {
        selected_model.setText(model_name);
        modelItemRecyclerView.setVisibility(View.GONE);
        model_dropdown = false;
    }

    @Override
    public void clickServiceItem(int index, String service_name) {
        selected_service.setText(service_name);
        serviceItemRecyclerView.setVisibility(View.GONE);
        model_dropdown = false;
    }
}
