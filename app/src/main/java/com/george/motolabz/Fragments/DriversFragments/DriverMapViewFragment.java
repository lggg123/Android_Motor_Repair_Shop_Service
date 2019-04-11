package com.brainyapps.motolabz.Fragments.DriversFragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DriverMapViewFragment extends Fragment implements OnMapReadyCallback, LocationListener{

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapView mapView;
    private GoogleMap gmap;


    public static final String FRAGMENT_TAG = "com_motolabz_driver_map_view_fragment_tag";
    private static Context mContext;

    public static android.app.Fragment newInstance(Context context) {
        mContext = context;

        android.app.Fragment f = new DriverMapViewFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_driver_map_view, container, false);

        mapView = (MapView) rootView.findViewById(R.id.driver_mapview);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(this);
        return  rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.getUiSettings().setMyLocationButtonEnabled(false);
       /*
       //in old Api Needs to call MapsInitializer before doing any CameraUpdateFactory call
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
       */

        // Updates the location and zoom of the MapView
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);*/
//        gmap.moveCamera(CameraUpdateFactory.newLatLng(43.1,-87.9));
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

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
