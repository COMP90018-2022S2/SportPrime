package com.example.myapplication2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication2.utils.Config;
import com.example.myapplication2.utils.SearchBean;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap mMap;
    private Marker currentMarker;
    private LocationManager locationManager = null;
    private ProgressDialog dialog;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.d("===", "onLocationChanged getLatitude:"
                    + location.getLatitude() + " getLongitude:" + location.getLongitude());

            createSelfMarker(new LatLng(location.getLatitude(),location.getLongitude()));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(null);
        mapView.onResume();
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS != errorCode) {
            GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0).show();
        } else {
            mapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        requestLocation();
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000*60,
                1000,
                locationListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    private void createSelfMarker(LatLng latLng){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.position(latLng);

        if(currentMarker!=null){
            currentMarker.remove();
        }
        markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentMarker = mMap.addMarker(markerOpt);
        currentMarker.setTag("self");
        //melbourne.showInfoWindow();

        getGYM(latLng);
    }

    private void getGYM(LatLng latLng){
        StringBuilder builder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        builder.append("&location="+latLng.latitude+","+latLng.longitude);
        builder.append("&radius=5000");
        builder.append("&type=gym");
        builder.append("&key="+ Config.DirectionsKey);

        dialog = new ProgressDialog(this);
        dialog.show();
        OkHttpClient httpClient =new OkHttpClient();
        Request request =new Request.Builder()
                .url(builder.toString())
                .get()
                .build();


        httpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        dialog.dismiss();
                    }

                    @Override public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                        dialog.dismiss();
                        SearchBean searchBean = new Gson().fromJson(response.body().string(),SearchBean.class);
                        runOnUiThread(()->createGymMarker(searchBean));
                    }
                });
    }

    private void createGymMarker(SearchBean searchBean){
        if(searchBean!=null&&searchBean.getResults()!=null&&searchBean.getResults().size()>0){
            for(int i=0;i<searchBean.getResults().size();i++){
                SearchBean.ResultsBean resultsBean = searchBean.getResults().get(i);
                LatLng latLng = new LatLng(resultsBean.getGeometry().getLocation().getLat(),resultsBean.getGeometry().getLocation().getLng());
                MarkerOptions markerOpt = new MarkerOptions();
                markerOpt.position(latLng);
                markerOpt.title(resultsBean.getName());
                markerOpt.snippet(resultsBean.getVicinity());


                markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(markerOpt);
            }
        }

    }
}
