package com.brainyapps.motolabz.ShopsView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brainyapps.motolabz.Adapters.VehicleModelRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.CarModel;
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
import java.util.Map;

public class ShopModelsActivity extends AppCompatActivity implements View.OnClickListener, VehicleModelRecyclerAdapter.OnClickItemListener{
    private ImageView btn_back;
    private RelativeLayout btn_add;
//    private ArrayList<CarModel> myModelList = new ArrayList<>();
    private ArrayList<String> modelStringList = new ArrayList<>();
    private RecyclerView modelItemRecyclerView;
    private VehicleModelRecyclerAdapter modelItemRecyclerAdapter;
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
        setContentView(R.layout.activity_shop_models);

        btn_back = (ImageView)findViewById(R.id.shop_models_back);
        btn_back.setOnClickListener(this);
        btn_add = (RelativeLayout)findViewById(R.id.shop_goto_add_model);
        btn_add.setOnClickListener(this);

        modelItemRecyclerAdapter = new VehicleModelRecyclerAdapter(modelStringList);
        modelItemRecyclerView = (RecyclerView)findViewById(R.id.shop_model_list_recycler_viewer);
        modelItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        modelItemRecyclerView.setAdapter(modelItemRecyclerAdapter);
        modelItemRecyclerAdapter.setOnClickItemListener(this);
    }

    public void updateList(){
        showProgressHUD("");
        Query modelInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("serviceModels");
        modelInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
//                    myModelList.clear();
                    modelStringList.clear();
                    Map<String,Object> result = (Map<String,Object>)dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : result.entrySet()){
                        Map singleEntry = (Map) entry.getValue();
//                        CarModel new_result = new CarModel();
//                        new_result.description = singleEntry.get("description").toString();
//                        new_result.engine = singleEntry.get("engine").toString();
//                        new_result.model = singleEntry.get("model").toString();
//                        new_result.photoUrl = singleEntry.get("photoUrl").toString();
//                        new_result.type = singleEntry.get("type").toString();
//                        myModelList.add(new_result);
                        modelStringList.add(singleEntry.get("model").toString());
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
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shop_models_back:
                super.onBackPressed();
                break;
            case R.id.shop_goto_add_model:
                Intent add_model_intent = new Intent(this, ShopAddModelActivity.class);
                add_model_intent.putExtra("modelShowAction", "add_model");
                add_model_intent.putStringArrayListExtra("exist_models", modelStringList);
                startActivity(add_model_intent);
                break;
        }
    }

    @Override
    public void clickModelItem(int index, String model_name) {
        Intent edit_model_intent = new Intent(this, ShopAddModelActivity.class);
        edit_model_intent.putExtra("modelShowAction", "edit_model");
        edit_model_intent.putExtra("currentModelName", model_name);
        startActivity(edit_model_intent);
    }
}
