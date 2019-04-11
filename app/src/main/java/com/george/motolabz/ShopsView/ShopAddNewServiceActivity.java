package com.brainyapps.motolabz.ShopsView;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.ShopServiceRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.Utils;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.brainyapps.motolabz.Views.AlertFactoryClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopAddNewServiceActivity extends AppCompatActivity implements View.OnClickListener, ShopServiceRecyclerAdapter.OnClickItemListener{

    private RelativeLayout addDone;
    private ImageView onBack;
    private TextView title;
    private ImageView onDelete;
    private TextView service_type;
    private EditText rate;
    private TextView rate_currency;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String service_name = "";
    private String service_rate = "";
    private Boolean isUpdate = false;
    Dialog dlg;

    private ArrayList<String> serviceList = new ArrayList<>();
    private ArrayList<String> existingService = new ArrayList<>();
    private RecyclerView serviceItemRecyclerView;
    private ShopServiceRecyclerAdapter serviceItemRecyclerAdapter;

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
        setContentView(R.layout.activity_shop_add_new_service);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        onBack = (ImageView)findViewById(R.id.shop_add_new_service_back);
        onBack.setOnClickListener(this);
        onDelete = (ImageView)findViewById(R.id.shop_delete_service_item);
        onDelete.setOnClickListener(this);
        title = (TextView)findViewById(R.id.shop_add_new_service_title);
        addDone = (RelativeLayout)findViewById(R.id.shop_add_new_service_done);
        addDone.setOnClickListener(this);
        service_type = (TextView)findViewById(R.id.shop_new_service_name);
        service_type.setOnClickListener(this);
        rate = (EditText)findViewById(R.id.shop_new_service_rate);
        onDelete.setVisibility(View.GONE);
        rate_currency = (TextView)findViewById(R.id.shop_new_service_rate_currency);

        rate.setVisibility(View.GONE);
        rate_currency.setVisibility(View.GONE);

        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            String service_action = i.getStringExtra("ServiceAction");
            if(service_action.equals("add_service")){
                existingService = (ArrayList<String>)i.getSerializableExtra("exist_services");
            }else if(service_action.equals("edit_service")){
                isUpdate = true;
                service_name = i.getStringExtra("myServiceName");
                service_rate = i.getStringExtra("myServiceRate");
                if(service_name.equals("Offsite Diagnosis") || service_name.equals("In-Shop Diagnosis")){
                    rate.setVisibility(View.VISIBLE);
                    rate_currency.setVisibility(View.VISIBLE);
                    onDelete.setVisibility(View.GONE);
                }else {
                    onDelete.setVisibility(View.VISIBLE);
                }
                if(!service_name.isEmpty()){
                    title.setText(service_name);
                    service_type.setText(service_name);
                    rate.setText(service_rate);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shop_add_new_service_back:
                super.onBackPressed();
                break;
            case R.id.shop_add_new_service_done:
                if(isUpdate){
                    deleteCurrentService();
                    setInfo();
                }else {
                    setInfo();
                }
                break;
            case R.id.shop_new_service_name:
                showModelDlg();
                break;
            case R.id.shop_delete_service_item:
                AlertFactory.showAlert(this, "Delete Service", "Are you sure want to delete this service?", "YES", "NO", new AlertFactoryClickListener() {
                    @Override
                    public void onClickYes(final AlertDialog dialog) {
                        deleteCurrentService();
                        goBack();
                        dialog.dismiss();
                    }
                    @Override
                    public void onClickNo(AlertDialog dialog) {
                        dialog.dismiss();
                    }
                    @Override
                    public void onClickDone(AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });
                break;
        }
    }

    public void deleteCurrentService(){
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("services").child(service_name).removeValue();
    }

    public Boolean checkValidation(){
        if(service_type.getText().toString().isEmpty()){
            service_type.requestFocus();
            AlertFactory.showAlert(this, "", "Please input name of service.");
            return false;
        }
        if(service_name.equals("Offsite Diagnosis") || service_name.equals("In-Shop Diagnosis")){
            if(rate.getText().toString().isEmpty()){
                rate.requestFocus();
                AlertFactory.showAlert(this, "", "Please input rate of service.");
                return false;
            }else {
                if(!Utils.isDouble(rate.getText().toString())){
                    rate.requestFocus();
                    AlertFactory.showAlert(this, "", "Invalided input type.");
                    return false;
                }
            }
        }
        return true;
    }

    public void showModelDlg(){
        dlg = new Dialog(this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dlg_model_info);
        TextView title = (TextView)dlg.findViewById(R.id.dlg_model_info_title);
        title.setText("Please select service you want to add:");

        serviceItemRecyclerAdapter = new ShopServiceRecyclerAdapter(serviceList);
        serviceItemRecyclerView = (RecyclerView)dlg.findViewById(R.id.dlg_model_recycler_view);
        serviceItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        serviceItemRecyclerView.setAdapter(serviceItemRecyclerAdapter);
        serviceItemRecyclerAdapter.setOnClickItemListener(this);

//        modelItemRecyclerView.setVisibility(View.GONE);
        showProgressHUD("");
        Query serviceInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_SERVICE);
        serviceInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    serviceList.clear();
                    Map<String,Object> result = (Map<String,Object>)dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : result.entrySet()){
                        if(!existingService.contains(entry.getKey().toString())){
                            serviceList.add(entry.getKey().toString());
                        }
                    }
                    serviceItemRecyclerAdapter.notifyDataSetChanged();
                }
                hideProgressHUD();
                dlg.show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    public void setInfo(){
        if(checkValidation()){
            addService();
//            showProgressHUD("");
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//            Query query = databaseReference.child(DBInfo.TBL_USER).child(myId).child("services");
//            query.orderByChild("serviceName").equalTo(service_type.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (!dataSnapshot.exists()) {
//                        addService();
//                    }else {
//                        service_type.requestFocus();
//                        AlertFactory.showAlert(getApplication(), "", "service is already exist.");
//                        hideProgressHUD();
//                        return;
//                    }
//                    hideProgressHUD();
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    hideProgressHUD();
//                    return;
//                }
//            });
        }
    }

    public void addService(){
        Map<String, Object> result = new HashMap<>();
        if(service_name.equals("Offsite Diagnosis") || service_name.equals("In-Shop Diagnosis")){
            result.put("rate", Double.parseDouble(rate.getText().toString()));
        }
        result.put("serviceName", service_type.getText().toString());
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("services").child(service_type.getText().toString()).setValue(result).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressHUD();
                goBack();
            }
        });
    }

    public void goBack(){
        super.onBackPressed();
    }

    @Override
    public void clickServiceItem(int index, String service_name) {
        this.service_name = service_name;
        if(service_name.equals("Offsite Diagnosis") || service_name.equals("In-Shop Diagnosis")){
            rate.setVisibility(View.VISIBLE);
            rate_currency.setVisibility(View.VISIBLE);
        }else {
            rate.setVisibility(View.GONE);
            rate_currency.setVisibility(View.GONE);
        }
        service_type.setText(service_name);
        dlg.hide();
    }
}
