package com.brainyapps.motolabz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brainyapps.motolabz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HappyBear on 12/13/2018.
 */

public class ShopServiceRecyclerAdapter extends RecyclerView.Adapter<ShopServiceRecyclerAdapter.ViewHolder>{
    public List<String> serviceList = new ArrayList<>();
    public ShopServiceRecyclerAdapter(ArrayList<String> serviceList){
        super();
        this.serviceList = serviceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_shop_service_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String serviceName = serviceList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.service_name.setText(serviceName);

        viewHolder.service_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickServiceItem(position, serviceName);
                }
            }
        });
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickServiceItem(int index, String service_name);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public void setData(ArrayList<String> data) {
        serviceList.clear();
        serviceList.addAll(data);
    }

    public void clear() {
        serviceList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView service_name;

        public ViewHolder(View convertView) {
            super(convertView);
            service_name = (TextView) convertView.findViewById(R.id.shop_service_name);
        }
    }
}
