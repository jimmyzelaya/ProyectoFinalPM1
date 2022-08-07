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

public class ActivityABebes extends AppCompatActivity {
    int contador,preciou;
    Button btnMenosAB, btnMasAB, btnACarritoAB;
    Button btnvolver;
    TextView txvCantidadAB,txvPTotalABBE;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    String idUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contador =0;
        preciou = 120;
        setContentView(R.layout.activity_abebes);

        //Recuperación de datos DB
        mAuth = FirebaseAuth.getInstance();
        idUsuario = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        txvCantidadAB = (TextView) findViewById(R.id.txtvCantidadABB);
        txvPTotalABBE = (TextView) findViewById(R.id.txvPTotalABB);

        btnvolver = (Button) findViewById(R.id.btnVolverM2);
        btnvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityABebes.this, ActivityCategoria.class);
                startActivity(intent);
            }
        });

        btnMenosAB = (Button) findViewById(R.id.btnMenosABB);
        btnMenosAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador =1;
                txvCantidadAB.setText(Integer.toString(contador));
                txvPTotalABBE.setText(Integer.toString(preciou));
            }
        });
        btnMasAB = (Button) findViewById(R.id.btnMasABB);
        btnMasAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador ++;
                txvCantidadAB.setText(Integer.toString(contador));
                txvPTotalABBE.setText(Integer.toString(contador*preciou));
            }
        });

        btnACarritoAB = (Button) findViewById(R.id.btnAggCaritoABB);
        btnACarritoAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contador > 0) {
                    Map<String, Object> carrito = new HashMap<>();
                    carrito.put("id", idUsuario);
                    carrito.put("NomProBebes", "Pañales para bebe 45 Unidades");
                    carrito.put("cantidadPañales", contador);
                    carrito.put("precUPañales", "120.00");
                    carrito.put("precioPañales", txvPTotalABBE.getText().toString());
                    carrito.put("imgUrlBebes", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2Fpaniales.png?alt=media&token=cd9f01a7-1159-49d5-9f2c-5a791793e0c6");
                    mDatabase.child("carrito").child(idUsuario).child("catBebes").setValue(carrito);
                    //mDatabase.child("carrito").child(idUsuario).updateChildren(carrito);

                    Toast.makeText(ActivityABebes.this, "Producto agregado al Carrito", Toast.LENGTH_LONG).show();
                } else {
                    mensaje();
                }
            }
        });
    }

    public void mensaje(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityABebes.this);
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