package com.aplicacion.proyectofinalpm1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.aplicacion.proyectofinalpm1.ActivityAdmin.ActivityAdministrador;
import com.aplicacion.proyectofinalpm1.ActivityClientes.ActivityMenu;
import com.aplicacion.proyectofinalpm1.ActivityControl.ActivityLogin;
import com.aplicacion.proyectofinalpm1.ActivityRepartidor.ActivityRepartidor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivitySplash extends AppCompatActivity {

    private final int DURACION_SPLASH = 2000;

    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Persistencia de Datos
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null){
                    tipoUsuario();
                } else {
                    startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));
                    finish();
                }
            }
        },DURACION_SPLASH);

    }

    private void tipoUsuario() {
        String id = firebaseAuth.getCurrentUser().getUid();

        //Evalua los usuarios clientes en la BD
        mDatabase.child("usuarios").child("clientes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    startActivity(new Intent(ActivitySplash.this, ActivityMenu.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Evalua los usuarios repartidores en la BD
        mDatabase.child("usuarios").child("repartidores").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    startActivity(new Intent(ActivitySplash.this, ActivityRepartidor.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Evalua los usuarios Administradores dentro de la BD
        mDatabase.child("usuarios").child("administradores").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    startActivity(new Intent(ActivitySplash.this, ActivityAdministrador.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}