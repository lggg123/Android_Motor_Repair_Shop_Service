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
 * Created by HappyBear on 8/24/2018.
 */

public class VehicleModelRecyclerAdapter extends RecyclerView.Adapter<VehicleModelRecyclerAdapter.ViewHolder>{
    public List<String> modelList = new ArrayList<>();
    public VehicleModelRecyclerAdapter(ArrayList<String> modelList){
        super();
        this.modelList = modelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_model_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String modelName = modelList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.model_name.setText(modelName);

        viewHolder.model_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickModelItem(position, modelName);
                }
            }
        });
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickModelItem(int index, String model_name);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void setData(ArrayList<String> data) {
        modelList.clear();
        modelList.addAll(data);
    }

    public void clear() {
        modelList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView model_name;

        public ViewHolder(View convertView) {
            super(convertView);
            model_name = (TextView) convertView.findViewById(R.id.vehicle_model_name);
        }
    }
}
