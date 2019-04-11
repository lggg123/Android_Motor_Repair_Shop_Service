package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.TaskStatus;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HappyBear on 9/3/2018.
 */

public class RequestRecyclerAdapter extends RecyclerView.Adapter<RequestRecyclerAdapter.ViewHolder>{

    public List<TaskStatus> taskList = new ArrayList<>();
    public RequestRecyclerAdapter(ArrayList<TaskStatus> taskList){
        super();
        this.taskList = taskList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_request_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final TaskStatus task = taskList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        if(task.status.equals("pending")){
            viewHolder.statusDone.setVisibility(View.GONE);
            viewHolder.statusWorking.setVisibility(View.GONE);
            viewHolder.statusAccept.setVisibility(View.GONE);
            viewHolder.statusDecline.setVisibility(View.GONE);
            viewHolder.statusPending.setVisibility(View.VISIBLE);
            viewHolder.statusAssigned.setVisibility(View.GONE);
        } else if(task.status.equals("assigning")){
            if(FirebaseManager.getInstance().currentUserType.equals("mechanic")){
                viewHolder.statusAccept.setVisibility(View.VISIBLE);
                viewHolder.statusDecline.setVisibility(View.VISIBLE);
                viewHolder.statusAssigned.setVisibility(View.GONE);
            }else {
                viewHolder.statusAccept.setVisibility(View.GONE);
                viewHolder.statusDecline.setVisibility(View.GONE);
                viewHolder.statusAssigned.setVisibility(View.VISIBLE);
            }
            viewHolder.statusDone.setVisibility(View.GONE);
            viewHolder.statusWorking.setVisibility(View.GONE);
            viewHolder.statusPending.setVisibility(View.GONE);
        }else if(task.status.equals("working")){
            viewHolder.statusDone.setVisibility(View.GONE);
            viewHolder.statusWorking.setVisibility(View.VISIBLE);
            viewHolder.statusAccept.setVisibility(View.GONE);
            viewHolder.statusDecline.setVisibility(View.GONE);
            viewHolder.statusPending.setVisibility(View.GONE);
            viewHolder.statusAssigned.setVisibility(View.GONE);
        }else {
            viewHolder.statusDone.setVisibility(View.VISIBLE);
            viewHolder.statusWorking.setVisibility(View.GONE);
            viewHolder.statusAccept.setVisibility(View.GONE);
            viewHolder.statusDecline.setVisibility(View.GONE);
            viewHolder.statusPending.setVisibility(View.GONE);
            viewHolder.statusAssigned.setVisibility(View.GONE);
        }
        viewHolder.request_time.setText(Utils.converteTimestamp(task.time));
        viewHolder.request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickRequestItem(position, task.key, task.status);
            }
        });

        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(task.customerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    viewHolder.requester_name.setText(dataSnapshot.child("fullName").getValue().toString());
                    if(!dataSnapshot.child("photoUrl").getValue().toString().isEmpty()){
                        Glide.with(viewHolder.requester_avatar.getContext()).load(dataSnapshot.child("photoUrl").getValue().toString()).into(viewHolder.requester_avatar);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private OnClickRequestListener mListener;

    public void setOnClickRequestListener(OnClickRequestListener listener) {
        this.mListener = listener;
    }

    public interface OnClickRequestListener{
        void onClickRequestItem(int index, String taskId, String taskStatus);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setData(ArrayList<TaskStatus> data) {
        taskList.clear();
        taskList.addAll(data);
    }

    public void clear() {
        taskList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout request;
        public CircleImageView requester_avatar;
        public TextView requester_name;
        public TextView request_time;

        public RelativeLayout statusPending;
        public RelativeLayout statusAccept;
        public RelativeLayout statusDecline;
        public RelativeLayout statusWorking;
        public RelativeLayout statusDone;
        public RelativeLayout statusAssigned;

        public ViewHolder(View convertView) {
            super(convertView);
            request = (RelativeLayout) convertView.findViewById(R.id.shop_requester_content);
            requester_avatar = (CircleImageView)convertView.findViewById(R.id.shop_request_avatar);
            requester_name = (TextView) convertView.findViewById(R.id.shop_request_name);
            request_time = (TextView) convertView.findViewById(R.id.shop_request_time);

            statusPending = (RelativeLayout)convertView.findViewById(R.id.shop_request_status_pending);
            statusAccept = (RelativeLayout)convertView.findViewById(R.id.shop_request_status_accept);
            statusDecline = (RelativeLayout)convertView.findViewById(R.id.shop_request_status_decline);
            statusWorking = (RelativeLayout)convertView.findViewById(R.id.shop_request_status_working);
            statusDone = (RelativeLayout)convertView.findViewById(R.id.shop_request_status_done);
            statusAssigned = (RelativeLayout)convertView.findViewById(R.id.shop_request_status_assigned);
        }
    }
}
