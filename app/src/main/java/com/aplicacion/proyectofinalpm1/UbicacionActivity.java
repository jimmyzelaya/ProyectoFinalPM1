package com.aplicacion.proyectofinalpm1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class UbicacionActivity extends AppCompatActivity implements View.OnClickListener {
   private Button btnMap;
    private int MY_PERMISSION_REQUEST_READ_CONTACT ;
    private FusedLocationProviderClient mFusedLocationClient;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        subirLatLongFirebase();
        btnMap = findViewById(R.id.btnMaps);
       btnMap.setOnClickListener(this);
    }

    private void subirLatLongFirebase() {

        if (ActivityCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

           ActivityCompat.requestPermissions(UbicacionActivity.this,
                   new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSION_REQUEST_READ_CONTACT);


            return;
    }
     mFusedLocationClient.getLastLocation()
             .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                 @Override
                 public void onSuccess(Location location) {
                     // la ultima localizacion en algunos casos sera nullo
                     if (location != null) {
                         Log.e("Latitud: ", +location.getLatitude() +"Longitud:" + location.getLatitude());

                                 Map<String, Object> latlang = new HashMap<>();
                                 latlang.put("latitud", location.getLatitude());
                                 latlang.put("longitud", location.getLongitude());
                                 mDatabase.child("usuario").push().setValue(latlang);


                     }
                 }
             });



}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R. id.btnMaps: Intent  intent = new Intent(UbicacionActivity.this,MapsActivity.class);
            startActivity(intent);
            break;
        }
    }
}
