package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.Channel;
import com.brainyapps.motolabz.R;
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
 * Created by HappyBear on 8/30/2018.
 */

public class MessageChannelRecyclerAdapter extends RecyclerView.Adapter<MessageChannelRecyclerAdapter.ViewHolder>{

    public List<Channel> channelList = new ArrayList<>();
    public MessageChannelRecyclerAdapter(ArrayList<Channel> channelList){
        super();
        this.channelList = channelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_contact_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Channel channelItem = channelList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.last_mesasge_time.setText(Utils.converteTimestamp(channelItem.time));
        if(channelItem.isRead){
            viewHolder.unread_message_exist.setVisibility(View.GONE);
        }else {
            viewHolder.unread_message_exist.setVisibility(View.VISIBLE);
        }
        if(channelItem.lastMsg.length() > 35){
            viewHolder.last_message.setText(channelItem.lastMsg.substring(0,35) + "...");
        }else {
            viewHolder.last_message.setText(channelItem.lastMsg);
        }

        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(channelItem.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("fullName").getValue().toString().length() > 20){
                        viewHolder.user_name.setText(dataSnapshot.child("fullName").getValue().toString().substring(0,20)+"...");
                    }else {
                        viewHolder.user_name.setText(dataSnapshot.child("fullName").getValue().toString());
                    }
                    if(!dataSnapshot.child("photoUrl").getValue().toString().isEmpty()){
                        Glide.with(viewHolder.avatarImg.getContext()).load(dataSnapshot.child("photoUrl").getValue().toString()).into(viewHolder.avatarImg);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        viewHolder.channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickChannel(position, channelItem.userID);
                }
            }
        });
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickChannel(int index, String userId);
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public void setData(ArrayList<Channel> data) {
        channelList.clear();
        channelList.addAll(data);
    }

    public void clear() {
        channelList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout channel;
        public CircleImageView avatarImg;
        public TextView user_name;
        public TextView last_message;
        public TextView last_mesasge_time;
        public ImageView unread_message_exist;

        public ViewHolder(View convertView) {
            super(convertView);
            channel = (LinearLayout)convertView.findViewById(R.id.message_channel_item);
            avatarImg = (CircleImageView) convertView.findViewById(R.id.message_channel_avatar);
            user_name = (TextView) convertView.findViewById(R.id.message_channel_user_name);
            last_message = (TextView)convertView.findViewById(R.id.message_channel_last_message_content);
            last_mesasge_time = (TextView)convertView.findViewById(R.id.message_channel_last_message_time);
            unread_message_exist = (ImageView)convertView.findViewById(R.id.message_channel_unread_message_img);
        }
    }
}

