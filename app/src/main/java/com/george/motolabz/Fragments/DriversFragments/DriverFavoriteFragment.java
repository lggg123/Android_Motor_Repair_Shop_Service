package com.brainyapps.motolabz.Fragments.DriversFragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brainyapps.motolabz.Adapters.FavouriteShopRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.DriversView.DriverRepairShopServiceActivity;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.brainyapps.motolabz.Views.AlertFactoryClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DriverFavoriteFragment extends Fragment implements View.OnClickListener, FavouriteShopRecyclerAdapter.OnClickItemListener{
    private RecyclerView favouriteRecyclerView;
    private FavouriteShopRecyclerAdapter favouriteShopRecyclerAdapter;
    private ArrayList<RepairShop> shopList = new ArrayList<>();
    private ArrayList<String> favouriteList = new ArrayList<>();
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Driver myInfo;

    public static final String FRAGMENT_TAG = "com_motolabz_driver_favorite_fragment_tag";
    private static Context mContext;

    private ProgressHUD mProgressDialog;

    private void showProgressHUD(String text) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = ProgressHUD.show(getActivity(), text, true);
        mProgressDialog.show();
    }

    private void hideProgressHUD() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
    public static android.app.Fragment newInstance(Context context) {
        mContext = context;

        android.app.Fragment f = new DriverFavoriteFragment();
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_driver_favorite, container, false);
        favouriteShopRecyclerAdapter = new FavouriteShopRecyclerAdapter(getActivity(),shopList);
        favouriteRecyclerView = (RecyclerView) rootView.findViewById(R.id.driver_favourite_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        favouriteRecyclerView.setLayoutManager(mLayoutManager);
//        favouriteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        favouriteRecyclerView.setAdapter(favouriteShopRecyclerAdapter);
        favouriteShopRecyclerAdapter.setOnClickItemListener(DriverFavoriteFragment.this);
        showProgressHUD("");
        Query myQuery = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("likes");
        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot shop : dataSnapshot.getChildren()){
                        favouriteList.add(shop.getKey().toString());
                    }
                    refreshList(favouriteList);
                }else {
                    hideProgressHUD();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
        return rootView;
    }

    public void refreshList(final ArrayList<String> list){
        Query shopInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER);
        shopInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    shopList.clear();
                    for (DataSnapshot userItem : dataSnapshot.getChildren()){
                        if(list.contains(userItem.getKey().toString())){
                            RepairShop shop = userItem.getValue(RepairShop.class);
                            shopList.add(shop);
                        }
                    }
                    favouriteShopRecyclerAdapter.notifyDataSetChanged();
                }
                hideProgressHUD();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            default:
                break;
        }
    }

    @Override
    public void clickFavouriteItem(int index, String shopId) {
        Intent shop_intent = new Intent(getActivity(), DriverRepairShopServiceActivity.class);
        shop_intent.putExtra("shopId", shopId);
        startActivity(shop_intent);
    }

    @Override
    public void longClickItem(final int index, final String shopId) {
        AlertFactory.showAlert(getActivity(), "Remove Shop", "Do you want remove this shop?", "YES", "NO", new AlertFactoryClickListener() {
            @Override
            public void onClickYes(final AlertDialog dialog) {
                FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("likes").child(shopId).removeValue();
                shopList.remove(index);
                favouriteShopRecyclerAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            @Override
            public void onClickNo(AlertDialog dialog) {
                dialog.dismiss();
            }
            @Override
            public void onClickDone(AlertDialog dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void removeFavouriteItem(int index, String shopId) {

    }
}
