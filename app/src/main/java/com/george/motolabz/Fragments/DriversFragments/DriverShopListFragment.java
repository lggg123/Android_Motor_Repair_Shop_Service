package com.brainyapps.motolabz.Fragments.DriversFragments;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.ShopListRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.DriversView.DriverFilterSearchActivity;
import com.brainyapps.motolabz.DriversView.DriverRepairShopServiceActivity;
import com.brainyapps.motolabz.Models.Driver;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.PrefUtils;
import com.brainyapps.motolabz.Utils.Utils;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DriverShopListFragment extends Fragment implements View.OnClickListener, ShopListRecyclerAdapter.OnClickItemListener, LocationListener{

    private CircleImageView avatarImg;
    private EditText searchText;
    private ImageView filter;
    private LinearLayout distance;
    private TextView distanceText;
    private int selected_distance = 10;

    private ArrayList<RepairShop> shopList = new ArrayList<>();
    private RecyclerView shopRecyclerView;
    private ShopListRecyclerAdapter shopRecyclerAdapter;

    private Location currentMyLocation;

    public static final String FRAGMENT_TAG = "com_motolabz_driver_shop_list_fragment_tag";
    private static Context mContext;
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Driver myInfo;

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

        android.app.Fragment f = new DriverShopListFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_driver_shop_list, container, false);
        initLocation();
        showProgressHUD("");
        avatarImg = (CircleImageView)rootView.findViewById(R.id.driver_main_list_avatar);
        searchText = (EditText)rootView.findViewById(R.id.driver_main_list_search_text);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    refreshList();
                    return true;
                } else {
                    return false;
                }
            }
        });
        filter = (ImageView)rootView.findViewById(R.id.driver_main_filter);
        filter.setOnClickListener(this);
        distance = (LinearLayout)rootView.findViewById(R.id.driver_main_list_distance_field);
        distance.setOnClickListener(this);
        distanceText = (TextView)rootView.findViewById(R.id.driver_main_list_distance_content);
        shopRecyclerView = (RecyclerView) rootView.findViewById(R.id.driver_shop_list_recycler_view);
        distanceText.setText("10 mi.");

        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    myInfo = dataSnapshot.getValue(Driver.class);
                    if(!myInfo.photoUrl.isEmpty()){
                        Glide.with(getActivity()).load(myInfo.photoUrl).into(avatarImg);
                    }
                    if(currentMyLocation == null){
                        currentMyLocation = new Location("currentMyLocation");
                        currentMyLocation.setLatitude(myInfo.latitude);
                        currentMyLocation.setLongitude(myInfo.longitude);
                    }
                    shopRecyclerAdapter = new ShopListRecyclerAdapter(shopList, currentMyLocation);
                    shopRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    shopRecyclerView.setAdapter(shopRecyclerAdapter);
                    shopRecyclerAdapter.setOnClickItemListener(DriverShopListFragment.this);
                    refreshList();
                }
                hideProgressHUD();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressHUD();
            }
        });
        return  rootView;
    }

    public void initLocation() {

        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Utils.checkLocationPermission(getActivity());
            return;
        }
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this,null);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
    }

    public boolean checkGpsService() {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if (!gps_enabled && !network_enabled) {
            AlertFactory.showAlert(mContext, "", "Check your Location service");
            return false;
        }else {
            return true;
        }
    }

    public void refreshList(){
//        showProgressHUD("");
        Query shopInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER);
        shopInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    shopList.clear();
                    for (DataSnapshot userItem : dataSnapshot.getChildren()){
                        if(userItem.child("userType").getValue().toString().equals("repairshop")){
                            if(userItem.hasChild("latitude") && userItem.hasChild("longitude")){
                                RepairShop shop = userItem.getValue(RepairShop.class);
                                Location shop_postion = new Location("shop_postion");
                                shop_postion.setLatitude(shop.latitude);
                                shop_postion.setLongitude(shop.longitude);

//                                String model_key = PrefUtils.getInstance().getSearchModel(PrefUtils.PREF_SEARCH_MODEL, "");
                                String service_key = PrefUtils.getInstance().getSearchService(PrefUtils.PREF_SEARCH_SERVICE,"");
//                                Log.e("SERVICE_KEY", service_key);
//                                if(!model_key.isEmpty()){
//                                    if(shop.serviceModels.size() > 0) {
////                                        Log.e("MODEL_KEY", model_key);
//                                        Map<String, Object> models = shop.serviceModels;
//                                        if (models.containsKey(model_key)){
//                                            if(!service_key.isEmpty()){
//                                                if (!shop.services.isEmpty()) {
//                                                    Map<String, Object> services = shop.services;
//                                                    if (services.containsKey(service_key)) {
//                                                        if(searchText.getText().toString().isEmpty()) {
//                                                            if (shop_postion.distanceTo(currentMyLocation) < selected_distance * 1609) {
//                                                                shopList.add(shop);
//                                                            }
//                                                        }else {
//                                                            if(shop.fullName.toLowerCase().contains(searchText.getText().toString().toLowerCase())
//                                                                    && shop_postion.distanceTo(currentMyLocation) < selected_distance*1609){
//                                                                shopList.add(shop);
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }else {
//                                                if(searchText.getText().toString().isEmpty()) {
//                                                    if (shop_postion.distanceTo(currentMyLocation) < selected_distance * 1609) {
//                                                        shopList.add(shop);
//                                                    }
//                                                }else {
//                                                    if(shop.fullName.toLowerCase().contains(searchText.getText().toString().toLowerCase())
//                                                            && shop_postion.distanceTo(currentMyLocation) < selected_distance*1609){
//                                                        shopList.add(shop);
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }else{
                                    if(!service_key.isEmpty()){
                                        if (!shop.services.isEmpty()) {
                                            Map<String, Object> services = shop.services;
                                            if (services.containsKey(service_key)) {
                                                if(searchText.getText().toString().isEmpty()) {
                                                    if (shop_postion.distanceTo(currentMyLocation) < selected_distance * 1609) {
                                                        shopList.add(shop);
                                                    }
                                                }else {
                                                    if(shop.fullName.toLowerCase().contains(searchText.getText().toString().toLowerCase())
                                                            && shop_postion.distanceTo(currentMyLocation) < selected_distance*1609){
                                                        shopList.add(shop);
                                                    }
                                                }
                                            }
                                        }
                                    }else {
                                        if(searchText.getText().toString().isEmpty()) {
                                            if (shop_postion.distanceTo(currentMyLocation) < selected_distance * 1609) {
                                                shopList.add(shop);
                                            }
                                        }else {
                                            if(shop.fullName.toLowerCase().contains(searchText.getText().toString().toLowerCase())
                                                    && shop_postion.distanceTo(currentMyLocation) < selected_distance*1609){
                                                shopList.add(shop);
                                            }
                                        }
                                    }
//                                }
                            }
                        }
                    }
                    shopRecyclerAdapter.notifyDataSetChanged();
                }
