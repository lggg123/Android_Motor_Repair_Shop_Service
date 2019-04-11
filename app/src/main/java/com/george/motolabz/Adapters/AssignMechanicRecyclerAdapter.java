package com.brainyapps.motolabz.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HappyBear on 9/4/2018.
 */

public class AssignMechanicRecyclerAdapter extends RecyclerView.Adapter<AssignMechanicRecyclerAdapter.ViewHolder>{

    public List<Mechanic> mechanicList = new ArrayList<>();
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int previousPosition = -1;

    public AssignMechanicRecyclerAdapter(ArrayList<Mechanic> mechanicList){
        super();
        this.mechanicList = mechanicList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        for(int i = 0; i<mechanicList.size();i++){
            selectedItems.put(i, false);
        }
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
        viewHolder.mechanicName.setText(mechanic.fullName);
        if(selectedItems.get(position)){
            viewHolder.mechanicItem.setBackgroundColor(Color.GRAY);
        }else {
            viewHolder.mechanicItem.setBackgroundColor(Color.WHITE);
        }
//        if(mechanic.fullName.length()>25){
//            viewHolder.mechanicName.setText(mechanic.fullName.substring(0,25)+"...");
//        }else {
//            viewHolder.mechanicName.setText(mechanic.fullName);
//        }

        viewHolder.mechanicItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i<selectedItems.size();i++){
                    selectedItems.put(i, false);
                }
                if (mListener != null) {
                    selectedItems.put(position, true);
                    notifyDataSetChanged();
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
