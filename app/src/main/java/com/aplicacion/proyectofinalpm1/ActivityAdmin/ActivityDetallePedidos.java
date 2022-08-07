package com.aplicacion.proyectofinalpm1.ActivityAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aplicacion.proyectofinalpm1.ActivityRepartidor.ActivityPedidosN;
import com.aplicacion.proyectofinalpm1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ActivityDetallePedidos extends AppCompatActivity {


    private final int cargaDatos = 400;

    String identificador, tipoPedido, estadoPedido;
    TextView tvIdPedidoR;

    String idUsuario;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    String NomProBebes = "", cantidadPañales = "", precioPañales = "";
    String NomProBebidas = "", cantidadBebidas = "", precioBebidas = "";
    String NomProCarnes = "", cantidadCarnes = "", precioCarnes = "";
    String NomProGranosB = "", cantidadGranosB = "", precioGranosB = "";
    String NomProLacteos = "", cantidadLacteos = "", precioLacteos = "";

    double subtotalBebes = 0, subtotalBebidas = 0, subtotalCarnes = 0, subtotalGranosB = 0, subtotalLacteos = 0, subtotal = 0;
    double impBebes = 0, impBebidas = 0, impCarnes = 0, impGranosB = 0, impLacteos = 0, impuesto = 0;
    double totalBebes = 0, totalBebidas = 0, totalCarnes = 0, totalGranosB = 0, totalLacteos = 0, total = 0;
    double precUBebes = 0, precUBebidas = 0, PrecUCarnes = 0, precGranosB = 0, precULacteos = 0;
    String idCliente, nombreCliente, apellidoCliente, telefonoCliente, direccionCliente, correoCliente;
    String comentarioDB, evaluacionDB;

    TextView tvRepNomP1, tvRepPreP1, tvRepCantP1, tvRepSubP1;
    TextView tvNomP2, tvPreP2, tvCantP2;
    TextView tvNomP3, tvPreP3, tvCantP3;
    TextView tvNomP4, tvPreP4, tvCantP4;
    TextView tvNomP5, tvPreP5, tvCantP5;
    TextView tvNomPdes1, tvNomPdes2, tvNomPdes3, tvNomPdes4, tvNomPdes5;
    TextView tvPrePdes1, tvPrePdes2, tvPrePdes3, tvPrePdes4, tvPrePdes5;
    TextView tvCantPdes1, tvCantPdes2, tvCantPdes3, tvCantPdes4, tvCantPdes5;
    TextView tvDetSubPdes1, tvDetSubPdes2, tvDetSubPdes3, tvDetSubPdes4, tvDetSubPdes5;
    TextView tvDetSubP1, tvDetSubP2, tvDetSubP3, tvDetSubP4, tvDetSubP5;
    TextView tvIdPedidoRNom, tvIdPedidoRTel, tvIdPedidoRDir;
    TextView tvRepSubT, tVRepImp, tVRepTotal;
    TextView tvEstadoPRep, tvDetalleComenA, tvDetalleEvaA, tvDetallePedA, tvDeCom;

    ImageView imgRepP1, imgP2, imgP3, imgP4, imgP5;

    int tip = 0;
    String contEva = "";
    TextView tvDetalleRecA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedidos);

        //Recibe parametros
        Intent intent = getIntent();
        identificador = (intent.getStringExtra("identificador"));
        tipoPedido = (intent.getStringExtra("tipoPedido"));
        estadoPedido = (intent.getStringExtra("estadoPedido"));
        contEva = (intent.getStringExtra("tip"));
        tvIdPedidoR = (TextView) findViewById(R.id.tvIdPedidoR);
        tvIdPedidoR.setText("Id del Pedido: " + identificador);

        tvDetallePedA = (TextView) findViewById(R.id.tvDetallePedA);
        tvDetallePedA.setText("Estado del pedido: " + estadoPedido);

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

        tvDetalleEvaA.setVisibility(View.GONE);
        tvDeCom.setVisibility(View.GONE);
        tvDetalleComenA.setVisibility(View.GONE);

        tvDetalleRecA = (TextView) findViewById(R.id.tvDetalleRecA);
        tvDetalleRecA.setText(contEva);
        if (!tvDetalleRecA.getText().toString().isEmpty()){
            infoPedido();
        }
        tvDetalleRecA.setVisibility(View.GONE);

        //Carga de Datos
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                impuesto = impBebes + impBebidas + impCarnes + impGranosB + impLacteos;
                subtotal = subtotalBebes + subtotalBebidas + subtotalCarnes + subtotalGranosB + subtotalLacteos;
                total = totalBebes + totalBebidas + totalCarnes + totalGranosB + totalLacteos;

                tvRepSubT.setText(subtotal + " Lps");
                tVRepImp.setText(impuesto + " Lps");
                tVRepTotal.setText(total + " Lps");
            }
        },cargaDatos);
        //Termina carga de Datos
    }

    public void infoPedido(){
        mDatabase.child("pedidos").child(tipoPedido).child(identificador).child("infoPedido").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    comentarioDB = dataSnapshot.child("comentario").getValue().toString();
                    evaluacionDB = dataSnapshot.child("evaluacion").getValue().toString();

                    tvDetalleEvaA.setText("Cantidad de Estrellas: " + evaluacionDB);
                    tvDetalleComenA.setText(comentarioDB);

                    tvDetalleEvaA.setVisibility(View.VISIBLE);
                    tvDeCom.setVisibility(View.VISIBLE);
                    tvDetalleComenA.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void infoUsuario(){
        mDatabase.child("pedidos").child(tipoPedido).child(identificador).child("infoCliente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    idCliente = dataSnapshot.child("idCliente").getValue().toString();
                    nombreCliente = dataSnapshot.child("nomCliente").getValue().toString();
                    apellidoCliente = dataSnapshot.child("apeCliente").getValue().toString();
                    telefonoCliente = dataSnapshot.child("telCliente").getValue().toString();
                    direccionCliente = dataSnapshot.child("dirCliente").getValue().toString();
                    correoCliente = dataSnapshot.child("corCliente").getValue().toString();

                    tvIdPedidoRNom.setText("Cliente:            " + nombreCliente + " " + apellidoCliente);
                    tvIdPedidoRTel.setText("Teléfono:         " + telefonoCliente);
                    tvIdPedidoRDir.setText("Dirección:       " + direccionCliente);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void catBebidas(){
        mDatabase.child("pedidos").child(tipoPedido).child(identificador).child("catBebidas").addValueEventListener(new ValueEventListener() {
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

                    if (tvRepNomP1.getText().toString().isEmpty()) {
                        tvRepNomP1.setText(NomProBebidas);
                        tvRepPreP1.setText(precUBebidas + " Lps");
                        tvRepCantP1.setText(cantidadBebidas);
                        tvDetSubP1.setText(precioBebidas + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgRepP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProBebidas);
                        tvPreP2.setText(precUBebidas + " Lps");
                        tvCantP2.setText(cantidadBebidas);
                        tvDetSubP2.setText(precioBebidas + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProBebidas);
                        tvPreP3.setText(precUBebidas + " Lps");
                        tvCantP3.setText(cantidadBebidas);
                        tvDetSubP3.setText(precioBebidas + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProBebidas);
                        tvPreP4.setText(precUBebidas + " Lps");
                        tvCantP4.setText(cantidadBebidas);
                        tvDetSubP4.setText(precioBebidas + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProBebidas);
                        tvPreP5.setText(precUBebidas + " Lps");
                        tvCantP5.setText(cantidadBebidas);
                        tvDetSubP5.setText(precioBebidas + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP5);
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
        mDatabase.child("pedidos").child(tipoPedido).child(identificador).child("catBebes").addValueEventListener(new ValueEventListener() {
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

                    if (tvRepNomP1.getText().toString().isEmpty()) {
                        tvRepNomP1.setText(NomProBebes);
                        tvRepPreP1.setText(precUPañales + " Lps");
                        tvRepCantP1.setText(cantidadPañales);
                        tvDetSubP1.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgRepP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProBebes);
                        tvPreP2.setText(precUPañales + " Lps");
                        tvCantP2.setText(cantidadPañales);
                        tvDetSubP2.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProBebes);
                        tvPreP3.setText(precUPañales + " Lps");
                        tvCantP3.setText(cantidadPañales);
                        tvDetSubP3.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProBebes);
                        tvPreP4.setText(precUPañales + " Lps");
                        tvCantP4.setText(cantidadPañales);
                        tvDetSubP4.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProBebes);
                        tvPreP5.setText(precUPañales + " Lps");
                        tvCantP5.setText(cantidadPañales);
                        tvDetSubP5.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP5);
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
        mDatabase.child("pedidos").child(tipoPedido).child(identificador).child("catCarnes").addValueEventListener(new ValueEventListener() {
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

                    if (tvRepNomP1.getText().toString().isEmpty()) {
                        tvRepNomP1.setText(NomProCarnes);
                        tvRepPreP1.setText(precUCarnes + " Lps");
                        tvRepCantP1.setText(cantidadCarnes);
                        tvDetSubP1.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgRepP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProCarnes);
                        tvPreP2.setText(precUCarnes + " Lps");
                        tvCantP2.setText(cantidadCarnes);
                        tvDetSubP2.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProCarnes);
                        tvPreP3.setText(precUCarnes + " Lps");
                        tvCantP3.setText(cantidadCarnes);
                        tvDetSubP3.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProCarnes);
                        tvPreP4.setText(precUCarnes + " Lps");
                        tvCantP4.setText(cantidadCarnes);
                        tvDetSubP4.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProCarnes);
                        tvPreP5.setText(precUCarnes + " Lps");
                        tvCantP5.setText(cantidadCarnes);
                        tvDetSubP5.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP5);
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
        mDatabase.child("pedidos").child(tipoPedido).child(identificador).child("catGranosB").addValueEventListener(new ValueEventListener() {
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

                    if (tvRepNomP1.getText().toString().isEmpty()) {
                        tvRepNomP1.setText(NomProGranosB);
                        tvRepPreP1.setText(precUGranosB + " Lps");
                        tvRepCantP1.setText(cantidadGranosB);
                        tvDetSubP1.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgRepP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProGranosB);
                        tvPreP2.setText(precUGranosB + " Lps");
                        tvCantP2.setText(cantidadGranosB);
                        tvDetSubP2.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProGranosB);
                        tvPreP3.setText(precUGranosB + " Lps");
                        tvCantP3.setText(cantidadGranosB);
                        tvDetSubP3.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProGranosB);
                        tvPreP4.setText(precUGranosB + " Lps");
                        tvCantP4.setText(cantidadGranosB);
                        tvDetSubP4.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProGranosB);
                        tvPreP5.setText(precUGranosB + " Lps");
                        tvCantP5.setText(cantidadGranosB);
                        tvDetSubP5.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP5);
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
        mDatabase.child("pedidos").child(tipoPedido).child(identificador).child("catLacteos").addValueEventListener(new ValueEventListener() {
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

                    if (tvRepNomP1.getText().toString().isEmpty()) {
                        tvRepNomP1.setText(NomProLacteos);
                        tvRepPreP1.setText(precULacteos + " Lps");
                        tvRepCantP1.setText(cantidadLacteos);
                        tvDetSubP1.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgRepP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProLacteos);
                        tvPreP2.setText(precULacteos + " Lps");
                        tvCantP2.setText(cantidadLacteos);
                        tvDetSubP2.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProLacteos);
                        tvPreP3.setText(precULacteos + " Lps");
                        tvCantP3.setText(cantidadLacteos);
                        tvDetSubP3.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProLacteos);
                        tvPreP4.setText(precULacteos + " Lps");
                        tvCantP4.setText(cantidadLacteos);
                        tvDetSubP4.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProLacteos);
                        tvPreP5.setText(precULacteos + " Lps");
                        tvCantP5.setText(cantidadLacteos);
                        tvDetSubP5.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityDetallePedidos.this).load(imagenURL).into(imgP5);
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
        tvDetalleEvaA = (TextView) findViewById(R.id.tvDetalleEvaA);
        tvDetalleComenA = (TextView) findViewById(R.id.tvDetalleComenA);

        tvDeCom = (TextView) findViewById(R.id.tvDeCom);

        tvRepSubT = (TextView) findViewById(R.id.tvDetSubT);
        tVRepImp = (TextView) findViewById(R.id.tVDetImp);
        tVRepTotal = (TextView) findViewById(R.id.tVDetTotal);

        tvRepNomP1 = (TextView) findViewById(R.id.tvDetNomP1);
        tvNomP2 = (TextView) findViewById(R.id.tvDetNomP2);
        tvNomP3 = (TextView) findViewById(R.id.tvDetNomP3);
        tvNomP4 = (TextView) findViewById(R.id.tvDetNomP4);
        tvNomP5 = (TextView) findViewById(R.id.tvDetNomP5);

        tvRepPreP1 = (TextView) findViewById(R.id.tvDetPreP1);
        tvPreP2 = (TextView) findViewById(R.id.tvDetPreP2);
        tvPreP3 = (TextView) findViewById(R.id.tvDetPreP3);
        tvPreP4 = (TextView) findViewById(R.id.tvDetPreP4);
        tvPreP5 = (TextView) findViewById(R.id.tvDetPreP5);

        tvRepCantP1 = (TextView) findViewById(R.id.tvDetCantP1);
        tvCantP2 = (TextView) findViewById(R.id.tvDetCantP2);
        tvCantP3 = (TextView) findViewById(R.id.tvDetCantP3);
        tvCantP4 = (TextView) findViewById(R.id.tvDetCantP4);
        tvCantP5 = (TextView) findViewById(R.id.tvDetCantP5);

        tvDetSubP1 = (TextView) findViewById(R.id.tvDetSubP1);
        tvDetSubP2 = (TextView) findViewById(R.id.tvDetSubP2);
        tvDetSubP3 = (TextView) findViewById(R.id.tvDetSubP3);
        tvDetSubP4 = (TextView) findViewById(R.id.tvDetSubP4);
        tvDetSubP5 = (TextView) findViewById(R.id.tvDetSubP5);

        //Instancia de Imagenes (Productos)
        imgRepP1 = (ImageView) findViewById(R.id.imgDetP1);
        imgP2 = (ImageView) findViewById(R.id.imgDetP2);
        imgP3 = (ImageView) findViewById(R.id.imgDetP3);
        imgP4 = (ImageView) findViewById(R.id.imgDetP4);
        imgP5 = (ImageView) findViewById(R.id.imgDetP5);

        tvNomPdes1 = (TextView) findViewById(R.id.tvDetNomPdes1);
        tvNomPdes2 = (TextView) findViewById(R.id.tvDetNomPdes2);
        tvNomPdes3 = (TextView) findViewById(R.id.tvDetNomPdes3);
        tvNomPdes4 = (TextView) findViewById(R.id.tvDetNomPdes4);
        tvNomPdes5 = (TextView) findViewById(R.id.tvDetNomPdes5);

        tvPrePdes1 = (TextView) findViewById(R.id.tvDetPrePdes1);
        tvPrePdes2 = (TextView) findViewById(R.id.tvDetPrePdes2);
        tvPrePdes3 = (TextView) findViewById(R.id.tvDetPrePdes3);
        tvPrePdes4 = (TextView) findViewById(R.id.tvDetPrePdes4);
        tvPrePdes5 = (TextView) findViewById(R.id.tvDetPrePdes5);

        tvCantPdes1 = (TextView) findViewById(R.id.tvDetCantPdes1);
        tvCantPdes2 = (TextView) findViewById(R.id.tvDetCantPdes2);
        tvCantPdes3 = (TextView) findViewById(R.id.tvDetCantPdes3);
        tvCantPdes4 = (TextView) findViewById(R.id.tvDetCantPdes4);
        tvCantPdes5 = (TextView) findViewById(R.id.tvDetCantPdes5);

        tvDetSubPdes1 = (TextView) findViewById(R.id.tvDetSubPdes1);
        tvDetSubPdes2 = (TextView) findViewById(R.id.tvDetSubPdes2);
        tvDetSubPdes3 = (TextView) findViewById(R.id.tvDetSubPdes3);
        tvDetSubPdes4 = (TextView) findViewById(R.id.tvDetSubPdes4);
        tvDetSubPdes5 = (TextView) findViewById(R.id.tvDetSubPdes5);

        tvIdPedidoRNom = (TextView) findViewById(R.id.tvIdPedidoRNom);
        tvIdPedidoRTel = (TextView) findViewById(R.id.tvIdPedidoRTel);
        tvIdPedidoRDir = (TextView) findViewById(R.id.tvIdPedidoRDir);
    }

    public void limpiar(){
        tvRepNomP1.setVisibility(View.GONE);
        tvNomP2.setVisibility(View.GONE);
        tvNomP3.setVisibility(View.GONE);
        tvNomP4.setVisibility(View.GONE);
        tvNomP5.setVisibility(View.GONE);

        tvRepPreP1.setVisibility(View.GONE);
        tvPreP2.setVisibility(View.GONE);
        tvPreP3.setVisibility(View.GONE);
        tvPreP4.setVisibility(View.GONE);
        tvPreP5.setVisibility(View.GONE);

        tvRepCantP1.setVisibility(View.GONE);
        tvCantP2.setVisibility(View.GONE);
        tvCantP3.setVisibility(View.GONE);
        tvCantP4.setVisibility(View.GONE);
        tvCantP5.setVisibility(View.GONE);

        tvDetSubP1.setVisibility(View.GONE);
        tvDetSubP2.setVisibility(View.GONE);
        tvDetSubP3.setVisibility(View.GONE);
        tvDetSubP4.setVisibility(View.GONE);
        tvDetSubP5.setVisibility(View.GONE);

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

        tvDetSubPdes1.setVisibility(View.GONE);
        tvDetSubPdes2.setVisibility(View.GONE);
        tvDetSubPdes3.setVisibility(View.GONE);
        tvDetSubPdes4.setVisibility(View.GONE);
        tvDetSubPdes5.setVisibility(View.GONE);

        imgRepP1.setVisibility(View.GONE);
        imgP2.setVisibility(View.GONE);
        imgP3.setVisibility(View.GONE);
        imgP4.setVisibility(View.GONE);
        imgP5.setVisibility(View.GONE);
    }

    public void productoUno(){
        imgRepP1.setVisibility(View.VISIBLE);
        tvRepNomP1.setVisibility(View.VISIBLE);
        tvRepPreP1.setVisibility(View.VISIBLE);
        tvRepCantP1.setVisibility(View.VISIBLE);
        tvDetSubP1.setVisibility(View.VISIBLE);

        tvNomPdes1.setVisibility(View.VISIBLE);
        tvPrePdes1.setVisibility(View.VISIBLE);
        tvCantPdes1.setVisibility(View.VISIBLE);
        tvDetSubPdes1.setVisibility(View.VISIBLE);
    }

    public void productoDos(){
        imgP2.setVisibility(View.VISIBLE);

        tvNomP2.setVisibility(View.VISIBLE);
        tvPreP2.setVisibility(View.VISIBLE);
        tvCantP2.setVisibility(View.VISIBLE);
        tvDetSubP2.setVisibility(View.VISIBLE);
        tvDetSubP2.setVisibility(View.VISIBLE);

        tvNomPdes2.setVisibility(View.VISIBLE);
        tvPrePdes2.setVisibility(View.VISIBLE);
        tvCantPdes2.setVisibility(View.VISIBLE);
        tvDetSubPdes2.setVisibility(View.VISIBLE);
        tvDetSubPdes2.setVisibility(View.VISIBLE);
    }

    public void productoTres(){
        imgP3.setVisibility(View.VISIBLE);

        tvNomP3.setVisibility(View.VISIBLE);
        tvPreP3.setVisibility(View.VISIBLE);
        tvCantP3.setVisibility(View.VISIBLE);
        tvDetSubP3.setVisibility(View.VISIBLE);

        tvNomPdes3.setVisibility(View.VISIBLE);
        tvPrePdes3.setVisibility(View.VISIBLE);
        tvCantPdes3.setVisibility(View.VISIBLE);
        tvDetSubPdes3.setVisibility(View.VISIBLE);
    }

    public void productoCuatro(){
        imgP4.setVisibility(View.VISIBLE);

        tvNomP4.setVisibility(View.VISIBLE);
        tvPreP4.setVisibility(View.VISIBLE);
        tvCantP4.setVisibility(View.VISIBLE);
        tvDetSubP4.setVisibility(View.VISIBLE);

        tvNomPdes4.setVisibility(View.VISIBLE);
        tvPrePdes4.setVisibility(View.VISIBLE);
        tvCantPdes4.setVisibility(View.VISIBLE);
        tvDetSubPdes4.setVisibility(View.VISIBLE);
    }

    public void productoCinco(){
        imgP5.setVisibility(View.VISIBLE);

        tvNomP5.setVisibility(View.VISIBLE);
        tvPreP5.setVisibility(View.VISIBLE);
        tvCantP5.setVisibility(View.VISIBLE);
        tvDetSubP5.setVisibility(View.VISIBLE);

        tvNomPdes5.setVisibility(View.VISIBLE);
        tvPrePdes5.setVisibility(View.VISIBLE);
        tvCantPdes5.setVisibility(View.VISIBLE);
        tvDetSubPdes5.setVisibility(View.VISIBLE);
    }

    public void cerrarPedido(){
        if (!NomProBebes.isEmpty()) {
            Map<String, Object> pBebes = new HashMap<>();
            pBebes.put("NomProBebes", NomProBebes);
            pBebes.put("cantidadPañales", cantidadPañales);
            pBebes.put("precioPañales", precioPañales);
            pBebes.put("precUPañales", "120.00");
            pBebes.put("imgUrlBebes", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2Fpaniales.png?alt=media&token=cd9f01a7-1159-49d5-9f2c-5a791793e0c6");
            mDatabase.child("pedidos").child("cerrados").child(identificador).child("catBebes").setValue(pBebes);
        }

        if (!NomProBebidas.isEmpty()) {
            Map<String, Object> pBebidas = new HashMap<>();
            pBebidas.put("NomProBebidas", NomProBebidas);
            pBebidas.put("cantidadBebidas", cantidadBebidas);
            pBebidas.put("precioBebidas", precioBebidas);
            pBebidas.put("precUBebidas", "150.00");
            pBebidas.put("imgUrlBebidas", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2FbebidaCoronaC.png?alt=media&token=6805fb9f-3bdd-4541-90c7-95e8a9dcec20");
            mDatabase.child("pedidos").child("cerrados").child(identificador).child("catBebidas").setValue(pBebidas);
        }

        if(!NomProCarnes.isEmpty()) {
            Map<String, Object> pCarnes = new HashMap<>();
            pCarnes.put("NomProCarnes", NomProCarnes);
            pCarnes.put("cantidadCarnes", cantidadCarnes);
            pCarnes.put("precioCarnes", precioCarnes);
            pCarnes.put("precUCarnes", "100.00");
            pCarnes.put("imgUrlCarnes", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2Fchuleta.jpg?alt=media&token=36715cc1-23ee-43b6-92ab-a6d5a2a258d4");
            mDatabase.child("pedidos").child("cerrados").child(identificador).child("catCarnes").setValue(pCarnes);
        }

        if(!NomProGranosB.isEmpty()) {
            Map<String, Object> pGranosB = new HashMap<>();
            pGranosB.put("NomProGranosB", NomProGranosB);
            pGranosB.put("cantidadGranosB", cantidadGranosB);
            pGranosB.put("precioGranosB", precioGranosB);
            pGranosB.put("precUGranosB", "14.00");
            pGranosB.put("imgUrlGranosB", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2Farroz.jpg?alt=media&token=8e691cc4-1b32-4cd8-bb6f-7e07e07f6831");
            mDatabase.child("pedidos").child("cerrados").child(identificador).child("catGranosB").setValue(pGranosB);
        }

        if (!NomProLacteos.isEmpty()) {
            Map<String, Object> pLacteos = new HashMap<>();
            pLacteos.put("NomProLacteos", NomProLacteos);
            pLacteos.put("cantidadLacteos", cantidadLacteos);
            pLacteos.put("precioLacteos", precioLacteos);
            pLacteos.put("precULacteos", "30.00");
            pLacteos.put("imgUrlLacteos", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2FlecheCeteco.png?alt=media&token=7bc5a649-956e-42e7-b00b-adf651115661");
            mDatabase.child("pedidos").child("cerrados").child(identificador).child("catLacteos").setValue(pLacteos);
        }

        Map<String, Object> pPedidoInfo = new HashMap<>();
        pPedidoInfo.put("subtotal", subtotal);
        pPedidoInfo.put("impuesto", impuesto);
        pPedidoInfo.put("total", total);
        mDatabase.child("pedidos").child("cerrados").child(identificador).child("infoPedido").setValue(pPedidoInfo);

        Map<String, Object> pClienteInfo = new HashMap<>();
        pClienteInfo.put("idCliente", idCliente);
        pClienteInfo.put("nomCliente", nombreCliente);
        pClienteInfo.put("apeCliente", apellidoCliente);
        pClienteInfo.put("telCliente", telefonoCliente);
        pClienteInfo.put("dirCliente", direccionCliente);
        pClienteInfo.put("corCliente", correoCliente);
        mDatabase.child("pedidos").child("cerrados").child(identificador).child("infoCliente").setValue(pClienteInfo);

        controlPedidos();
        eliminarPedido();

        Toast.makeText(ActivityDetallePedidos.this, "Pedido Cerrado", Toast.LENGTH_LONG).show();
    }

    private void controlPedidos() {
        Map<String, Object> Regpedido = new HashMap<>();
        Regpedido.put(identificador, identificador);
        mDatabase.child("pedidos").child("registroCer").updateChildren(Regpedido);

        Map<String, Object> RegConpedido = new HashMap<>();
        RegConpedido.put(identificador, identificador);
        mDatabase.child("pedidos").child("registroCli").child(identificador).updateChildren(RegConpedido);
    }

    public void eliminarPedido(){
        mDatabase.child("pedidos").child("nuevos").child(identificador).removeValue();
        mDatabase.child("pedidos").child("registroNue").child(identificador).removeValue();
    }
}