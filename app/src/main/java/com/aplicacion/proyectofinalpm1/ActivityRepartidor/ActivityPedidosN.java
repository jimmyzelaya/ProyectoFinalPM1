package com.aplicacion.proyectofinalpm1.ActivityRepartidor;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aplicacion.proyectofinalpm1.ActivityClientes.ActivityCarritoCompras;
import com.aplicacion.proyectofinalpm1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityPedidosN extends AppCompatActivity {

    private final int cargaDatos = 400;

    String identificador;
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
    String impuestoDB, subtotalDB, totalDB, latitud, longitud;

    TextView tvRepNomP1, tvRepPreP1, tvRepCantP1, tvRepSubP1;
    TextView tvNomP2, tvPreP2, tvCantP2;
    TextView tvNomP3, tvPreP3, tvCantP3;
    TextView tvNomP4, tvPreP4, tvCantP4;
    TextView tvNomP5, tvPreP5, tvCantP5;
    TextView tvNomPdes1, tvNomPdes2, tvNomPdes3, tvNomPdes4, tvNomPdes5;
    TextView tvPrePdes1, tvPrePdes2, tvPrePdes3, tvPrePdes4, tvPrePdes5;
    TextView tvCantPdes1, tvCantPdes2, tvCantPdes3, tvCantPdes4, tvCantPdes5;
    TextView tvIdPedidoRNom, tvIdPedidoRTel, tvIdPedidoRDir;
    TextView tvRepSubT, tVRepImp, tVRepTotal;
    TextView tvEstadoPRep;

    ImageView imgRepP1, imgP2, imgP3, imgP4, imgP5;

    Button btnCerrarPRep, btnMostrarUbi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_n);

        Intent intent = getIntent();
        identificador = (intent.getStringExtra("identificador"));
        tvIdPedidoR = (TextView) findViewById(R.id.tvIdPedidoR);
        tvIdPedidoR.setText("Id del Pedido: " + identificador);

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

            }
        },cargaDatos);
        //Termina carga de Datos

        //SUSCRIBIR A UNA PERSONA AL TOPICO
        FirebaseMessaging.getInstance().subscribeToTopic("enviaratodos").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ActivityPedidosN.this,"Suscrito al enviar a todos!",Toast.LENGTH_SHORT).show();

            }
        });

        btnCerrarPRep = (Button) findViewById(R.id.btnCerrarPRep);
        btnCerrarPRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvEstadoPRep = (TextView) findViewById(R.id.tvEstadoPRep);
                tvEstadoPRep.setText("Estado del Pedido: Entregado");
                cerrarPedido();
                llamaratopico("Compra Cerrada","Su pedido a sido entregado","enviaratodos");
            }
        });

        //mostrar ubicación dl cliene
        btnMostrarUbi = (Button) findViewById(R.id.btnMostrarUbi);
        btnMostrarUbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                startActivity(intent);
            }
        });
    }

    // ESTE ENVIARIA UN MENSAJE PUSH A TODOS LOS USUARIOS QUE TENGAN INSTALADOS LA APLICACION
    // AQUI SOLAMENTE SE TIENE  QUE SUSCRIBIR A LOS USUARIOS EN UN TOPICO ASI UNA VEZ QUE ESLLOS SE SUSCRIBAN SE LE ENVIA LA NOTIFICACION A TODOS LOS USUARIOS
    private void llamaratopico(String titulo,String mensaje, String topico) {
        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json =new JSONObject();

        try {
            //String token ="dOkBWZjISe-B_TFO_5Uixl:APA91bGAOtVp48blQksEY7uP54zQaoLFUrOFaPYsSWxup1jGMjrU9I8FZkngImYqcuLPEdZ1ZVnbTep-v6Aj5NngWejK29RwotqTHoxbv2fw8240zBcYonx40zCbp5HbrJME7hYTypFM";

            json.put("to","/topics/"+topico);
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo ",titulo);
            notificacion.put("detalle",mensaje);
            json.put("data",notificacion);

            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,json,null,null){
                @Override
                public Map<String, String> getHeaders()  {
                    Map<String,String> header = new HashMap<>();

                    header.put("content-type", "application/json");
                    header.put("authorization","key=AAAAh3G9j9U:APA91bEy8uCRppmLZ4EUhfe8tbLb4NwQ__l_DpJxcrYDe8pOQddHSvXocXgHYc8-2emAeR6G6ei2TjHjC8x6t2WG0yEM2rBKLa95K7dKhkhD9kJOVrCXrLj9Zrjzi-O66CR91lRlRJBw");

                    return header;
                }
            };
            myrequest.add(request);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    public void infoPedido(){
        mDatabase.child("pedidos").child("nuevos").child(identificador).child("infoPedido").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    subtotalDB = dataSnapshot.child("subtotal").getValue().toString();
                    impuestoDB = dataSnapshot.child("impuesto").getValue().toString();
                    totalDB = dataSnapshot.child("total").getValue().toString();

                    latitud = dataSnapshot.child("latitud").getValue().toString();
                    longitud = dataSnapshot.child("longitud").getValue().toString();

                    tvRepSubT.setText(subtotalDB + ".00 Lps");
                    tVRepImp.setText(impuestoDB + ".00 Lps");
                    tVRepTotal.setText(totalDB + ".00 Lps");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

                    if (tvRepNomP1.getText().toString().isEmpty()) {
                        tvRepNomP1.setText(NomProBebidas);
                        tvRepPreP1.setText(precUBebidas + " Lps");
                        tvRepCantP1.setText(cantidadBebidas);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgRepP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProBebidas);
                        tvPreP2.setText(precUBebidas + " Lps");
                        tvCantP2.setText(cantidadBebidas);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProBebidas);
                        tvPreP3.setText(precUBebidas + " Lps");
                        tvCantP3.setText(cantidadBebidas);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProBebidas);
                        tvPreP4.setText(precUBebidas + " Lps");
                        tvCantP4.setText(cantidadBebidas);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProBebidas);
                        tvPreP5.setText(precUBebidas + " Lps");
                        tvCantP5.setText(cantidadBebidas);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP5);
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

                    if (tvRepNomP1.getText().toString().isEmpty()) {
                        tvRepNomP1.setText(NomProBebes);
                        tvRepPreP1.setText(precUPañales + " Lps");
                        tvRepCantP1.setText(cantidadPañales);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgRepP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProBebes);
                        tvPreP2.setText(precUPañales + " Lps");
                        tvCantP2.setText(cantidadPañales);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProBebes);
                        tvPreP3.setText(precUPañales + " Lps");
                        tvCantP3.setText(cantidadPañales);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProBebes);
                        tvPreP4.setText(precUPañales + " Lps");
                        tvCantP4.setText(cantidadPañales);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProBebes);
                        tvPreP5.setText(precUPañales + " Lps");
                        tvCantP5.setText(cantidadPañales);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP5);
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

                    if (tvRepNomP1.getText().toString().isEmpty()) {
                        tvRepNomP1.setText(NomProCarnes);
                        tvRepPreP1.setText(precUCarnes + " Lps");
                        tvRepCantP1.setText(cantidadCarnes);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgRepP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProCarnes);
                        tvPreP2.setText(precUCarnes + " Lps");
                        tvCantP2.setText(cantidadCarnes);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProCarnes);
                        tvPreP3.setText(precUCarnes + " Lps");
                        tvCantP3.setText(cantidadCarnes);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProCarnes);
                        tvPreP4.setText(precUCarnes + " Lps");
                        tvCantP4.setText(cantidadCarnes);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProCarnes);
                        tvPreP5.setText(precUCarnes + " Lps");
                        tvCantP5.setText(cantidadCarnes);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP5);
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

                    if (tvRepNomP1.getText().toString().isEmpty()) {
                        tvRepNomP1.setText(NomProGranosB);
                        tvRepPreP1.setText(precUGranosB + " Lps");
                        tvRepCantP1.setText(cantidadGranosB);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgRepP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProGranosB);
                        tvPreP2.setText(precUGranosB + " Lps");
                        tvCantP2.setText(cantidadGranosB);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProGranosB);
                        tvPreP3.setText(precUGranosB + " Lps");
                        tvCantP3.setText(cantidadGranosB);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProGranosB);
                        tvPreP4.setText(precUGranosB + " Lps");
                        tvCantP4.setText(cantidadGranosB);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProGranosB);
                        tvPreP5.setText(precUGranosB + " Lps");
                        tvCantP5.setText(cantidadGranosB);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP5);
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

                    if (tvRepNomP1.getText().toString().isEmpty()) {
                        tvRepNomP1.setText(NomProLacteos);
                        tvRepPreP1.setText(precULacteos + " Lps");
                        tvRepCantP1.setText(cantidadLacteos);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgRepP1);
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProLacteos);
                        tvPreP2.setText(precULacteos + " Lps");
                        tvCantP2.setText(cantidadLacteos);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP2);
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProLacteos);
                        tvPreP3.setText(precULacteos + " Lps");
                        tvCantP3.setText(cantidadLacteos);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP3);
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProLacteos);
                        tvPreP4.setText(precULacteos + " Lps");
                        tvCantP4.setText(cantidadLacteos);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP4);
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProLacteos);
                        tvPreP5.setText(precULacteos + " Lps");
                        tvCantP5.setText(cantidadLacteos);
                        Picasso.with(ActivityPedidosN.this).load(imagenURL).into(imgP5);
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
        tvRepSubT = (TextView) findViewById(R.id.tvRepSubT);
        tVRepImp = (TextView) findViewById(R.id.tVRepImp);
        tVRepTotal = (TextView) findViewById(R.id.tVRepTotal);

        tvRepNomP1 = (TextView) findViewById(R.id.tvRepNomP1);
        tvNomP2 = (TextView) findViewById(R.id.tvRepNomP2);
        tvNomP3 = (TextView) findViewById(R.id.tvRepNomP3);
        tvNomP4 = (TextView) findViewById(R.id.tvRepNomP4);
        tvNomP5 = (TextView) findViewById(R.id.tvRepNomP5);

        tvRepPreP1 = (TextView) findViewById(R.id.tvRepPreP1);
        tvPreP2 = (TextView) findViewById(R.id.tvRepPreP2);
        tvPreP3 = (TextView) findViewById(R.id.tvRepPreP3);
        tvPreP4 = (TextView) findViewById(R.id.tvRepPreP4);
        tvPreP5 = (TextView) findViewById(R.id.tvRepPreP5);

        tvRepCantP1 = (TextView) findViewById(R.id.tvRepCantP1);
        tvCantP2 = (TextView) findViewById(R.id.tvRepCantP2);
        tvCantP3 = (TextView) findViewById(R.id.tvRepCantP3);
        tvCantP4 = (TextView) findViewById(R.id.tvRepCantP4);
        tvCantP5 = (TextView) findViewById(R.id.tvRepCantP5);

        //Instancia de Imagenes (Productos)
        imgRepP1 = (ImageView) findViewById(R.id.imgRepP1);
        imgP2 = (ImageView) findViewById(R.id.imgRepP2);
        imgP3 = (ImageView) findViewById(R.id.imgRepP3);
        imgP4 = (ImageView) findViewById(R.id.imgRepP4);
        imgP5 = (ImageView) findViewById(R.id.imgRepP5);

        tvNomPdes1 = (TextView) findViewById(R.id.tvNomRepPdes1);
        tvNomPdes2 = (TextView) findViewById(R.id.tvRepNomPdes2);
        tvNomPdes3 = (TextView) findViewById(R.id.tvRepNomPdes3);
        tvNomPdes4 = (TextView) findViewById(R.id.tvRepNomPdes4);
        tvNomPdes5 = (TextView) findViewById(R.id.tvRepNomPdes5);

        tvPrePdes1 = (TextView) findViewById(R.id.tvRepPrePdes1);
        tvPrePdes2 = (TextView) findViewById(R.id.tvRepPrePdes2);
        tvPrePdes3 = (TextView) findViewById(R.id.tvRepPrePdes3);
        tvPrePdes4 = (TextView) findViewById(R.id.tvRepPrePdes4);
        tvPrePdes5 = (TextView) findViewById(R.id.tvRepPrePdes5);

        tvCantPdes1 = (TextView) findViewById(R.id.tvRepCantPdes1);
        tvCantPdes2 = (TextView) findViewById(R.id.tvRepCantPdes2);
        tvCantPdes3 = (TextView) findViewById(R.id.tvRepCantPdes3);
        tvCantPdes4 = (TextView) findViewById(R.id.tvRepCantPdes4);
        tvCantPdes5 = (TextView) findViewById(R.id.tvRepCantPdes5);

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

        tvNomPdes1.setVisibility(View.VISIBLE);
        tvPrePdes1.setVisibility(View.VISIBLE);
        tvCantPdes1.setVisibility(View.VISIBLE);
    }

    public void productoDos(){
        imgP2.setVisibility(View.VISIBLE);

        tvNomP2.setVisibility(View.VISIBLE);
        tvPreP2.setVisibility(View.VISIBLE);
        tvCantP2.setVisibility(View.VISIBLE);

        tvNomPdes2.setVisibility(View.VISIBLE);
        tvPrePdes2.setVisibility(View.VISIBLE);
        tvCantPdes2.setVisibility(View.VISIBLE);
    }

    public void productoTres(){
        imgP3.setVisibility(View.VISIBLE);

        tvNomP3.setVisibility(View.VISIBLE);
        tvPreP3.setVisibility(View.VISIBLE);
        tvCantP3.setVisibility(View.VISIBLE);

        tvNomPdes3.setVisibility(View.VISIBLE);
        tvPrePdes3.setVisibility(View.VISIBLE);
        tvCantPdes3.setVisibility(View.VISIBLE);
    }

    public void productoCuatro(){
        imgP4.setVisibility(View.VISIBLE);

        tvNomP4.setVisibility(View.VISIBLE);
        tvPreP4.setVisibility(View.VISIBLE);
        tvCantP4.setVisibility(View.VISIBLE);

        tvNomPdes4.setVisibility(View.VISIBLE);
        tvPrePdes4.setVisibility(View.VISIBLE);
        tvCantPdes4.setVisibility(View.VISIBLE);
    }

    public void productoCinco(){
        imgP5.setVisibility(View.VISIBLE);

        tvNomP5.setVisibility(View.VISIBLE);
        tvPreP5.setVisibility(View.VISIBLE);
        tvCantP5.setVisibility(View.VISIBLE);

        tvNomPdes5.setVisibility(View.VISIBLE);
        tvPrePdes5.setVisibility(View.VISIBLE);
        tvCantPdes5.setVisibility(View.VISIBLE);
    }

    public void cerrarPedido(){
        if (!NomProBebes.isEmpty()) {
            Map<String, Object> pBebes = new HashMap<>();
            pBebes.put("id", identificador);
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
        pPedidoInfo.put("subtotal", subtotalDB);
        pPedidoInfo.put("impuesto", impuestoDB);
        pPedidoInfo.put("total", totalDB);
        mDatabase.child("pedidos").child("cerrados").child(identificador).child("infoPedido").setValue(pPedidoInfo);

        Map<String, Object> pClienteInfo = new HashMap<>();
        pClienteInfo.put("idCliente", idCliente);
        pClienteInfo.put("nomCliente", nombreCliente);
        pClienteInfo.put("apeCliente", apellidoCliente);
        pClienteInfo.put("telCliente", telefonoCliente);
        pClienteInfo.put("dirCliente", direccionCliente);
        pClienteInfo.put("corCliente", correoCliente);
        mDatabase.child("pedidos").child("cerrados").child(identificador).child("infoCliente").setValue(pClienteInfo);

        Map<String, Object> pClienteEva = new HashMap<>();
        pClienteEva.put("comentario", "---");
        pClienteEva.put("evaluacion", "No Evaluado");
        mDatabase.child("pedidos").child("cerrados").child(identificador).child("infoEva").setValue(pClienteEva);

        controlPedidos();
        eliminarPedido();

        Toast.makeText(ActivityPedidosN.this, "Pedido Cerrado", Toast.LENGTH_LONG).show();
    }

    private void controlPedidos() {
        Map<String, Object> Regpedido = new HashMap<>();
        Regpedido.put(identificador, identificador);
        mDatabase.child("pedidos").child("registroCer").updateChildren(Regpedido);

        Map<String, Object> RegConpedido = new HashMap<>();
        RegConpedido.put(identificador, identificador);
        mDatabase.child("pedidos").child("registroCli").child(idCliente).updateChildren(RegConpedido);
    }

    public void eliminarPedido(){
        mDatabase.child("pedidos").child("nuevos").child(identificador).removeValue();
        mDatabase.child("pedidos").child("registroNue").child(identificador).removeValue();
        mDatabase.child("pedidos").child("registroNueID").child(idCliente).child(identificador).removeValue();
    }
}