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

public class ActivityCarnes extends AppCompatActivity {

    int contador;
    int preciou;
    Button btnMenosCA, btnMasCA, btnACarritoCA,btnvolver;
    TextView txvCantidadCA,txvPTotalCA;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    String idUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contador =0;
        preciou = 100;
        setContentView(R.layout.activity_carnes);

        //Recuperación de datos DB
        mAuth = FirebaseAuth.getInstance();
        idUsuario = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        txvCantidadCA = (TextView) findViewById(R.id.txtvCantidadC);
        txvPTotalCA = (TextView) findViewById(R.id.txvPTotalC);

        btnvolver = (Button) findViewById(R.id.btnVolverM4);
        btnvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityCarnes.this, ActivityCategoria.class);
                startActivity(intent);
            }
        });


        btnMenosCA = (Button) findViewById(R.id.btnmenosC);
        btnMenosCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador =1;
                txvCantidadCA.setText(Integer.toString(contador));
                txvPTotalCA.setText(Integer.toString(preciou));

            }
        });
        btnMasCA = (Button) findViewById(R.id.btnmasC);
        btnMasCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador ++;
                txvCantidadCA.setText(Integer.toString(contador));
                txvPTotalCA.setText(Integer.toString(contador*preciou));
            }
        });

        btnACarritoCA = (Button) findViewById(R.id.btnAggCarritoC);
        btnACarritoCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contador > 0) {
                    Map<String, Object> carrito = new HashMap<>();
                    carrito.put("id", idUsuario);
                    carrito.put("NomProCarnes", "Chuleta de Res");
                    carrito.put("cantidadCarnes", contador);
                    carrito.put("precUCarnes", "100.00");
                    carrito.put("precioCarnes", txvPTotalCA.getText().toString());
                    carrito.put("imgUrlCarnes", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2Fchuleta.png?alt=media&token=76be6ed9-ae5f-4d92-b97d-a069709a3b69");
                    mDatabase.child("carrito").child(idUsuario).child("catCarnes").setValue(carrito);
                    //mDatabase.child("carrito").child(idUsuario).updateChildren(carrito);

                    Toast.makeText(ActivityCarnes.this, "Producto agregado al Carrito", Toast.LENGTH_LONG).show();
                } else {
                    mensaje();
                }
            }
        });
    }

    public void mensaje(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCarnes.this);
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