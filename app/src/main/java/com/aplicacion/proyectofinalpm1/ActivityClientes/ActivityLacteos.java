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

public class ActivityLacteos extends AppCompatActivity {

    int contador,preciou;
    Button btnMenosLA, btnMasLA, btnACarritoLA,btnvolver;
    TextView txvCantidadLA,txvPTotalLA;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    String idUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contador =0;
        preciou = 30;
        setContentView(R.layout.activity_lacteos);

        //Recuperación de datos DB
        mAuth = FirebaseAuth.getInstance();
        idUsuario = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        txvCantidadLA = (TextView) findViewById(R.id.txtvCantidadL);
        txvPTotalLA = (TextView) findViewById(R.id.txvPTotalL);

        btnvolver = (Button) findViewById(R.id.btnVolverM5);
        btnvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLacteos.this, ActivityCategoria.class);
                startActivity(intent);
            }
        });

        btnMenosLA = (Button) findViewById(R.id.btnMenosL);
        btnMenosLA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador --;
                if(contador < 0 ) {
                    btnMenosLA.setEnabled(false);
                } else {
                    btnMenosLA.setEnabled(true);
                    txvCantidadLA.setText(Integer.toString(contador));
                    txvPTotalLA.setText(Integer.toString(contador * preciou));
                }
            }
        });
        btnMasLA = (Button) findViewById(R.id.btnMasL);
        btnMasLA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador ++;
                txvCantidadLA.setText(Integer.toString(contador));
                txvPTotalLA.setText(Integer.toString(contador*preciou));
            }
        });

        btnACarritoLA = (Button) findViewById(R.id.btnAggCaritoL);
        btnACarritoLA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contador > 0) {
                    Map<String, Object> carrito = new HashMap<>();
                    carrito.put("id", idUsuario);
                    carrito.put("NomProLacteos", "Leche Ceteco 500g");
                    carrito.put("cantidadLacteos", contador);
                    carrito.put("precULacteos", "30.00");
                    carrito.put("precioLacteos", txvPTotalLA.getText().toString());
                    carrito.put("imgUrlLacteos", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2FlecheCeteco.png?alt=media&token=7bc5a649-956e-42e7-b00b-adf651115661");
                    //mDatabase.child("carrito").child(idUsuario).updateChildren(carrito);
                    mDatabase.child("carrito").child(idUsuario).child("catLacteos").setValue(carrito);

                    Toast.makeText(ActivityLacteos.this, "Producto agregado al Carrito", Toast.LENGTH_LONG).show();
                } else {
                    mensaje();
                }
            }
        });
    }

    public void mensaje(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLacteos.this);
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