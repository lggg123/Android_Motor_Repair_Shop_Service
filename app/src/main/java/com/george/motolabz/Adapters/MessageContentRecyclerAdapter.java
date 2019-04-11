package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.Message;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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

public class MessageContentRecyclerAdapter extends RecyclerView.Adapter<MessageContentRecyclerAdapter.ViewHolder>{

    public List<Message> messageList = new ArrayList<>();
    public String avatarUrl = "";
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public MessageContentRecyclerAdapter(ArrayList<Message> messageList, String avatarUrl){
        super();
        this.messageList = messageList;
        this.avatarUrl = avatarUrl;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_message_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Message messageItem = messageList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        if(messageItem.userID.equals(myId)){
            viewHolder.myMessage.setVisibility(View.VISIBLE);
            viewHolder.oppMessage.setVisibility(View.GONE);
            viewHolder.myMessageContent.setText(messageItem.text);
            viewHolder.myMessageTime.setText(Utils.converteTimestamp(messageItem.time));
        }else {
            viewHolder.myMessage.setVisibility(View.GONE);
            viewHolder.oppMessage.setVisibility(View.VISIBLE);
            viewHolder.oppMessageContent.setText(messageItem.text);
            viewHolder.oppMessageTime.setText(Utils.converteTimestamp(messageItem.time));
            if(!avatarUrl.isEmpty()){
                Glide.with(viewHolder.oppAvatarImg.getContext()).load(avatarUrl).into(viewHolder.oppAvatarImg);
            }

        }
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
        return messageList.size();
    }

    public void setData(ArrayList<Message> data) {
        messageList.clear();
        messageList.addAll(data);
    }

    public void clear() {
        messageList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout myMessage;
        public LinearLayout oppMessage;
        public CircleImageView oppAvatarImg;
        public TextView myMessageContent;
        public TextView oppMessageContent;
        public TextView myMessageTime;
        public TextView oppMessageTime;

        public ViewHolder(View convertView) {
            super(convertView);
            myMessage = (LinearLayout)convertView.findViewById(R.id.message_my_post);
            oppMessage = (LinearLayout)convertView.findViewById(R.id.message_opp_post);
            oppAvatarImg = (CircleImageView) convertView.findViewById(R.id.message_opp_avatar);
            myMessageContent = (TextView) convertView.findViewById(R.id.message_my_message);
            oppMessageContent = (TextView)convertView.findViewById(R.id.message_opp_message);
            myMessageTime = (TextView)convertView.findViewById(R.id.message_my_message_time);
            oppMessageTime = (TextView) convertView.findViewById(R.id.message_opp_message_time);
        }
    }
}
