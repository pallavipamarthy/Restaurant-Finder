package com.example.sforestaurants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sforestaurants.Utils.PrefUtils;
import com.example.sforestaurants.data.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Set;

public class RestaurantMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    MapActivityBroadcastReceiver mMapActivityBroadcastReceiver;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_map_layout);

        getSupportActionBar().setLogo(R.drawable.gap_between_icon_title);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mMapActivityBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);

        LatLng sanFrancisco = new LatLng(37.7749, -122.4194);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sanFrancisco));

        //Move camera viewpoint to particular zoom level.
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mMap.animateCamera(zoom);
    }

    @Override
    public void onCameraIdle() {
        mProgressBar.setVisibility(View.VISIBLE);
        CameraPosition cameraPositionObj = mMap.getCameraPosition();
        double currentLatitude = cameraPositionObj.target.latitude;
        double currentLongitude = cameraPositionObj.target.longitude;
        Intent onGestureStopIntent = new Intent(RestaurantMapActivity.this, RestaurantFetchIntentService.class);
        onGestureStopIntent.setAction(Constants.ACTION_GET_VISIBLE_COORDINATES);
        onGestureStopIntent.putExtra(Constants.EXTRA_LATITUDE, String.valueOf(currentLatitude));
        onGestureStopIntent.putExtra(Constants.EXTRA_LONGITUDE, String.valueOf(currentLongitude));
        startService(onGestureStopIntent);
    }

    private void positionMarkers() {
        Set<String> restaurantSet = PrefUtils.getRestaurantCoordinates(this);
        for (String s : restaurantSet) {
            try {
                JSONObject placeObj = (JSONObject) new JSONTokener(s).nextValue();
                String lat = placeObj.getString("lat");
                String lng = placeObj.getString("lng");
                String placeId = placeObj.getString("place_id");
                createMarkers(lat, lng, placeId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void createMarkers(String latitude, String longitude, String placeId) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))));
        marker.setTag(placeId);
    }

    private void registerReceiver() {
        mMapActivityBroadcastReceiver = new MapActivityBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_FETCH_COMPLETE);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mMapActivityBroadcastReceiver, filter);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(RestaurantMapActivity.this, RestaurantDetailActivity.class);
        intent.putExtra(Constants.EXTRA_PLACE_ID, (String) marker.getTag());
        startActivity(intent);
        return true;
    }

    public class MapActivityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_FETCH_COMPLETE)) {
                mProgressBar.setVisibility(View.GONE);
                positionMarkers();
            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (!isNetworkConnected(context)) {
                    Intent noNetworkIntent = new Intent(context, NoNetworkActivity.class);
                    startActivity(noNetworkIntent);
                    finish();
                }
            }
        }
    }

    public boolean isNetworkConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
