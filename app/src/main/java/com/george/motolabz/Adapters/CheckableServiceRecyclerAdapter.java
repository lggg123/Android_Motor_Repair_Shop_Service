package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Models.ServiceListItem;
import com.brainyapps.motolabz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HappyBear on 8/31/2018.
 */

public class CheckableServiceRecyclerAdapter extends RecyclerView.Adapter<CheckableServiceRecyclerAdapter.ViewHolder>{

    public List<ServiceListItem> serviceList = new ArrayList<>();
    public ArrayList<String> selectedService = new ArrayList<>();
    public CheckableServiceRecyclerAdapter(ArrayList<ServiceListItem> serviceList){
        super();
        this.serviceList = serviceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_checkable_service_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ServiceListItem serviceItem = serviceList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        holder.service_name.setOnCheckedChangeListener(null);
        holder.service_name.setSelected(serviceItem.isSelected());
        viewHolder.service_name.setText(serviceItem.service_name);
        if(!serviceItem.service_rate.isEmpty()){
            viewHolder.service_rate.setText("$" + serviceItem.service_rate);
        }else {
            viewHolder.service_rate.setText("");
        }
        if(serviceItem.isSelected()){
            viewHolder.service_name.setChecked(true);
        }else {
            viewHolder.service_name.setChecked(false);
        }

        viewHolder.service_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                holder.service_name.setSelected(isChecked);
                if(isChecked){
                    selectedService.add(serviceItem.service_name);
                    serviceItem.setSelected(true);
                    viewHolder.service_name.setChecked(serviceItem.isSelected());
                }else {
                    selectedService.remove(serviceItem.service_name);
                    serviceItem.setSelected(false);
                    viewHolder.service_name.setChecked(serviceItem.isSelected());
                }
            }
        });

        viewHolder.service_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickServiceItem(position, serviceItem.service_name, serviceItem.service_rate);
                }
            }
        });
    }

    public ArrayList<String> getSelectedService(){
        return selectedService;
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickServiceItem(int index, String service_name, String service_rate);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public void setData(ArrayList<ServiceListItem> data) {
        serviceList.clear();
        serviceList.addAll(data);
    }

    public void clear() {
        serviceList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout service_line;
        public CheckBox service_name;
        public TextView service_rate;

        public ViewHolder(View convertView) {
            super(convertView);
            service_line = (RelativeLayout) convertView.findViewById(R.id.driver_checkable_service);
            service_name = (CheckBox) convertView.findViewById(R.id.driver_checkable_service_name);
            service_rate = (TextView) convertView.findViewById(R.id.driver_checkable_service_rate);
        }
    }
}