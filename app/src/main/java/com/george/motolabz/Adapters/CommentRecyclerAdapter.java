package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.Comment;
import com.brainyapps.motolabz.Models.Driver;
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
 * Created by HappyBear on 8/25/2018.
 */

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>{

    public List<Comment> commentList = new ArrayList<>();
    public CommentRecyclerAdapter(ArrayList<Comment> commentList){
        super();
        this.commentList = commentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_comment_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Comment commentItem = commentList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.commentTime.setText(Utils.converteTimestamp(commentItem.time));
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(commentItem.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewHolder.commentContent.setText(dataSnapshot.child("fullName").getValue().toString()+": "+commentItem.text);
                if(!dataSnapshot.child("photoUrl").getValue().toString().isEmpty()){
                    Glide.with(viewHolder.avatarImg.getContext()).load(dataSnapshot.child("photoUrl").getValue().toString()).into(viewHolder.avatarImg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        viewHolder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickReply(position, commentItem.userID, commentItem.commentID);
                }
            }
        });
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickReply(int index, String userId, String commentId);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void setData(ArrayList<Comment> data) {
        commentList.clear();
        commentList.addAll(data);
    }

    public void clear() {
        commentList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView avatarImg;
        public TextView commentContent;
        private TextView commentTime;
        private TextView replyButton;

        public ViewHolder(View convertView) {
            super(convertView);
            avatarImg = (CircleImageView) convertView.findViewById(R.id.driver_comment_avatar);
            commentContent = (TextView) convertView.findViewById(R.id.driver_comment_content);
            commentTime = (TextView)convertView.findViewById(R.id.driver_comment_time);
            replyButton = (TextView)convertView.findViewById(R.id.driver_comment_reply_button);
        }
    }
}
