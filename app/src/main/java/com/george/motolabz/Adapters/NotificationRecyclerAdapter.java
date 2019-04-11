package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Models.Notification;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HappyBear on 8/30/2018.
 */

public class NotificationRecyclerAdapter  extends RecyclerView.Adapter<NotificationRecyclerAdapter.ViewHolder>{

    public List<Notification> notificationList = new ArrayList<>();
    public NotificationRecyclerAdapter(ArrayList<Notification> notificationList){
        super();
        this.notificationList = notificationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_notification_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Notification notificationItem = notificationList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.time.setText(Utils.converteTimestamp(notificationItem.time));
        viewHolder.postKey.setText(notificationItem.postKey);
        if(notificationItem.type == 1){
            Glide.with(viewHolder.typeImg.getContext()).load("").into(viewHolder.typeImg);
        }
        if(notificationItem.type == 4){
            viewHolder.btn_accept.setVisibility(View.VISIBLE);
            viewHolder.btn_accept.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.clickBtnAccept(position, notificationItem.notificationID, notificationItem.senderID);
                    }
                }
            });
            viewHolder.btn_decline.setVisibility(View.VISIBLE);
            viewHolder.btn_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        mListener.clickBtnDecline(position, notificationItem.notificationID, notificationItem.senderID);
                    }
                }
            });
        }
        viewHolder.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickNotification(position, notificationItem.notificationID, notificationItem.type);
                }
            }
        });
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickNotification(int index, String notificationID, int notificationType);
        void clickBtnAccept(int index, String notificationID, String senderID);
        void clickBtnDecline(int index, String notificationID, String senderID);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void setData(ArrayList<Notification> data) {
        notificationList.clear();
        notificationList.addAll(data);
    }

    public void clear() {
        notificationList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout notification;
        public ImageView typeImg;
        public TextView postKey;
        public TextView time;
        public RelativeLayout btn_accept;
        public RelativeLayout btn_decline;

        public ViewHolder(View convertView) {
            super(convertView);
            notification = (LinearLayout)convertView.findViewById(R.id.notification_field);
            typeImg = (ImageView) convertView.findViewById(R.id.notification_type_image);
            postKey = (TextView) convertView.findViewById(R.id.notification_post_key);
            time = (TextView)convertView.findViewById(R.id.notification_time);
            btn_accept = (RelativeLayout)convertView.findViewById(R.id.notification_item_btn_accept);
            btn_decline = (RelativeLayout)convertView.findViewById(R.id.notification_item_btn_decline);
        }
    }
}