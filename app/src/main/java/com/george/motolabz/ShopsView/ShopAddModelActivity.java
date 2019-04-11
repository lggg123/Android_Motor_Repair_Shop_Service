package com.brainyapps.motolabz.ShopsView;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.brainyapps.motolabz.Models.CarModel;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.brainyapps.motolabz.Views.AlertFactoryClickListener;
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

public class ShopAddModelActivity extends AppCompatActivity implements View.OnClickListener, VehicleModelRecyclerAdapter.OnClickItemListener{

    private ImageView btn_back;
    private ImageView btn_remove_model;
    private TextView text_model;
    private RelativeLayout btn_save;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String model_name = "";
    private Boolean isUpdate = false;

    private HashMap<String, CarModel> availableModelList = new HashMap<>();
    private ArrayList<String> existingModels = new ArrayList<>();

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
        setContentView(R.layout.activity_shop_add_model);

        btn_back = (ImageView)findViewById(R.id.shop_add_model_back);
        btn_back.setOnClickListener(this);
        btn_remove_model = (ImageView)findViewById(R.id.shop_delete_model_item);
        btn_remove_model.setOnClickListener(this);
        text_model = (TextView)findViewById(R.id.shop_new_model_name);
        text_model.setOnClickListener(this);
        btn_save = (RelativeLayout)findViewById(R.id.shop_add_model_done);
        btn_save.setOnClickListener(this);

        modelItemRecyclerAdapter = new VehicleModelRecyclerAdapter(modelList);
        modelItemRecyclerView = (RecyclerView)findViewById(R.id.available_models_recycler_model);
        modelItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        modelItemRecyclerView.setAdapter(modelItemRecyclerAdapter);
        modelItemRecyclerAdapter.setOnClickItemListener(this);

        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            String service_action = i.getStringExtra("modelShowAction");
            if(service_action.equals("add_model")){
                existingModels = (ArrayList<String>)i.getSerializableExtra("exist_models");
                btn_remove_model.setVisibility(View.GONE);
            }else if(service_action.equals("edit_model")){
                isUpdate = true;
                btn_remove_model.setVisibility(View.VISIBLE);
                model_name = i.getStringExtra("currentModelName");
                if(!model_name.isEmpty()){
                    btn_remove_model.setVisibility(View.VISIBLE);
                    text_model.setText(model_name);
                }
            }
        }
        showProgressHUD("");
        Query modelInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_MODELS);
        modelInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    modelList.clear();
                    Map<String,Object> result = (Map<String,Object>)dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : result.entrySet()){
                        if(!existingModels.contains(entry.getKey().toString())){
                            Map singleEntry = (Map) entry.getValue();
                            CarModel new_result = new CarModel();
                            new_result.description = singleEntry.get("description").toString();
//                            new_result.engine = singleEntry.get("engine").toString();
                            new_result.model = singleEntry.get("model").toString();
                            new_result.photoUrl = singleEntry.get("photoUrl").toString();
//                            new_result.type = singleEntry.get("type").toString();
                            availableModelList.put(singleEntry.get("model").toString(), new_result);
                            modelList.add(entry.getKey().toString());
                        }
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shop_add_model_back:
                super.onBackPressed();
                break;
            case R.id.shop_new_model_name:
                modelItemRecyclerView.setVisibility(View.VISIBLE);
                break;
            case R.id.shop_delete_model_item:
                AlertFactory.showAlert(this, "Delete Model", "Are you sure want to delete this model?", "YES", "NO", new AlertFactoryClickListener() {
                    @Override
                    public void onClickYes(final AlertDialog dialog) {
                        deleteCurrentModel();
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
            case R.id.shop_add_model_done:
                if(checkValidation()){
                    addModel();
                }
                break;
        }
    }

    public void deleteCurrentModel(){
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("serviceModels").child(model_name).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                goBack();
            }
        });
    }

    public Boolean checkValidation(){
        if(text_model.getText().toString().isEmpty()){
            text_model.requestFocus();
            AlertFactory.showAlert(this, "", "Please select model you want to add.");
            return false;
        }
        return true;
    }

    public void addModel(){
        CarModel selectedModel = availableModelList.get(text_model.getText().toString());
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("serviceModels").child(text_model.getText().toString()).setValue(selectedModel).addOnCompleteListener(new OnCompleteListener<Void>() {
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
    public void clickModelItem(int index, String model_name) {
        text_model.setText(model_name);
        modelItemRecyclerView.setVisibility(View.GONE);
    }
}
