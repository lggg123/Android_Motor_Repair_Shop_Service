package com.brainyapps.motolabz.ShopsView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.brainyapps.motolabz.Adapters.MechanicRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;

public class ShopMechanicsListActivity extends AppCompatActivity implements View.OnClickListener, MechanicRecyclerAdapter.OnClickItemListener{

    private ImageView onBack;
    private RecyclerView mechanicRecyclerView;
    private MechanicRecyclerAdapter mechanicRecyclerAdapter;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<String> myMechanics = new ArrayList<>();
    private ArrayList<Mechanic> mechanicList = new ArrayList<>();

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
        setContentView(R.layout.activity_shop_mechanics_list);

        onBack = (ImageView)findViewById(R.id.shop_mechanic_list_back);
        onBack.setOnClickListener(this);

        mechanicRecyclerAdapter = new MechanicRecyclerAdapter(mechanicList);
        mechanicRecyclerView = (RecyclerView)findViewById(R.id.shop_mechanic_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mechanicRecyclerView.setLayoutManager(mLayoutManager);
        mechanicRecyclerView.setAdapter(mechanicRecyclerAdapter);
        mechanicRecyclerAdapter.setOnClickItemListener(this);

        getMechanic();
    }

    public void getMechanic(){
        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("mechanics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot mt : dataSnapshot.getChildren()){
                        myMechanics.add(mt.getKey().toString());
                    }
                    getMechanicList();
                }
                hideProgressHUD();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    public void getMechanicList(){
        showProgressHUD("");
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mechanicList.clear();
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        if(myMechanics.contains(data.getKey().toString())){
                            Mechanic mec = data.getValue(Mechanic.class);
                            mechanicList.add(mec);
                        }
                    }
                    mechanicRecyclerAdapter.notifyDataSetChanged();
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
            case R.id.shop_mechanic_list_back:
                super.onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void clickMechanicItem(int index, String mechanicId) {
        Intent intent = new Intent(this, ShopMechanicDetailActivity.class);
        intent.putExtra("mechanicId", mechanicId);
        startActivity(intent);
    }
}
