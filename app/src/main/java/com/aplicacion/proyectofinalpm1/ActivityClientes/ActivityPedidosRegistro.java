package com.aplicacion.proyectofinalpm1.ActivityClientes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aplicacion.proyectofinalpm1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityPedidosRegistro extends AppCompatActivity {

    String idUsuario;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList =new ArrayList<>();

    private final int cargaDatos = 400;

    TextView tvNomP1, tvPreP1, tvCantP1, tvSubP1;
    TextView tvNomP2, tvPreP2, tvCantP2, tvSubP2;
    TextView tvNomP3, tvPreP3, tvCantP3, tvSubP3;
    TextView tvNomP4, tvPreP4, tvCantP4, tvSubP4;
    TextView tvNomP5, tvPreP5, tvCantP5, tvSubP5;
    TextView tvNomPdes1, tvNomPdes2, tvNomPdes3, tvNomPdes4, tvNomPdes5;
    TextView tvPrePdes1, tvPrePdes2, tvPrePdes3, tvPrePdes4, tvPrePdes5;
    TextView tvCantPdes1, tvCantPdes2, tvCantPdes3, tvCantPdes4, tvCantPdes5;
    TextView tvSubPdes1, tvSubPdes2, tvSubPdes3, tvSubPdes4, tvSubPdes5;
    TextView tvEvaSubT, tVEvaImp, tvEvaTotal;
    EditText txtEvaComentario;

    ImageView imgP1, imgP2, imgP3, imgP4, imgP5;

    String NomProBebes = "", cantidadPañales = "", precioPañales = "";
    String NomProBebidas = "", cantidadBebidas = "", precioBebidas = "";
    String NomProCarnes = "", cantidadCarnes = "", precioCarnes = "";
    String NomProGranosB = "", cantidadGranosB = "", precioGranosB = "";
    String NomProLacteos = "", cantidadLacteos = "", precioLacteos = "";
    String comentarioEva;

    double subtotalBebes = 0, subtotalBebidas = 0, subtotalCarnes = 0, subtotalGranosB = 0, subtotalLacteos = 0, subtotal = 0;
    double impBebes = 0, impBebidas = 0, impCarnes = 0, impGranosB = 0, impLacteos = 0, impuesto = 0;
    double totalBebes = 0, totalBebidas = 0, totalCarnes = 0, totalGranosB = 0, totalLacteos = 0, total = 0;
    double precUBebes = 0, precUBebidas = 0, PrecUCarnes = 0, precGranosB = 0, precULacteos = 0;
    String subtotalDB, impuestoDB, totalDB;
    String idCliente, nombreCliente, apellidoCliente, telefonoCliente, direccionCliente, correoCliente;
    String identificador = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_registro);

        Intent intent = getIntent();
        identificador = (intent.getStringExtra("identificador"));

        mAuth = FirebaseAuth.getInstance();
        idUsuario = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Instancia de las cajas de Textos
        instanciarTextos();

        //Limpia las casillas
        limpiar();

        catBebidas();
        catBebes();
        catCarnes();
        catGranosB();
        catLacteos();
        infoUsuario();
        infoPedido();

        //Carga de Datos
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                impuesto = impBebes + impBebidas + impCarnes + impGranosB + impLacteos;
                subtotal = subtotalBebes + subtotalBebidas + subtotalCarnes + subtotalGranosB + subtotalLacteos;
                total = totalBebes + totalBebidas + totalCarnes + totalGranosB + totalLacteos;

                //tvSubtotal.setText(subtotal + " Lps");
                //tvImpuesto.setText(impuesto + " Lps");
                //tvTotal.setText(total + " Lps");
            }
        },cargaDatos);
        //Termina carga de Datos
    }

    public void infoUsuario(){
        mDatabase.child("pedidos").child("nuevos").child(identificador).child("infoCliente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    idCliente = dataSnapshot.child("idCliente").getValue().toString();
                    nombreCliente = dataSnapshot.child("nomCliente").getValue().toString();
                    apellidoCliente = dataSnapshot.child("apeCliente").getValue().toString();
                    telefonoCliente = dataSnapshot.child("telCliente").getValue().toString();
                    direccionCliente = dataSnapshot.child("dirCliente").getValue().toString();
                    correoCliente = dataSnapshot.child("corCliente").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void infoPedido(){
        mDatabase.child("pedidos").child("nuevos").child(identificador).child("infoPedido").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    subtotalDB = dataSnapshot.child("subtotal").getValue().toString();
                    impuestoDB = dataSnapshot.child("impuesto").getValue().toString();
                    totalDB = dataSnapshot.child("total").getValue().toString();

                    tvEvaSubT.setText(subtotalDB + ".00 Lps");
                    tVEvaImp.setText(impuestoDB + ".00 Lps");
                    tvEvaTotal.setText(totalDB + ".00 Lps");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void catBebidas(){
        mDatabase.child("pedidos").child("nuevos").child(identificador).child("catBebidas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    cantidadBebidas = dataSnapshot.child("cantidadBebidas").getValue().toString();
                    precioBebidas = dataSnapshot.child("precioBebidas").getValue().toString();
                    NomProBebidas = dataSnapshot.child("NomProBebidas").getValue().toString();
                    String precUBebidas = dataSnapshot.child("precUBebidas").getValue().toString();
                    String imagenURL = dataSnapshot.child("imgUrlBebidas").getValue().toString();

                    String subtota = precioBebidas;
                    subtotalBebidas = Integer.parseInt(subtota);
                    impBebidas = subtotalBebidas * 0.15;
                    totalBebidas = subtotalBebidas + impBebidas;

                    if (tvNomP1.getText().toString().isEmpty()) {
                        tvNomP1.setText(NomProBebidas);
                        tvPreP1.setText(precUBebidas + " Lps");
                        tvCantP1.setText(cantidadBebidas);
                        tvSubP1.setText(precioBebidas + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProBebidas);
                        tvPreP2.setText(precUBebidas + " Lps");
                        tvCantP2.setText(cantidadBebidas);
                        tvSubP2.setText(precioBebidas + ".00 .Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProBebidas);
                        tvPreP3.setText(precUBebidas + " Lps");
                        tvCantP3.setText(cantidadBebidas);
                        tvSubP3.setText(precioBebidas + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProBebidas);
                        tvPreP4.setText(precUBebidas + " Lps");
                        tvCantP4.setText(cantidadBebidas);
                        tvSubP4.setText(precioBebidas + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProBebidas);
                        tvPreP5.setText(precUBebidas + " Lps");
                        tvCantP5.setText(cantidadBebidas);
                        tvSubP5.setText(precioBebidas + "00. Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP5);
                        productoCinco();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void catBebes(){
        mDatabase.child("pedidos").child("nuevos").child(identificador).child("catBebes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    cantidadPañales = dataSnapshot.child("cantidadPañales").getValue().toString();
                    precioPañales = dataSnapshot.child("precioPañales").getValue().toString();
                    String precUPañales = dataSnapshot.child("precUPañales").getValue().toString();
                    NomProBebes = dataSnapshot.child("NomProBebes").getValue().toString();
                    String imagenURL = dataSnapshot.child("imgUrlBebes").getValue().toString();

                    String subtota = precioPañales;
                    subtotalBebes = Integer.parseInt(subtota);
                    impBebes = subtotalBebes * 0.15;
                    totalBebes = subtotalBebes + impBebes;

                    if (tvNomP1.getText().toString().isEmpty()) {
                        tvNomP1.setText(NomProBebes);
                        tvPreP1.setText(precUPañales + " Lps");
                        tvCantP1.setText(cantidadPañales);
                        tvSubP1.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProBebes);
                        tvPreP2.setText(precUPañales + " Lps");
                        tvCantP2.setText(cantidadPañales);
                        tvSubP2.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProBebes);
                        tvPreP3.setText(precUPañales + " Lps");
                        tvCantP3.setText(cantidadPañales);
                        tvSubP3.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProBebes);
                        tvPreP4.setText(precUPañales + " Lps");
                        tvCantP4.setText(cantidadPañales);
                        tvSubP4.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProBebes);
                        tvPreP5.setText(precUPañales + " Lps");
                        tvCantP5.setText(cantidadPañales);
                        tvSubP5.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP5);
                        productoCinco();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void catCarnes(){
        mDatabase.child("pedidos").child("nuevos").child(identificador).child("catCarnes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    cantidadCarnes = dataSnapshot.child("cantidadCarnes").getValue().toString();
                    precioCarnes = dataSnapshot.child("precioCarnes").getValue().toString();
                    NomProCarnes = dataSnapshot.child("NomProCarnes").getValue().toString();
                    String precUCarnes = dataSnapshot.child("precUCarnes").getValue().toString();
                    String imagenURL = dataSnapshot.child("imgUrlCarnes").getValue().toString();

                    String subtota = precioCarnes;
                    subtotalCarnes = Integer.parseInt(subtota);
                    impCarnes = subtotalCarnes * 0.15;
                    totalCarnes = subtotalCarnes + impCarnes;

                    if (tvNomP1.getText().toString().isEmpty()) {
                        tvNomP1.setText(NomProCarnes);
                        tvPreP1.setText(precUCarnes + " Lps");
                        tvCantP1.setText(cantidadCarnes);
                        tvSubP1.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProCarnes);
                        tvPreP2.setText(precUCarnes + " Lps");
                        tvCantP2.setText(cantidadCarnes);
                        tvSubP2.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProCarnes);
                        tvPreP3.setText(precUCarnes + " Lps");
                        tvCantP3.setText(cantidadCarnes);
                        tvSubP3.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProCarnes);
                        tvPreP4.setText(precUCarnes + " Lps");
                        tvCantP4.setText(cantidadCarnes);
                        tvSubP4.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProCarnes);
                        tvPreP5.setText(precUCarnes + " Lps");
                        tvCantP5.setText(cantidadCarnes);
                        tvSubP5.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP5);
                        productoCinco();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void catGranosB(){
        mDatabase.child("pedidos").child("nuevos").child(identificador).child("catGranosB").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    cantidadGranosB = dataSnapshot.child("cantidadGranosB").getValue().toString();
                    precioGranosB = dataSnapshot.child("precioGranosB").getValue().toString();
                    NomProGranosB = dataSnapshot.child("NomProGranosB").getValue().toString();
                    String precUGranosB = dataSnapshot.child("precUGranosB").getValue().toString();
                    String imagenURL = dataSnapshot.child("imgUrlGranosB").getValue().toString();

                    String subtota = precioGranosB;
                    subtotalGranosB = Integer.parseInt(subtota);
                    impGranosB = subtotalGranosB * 0.15;
                    totalGranosB = subtotalGranosB + impGranosB;

                    if (tvNomP1.getText().toString().isEmpty()) {
                        tvNomP1.setText(NomProGranosB);
                        tvPreP1.setText(precUGranosB + " Lps");
                        tvCantP1.setText(cantidadGranosB);
                        tvSubP1.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProGranosB);
                        tvPreP2.setText(precUGranosB + " Lps");
                        tvCantP2.setText(cantidadGranosB);
                        tvSubP2.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProGranosB);
                        tvPreP3.setText(precUGranosB + " Lps");
                        tvCantP3.setText(cantidadGranosB);
                        tvSubP3.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProGranosB);
                        tvPreP4.setText(precUGranosB + " Lps");
                        tvCantP4.setText(cantidadGranosB);
                        tvSubP4.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProGranosB);
                        tvPreP5.setText(precUGranosB + " Lps");
                        tvCantP5.setText(cantidadGranosB);
                        tvSubP5.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP5);
                        productoCinco();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void catLacteos(){
        mDatabase.child("pedidos").child("nuevos").child(identificador).child("catLacteos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    cantidadLacteos = dataSnapshot.child("cantidadLacteos").getValue().toString();
                    precioLacteos = dataSnapshot.child("precioLacteos").getValue().toString();
                    NomProLacteos = dataSnapshot.child("NomProLacteos").getValue().toString();
                    String precULacteos = dataSnapshot.child("precULacteos").getValue().toString();
                    String imagenURL = dataSnapshot.child("imgUrlLacteos").getValue().toString();

                    String subtota = precioLacteos;
                    subtotalLacteos = Integer.parseInt(subtota);
                    impLacteos = subtotalLacteos * 0.15;
                    totalLacteos = subtotalLacteos + impLacteos;

                    if (tvNomP1.getText().toString().isEmpty()) {
                        tvNomP1.setText(NomProLacteos);
                        tvPreP1.setText(precULacteos + " Lps");
                        tvCantP1.setText(cantidadLacteos);
                        tvSubP1.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProLacteos);
                        tvPreP2.setText(precULacteos + " Lps");
                        tvCantP2.setText(cantidadLacteos);
                        tvSubP2.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProLacteos);
                        tvPreP3.setText(precULacteos + " Lps");
                        tvCantP3.setText(cantidadLacteos);
                        tvSubP3.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProLacteos);
                        tvPreP4.setText(precULacteos + " Lps");
                        tvCantP4.setText(cantidadLacteos);
                        tvSubP4.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProLacteos);
                        tvPreP5.setText(precULacteos + " Lps");
                        tvCantP5.setText(cantidadLacteos);
                        tvSubP5.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityPedidosRegistro.this).load(imagenURL).into(imgP5);
                        productoCinco();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void instanciarTextos(){
        //Cuadros de Texto

        tvEvaSubT = (TextView) findViewById(R.id.tvMiSubT);
        tVEvaImp = (TextView) findViewById(R.id.tVMiImp);
        tvEvaTotal = (TextView) findViewById(R.id.tvMiTotal);

        tvNomP1 = (TextView) findViewById(R.id.tvMiNomP1);
        tvNomP2 = (TextView) findViewById(R.id.tvMiNomP2);
        tvNomP3 = (TextView) findViewById(R.id.tvMiNomP3);
        tvNomP4 = (TextView) findViewById(R.id.tvMiNomP4);
        tvNomP5 = (TextView) findViewById(R.id.tvMiNomP5);

        tvPreP1 = (TextView) findViewById(R.id.tvMiPreP1);
        tvPreP2 = (TextView) findViewById(R.id.tvMiPreP2);
        tvPreP3 = (TextView) findViewById(R.id.tvMiPreP3);
        tvPreP4 = (TextView) findViewById(R.id.tvMiPreP4);
        tvPreP5 = (TextView) findViewById(R.id.tvMiPreP5);

        tvCantP1 = (TextView) findViewById(R.id.tvMiCantP1);
        tvCantP2 = (TextView) findViewById(R.id.tvMiCantP2);
        tvCantP3 = (TextView) findViewById(R.id.tvMiCantP3);
        tvCantP4 = (TextView) findViewById(R.id.tvMiCantP4);
        tvCantP5 = (TextView) findViewById(R.id.tvMiCantP5);

        tvSubP1 = (TextView) findViewById(R.id.tvMiSubP1);
        tvSubP2 = (TextView) findViewById(R.id.tvMiSubP2);
        tvSubP3 = (TextView) findViewById(R.id.tvMiSubP3);
        tvSubP4 = (TextView) findViewById(R.id.tvMiSubP4);
        tvSubP5 = (TextView) findViewById(R.id.tvMiSubP5);

        //Instancia de Imagenes (Productos)
        imgP1 = (ImageView) findViewById(R.id.imgMiP1);
        imgP2 = (ImageView) findViewById(R.id.imgMiP2);
        imgP3 = (ImageView) findViewById(R.id.imgMiP3);
        imgP4 = (ImageView) findViewById(R.id.imgMiP4);
        imgP5 = (ImageView) findViewById(R.id.imgMiP5);

        tvNomPdes1 = (TextView) findViewById(R.id.tvMiNomPdes1);
        tvNomPdes2 = (TextView) findViewById(R.id.tvMiNomPdes2);
        tvNomPdes3 = (TextView) findViewById(R.id.tvMiNomPdes3);
        tvNomPdes4 = (TextView) findViewById(R.id.tvMiNomPdes4);
        tvNomPdes5 = (TextView) findViewById(R.id.tvMiNomPdes5);

        tvPrePdes1 = (TextView) findViewById(R.id.tvMiPrePdes1);
        tvPrePdes2 = (TextView) findViewById(R.id.tvMiPrePdes2);
        tvPrePdes3 = (TextView) findViewById(R.id.tvMiPrePdes3);
        tvPrePdes4 = (TextView) findViewById(R.id.tvMiPrePdes4);
        tvPrePdes5 = (TextView) findViewById(R.id.tvMiPrePdes5);

        tvCantPdes1 = (TextView) findViewById(R.id.tvMiCantPdes1);
        tvCantPdes2 = (TextView) findViewById(R.id.tvMiCantPdes2);
        tvCantPdes3 = (TextView) findViewById(R.id.tvMiCantPdes3);
        tvCantPdes4 = (TextView) findViewById(R.id.tvMiCantPdes4);
        tvCantPdes5 = (TextView) findViewById(R.id.tvMiCantPdes5);

        tvSubPdes1 = (TextView) findViewById(R.id.tvMiSubPdes1);
        tvSubPdes2 = (TextView) findViewById(R.id.tvMiSubPdes2);
        tvSubPdes3 = (TextView) findViewById(R.id.tvMiSubPdes3);
        tvSubPdes4 = (TextView) findViewById(R.id.tvMiSubPdes4);
        tvSubPdes5 = (TextView) findViewById(R.id.tvMiSubPdes5);
    }

    public void limpiar(){
        instanciarTextos();
        tvNomP1.setVisibility(View.GONE);
        tvNomP2.setVisibility(View.GONE);
        tvNomP3.setVisibility(View.GONE);
        tvNomP4.setVisibility(View.GONE);
        tvNomP5.setVisibility(View.GONE);

        tvPreP1.setVisibility(View.GONE);
        tvPreP2.setVisibility(View.GONE);
        tvPreP3.setVisibility(View.GONE);
        tvPreP4.setVisibility(View.GONE);
        tvPreP5.setVisibility(View.GONE);

        tvCantP1.setVisibility(View.GONE);
        tvCantP2.setVisibility(View.GONE);
        tvCantP3.setVisibility(View.GONE);
        tvCantP4.setVisibility(View.GONE);
        tvCantP5.setVisibility(View.GONE);

        tvSubP1.setVisibility(View.GONE);
        tvSubP2.setVisibility(View.GONE);
        tvSubP3.setVisibility(View.GONE);
        tvSubP4.setVisibility(View.GONE);
        tvSubP5.setVisibility(View.GONE);

        tvNomPdes1.setVisibility(View.GONE);
        tvNomPdes2.setVisibility(View.GONE);
        tvNomPdes3.setVisibility(View.GONE);
        tvNomPdes4.setVisibility(View.GONE);
        tvNomPdes5.setVisibility(View.GONE);

        tvPrePdes1.setVisibility(View.GONE);
        tvPrePdes2.setVisibility(View.GONE);
        tvPrePdes3.setVisibility(View.GONE);
        tvPrePdes4.setVisibility(View.GONE);
        tvPrePdes5.setVisibility(View.GONE);

        tvCantPdes1.setVisibility(View.GONE);
        tvCantPdes2.setVisibility(View.GONE);
        tvCantPdes3.setVisibility(View.GONE);
        tvCantPdes4.setVisibility(View.GONE);
        tvCantPdes5.setVisibility(View.GONE);

        tvSubPdes1.setVisibility(View.GONE);
        tvSubPdes2.setVisibility(View.GONE);
        tvSubPdes3.setVisibility(View.GONE);
        tvSubPdes4.setVisibility(View.GONE);
        tvSubPdes5.setVisibility(View.GONE);

        imgP1.setVisibility(View.GONE);
        imgP2.setVisibility(View.GONE);
        imgP3.setVisibility(View.GONE);
        imgP4.setVisibility(View.GONE);
        imgP5.setVisibility(View.GONE);
    }

    public void productoUno(){
        imgP1.setVisibility(View.VISIBLE);

        tvNomP1.setVisibility(View.VISIBLE);
        tvPreP1.setVisibility(View.VISIBLE);
        tvCantP1.setVisibility(View.VISIBLE);
        tvSubP1.setVisibility(View.VISIBLE);

        tvNomPdes1.setVisibility(View.VISIBLE);
        tvPrePdes1.setVisibility(View.VISIBLE);
        tvCantPdes1.setVisibility(View.VISIBLE);
        tvSubPdes1.setVisibility(View.VISIBLE);
    }

    public void productoDos(){
        imgP2.setVisibility(View.VISIBLE);

        tvNomP2.setVisibility(View.VISIBLE);
        tvPreP2.setVisibility(View.VISIBLE);
        tvCantP2.setVisibility(View.VISIBLE);
        tvSubP2.setVisibility(View.VISIBLE);

        tvNomPdes2.setVisibility(View.VISIBLE);
        tvPrePdes2.setVisibility(View.VISIBLE);
        tvCantPdes2.setVisibility(View.VISIBLE);
        tvSubPdes2.setVisibility(View.VISIBLE);
    }

    public void productoTres(){
        imgP3.setVisibility(View.VISIBLE);

        tvNomP3.setVisibility(View.VISIBLE);
        tvPreP3.setVisibility(View.VISIBLE);
        tvCantP3.setVisibility(View.VISIBLE);
        tvSubP3.setVisibility(View.VISIBLE);

        tvNomPdes3.setVisibility(View.VISIBLE);
        tvPrePdes3.setVisibility(View.VISIBLE);
        tvCantPdes3.setVisibility(View.VISIBLE);
        tvSubPdes3.setVisibility(View.VISIBLE);
    }

    public void productoCuatro(){
        imgP4.setVisibility(View.VISIBLE);

        tvNomP4.setVisibility(View.VISIBLE);
        tvPreP4.setVisibility(View.VISIBLE);
        tvCantP4.setVisibility(View.VISIBLE);
        tvSubP4.setVisibility(View.VISIBLE);

        tvNomPdes4.setVisibility(View.VISIBLE);
        tvPrePdes4.setVisibility(View.VISIBLE);
        tvCantPdes4.setVisibility(View.VISIBLE);
        tvSubPdes4.setVisibility(View.VISIBLE);
    }

    public void productoCinco(){
        imgP5.setVisibility(View.VISIBLE);

        tvNomP5.setVisibility(View.VISIBLE);
        tvPreP5.setVisibility(View.VISIBLE);
        tvCantP5.setVisibility(View.VISIBLE);
        tvSubP5.setVisibility(View.VISIBLE);

        tvNomPdes5.setVisibility(View.VISIBLE);
        tvPrePdes5.setVisibility(View.VISIBLE);
        tvCantPdes5.setVisibility(View.VISIBLE);
        tvSubPdes5.setVisibility(View.VISIBLE);
    }
}