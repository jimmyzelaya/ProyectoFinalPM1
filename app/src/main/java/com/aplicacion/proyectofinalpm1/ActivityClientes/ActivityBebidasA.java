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

public class ActivityBebidasA extends AppCompatActivity {
    int contador,preciou;
    Button btnMenosBC, btnMasBC, btnACarritoBC, btnvolver;
    TextView txvCantidadBC,txvPTotalBAC;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    String idUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contador =0;
        preciou = 150;
        setContentView(R.layout.activity_bebidas);

        //Recuperación de datos DB
        mAuth = FirebaseAuth.getInstance();
        idUsuario = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Cajas de Texto
        txvCantidadBC = (TextView) findViewById(R.id.txtvCantidadBC);
        txvPTotalBAC = (TextView) findViewById(R.id.txvPTotalBC);

        btnvolver = (Button) findViewById(R.id.btnVolverM6);
        btnvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityBebidasA.this, ActivityCategoria.class);
                startActivity(intent);
            }
        });

       //disminuir cantidad
        btnMenosBC = (Button) findViewById(R.id.btnMenosBC);
        btnMenosBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador =1;
                txvCantidadBC.setText(Integer.toString(contador));
                txvPTotalBAC.setText(Integer.toString(preciou));
            }
        });

        //aumentar cantidad
        btnMasBC = (Button) findViewById(R.id.btnMasBC);
        btnMasBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador ++;
                txvCantidadBC.setText(Integer.toString(contador));
                txvPTotalBAC.setText(Integer.toString(contador*preciou));
            }
        });

        //Carga la información al carrito en la BD
        btnACarritoBC = (Button) findViewById(R.id.btnAggCaritoBC);
        btnACarritoBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contador > 0){
                    Map<String, Object> carrito = new HashMap<>();
                    carrito.put("id", idUsuario);
                    carrito.put("NomProBebidas", "Six Pack Corona");
                    carrito.put("cantidadBebidas", contador);
                    carrito.put("precUBebidas", "150.00");
                    carrito.put("precioBebidas", txvPTotalBAC.getText().toString());
                    carrito.put("imgUrlBebidas", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2FbebidaCoronaC.png?alt=media&token=6805fb9f-3bdd-4541-90c7-95e8a9dcec20");
                    mDatabase.child("carrito").child(idUsuario).child("catBebidas").setValue(carrito);
                    //mDatabase.child("carrito").child(idUsuario).updateChildren(carrito);

                    Toast.makeText(ActivityBebidasA.this, "Producto agregado al Carrito", Toast.LENGTH_LONG).show();
                }else {
                    mensaje();
                }
            }
        });
    }
    public void mensaje(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityBebidasA.this);
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