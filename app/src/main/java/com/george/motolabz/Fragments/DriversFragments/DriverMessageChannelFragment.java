package com.brainyapps.motolabz.Fragments.DriversFragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brainyapps.motolabz.Adapters.MessageChannelRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.MessageActivity;
import com.brainyapps.motolabz.Models.Channel;
import com.brainyapps.motolabz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DriverMessageChannelFragment extends Fragment implements View.OnClickListener, MessageChannelRecyclerAdapter.OnClickItemListener{

    private RecyclerView channelRecyclerView;
    private ArrayList<Channel> channelList = new ArrayList<>();
    private MessageChannelRecyclerAdapter channelRecyclerAdapter;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public static final String FRAGMENT_TAG = "com_motolabz_driver_message_fragment_tag";
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

        android.app.Fragment f = new DriverMessageChannelFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_driver_message_channel, container, false);

        channelRecyclerAdapter = new MessageChannelRecyclerAdapter(channelList);
        channelRecyclerView = (RecyclerView)rootView.findViewById(R.id.message_channel_recycler_view);
        channelRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        channelRecyclerView.setAdapter(channelRecyclerAdapter);
        channelRecyclerAdapter.setOnClickItemListener(DriverMessageChannelFragment.this);

        return rootView;
    }

    public void refreshView(){
        Query shopInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_CHAT).child(myId);
        shopInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    channelList.clear();
                    for (DataSnapshot userItem : dataSnapshot.getChildren()){
                        Channel new_channel = new Channel();
                        new_channel.userID = userItem.getKey();
                        new_channel.lastMsg = userItem.child("lastMsg").getValue().toString();
                        new_channel.time = Long.parseLong(userItem.child("time").getValue().toString());
                        if(userItem.child("isRead").getValue().toString().equals("true")){
                            new_channel.isRead = true;
                        }else {
                            new_channel.isRead = false;
                        }
                        channelList.add(new_channel);
                    }
                    channelRecyclerAdapter.notifyDataSetChanged();
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
            default:
                break;
        }
    }

    @Override
    public void clickChannel(int index, String userId) {
        Intent msg_intent = new Intent(getActivity(), MessageActivity.class);
        msg_intent.putExtra("oppUserId", userId);
        startActivity(msg_intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }
}
