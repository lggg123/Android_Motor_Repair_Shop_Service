package com.brainyapps.motolabz.Adapters;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HappyBear on 8/26/2018.
 */

public class ShopListRecyclerAdapter extends RecyclerView.Adapter<ShopListRecyclerAdapter.ViewHolder>{

    public String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public List<RepairShop> shopList = new ArrayList<>();
    Location currnetLocation;
    public ShopListRecyclerAdapter(ArrayList<RepairShop> shopList, Location location){
        super();
        this.shopList = shopList;
        this.currnetLocation = location;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_shop_item, parent, false);
        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final RepairShop shopItem = shopList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.shopName.setText(shopItem.fullName);
        viewHolder.shopDescription.setText(shopItem.description);
        if(!shopItem.photoUrl.isEmpty()){
            Glide.with(viewHolder.showImg.getContext()).load(shopItem.photoUrl).into(viewHolder.showImg);
        }
        Location shopLocation =  new Location("shop location");
        shopLocation.setLatitude(shopItem.latitude);
        shopLocation.setLongitude(shopItem.longitude);
        viewHolder.shopDistance.setText(Utils.getDistance(currnetLocation, shopLocation)+" mi");
        viewHolder.shop_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.clickShop(position, shopItem.userID);
                }
            }
        });
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickShop(int index, String key);
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public void setData(ArrayList<RepairShop> data) {
        shopList.clear();
        shopList.addAll(data);
    }

    public void clear() {
        shopList.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout shop_list;
        public ImageView showImg;
        public TextView shopName;
        public TextView shopDescription;
        public TextView shopDistance;

        public ViewHolder(View convertView) {
            super(convertView);
            shop_list = (RelativeLayout) convertView.findViewById(R.id.driver_list_shop);
            showImg = (ImageView) convertView.findViewById(R.id.driver_list_shop_image);
            shopName = (TextView) convertView.findViewById(R.id.driver_list_shop_name);
            shopDescription = (TextView) convertView.findViewById(R.id.driver_list_shop_description);
            shopDistance = (TextView) convertView.findViewById(R.id.driver_list_shop_distance);
        }
    }
}
