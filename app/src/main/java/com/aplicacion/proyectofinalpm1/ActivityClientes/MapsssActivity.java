package com.aplicacion.proyectofinalpm1.ActivityClientes;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.aplicacion.proyectofinalpm1.ActivityRepartidor.MapsActivity;
import com.aplicacion.proyectofinalpm1.R;
import com.aplicacion.proyectofinalpm1.databinding.ActivityMapsssBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
//import com.aplicacion.proyectofinalpm1.ActivityClientes.databinding.ActivityMapsssBinding;

public class MapsssActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsssBinding binding;
    String lat, longi;
    Double latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsssBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

/*
        //Ubicación actual de cada usuario
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION));

            else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }


        //datos de ubicación a la bd



        LocationManager locationManager = (LocationManager) MapsssActivity.this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //latitud = Double.parseDouble(lat);
                //longitud = Double.parseDouble(longi);

                latitud =  location.getLatitude();
                longitud =  location.getLongitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras){}

            public void onProviderEnabled(String provider){}

            public void onProviderDisabled(String provider){}

            int permissionCheck = ContextCompat.checkSelfPermission(MapsssActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        };*/



        //


        //termina ubicación actual de cada usuario

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng Roatan = new LatLng(16.3480335, -86.4685334);
       // LatLng Roatan = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(Roatan).title("Marker in Roatan"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Roatan, 15f));
    }
}