//                hideProgressHUD();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
//                hideProgressHUD();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_main_list_distance_field:
                showReportDlg();
                break;
            case R.id.driver_main_filter:
                Intent filter_intent = new Intent(getActivity(), DriverFilterSearchActivity.class);
                startActivity(filter_intent);
                break;
            default:
                break;
        }
    }

    public void showReportDlg() {
        final Dialog dialog = new Dialog(this.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dlg_distance_popup);
        dialog.show();
        final RadioButton distance1 = (RadioButton) dialog.findViewById(R.id.radioButton_distance1);
        final RadioButton distance2 = (RadioButton) dialog.findViewById(R.id.radioButton_distance2);
        final RadioButton distance3 = (RadioButton) dialog.findViewById(R.id.radioButton_distance3);
        final RadioButton distance4 = (RadioButton) dialog.findViewById(R.id.radioButton_distance4);
        final RadioButton distance5 = (RadioButton) dialog.findViewById(R.id.radioButton_distance5);
        switch (selected_distance){
            case 5:
                distance1.setChecked(true);
                break;
            case 10:
                distance2.setChecked(true);
                break;
            case 20:
                distance3.setChecked(true);
                break;
            case 50:
                distance4.setChecked(true);
                break;
            case 1500:
                distance5.setChecked(true);
                break;
            default:
                break;
        }

        // if button is clicked, close the custom dialog
        distance1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_distance = 5;
                distanceText.setText("5 mi.");
                distance1.setChecked(true);
                dialog.dismiss();
                refreshList();
            }
        });
        distance2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_distance = 10;
                distanceText.setText("10 mi.");
                distance2.setChecked(true);
                dialog.dismiss();
                refreshList();
            }
        });
        distance3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_distance = 20;
                distanceText.setText("20 mi.");
                distance3.setChecked(true);
                dialog.dismiss();
                refreshList();
            }
        });
        distance4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_distance = 50;
                distanceText.setText("50 mi.");
                distance4.setChecked(true);
                dialog.dismiss();
                refreshList();
            }
        });
        distance5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_distance = 1500;
                distanceText.setText("50+mi.");
                distance5.setChecked(true);
                dialog.dismiss();
                refreshList();
            }
        });
    }

    @Override
    public void clickShop(int index, String key) {
        Intent shop_intent = new Intent(getActivity(), DriverRepairShopServiceActivity.class);
        shop_intent.putExtra("shopId", key);
        startActivity(shop_intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            currentMyLocation = location;
            refreshList();
            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("latitude").setValue(location.getLatitude());
            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("longitude").setValue(location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (s.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)) {
            currentMyLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        } else if (s.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
            currentMyLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onResume() {
        super.onResume();
        checkGpsService();
    }
}
