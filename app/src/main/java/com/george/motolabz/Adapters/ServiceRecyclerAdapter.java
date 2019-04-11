package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Models.ServiceListItem;
import com.brainyapps.motolabz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HappyBear on 8/22/2018.
 */

public class ServiceRecyclerAdapter extends RecyclerView.Adapter<ServiceRecyclerAdapter.ViewHolder>{

    public List<ServiceListItem> serviceList = new ArrayList<>();
    public ServiceRecyclerAdapter(ArrayList<ServiceListItem> serviceList){
        super();
        this.serviceList = serviceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_service_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ServiceListItem serviceItem = serviceList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.service_name.setText(serviceItem.service_name);
        if(!serviceItem.service_rate.isEmpty()){
            viewHolder.service_rate.setText("$" + serviceItem.service_rate);
        }else {
            viewHolder.service_rate.setText("");
        }

        viewHolder.service_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickServiceItem(position, serviceItem.service_name, serviceItem.service_rate);
                }
            }
        });
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
        public TextView service_name;
        public TextView service_rate;

        public ViewHolder(View convertView) {
            super(convertView);
            service_line = (RelativeLayout) convertView.findViewById(R.id.list_service_line);
            service_name = (TextView) convertView.findViewById(R.id.list_service_name);
            service_rate = (TextView) convertView.findViewById(R.id.list_service_rate);
        }
    }
}