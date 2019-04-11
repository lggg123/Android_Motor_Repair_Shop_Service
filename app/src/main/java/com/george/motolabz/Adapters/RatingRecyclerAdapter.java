package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.RateReview;
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
 * Created by HappyBear on 8/31/2018.
 */

public class RatingRecyclerAdapter extends RecyclerView.Adapter<RatingRecyclerAdapter.ViewHolder>{

    public List<RateReview> rateList = new ArrayList<>();
    private String userType = FirebaseManager.getInstance().getUserType();
    public RatingRecyclerAdapter(ArrayList<RateReview> rateList){
        super();
        this.rateList = rateList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_rating_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RateReview rateItem = rateList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;
        if(!userType.equals("mechanic")){
            viewHolder.reply.setVisibility(View.GONE);
        }else {
//            viewHolder.reply.setVisibility(View.VISIBLE);
        }
        viewHolder.rate_time.setText(Utils.converteTimestamp(rateItem.time));
        viewHolder.rate_review.setText(rateItem.rateContent);
        viewHolder.rate_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickRateItem(position, rateItem.userID);
                }
            }
        });
        viewHolder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickReplyRating(position, rateItem.userID);
                }
            }
        });
        switch (rateItem.rate){
            case 1:
                viewHolder.star1.setImageResource(R.drawable.ic_star_on);
                viewHolder.star2.setImageResource(R.drawable.ic_star_off);
                viewHolder.star3.setImageResource(R.drawable.ic_star_off);
                viewHolder.star4.setImageResource(R.drawable.ic_star_off);
                viewHolder.star5.setImageResource(R.drawable.ic_star_off);
                break;
            case 2:
                viewHolder.star1.setImageResource(R.drawable.ic_star_on);
                viewHolder.star2.setImageResource(R.drawable.ic_star_on);
                viewHolder.star3.setImageResource(R.drawable.ic_star_off);
                viewHolder.star4.setImageResource(R.drawable.ic_star_off);
                viewHolder.star5.setImageResource(R.drawable.ic_star_off);
                break;
            case 3:
                viewHolder.star1.setImageResource(R.drawable.ic_star_on);
                viewHolder.star2.setImageResource(R.drawable.ic_star_on);
                viewHolder.star3.setImageResource(R.drawable.ic_star_on);
                viewHolder.star4.setImageResource(R.drawable.ic_star_off);
                viewHolder.star5.setImageResource(R.drawable.ic_star_off);
                break;
            case 4:
                viewHolder.star1.setImageResource(R.drawable.ic_star_on);
                viewHolder.star2.setImageResource(R.drawable.ic_star_on);
                viewHolder.star3.setImageResource(R.drawable.ic_star_on);
                viewHolder.star4.setImageResource(R.drawable.ic_star_on);
                viewHolder.star5.setImageResource(R.drawable.ic_star_off);
                break;
            case 5:
                viewHolder.star1.setImageResource(R.drawable.ic_star_on);
                viewHolder.star2.setImageResource(R.drawable.ic_star_on);
                viewHolder.star3.setImageResource(R.drawable.ic_star_on);
                viewHolder.star4.setImageResource(R.drawable.ic_star_on);
                viewHolder.star5.setImageResource(R.drawable.ic_star_on);
                break;
            default:
                break;
        }

        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(rateItem.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    viewHolder.user_name.setText(dataSnapshot.child("fullName").getValue().toString());
                    if(!dataSnapshot.child("photoUrl").getValue().toString().isEmpty()){
                        Glide.with(viewHolder.userAvatar.getContext()).load(dataSnapshot.child("photoUrl").getValue().toString()).into(viewHolder.userAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickRateItem(int index, String userId);
        void clickReplyRating(int index, String userId);
    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    public void setData(ArrayList<RateReview> data) {
        rateList.clear();
        rateList.addAll(data);
    }

    public void clear() {
        rateList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout rate_line;
        public TextView user_name;
        public CircleImageView userAvatar;
        public TextView rate_time;
        public TextView rate_review;
        public ImageView star1;
        public ImageView star2;
        public ImageView star3;
        public ImageView star4;
        public ImageView star5;
        public TextView reply;

        public ViewHolder(View convertView) {
            super(convertView);
            rate_line = (LinearLayout) convertView.findViewById(R.id.driver_rate_item);
            user_name = (TextView) convertView.findViewById(R.id.driver_rating_name);
            rate_time = (TextView) convertView.findViewById(R.id.driver_rating_time);
            userAvatar = (CircleImageView)convertView.findViewById(R.id.driver_rating_avatar);
            rate_review = (TextView)convertView.findViewById(R.id.driver_rating_review);

            star1 = (ImageView)convertView.findViewById(R.id.driver_rating_star1);
            star2 = (ImageView)convertView.findViewById(R.id.driver_rating_star2);
            star3 = (ImageView)convertView.findViewById(R.id.driver_rating_star3);
            star4 = (ImageView)convertView.findViewById(R.id.driver_rating_star4);
            star5 = (ImageView)convertView.findViewById(R.id.driver_rating_star5);
            reply = (TextView)convertView.findViewById(R.id.mechanic_rating_reply);
            reply.setVisibility(View.GONE);
        }
    }
}