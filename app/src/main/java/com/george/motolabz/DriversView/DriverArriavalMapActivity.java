package com.brainyapps.motolabz.DriversView;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.Utils;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.brainyapps.motolabz.Views.DurationJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class DriverArriavalMapActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, LocationListener{

    private ImageView onBack;
    private TextView title;
    private TextView estimateTime;

    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Location driverLocation;
    private LatLng driverLatLng;
    private Location mechanicLocation;
    private LatLng mechanicLatLng;
    private Marker driverMarker;
    private Marker mechanicMarker;
    BitmapDescriptor mechanic_pin = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_blue);
    BitmapDescriptor driver_pin = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red);

    private String mechanicId = "";
    Query mechanicInfo;
    private ProgressHUD mProgressDialog;

    private void showProgressHUD(String text) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = ProgressHUD.show(this, text, true);
        mProgressDialog.show();
    }

    private void hideProgressHUD() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_arriaval_map);

        if(getIntent().getExtras() != null){
            Intent i = getIntent();
            mechanicId = i.getStringExtra("mechanicId");
        }else {
            super.onBackPressed();
        }
        mechanicLocation = new Location("mechanicLocation");

        onBack = (ImageView) findViewById(R.id.driver_arrival_map_back);
        onBack.setOnClickListener(this);
        title = (TextView)findViewById(R.id.driver_arrival_map_title);
        mechanicInfo = FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(mechanicId);
        mechanicInfo.addValueEventListener(getMechanicInfo);
        estimateTime = (TextView)findViewById(R.id.estimate_arrival_time);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = (MapView) findViewById(R.id.driver_map_viewer);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        initLocation();
    }

    private ValueEventListener getMechanicInfo = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
                Mechanic mechanic = dataSnapshot.getValue(Mechanic.class);
                title.setText(mechanic.fullName);
                mechanicLocation.setLatitude(mechanic.latitude);
                mechanicLocation.setLongitude(mechanic.longitude);
                getLocationNearyBy(true);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driver_arrival_map_back:
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
        checkGpsService();
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
        mechanicInfo.removeEventListener(getMechanicInfo);
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
        if(checkGpsService()){
            showProgressHUD("");
            getLocationNearyBy(true);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            driverLocation = location;
            getLocationNearyBy(true);
            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("latitude").setValue(location.getLatitude());
            FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("longitude").setValue(location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        initLocation();

        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (s.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)) {
            driverLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        } else if (s.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
            driverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
        getLocationNearyBy(true);
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void getLocationNearyBy(boolean goMyLocation) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (driverLocation != null && mechanicLocation != null && gmap != null) {
            if (driverMarker != null)
                driverMarker.remove();
            if(mechanicMarker != null)
                mechanicMarker.remove();

            driverLatLng = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());
            mechanicLatLng = new LatLng(mechanicLocation.getLatitude(), mechanicLocation.getLongitude());

            MarkerOptions driverOption = new MarkerOptions().position(driverLatLng);
            driverOption.icon(driver_pin);
            driverMarker = gmap.addMarker(driverOption);

            MarkerOptions mechanicOption = new MarkerOptions().position(mechanicLatLng);
            mechanicOption.icon(mechanic_pin);
            mechanicMarker = gmap.addMarker(mechanicOption);

            if (goMyLocation) {
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng,14));
            }

            String url = getDirectionsUrl(driverLatLng, mechanicLatLng);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
            hideProgressHUD();
        }
    }

    public void initLocation(){
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Utils.checkLocationPermission(this);
            return;
        }
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this,null);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
    }

    public boolean checkGpsService() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if (!gps_enabled && !network_enabled) {
            AlertFactory.showAlert(this, "", "Check your Location service in Settings -> Location");
            return false;
        }else {
            return true;
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        //
        String str_mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+str_mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){

        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, String >{

        // Parsing the data in non-ui thread
        @Override
        protected String doInBackground(String... jsonData) {

            JSONObject jObject;
            String duration = "";

            try{
                jObject = new JSONObject(jsonData[0]);
                DurationJSONParser parser = new DurationJSONParser();

                // Starts parsing data
                duration = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return duration;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(String result) {
            estimateTime.setText("EST: "+ result);
        }
    }
}
