package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HappyBear on 9/3/2018.
 */

public class MechanicRecyclerAdapter extends RecyclerView.Adapter<MechanicRecyclerAdapter.ViewHolder>{

    public List<Mechanic> mechanicList = new ArrayList<>();

    public MechanicRecyclerAdapter(ArrayList<Mechanic> mechanicList){
        super();
        this.mechanicList = mechanicList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_mechanic_item, parent, false);

        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Mechanic mechanic = mechanicList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;
        if(!mechanic.photoUrl.isEmpty()){
            Glide.with(viewHolder.mechanicAvatar.getContext()).load(mechanic.photoUrl).into(viewHolder.mechanicAvatar);
        }
        if(mechanic.fullName.length()>25){
            viewHolder.mechanicName.setText(mechanic.fullName.substring(0,25)+"...");
        }else {
            viewHolder.mechanicName.setText(mechanic.fullName);
        }

        viewHolder.mechanicItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickMechanicItem(position,mechanic.userID);
                }
            }
        });
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickMechanicItem(int index, String mechanicId);
    }

    @Override
    public int getItemCount() {
        return mechanicList.size();
    }

    public void setData(ArrayList<Mechanic> data) {
        mechanicList.clear();
        mechanicList.addAll(data);
    }

    public void clear() {
        mechanicList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mechanicItem;
        public CircleImageView mechanicAvatar;
        public TextView mechanicName;

        public ViewHolder(View convertView) {
            super(convertView);
            mechanicItem = (LinearLayout) convertView.findViewById(R.id.shop_mechanic_item);
            mechanicAvatar = (CircleImageView) convertView.findViewById(R.id.shop_mechanic_avatar_image);
            mechanicName = (TextView) convertView.findViewById(R.id.shop_mechanic_name);
        }
    }
}
