

package com.brainyapps.motolabz.MechanicsView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.DriversView.DriverArriavalMapActivity;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MechanicLocationMapActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback{

    private ImageView onBack;
    private MapView mapView;
    private GoogleMap gmap;
    private String driverId = "";
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Query getDriverInfo;

    private Location driverLocation;
    private LatLng driverLatLng;
    private Marker driverMarker;
    BitmapDescriptor driver_pin = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_blue);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_location_map);

        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            driverId = i.getStringExtra("driverId");
        }else {
            super.onBackPressed();
        }

        onBack = (ImageView) findViewById(R.id.mechanic_location_map_back);
        onBack.setOnClickListener(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        getDriverInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(driverId);
        getDriverInfo.addValueEventListener(driverInfo);

        mapView = (MapView) findViewById(R.id.mechanic_map_viewer);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    private ValueEventListener driverInfo = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
                Mechanic mechanic = dataSnapshot.getValue(Mechanic.class);
                driverLocation = new Location("myLocation");
                driverLocation.setLatitude(mechanic.latitude);
                driverLocation.setLongitude(mechanic.longitude);
                getLocationNearyBy(true);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void getLocationNearyBy(boolean goMyLocation) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (driverLocation != null && gmap != null) {
            if (driverMarker != null)
                driverMarker.remove();

            driverLatLng = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());

            MarkerOptions driverOption = new MarkerOptions().position(driverLatLng);
            driverOption.icon(driver_pin);
            driverMarker = gmap.addMarker(driverOption);

            if (goMyLocation) {
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng,14));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mechanic_location_map_back:
                super.onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
        getDriverInfo.removeEventListener(driverInfo);
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        getLocationNearyBy(true);
    }
}
