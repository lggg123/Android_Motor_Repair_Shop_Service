package com.brainyapps.motolabz.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.PostCommunity;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HappyBear on 8/24/2018.
 */

public class CommunityPostRecyclerAdapter extends RecyclerView.Adapter<CommunityPostRecyclerAdapter.ViewHolder>{

    public String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public List<PostCommunity> postList = new ArrayList<>();
    public CommunityPostRecyclerAdapter(ArrayList<PostCommunity> postList){
        super();
        this.postList = postList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_community_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        boolean isLike = false;
        boolean isDislike = false;

        final PostCommunity postItem = postList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;
        int likeCount = postItem.likeCount();
        int dislikeCount = postItem.unlikeCount();
        viewHolder.postDescription.setText(postItem.description);
        viewHolder.likeCount.setText(String.valueOf(postItem.likeCount()));
        viewHolder.unlikeCount.setText(String.valueOf(postItem.unlikeCount()));
        viewHolder.commentCount.setText(String.valueOf(postItem.commentCount())+" comments");
        viewHolder.postTime.setText(Utils.converteTimestamp(postItem.time));
        if(!postItem.image.isEmpty()){
            Glide.with(viewHolder.postImage.getContext()).load(postItem.image).into(viewHolder.postImage);
        }
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickComment(position, postItem.key);
                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(postItem.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewHolder.posterName.setText(dataSnapshot.child("fullName").getValue().toString());
                if(!dataSnapshot.child("photoUrl").getValue().toString().isEmpty()){
                    Glide.with(viewHolder.postImage.getContext()).load(dataSnapshot.child("photoUrl").getValue().toString()).into(viewHolder.avatarImg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        isLike = postItem.likes.containsKey(myId);
        if(isLike){
            viewHolder.icoLike.setImageResource(R.drawable.ic_like);
        }else {
            viewHolder.icoLike.setImageResource(R.drawable.ic_like_gray);
        }
        isDislike = postItem.dislikes.containsKey(myId);
        if(isDislike){
            viewHolder.icoUnlike.setImageResource(R.drawable.ic_unlike);
        }else {
            viewHolder.icoUnlike.setImageResource(R.drawable.ic_unlike_gray);
        }
        viewHolder.icoLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        viewHolder.icoUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(isDislike){
//                    isDislike = false;
//                    viewHolder.icoUnlike.setImageResource(R.drawable.ic_unlike_gray);
//                    FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(postItem.category).child(postItem.key).child("dislikes").child(myId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            viewHolder.unlikeCount.setText(String.valueOf(postItem.unlikeCount()-1));
//                        }
//                    });
//                }else {
//                    isDislike = true;
//                    viewHolder.icoUnlike.setImageResource(R.drawable.ic_unlike);
//                    FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(postItem.category).child(postItem.key).child("dislikes").child(myId).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            viewHolder.unlikeCount.setText(String.valueOf(postItem.unlikeCount()+1));
//                        }
//                    });
//                    if(isLike){
//                        viewHolder.icoLike.setImageResource(R.drawable.ic_like_gray);
//                        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_POST).child(postItem.category).child(postItem.key).child("likes").child(myId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                viewHolder.likeCount.setText(String.valueOf(postItem.likeCount()-1));
//                            }
//                        });
//                    }
//                }
            }
        });
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickComment(int index, String key);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void setData(ArrayList<PostCommunity> data) {
        postList.clear();
        postList.addAll(data);
    }

    public void clear() {
        postList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView avatarImg;
        public TextView posterName;
        public TextView postTime;
        public TextView postDescription;
        public ImageView postImage;
        public ImageView icoLike;
        public TextView likeCount;
        public ImageView icoUnlike;
        public TextView unlikeCount;

        public LinearLayout comment;
        public TextView commentCount;

        public ViewHolder(View convertView) {
            super(convertView);
            avatarImg = (CircleImageView) convertView.findViewById(R.id.driver_community_post_avatar_img);
            posterName = (TextView) convertView.findViewById(R.id.driver_community_post_name);
            postTime = (TextView) convertView.findViewById(R.id.driver_community_post_time);
            postDescription = (TextView) convertView.findViewById(R.id.driver_community_post_description);
            postImage = (ImageView)convertView.findViewById(R.id.driver_community_post_image);
            icoLike = (ImageView)convertView.findViewById(R.id.driver_community_post_ic_like);
            likeCount = (TextView)convertView.findViewById(R.id.driver_community_post_like_count);
            icoUnlike = (ImageView)convertView.findViewById(R.id.driver_community_post_ic_unlike);
            unlikeCount = (TextView)convertView.findViewById(R.id.driver_community_post_unlike_count);
            comment = (LinearLayout)convertView.findViewById(R.id.driver_post_community_on_add_comment);
            commentCount = (TextView)convertView.findViewById(R.id.driver_community_post_comment_count);
        }
    }
}
