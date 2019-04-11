package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brainyapps.motolabz.Models.VehicleInfo;
import com.brainyapps.motolabz.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HappyBear on 1/6/2019.
 */

public class DriverVehicleDetailRecyclerAdapter extends RecyclerView.Adapter<DriverVehicleDetailRecyclerAdapter.ViewHolder>{
    public List<VehicleInfo> vehicleList = new ArrayList<>();
    public DriverVehicleDetailRecyclerAdapter(ArrayList<VehicleInfo> vehicleList){
        super();
        this.vehicleList = vehicleList;
    }

    @Override
    public DriverVehicleDetailRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_driver_vehicle_item, parent, false);
        return new DriverVehicleDetailRecyclerAdapter.ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(DriverVehicleDetailRecyclerAdapter.ViewHolder holder, final int position) {
        final VehicleInfo info = vehicleList.get(position);
        final DriverVehicleDetailRecyclerAdapter.ViewHolder viewHolder = (DriverVehicleDetailRecyclerAdapter.ViewHolder) holder;

        if(info.vehicleImageUrl.isEmpty()){
            viewHolder.vehicle_img.setVisibility(View.GONE);
        }else {
            viewHolder.vehicle_img.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.vehicle_img.getContext()).load(info.vehicleImageUrl).into(viewHolder.vehicle_img);

        }
        viewHolder.vehicle_info.setText("Model: "+info.model+"\n"+"Engine: "+info.engine+"\n"+"Year: "+info.year+"\n"+"Manufacturer: "+info.manufacturer+"\n"+"Transmission: "+info.transmission+"\n"+"VIN: "+info.vin);
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public void setData(ArrayList<VehicleInfo> data) {
        vehicleList.clear();
        vehicleList.addAll(data);
    }

    public void clear() {
        vehicleList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView vehicle_img;
        public TextView vehicle_info;

        public ViewHolder(View convertView) {
            super(convertView);
            vehicle_img = (ImageView) convertView.findViewById(R.id.shop_driver_vehicle_info_image);
            vehicle_info = (TextView)convertView.findViewById(R.id.shop_driver_vehicle_info_text);
        }
    }
}