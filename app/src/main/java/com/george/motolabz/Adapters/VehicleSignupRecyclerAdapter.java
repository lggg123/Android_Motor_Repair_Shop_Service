package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brainyapps.motolabz.Models.SignupVehicle;
import com.brainyapps.motolabz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HappyBear on 1/5/2019.
 */

public class VehicleSignupRecyclerAdapter extends RecyclerView.Adapter<VehicleSignupRecyclerAdapter.ViewHolder> {
    public List<SignupVehicle> vehicleList = new ArrayList<>();

    public VehicleSignupRecyclerAdapter(ArrayList<SignupVehicle> vehicleList) {
        super();
        this.vehicleList = vehicleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_vehicle_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SignupVehicle vehicleInfo = vehicleList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.model_name.setText(vehicleInfo.model);

        viewHolder.model_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickModelItem(position, vehicleInfo.key);
                }
            }
        });
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickModelItem(int index, String vehicle_key);
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public void setData(ArrayList<SignupVehicle> data) {
        vehicleList.clear();
        vehicleList.addAll(data);
    }

    public void clear() {
        vehicleList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView model_name;

        public ViewHolder(View convertView) {
            super(convertView);
            model_name = (TextView) convertView.findViewById(R.id.vehicle_info);
        }
    }
}