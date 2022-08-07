package com.aplicacion.proyectofinalpm1.ActivityClientes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aplicacion.proyectofinalpm1.R;

public class ActivityCategoria extends AppCompatActivity {

    Button btnvolver;
    CardView btnLacteos,btnGranosB,btnBebidasA,btnAreaBebe, btnCarnes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        btnvolver = (Button) findViewById(R.id.btnVolverM);
        btnvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityCategoria.this, ActivityMenu.class);
                startActivity(intent);
                finish();
            }
        });

        btnCarnes = (CardView) findViewById(R.id.btncarnes);
        btnCarnes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityCategoria.this, ActivityCarnes.class));
                //finish();
            }
        });

        btnLacteos = (CardView) findViewById(R.id.btnlacteos);
        btnLacteos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityCategoria.this, ActivityLacteos.class));
                //finish();
            }
        });

        btnGranosB = (CardView) findViewById(R.id.btngranos);
        btnGranosB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityCategoria.this, ActivityGranosB.class));
                //finish();
            }
        });

        btnBebidasA = (CardView) findViewById(R.id.btnbebidas);
        btnBebidasA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityCategoria.this, ActivityBebidasA.class));
                //finish();
            }
        });

        btnAreaBebe = (CardView) findViewById(R.id.btnbebes);
        btnAreaBebe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(ActivityCategoria.this, ActivityABebes.class));
              //finish();
            }
        });
    }
}