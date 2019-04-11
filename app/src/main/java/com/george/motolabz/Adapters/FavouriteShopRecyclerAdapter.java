package com.brainyapps.motolabz.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HappyBear on 8/26/2018.
 */

public class FavouriteShopRecyclerAdapter extends RecyclerView.Adapter<FavouriteShopRecyclerAdapter.ViewHolder>{

    public List<RepairShop> shopList = new ArrayList<>();
    private final int cellWidth;
    private Context mContext;
    private Boolean isLongClicked = false;

    public FavouriteShopRecyclerAdapter(Context context, ArrayList<RepairShop> shopList){
        super();
        mContext = context;
        cellWidth = Utils.getScreenWidth(mContext)/2;
        this.shopList = shopList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rightItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_favourite_item, parent, false);

        return new ViewHolder(rightItemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RepairShop shop = shopList.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        Glide.with(viewHolder.shopImg.getContext()).load(shop.photoUrl).into(viewHolder.shopImg);
        if(shop.fullName.length()>25){
            viewHolder.shopName.setText(shop.fullName.substring(0,25)+"...");
        }else {
            viewHolder.shopName.setText(shop.fullName);
        }

        viewHolder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLongClicked){
                    viewHolder.btnRemove.setVisibility(View.GONE);
                    isLongClicked = false;
                }else {
                    if (mListener != null) {
                        mListener.clickFavouriteItem(position,shop.userID);
                    }
                }
            }
        });

        viewHolder.favourite.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mListener != null) {
                    mListener.longClickItem(position,shop.userID);
                }
//                viewHolder.btnRemove.setVisibility(View.VISIBLE);
//                isLongClicked = true;
                return true;
            }
        });

        viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.btnRemove.setVisibility(View.GONE);
                isLongClicked = false;
                if (mListener != null) {
                    mListener.removeFavouriteItem(position,shop.userID);
                }
            }
        });
    }

    private OnClickItemListener mListener;

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.mListener = listener;
    }

    public interface OnClickItemListener {
        void clickFavouriteItem(int index, String shopId);
        void longClickItem(int index, String shopId);
        void removeFavouriteItem(int index, String shopId);
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

        public LinearLayout favourite;
        public ImageView shopImg;
        public TextView shopName;
        public TextView btnRemove;

        public ViewHolder(View convertView) {
            super(convertView);
            favourite = (LinearLayout) convertView.findViewById(R.id.driver_favor);
            shopImg = (ImageView) convertView.findViewById(R.id.driver_favourite_image);
            shopName = (TextView) convertView.findViewById(R.id.driver_favourite_shop_title);
            btnRemove = (TextView)convertView.findViewById(R.id.driver_favourite_btn_remove);
        }
    }
}
