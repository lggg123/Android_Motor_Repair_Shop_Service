package com.brainyapps.motolabz.Fragments.ShopsFragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.brainyapps.motolabz.Adapters.RequestRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.TaskStatus;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.ShopsView.ShopCustomerDetailActivity;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShopServiceRequestFragment extends Fragment implements View.OnClickListener, RequestRecyclerAdapter.OnClickRequestListener{

    private ArrayList<TaskStatus> taskList = new ArrayList<>();
    private RequestRecyclerAdapter requestRecyclerAdapter;
    private RecyclerView requestRecyclerView;

    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Query getInfo;

    public static final String FRAGMENT_TAG = "com_motolabz_shop_request_fragment_tag";
    private static Context mContext;

    private ProgressHUD mProgressDialog;

    private void showProgressHUD(String text) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = ProgressHUD.show(getActivity(), text, true);
        mProgressDialog.show();
    }

    private void hideProgressHUD() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
    public static android.app.Fragment newInstance(Context context) {
        mContext = context;

        android.app.Fragment f = new ShopServiceRequestFragment();
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_shop_service_request, container, false);

        requestRecyclerAdapter = new RequestRecyclerAdapter(taskList);
        requestRecyclerView = (RecyclerView) rootView.findViewById(R.id.shop_request_recycler_view);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        requestRecyclerView.setAdapter(requestRecyclerAdapter);
        requestRecyclerAdapter.setOnClickRequestListener(ShopServiceRequestFragment.this);
        refreshTable();
        return rootView;
    }

    private ValueEventListener updateList = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                taskList.clear();
                for(DataSnapshot task : dataSnapshot.getChildren()){
                    TaskStatus ts = task.getValue(TaskStatus.class);
                    taskList.add(ts);
                }
                requestRecyclerAdapter.notifyDataSetChanged();
                requestRecyclerView.smoothScrollToPosition(taskList.size());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void refreshTable(){
        getInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_TASK).child(myId);
        getInfo.addValueEventListener(updateList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            default:
                break;
        }
    }

    @Override
    public void onClickRequestItem(int index, String taskId, String taskStatus) {
        Intent intent = new Intent(getActivity(), ShopCustomerDetailActivity.class);
        intent.putExtra("requesterId", taskId);
        startActivity(intent);
    }
}