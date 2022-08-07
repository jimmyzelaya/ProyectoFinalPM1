package com.aplicacion.proyectofinalpm1.ActivityRepartidor;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.aplicacion.proyectofinalpm1.R;
import com.aplicacion.proyectofinalpm1.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.aplicacion.proyectofinalpm1.ActivityRepartidor.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private double latitud, longitud;
    String lat, longi;
    private String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        lat = (intent.getStringExtra("latitud"));
        longi = (intent.getStringExtra("longitud"));

        latitud = Double.parseDouble(lat);
        longitud = Double.parseDouble(longi);

        /* latitud = Double.parseDouble(getIntent().getStringExtra("LATITUD"));
        longitud = Double.parseDouble(getIntent().getStringExtra("LONGITUD"));
        nombre = getIntent().getStringExtra("NOMBRE");*/

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
      //   nombre = (nombre.isEmpty()) ? "Ubicación" : nombre;

        // Add a marker in Sydney and move the camera
       LatLng Roatan = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(Roatan).title("Marker in Roatan"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Roatan, 15f));



        // Add a marker in Sydney and move the camera
        /*LatLng Roatan = new LatLng(16.3480335, -86.4685334);
        mMap.addMarker(new MarkerOptions().position(Roatan).title("Marker in Roatan"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Roatan, 15f));*/
    }
}