package com.aplicacion.proyectofinalpm1.ActivityClientes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aplicacion.proyectofinalpm1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ActivityGranosB extends AppCompatActivity {

    int contador;
    int preciou;
    Button btnMenosGR, btnMasGR, btnAggCarritoG, btnvolver;
    TextView txvCantidadGR,txvPTotalGR;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    String idUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contador = 0;
        preciou = 14;
        setContentView(R.layout.activity_granos_b);

        //Recuperación de datos DB
        mAuth = FirebaseAuth.getInstance();
        idUsuario = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        txvCantidadGR = (TextView) findViewById(R.id.txtvCantidadG);
        txvPTotalGR = (TextView) findViewById(R.id.txvPTotalG);

        btnvolver = (Button) findViewById(R.id.btnVolverM3);
        btnvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityGranosB.this, ActivityCategoria.class);
                startActivity(intent);
            }
        });

        btnMenosGR = (Button) findViewById(R.id.btnmenosG);
        btnMenosGR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador =1;
                txvCantidadGR.setText(Integer.toString(contador));
                txvPTotalGR.setText(Integer.toString(preciou));
            }
        });
        btnMasGR = (Button) findViewById(R.id.btnmasG);
        btnMasGR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador ++;
                txvCantidadGR.setText(Integer.toString(contador));
                txvPTotalGR.setText(Integer.toString(contador*preciou));
            }
        });

        btnAggCarritoG = (Button) findViewById(R.id.btnAggCarritoG);
        btnAggCarritoG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contador > 0) {
                    Map<String, Object> carrito = new HashMap<>();
                    carrito.put("id", idUsuario);
                    carrito.put("NomProGranosB", "Arroz");
                    carrito.put("cantidadGranosB", contador);
                    carrito.put("precUGranosB", "14.00");
                    carrito.put("precioGranosB", txvPTotalGR.getText().toString());
                    carrito.put("imgUrlGranosB", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2Farroz.jpg?alt=media&token=8e691cc4-1b32-4cd8-bb6f-7e07e07f6831");
                    mDatabase.child("carrito").child(idUsuario).child("catGranosB").setValue(carrito);
                    //mDatabase.child("carrito").child(idUsuario).updateChildren(carrito);

                    Toast.makeText(ActivityGranosB.this, "Producto agregado al Carrito", Toast.LENGTH_LONG).show();
                } else {
                    mensaje();
                }
            }
        });
    }

    public void mensaje(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityGranosB.this);
        builder.setMessage("Debe Agregar por lo menos un Producto para que se añada al Carrito")
                .setTitle("Atención");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}