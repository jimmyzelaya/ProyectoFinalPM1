package com.aplicacion.proyectofinalpm1.ActivityClientes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

public class ActivityCarritoCompras extends AppCompatActivity {

    private final int cargaDatos = 2500;

    TextView tvSubtotal, tvImpuesto, tvTotal;
    TextView tvNomP1, tvPreP1, tvCantP1, tvSubP1;
    TextView tvNomP2, tvPreP2, tvCantP2, tvSubP2;
    TextView tvNomP3, tvPreP3, tvCantP3, tvSubP3;
    TextView tvNomP4, tvPreP4, tvCantP4, tvSubP4;
    TextView tvNomP5, tvPreP5, tvCantP5, tvSubP5;
    TextView tvNomPdes1, tvNomPdes2, tvNomPdes3, tvNomPdes4, tvNomPdes5;
    TextView tvPrePdes1, tvPrePdes2, tvPrePdes3, tvPrePdes4, tvPrePdes5;
    TextView tvCantPdes1, tvCantPdes2, tvCantPdes3, tvCantPdes4, tvCantPdes5;
    TextView tvSubPdes1, tvSubPdes2, tvSubPdes3, tvSubPdes4, tvSubPdes5;

    ImageView imgP1, imgP2, imgP3, imgP4, imgP5;

    Button btnCerrarP, btnCaEliminarP1, btnCaEliminarP2, btnCaEliminarP3, btnCaEliminarP4, btnCaEliminarP5;

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String idUsuario = "";
    String idGenerado = "";

    String NomProBebes = "", cantidadPañales = "", precioPañales = "";
    String NomProBebidas = "", cantidadBebidas = "", precioBebidas = "";
    String NomProCarnes = "", cantidadCarnes = "", precioCarnes = "";
    String NomProGranosB = "", cantidadGranosB = "", precioGranosB = "";
    String NomProLacteos = "", cantidadLacteos = "", precioLacteos = "";
    String prod1, prod2, prod3, prod4, prod5;

    double subtotalBebes = 0, subtotalBebidas = 0, subtotalCarnes = 0, subtotalGranosB = 0, subtotalLacteos = 0, subtotal = 0;
    double impBebes = 0, impBebidas = 0, impCarnes = 0, impGranosB = 0, impLacteos = 0, impuesto = 0;
    double totalBebes = 0, totalBebidas = 0, totalCarnes = 0, totalGranosB = 0, totalLacteos = 0, total = 0;
    double precUBebes = 0, precUBebidas = 0, PrecUCarnes = 0, precGranosB = 0, precULacteos = 0;
    String nombreCliente, apellidoCliente, telefonoCliente, direccionCliente, correoCliente;
    String latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito_compras);

        //Id de usuario
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

        //SUSCRIBIR A UNA PERSONA AL TOPICO
        FirebaseMessaging.getInstance().subscribeToTopic("enviaratodos").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ActivityCarritoCompras.this,"Suscrito al enviar a todos!",Toast.LENGTH_SHORT).show();

            }
        });

        //Carga de Datos
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                impuesto = impBebes + impBebidas + impCarnes + impGranosB + impLacteos;
                subtotal = subtotalBebes + subtotalBebidas + subtotalCarnes + subtotalGranosB + subtotalLacteos;
                total = totalBebes + totalBebidas + totalCarnes + totalGranosB + totalLacteos;

                tvSubtotal.setText(subtotal + " Lps");
                tvImpuesto.setText(impuesto + " Lps");
                tvTotal.setText(total + " Lps");
            }
        },cargaDatos);
        //Termina carga de Datos

        //Boton para realizar la compra
        btnCerrarP = (Button) findViewById(R.id.btnCerrarP);
        btnCerrarP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tvNomP1.getText().toString().isEmpty() && tvNomP2.getText().toString().isEmpty() &&
                        tvNomP3.getText().toString().isEmpty() && tvNomP4.getText().toString().isEmpty() &&
                        tvNomP5.getText().toString().isEmpty()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCarritoCompras.this);
                    builder.setMessage("Para Realizar una Compra, Primero debe agregar Productos al Carrito!!!")
                            .setTitle("Carrito Vacio");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCarritoCompras.this);
                    builder.setMessage("¿Desea Realizar el Pedido?")
                            .setTitle("Atención");

                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            realizarPedido();
                            eliminarCarrito();
                            //llamaratopico("Compra Realizada","En procesa de entrega","enviaratodos");
                            llamarespecifico();
                            onBackPressed();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        ///// Permisos de ubicación

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION));

            else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }


        /////

        //Boton para Eliminar pedido 1
        btnCaEliminarP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCarritoCompras.this);
                builder.setMessage("¿Desea Eliminar este Producto del Carrito?")
                        .setTitle(tvNomP1.getText().toString());

                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eliminarP1();
                        Toast.makeText(ActivityCarritoCompras.this, "Producto Eliminado del Carrito", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //Boton para Eliminar pedido 2
        btnCaEliminarP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCarritoCompras.this);
                builder.setMessage("¿Desea Eliminar este Producto del Carrito?")
                        .setTitle(tvNomP2.getText().toString());

                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eliminarP2();
                        Toast.makeText(ActivityCarritoCompras.this, "Producto Eliminado del Carrito", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //Boton para Eliminar pedido 3
        btnCaEliminarP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCarritoCompras.this);
                builder.setMessage("¿Desea Eliminar este Producto del Carrito?")
                        .setTitle(tvNomP3.getText().toString());

                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eliminarP3();
                        Toast.makeText(ActivityCarritoCompras.this, "Producto Eliminado del Carrito", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //Boton para Eliminar pedido 4
        btnCaEliminarP4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCarritoCompras.this);
                builder.setMessage("¿Desea Eliminar este Producto del Carrito?")
                        .setTitle(tvNomP4.getText().toString());

                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eliminarP4();
                        Toast.makeText(ActivityCarritoCompras.this, "Producto Eliminado del Carrito", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //Boton para Eliminar pedido 5
        btnCaEliminarP5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCarritoCompras.this);
                builder.setMessage("¿Desea Eliminar este Producto del Carrito?")
                        .setTitle(tvNomP5.getText().toString());

                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eliminarP5();
                        Toast.makeText(ActivityCarritoCompras.this, "Producto Eliminado del Carrito", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }////////////////////

    // ESTE ENVIARIA UN MENSAJE PUSH A TODOS LOS USUARIOS QUE TENGAN INSTALADOS LA APLICACION
    // AQUI SOLAMENTE SE TIENE  QUE SUSCRIBIR A LOS USUARIOS EN UN TOPICO ASI UNA VEZ QUE ESLLOS SE SUSCRIBAN SE LE ENVIA LA NOTIFICACION A TODOS LOS USUARIOS
    private void llamaratopico(String titulo,String mensaje, String topico) {
        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json =new JSONObject();

        try {
            //String token ="dJWgzZJgxoU:APA91bHCrssO8p_WNjezcgiD6bYcp57bq6HExP_lqScwElHSTlVsDmgJfUXAor-i4ZcWvkXucSivCLcsgJsYPAm4-7CmTdqJeM37eM3RV6nSA7VGWatwJvkBDMzu704AWY5EVFyEkjMp";

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



    //INICIA CODIGO ESPECIFICO MENSAJE SI SE ESTA UTILIZANDO
    //CODIGO PARA ENVIAR NOTIFICACION PUSH A UN USUARIO EN ESPECIFICO OSEA AL USUARIO QUE TENGA ESE TOKEN
    // Se puede utilizar para que le envie un mensje a solamente un usuario
    private void llamarespecifico() {

        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json =new JSONObject();

        try {
            // String token ="dJWgzZJgxoU:APA91bHCrssO8p_WNjezcgiD6bYcp57bq6HExP_lqScwElHSTlVsDmgJfUXAor-i4ZcWvkXucSivCLcsgJsYPAm4-7CmTdqJeM37eM3RV6nSA7VGWatwJvkBDMzu704AWY5EVFyEkjMp";
            //String token = FirebaseMessaging.getInstance().getToken().toString();
            //String token= new MyAplication().getSomeVariable();
            SharedPreferences prefs = getSharedPreferences("unique_name", MODE_PRIVATE);
            String token = prefs.getString("token", ""); // will return 0 if no  value is saved
            json.put("to",token);

            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo ","Compra Realizada ");
            notificacion.put("detalle","En procesa de entrega");
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
    }//FINAL DE CODIGO ESPECIFICO


    public void infoUsuario(){
        mDatabase.child("usuarios").child("clientes").child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    nombreCliente = dataSnapshot.child("nombre").getValue().toString();
                    apellidoCliente = dataSnapshot.child("apellido").getValue().toString();
                    telefonoCliente = dataSnapshot.child("telefono").getValue().toString();
                    direccionCliente = dataSnapshot.child("direccion").getValue().toString();
                    correoCliente = dataSnapshot.child("correo").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void catBebidas(){
        mDatabase.child("carrito").child(idUsuario).child("catBebidas").addValueEventListener(new ValueEventListener() {
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
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP1);
                        prod1 = "bebidas";
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProBebidas);
                        tvPreP2.setText(precUBebidas + " Lps");
                        tvCantP2.setText(cantidadBebidas);
                        tvSubP2.setText(precioBebidas + ".00 .Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP2);
                        prod2 = "bebidas";
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProBebidas);
                        tvPreP3.setText(precUBebidas + " Lps");
                        tvCantP3.setText(cantidadBebidas);
                        tvSubP3.setText(precioBebidas + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP3);
                        prod3 = "bebidas";
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProBebidas);
                        tvPreP4.setText(precUBebidas + " Lps");
                        tvCantP4.setText(cantidadBebidas);
                        tvSubP4.setText(precioBebidas + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP4);
                        prod4 = "bebidas";
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProBebidas);
                        tvPreP5.setText(precUBebidas + " Lps");
                        tvCantP5.setText(cantidadBebidas);
                        tvSubP5.setText(precioBebidas + "00. Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP5);
                        prod5 = "bebidas";
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
        mDatabase.child("carrito").child(idUsuario).child("catBebes").addValueEventListener(new ValueEventListener() {
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
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP1);
                        prod1 = "bebes";
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProBebes);
                        tvPreP2.setText(precUPañales + " Lps");
                        tvCantP2.setText(cantidadPañales);
                        tvSubP2.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP2);
                        prod2 = "bebes";
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProBebes);
                        tvPreP3.setText(precUPañales + " Lps");
                        tvCantP3.setText(cantidadPañales);
                        tvSubP3.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP3);
                        prod3 = "bebes";
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProBebes);
                        tvPreP4.setText(precUPañales + " Lps");
                        tvCantP4.setText(cantidadPañales);
                        tvSubP4.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP4);
                        prod4 = "bebes";
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProBebes);
                        tvPreP5.setText(precUPañales + " Lps");
                        tvCantP5.setText(cantidadPañales);
                        tvSubP5.setText(precioPañales + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP5);
                        prod5 = "bebes";
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
        mDatabase.child("carrito").child(idUsuario).child("catCarnes").addValueEventListener(new ValueEventListener() {
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
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP1);
                        prod1 = "carnes";
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProCarnes);
                        tvPreP2.setText(precUCarnes + " Lps");
                        tvCantP2.setText(cantidadCarnes);
                        tvSubP2.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP2);
                        prod2 = "carnes";
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProCarnes);
                        tvPreP3.setText(precUCarnes + " Lps");
                        tvCantP3.setText(cantidadCarnes);
                        tvSubP3.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP3);
                        prod3 = "carnes";
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProCarnes);
                        tvPreP4.setText(precUCarnes + " Lps");
                        tvCantP4.setText(cantidadCarnes);
                        tvSubP4.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP4);
                        prod4 = "carnes";
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProCarnes);
                        tvPreP5.setText(precUCarnes + " Lps");
                        tvCantP5.setText(cantidadCarnes);
                        tvSubP5.setText(precioCarnes + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP5);
                        prod5 = "carnes";
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
        mDatabase.child("carrito").child(idUsuario).child("catGranosB").addValueEventListener(new ValueEventListener() {
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
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP1);
                        prod1 = "granosB";
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProGranosB);
                        tvPreP2.setText(precUGranosB + " Lps");
                        tvCantP2.setText(cantidadGranosB);
                        tvSubP2.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP2);
                        prod2 = "granosB";
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProGranosB);
                        tvPreP3.setText(precUGranosB + " Lps");
                        tvCantP3.setText(cantidadGranosB);
                        tvSubP3.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP3);
                        prod3 = "granosB";
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProGranosB);
                        tvPreP4.setText(precUGranosB + " Lps");
                        tvCantP4.setText(cantidadGranosB);
                        tvSubP4.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP4);
                        prod4 = "granosB";
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProGranosB);
                        tvPreP5.setText(precUGranosB + " Lps");
                        tvCantP5.setText(cantidadGranosB);
                        tvSubP5.setText(precioGranosB + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP5);
                        prod5 = "granosB";
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
        mDatabase.child("carrito").child(idUsuario).child("catLacteos").addValueEventListener(new ValueEventListener() {
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
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP1);
                        prod1 = "lacteos";
                        productoUno();
                    } else if (tvNomP2.getText().toString().isEmpty()) {
                        tvNomP2.setText(NomProLacteos);
                        tvPreP2.setText(precULacteos + " Lps");
                        tvCantP2.setText(cantidadLacteos);
                        tvSubP2.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP2);
                        prod2 = "lacteos";
                        productoDos();
                    } else if (tvNomP3.getText().toString().isEmpty()) {
                        tvNomP3.setText(NomProLacteos);
                        tvPreP3.setText(precULacteos + " Lps");
                        tvCantP3.setText(cantidadLacteos);
                        tvSubP3.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP3);
                        prod3 = "lacteos";
                        productoTres();
                    } else if (tvNomP4.getText().toString().isEmpty()) {
                        tvNomP4.setText(NomProLacteos);
                        tvPreP4.setText(precULacteos + " Lps");
                        tvCantP4.setText(cantidadLacteos);
                        tvSubP4.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP4);
                        prod4 = "lacteos";
                        productoCuatro();
                    } else if (tvNomP5.getText().toString().isEmpty()) {
                        tvNomP5.setText(NomProLacteos);
                        tvPreP5.setText(precULacteos + " Lps");
                        tvCantP5.setText(cantidadLacteos);
                        tvSubP5.setText(precioLacteos + ".00 Lps");
                        Picasso.with(ActivityCarritoCompras.this).load(imagenURL).into(imgP5);
                        prod5 = "lacteos";
                        productoCinco();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void realizarPedido(){

        idGenerado = mDatabase.push().getKey();

        if (!NomProBebes.isEmpty()) {
            Map<String, Object> pBebes = new HashMap<>();
            pBebes.put("id", idUsuario);
            pBebes.put("NomProBebes", NomProBebes);
            pBebes.put("cantidadPañales", cantidadPañales);
            pBebes.put("precioPañales", precioPañales);
            pBebes.put("precUPañales", "120.00");
            pBebes.put("imgUrlBebes", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2Fpaniales.png?alt=media&token=cd9f01a7-1159-49d5-9f2c-5a791793e0c6");
            mDatabase.child("pedidos").child("nuevos").child(idGenerado).child("catBebes").setValue(pBebes);
        }

        if (!NomProBebidas.isEmpty()) {
            Map<String, Object> pBebidas = new HashMap<>();
            pBebidas.put("NomProBebidas", NomProBebidas);
            pBebidas.put("cantidadBebidas", cantidadBebidas);
            pBebidas.put("precioBebidas", precioBebidas);
            pBebidas.put("precUBebidas", "150.00");
            pBebidas.put("imgUrlBebidas", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2FbebidaCoronaC.png?alt=media&token=6805fb9f-3bdd-4541-90c7-95e8a9dcec20");
            mDatabase.child("pedidos").child("nuevos").child(idGenerado).child("catBebidas").setValue(pBebidas);
        }

        if(!NomProCarnes.isEmpty()) {
            Map<String, Object> pCarnes = new HashMap<>();
            pCarnes.put("NomProCarnes", NomProCarnes);
            pCarnes.put("cantidadCarnes", cantidadCarnes);
            pCarnes.put("precioCarnes", precioCarnes);
            pCarnes.put("precUCarnes", "100.00");
            pCarnes.put("imgUrlCarnes", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2Fchuleta.png?alt=media&token=76be6ed9-ae5f-4d92-b97d-a069709a3b69");
            mDatabase.child("pedidos").child("nuevos").child(idGenerado).child("catCarnes").setValue(pCarnes);
        }

        if(!NomProGranosB.isEmpty()) {
            Map<String, Object> pGranosB = new HashMap<>();
            pGranosB.put("NomProGranosB", NomProGranosB);
            pGranosB.put("cantidadGranosB", cantidadGranosB);
            pGranosB.put("precioGranosB", precioGranosB);
            pGranosB.put("precUGranosB", "14.00");
            pGranosB.put("imgUrlGranosB", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2Farroz.jpg?alt=media&token=8e691cc4-1b32-4cd8-bb6f-7e07e07f6831");
            mDatabase.child("pedidos").child("nuevos").child(idGenerado).child("catGranosB").setValue(pGranosB);
        }

        if (!NomProLacteos.isEmpty()) {
            Map<String, Object> pLacteos = new HashMap<>();
            pLacteos.put("NomProLacteos", NomProLacteos);
            pLacteos.put("cantidadLacteos", cantidadLacteos);
            pLacteos.put("precioLacteos", precioLacteos);
            pLacteos.put("precULacteos", "30.00");
            pLacteos.put("imgUrlLacteos", "https://firebasestorage.googleapis.com/v0/b/appsupermercado-37259.appspot.com/o/img_productos%2FlecheCeteco.png?alt=media&token=7bc5a649-956e-42e7-b00b-adf651115661");
            mDatabase.child("pedidos").child("nuevos").child(idGenerado).child("catLacteos").setValue(pLacteos);
        }

        Map<String, Object> pPedidoInfo = new HashMap<>();
        pPedidoInfo.put("subtotal", subtotal);
        pPedidoInfo.put("impuesto", impuesto);
        pPedidoInfo.put("total", total);
        mDatabase.child("pedidos").child("nuevos").child(idGenerado).child("infoPedido").setValue(pPedidoInfo);

        Map<String, Object> pClienteInfo = new HashMap<>();
        pClienteInfo.put("idCliente", idUsuario);
        pClienteInfo.put("nomCliente", nombreCliente);
        pClienteInfo.put("apeCliente", apellidoCliente);
        pClienteInfo.put("telCliente", telefonoCliente);
        pClienteInfo.put("dirCliente", direccionCliente);
        pClienteInfo.put("corCliente", correoCliente);
        mDatabase.child("pedidos").child("nuevos").child(idGenerado).child("infoCliente").setValue(pClienteInfo);

        registroPedidos();

        Toast.makeText(ActivityCarritoCompras.this, "Pedido Realizado", Toast.LENGTH_LONG).show();


        //datos de ubicación a la bd

        LocationManager locationManager = (LocationManager) ActivityCarritoCompras.this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitud = "" + location.getLatitude();
                longitud = "" + location.getLongitude();

                Map<String, Object> ubicaInfo = new HashMap<>();
                ubicaInfo.put("latitud", latitud);
                ubicaInfo.put("longitud", longitud);
                mDatabase.child("pedidos").child("nuevos").child(idGenerado).child("infoPedido").updateChildren(ubicaInfo);
            }

            public void onStatusChanged(String provider, int status, Bundle extras){}

            public void onProviderEnabled(String provider){}

            public void onProviderDisabled(String provider){}
        };
        int permissionCheck = ContextCompat.checkSelfPermission(ActivityCarritoCompras.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, locationListener);

        //
    }

    public void registroPedidos(){
        Map<String, Object> Regpedido = new HashMap<>();
        Regpedido.put(idGenerado, idGenerado);
        mDatabase.child("pedidos").child("registroNue").updateChildren(Regpedido);

        Map<String, Object> RegpedidoID = new HashMap<>();
        RegpedidoID.put(idGenerado, idGenerado);
        mDatabase.child("pedidos").child("registroNueID").child(idUsuario).updateChildren(RegpedidoID);
    }

    public void eliminarCarrito(){
        mDatabase.child("carrito").child(idUsuario).removeValue();
    }

    public void instanciarTextos(){
        //Cuadros de Texto

        btnCaEliminarP1 = (Button) findViewById(R.id.btnCaEliminarP1);
        btnCaEliminarP2 = (Button) findViewById(R.id.btnCaEliminarP2);
        btnCaEliminarP3 = (Button) findViewById(R.id.btnCaEliminarP3);
        btnCaEliminarP4 = (Button) findViewById(R.id.btnCaEliminarP4);
        btnCaEliminarP5 = (Button) findViewById(R.id.btnCaEliminarP5);

        tvSubtotal = (TextView) findViewById(R.id.tvSubtotal);
        tvImpuesto = (TextView) findViewById(R.id.tvImpuesto);
        tvTotal = (TextView) findViewById(R.id.tvTotal);

        tvNomP1 = (TextView) findViewById(R.id.tvNomP1);
        tvNomP2 = (TextView) findViewById(R.id.tvNomP2);
        tvNomP3 = (TextView) findViewById(R.id.tvNomP3);
        tvNomP4 = (TextView) findViewById(R.id.tvNomP4);
        tvNomP5 = (TextView) findViewById(R.id.tvNomP5);

        tvPreP1 = (TextView) findViewById(R.id.tvPreP1);
        tvPreP2 = (TextView) findViewById(R.id.tvPreP2);
        tvPreP3 = (TextView) findViewById(R.id.tvPreP3);
        tvPreP4 = (TextView) findViewById(R.id.tvPreP4);
        tvPreP5 = (TextView) findViewById(R.id.tvPreP5);

        tvCantP1 = (TextView) findViewById(R.id.tvCantP1);
        tvCantP2 = (TextView) findViewById(R.id.tvCantP2);
        tvCantP3 = (TextView) findViewById(R.id.tvCantP3);
        tvCantP4 = (TextView) findViewById(R.id.tvCantP4);
        tvCantP5 = (TextView) findViewById(R.id.tvCantP5);

        tvSubP1 = (TextView) findViewById(R.id.tvSubP1);
        tvSubP2 = (TextView) findViewById(R.id.tvSubP2);
        tvSubP3 = (TextView) findViewById(R.id.tvSubP3);
        tvSubP4 = (TextView) findViewById(R.id.tvSubP4);
        tvSubP5 = (TextView) findViewById(R.id.tvSubP5);

        //Instancia de Imagenes (Productos)
        imgP1 = (ImageView) findViewById(R.id.imgP1);
        imgP2 = (ImageView) findViewById(R.id.imgP2);
        imgP3 = (ImageView) findViewById(R.id.imgP3);
        imgP4 = (ImageView) findViewById(R.id.imgP4);
        imgP5 = (ImageView) findViewById(R.id.imgP5);

        tvNomPdes1 = (TextView) findViewById(R.id.tvNomPdes1);
        tvNomPdes2 = (TextView) findViewById(R.id.tvNomPdes2);
        tvNomPdes3 = (TextView) findViewById(R.id.tvNomPdes3);
        tvNomPdes4 = (TextView) findViewById(R.id.tvNomPdes4);
        tvNomPdes5 = (TextView) findViewById(R.id.tvNomPdes5);

        tvPrePdes1 = (TextView) findViewById(R.id.tvPrePdes1);
        tvPrePdes2 = (TextView) findViewById(R.id.tvPrePdes2);
        tvPrePdes3 = (TextView) findViewById(R.id.tvPrePdes3);
        tvPrePdes4 = (TextView) findViewById(R.id.tvPrePdes4);
        tvPrePdes5 = (TextView) findViewById(R.id.tvPrePdes5);

        tvCantPdes1 = (TextView) findViewById(R.id.tvCantPdes1);
        tvCantPdes2 = (TextView) findViewById(R.id.tvCantPdes2);
        tvCantPdes3 = (TextView) findViewById(R.id.tvCantPdes3);
        tvCantPdes4 = (TextView) findViewById(R.id.tvCantPdes4);
        tvCantPdes5 = (TextView) findViewById(R.id.tvCantPdes5);

        tvSubPdes1 = (TextView) findViewById(R.id.tvSubPdes1);
        tvSubPdes2 = (TextView) findViewById(R.id.tvSubPdes2);
        tvSubPdes3 = (TextView) findViewById(R.id.tvSubPdes3);
        tvSubPdes4 = (TextView) findViewById(R.id.tvSubPdes4);
        tvSubPdes5 = (TextView) findViewById(R.id.tvSubPdes5);
    }

    public void limpiar(){
        instanciarTextos();
        btnCaEliminarP1.setVisibility(View.INVISIBLE);
        btnCaEliminarP2.setVisibility(View.INVISIBLE);
        btnCaEliminarP3.setVisibility(View.INVISIBLE);
        btnCaEliminarP4.setVisibility(View.INVISIBLE);
        btnCaEliminarP5.setVisibility(View.INVISIBLE);

        tvNomP1.setVisibility(View.INVISIBLE);
        tvNomP2.setVisibility(View.INVISIBLE);
        tvNomP3.setVisibility(View.INVISIBLE);
        tvNomP4.setVisibility(View.INVISIBLE);
        tvNomP5.setVisibility(View.INVISIBLE);

        tvPreP1.setVisibility(View.INVISIBLE);
        tvPreP2.setVisibility(View.INVISIBLE);
        tvPreP3.setVisibility(View.INVISIBLE);
        tvPreP4.setVisibility(View.INVISIBLE);
        tvPreP5.setVisibility(View.INVISIBLE);

        tvCantP1.setVisibility(View.INVISIBLE);
        tvCantP2.setVisibility(View.INVISIBLE);
        tvCantP3.setVisibility(View.INVISIBLE);
        tvCantP4.setVisibility(View.INVISIBLE);
        tvCantP5.setVisibility(View.INVISIBLE);

        tvSubP1.setVisibility(View.INVISIBLE);
        tvSubP2.setVisibility(View.INVISIBLE);
        tvSubP3.setVisibility(View.INVISIBLE);
        tvSubP4.setVisibility(View.INVISIBLE);
        tvSubP5.setVisibility(View.INVISIBLE);

        tvNomPdes1.setVisibility(View.INVISIBLE);
        tvNomPdes2.setVisibility(View.INVISIBLE);
        tvNomPdes3.setVisibility(View.INVISIBLE);
        tvNomPdes4.setVisibility(View.INVISIBLE);
        tvNomPdes5.setVisibility(View.INVISIBLE);

        tvPrePdes1.setVisibility(View.INVISIBLE);
        tvPrePdes2.setVisibility(View.INVISIBLE);
        tvPrePdes3.setVisibility(View.INVISIBLE);
        tvPrePdes4.setVisibility(View.INVISIBLE);
        tvPrePdes5.setVisibility(View.INVISIBLE);

        tvCantPdes1.setVisibility(View.INVISIBLE);
        tvCantPdes2.setVisibility(View.INVISIBLE);
        tvCantPdes3.setVisibility(View.INVISIBLE);
        tvCantPdes4.setVisibility(View.INVISIBLE);
        tvCantPdes5.setVisibility(View.INVISIBLE);

        tvSubPdes1.setVisibility(View.INVISIBLE);
        tvSubPdes2.setVisibility(View.INVISIBLE);
        tvSubPdes3.setVisibility(View.INVISIBLE);
        tvSubPdes4.setVisibility(View.INVISIBLE);
        tvSubPdes5.setVisibility(View.INVISIBLE);

        imgP1.setVisibility(View.INVISIBLE);
        imgP2.setVisibility(View.INVISIBLE);
        imgP3.setVisibility(View.INVISIBLE);
        imgP4.setVisibility(View.INVISIBLE);
        imgP5.setVisibility(View.INVISIBLE);
    }

    public void productoUno(){
        imgP1.setVisibility(View.VISIBLE);
        btnCaEliminarP1.setVisibility(View.VISIBLE);

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
        btnCaEliminarP2.setVisibility(View.VISIBLE);

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
        btnCaEliminarP3.setVisibility(View.VISIBLE);

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
        btnCaEliminarP4.setVisibility(View.VISIBLE);

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
        btnCaEliminarP5.setVisibility(View.VISIBLE);

        tvNomP5.setVisibility(View.VISIBLE);
        tvPreP5.setVisibility(View.VISIBLE);
        tvCantP5.setVisibility(View.VISIBLE);
        tvSubP5.setVisibility(View.VISIBLE);

        tvNomPdes5.setVisibility(View.VISIBLE);
        tvPrePdes5.setVisibility(View.VISIBLE);
        tvCantPdes5.setVisibility(View.VISIBLE);
        tvSubPdes5.setVisibility(View.VISIBLE);
    }

    public void eliminarP1() {
        if (prod1 == "bebidas") {
            NomProBebidas = "";
            cantidadBebidas = "";
            precioBebidas = "";
            impBebidas = 0;
            subtotalBebidas = 0;
            totalBebidas = 0;
            mDatabase.child("carrito").child(idUsuario).child("catBebidas").removeValue();
        } else if (prod1 == "bebes") {
            NomProBebes = "";
            cantidadPañales = "";
            precioPañales = "";
            impBebes = 0;
            subtotalBebes = 0;
            totalBebes = 0;
            mDatabase.child("carrito").child(idUsuario).child("catBebes").removeValue();
        } else if (prod1 == "carnes") {
            NomProCarnes = "";
            cantidadCarnes = "";
            precioCarnes = "";
            impCarnes = 0;
            subtotalCarnes = 0;
            totalCarnes = 0;
            mDatabase.child("carrito").child(idUsuario).child("catCarnes").removeValue();
        } else if (prod1 == "granosB") {
            NomProGranosB = "";
            cantidadGranosB = "";
            precioGranosB = "";
            impGranosB = 0;
            subtotalGranosB = 0;
            totalGranosB = 0;
            mDatabase.child("carrito").child(idUsuario).child("catGranosB").removeValue();
        } else if (prod1 == "lacteos") {
            NomProLacteos = "";
            cantidadLacteos = "";
            precioLacteos = "";
            impLacteos = 0;
            subtotalLacteos = 0;
            totalLacteos = 0;
            mDatabase.child("carrito").child(idUsuario).child("catLacteos").removeValue();
        }

        calcularTotal();

        imgP1.setVisibility(View.GONE);
        btnCaEliminarP1.setVisibility(View.GONE);

        tvNomP1.setVisibility(View.GONE);
        tvPreP1.setVisibility(View.GONE);
        tvCantP1.setVisibility(View.GONE);
        tvSubP1.setVisibility(View.GONE);

        tvNomPdes1.setVisibility(View.GONE);
        tvPrePdes1.setVisibility(View.GONE);
        tvCantPdes1.setVisibility(View.GONE);
        tvSubPdes1.setVisibility(View.GONE);
    }

    public void eliminarP2() {
        if (prod2 == "bebidas") {
            NomProBebidas = "";
            cantidadBebidas = "";
            precioBebidas = "";
            impBebidas = 0;
            subtotalBebidas = 0;
            totalBebidas = 0;
            mDatabase.child("carrito").child(idUsuario).child("catBebidas").removeValue();
        } else if (prod2 == "bebes") {
            NomProBebes = "";
            cantidadPañales = "";
            precioPañales = "";
            impBebes = 0;
            subtotalBebes = 0;
            totalBebes = 0;
            mDatabase.child("carrito").child(idUsuario).child("catBebes").removeValue();
        } else if (prod2 == "carnes") {
            NomProCarnes = "";
            cantidadCarnes = "";
            precioCarnes = "";
            impCarnes = 0;
            subtotalCarnes = 0;
            totalCarnes = 0;
            mDatabase.child("carrito").child(idUsuario).child("catCarnes").removeValue();
        } else if (prod2 == "granosB") {
            NomProGranosB = "";
            cantidadGranosB = "";
            precioGranosB = "";
            impGranosB = 0;
            subtotalGranosB = 0;
            totalGranosB = 0;
            mDatabase.child("carrito").child(idUsuario).child("catGranosB").removeValue();
        } else if (prod2 == "lacteos") {
            NomProLacteos = "";
            cantidadLacteos = "";
            precioLacteos = "";
            impLacteos = 0;
            subtotalLacteos = 0;
            totalLacteos = 0;
            mDatabase.child("carrito").child(idUsuario).child("catLacteos").removeValue();
        }

        calcularTotal();

        imgP2.setVisibility(View.GONE);
        btnCaEliminarP2.setVisibility(View.GONE);

        tvNomP2.setVisibility(View.GONE);
        tvPreP2.setVisibility(View.GONE);
        tvCantP2.setVisibility(View.GONE);
        tvSubP2.setVisibility(View.GONE);

        tvNomPdes2.setVisibility(View.GONE);
        tvPrePdes2.setVisibility(View.GONE);
        tvCantPdes2.setVisibility(View.GONE);
        tvSubPdes2.setVisibility(View.GONE);
    }

    public void eliminarP3() {
        if (prod3 == "bebidas") {
            NomProBebidas = "";
            cantidadBebidas = "";
            precioBebidas = "";
            impBebidas = 0;
            subtotalBebidas = 0;
            totalBebidas = 0;
            mDatabase.child("carrito").child(idUsuario).child("catBebidas").removeValue();
        } else if (prod3 == "bebes") {
            NomProBebes = "";
            cantidadPañales = "";
            precioPañales = "";
            impBebes = 0;
            subtotalBebes = 0;
            totalBebes = 0;
            mDatabase.child("carrito").child(idUsuario).child("catBebes").removeValue();
        } else if (prod3 == "carnes") {
            NomProCarnes = "";
            cantidadCarnes = "";
            precioCarnes = "";
            impCarnes = 0;
            subtotalCarnes = 0;
            totalCarnes = 0;
            mDatabase.child("carrito").child(idUsuario).child("catCarnes").removeValue();
        } else if (prod3 == "granosB") {
            NomProGranosB = "";
            cantidadGranosB = "";
            precioGranosB = "";
            impGranosB = 0;
            subtotalGranosB = 0;
            totalGranosB = 0;
            mDatabase.child("carrito").child(idUsuario).child("catGranosB").removeValue();
        } else if (prod3 == "lacteos") {
            NomProLacteos = "";
            cantidadLacteos = "";
            precioLacteos = "";
            impLacteos = 0;
            subtotalLacteos = 0;
            totalLacteos = 0;
            mDatabase.child("carrito").child(idUsuario).child("catLacteos").removeValue();
        }

        calcularTotal();

        imgP3.setVisibility(View.GONE);
        btnCaEliminarP3.setVisibility(View.GONE);

        tvNomP3.setVisibility(View.GONE);
        tvPreP3.setVisibility(View.GONE);
        tvCantP3.setVisibility(View.GONE);
        tvSubP3.setVisibility(View.GONE);

        tvNomPdes3.setVisibility(View.GONE);
        tvPrePdes3.setVisibility(View.GONE);
        tvCantPdes3.setVisibility(View.GONE);
        tvSubPdes3.setVisibility(View.GONE);
    }

    public void eliminarP4() {
        if (prod4 == "bebidas") {
            NomProBebidas = "";
            cantidadBebidas = "";
            precioBebidas = "";
            impBebidas = 0;
            subtotalBebidas = 0;
            totalBebidas = 0;
            mDatabase.child("carrito").child(idUsuario).child("catBebidas").removeValue();
        } else if (prod4 == "bebes") {
            NomProBebes = "";
            cantidadPañales = "";
            precioPañales = "";
            impBebes = 0;
            subtotalBebes = 0;
            totalBebes = 0;
            mDatabase.child("carrito").child(idUsuario).child("catBebes").removeValue();
        } else if (prod4 == "carnes") {
            NomProCarnes = "";
            cantidadCarnes = "";
            precioCarnes = "";
            impCarnes = 0;
            subtotalCarnes = 0;
            totalCarnes = 0;
            mDatabase.child("carrito").child(idUsuario).child("catCarnes").removeValue();
        } else if (prod4 == "granosB") {
            NomProGranosB = "";
            cantidadGranosB = "";
            precioGranosB = "";
            impGranosB = 0;
            subtotalGranosB = 0;
            totalGranosB = 0;
            mDatabase.child("carrito").child(idUsuario).child("catGranosB").removeValue();
        } else if (prod4 == "lacteos") {
            NomProLacteos = "";
            cantidadLacteos = "";
            precioLacteos = "";
            impLacteos = 0;
            subtotalLacteos = 0;
            totalLacteos = 0;
            mDatabase.child("carrito").child(idUsuario).child("catLacteos").removeValue();
        }

        calcularTotal();

        imgP4.setVisibility(View.GONE);
        btnCaEliminarP4.setVisibility(View.GONE);

        tvNomP4.setVisibility(View.GONE);
        tvPreP4.setVisibility(View.GONE);
        tvCantP4.setVisibility(View.GONE);
        tvSubP4.setVisibility(View.GONE);

        tvNomPdes4.setVisibility(View.GONE);
        tvPrePdes4.setVisibility(View.GONE);
        tvCantPdes4.setVisibility(View.GONE);
        tvSubPdes4.setVisibility(View.GONE);
    }

    public void eliminarP5() {
        if (prod5 == "bebidas") {
            NomProBebidas = "";
            cantidadBebidas = "";
            precioBebidas = "";
            impBebidas = 0;
            subtotalBebidas = 0;
            totalBebidas = 0;
            mDatabase.child("carrito").child(idUsuario).child("catBebidas").removeValue();
        } else if (prod5 == "bebes") {
            NomProBebes = "";
            cantidadPañales = "";
            precioPañales = "";
            impBebes = 0;
            subtotalBebes = 0;
            totalBebes = 0;
            mDatabase.child("carrito").child(idUsuario).child("catBebes").removeValue();
        } else if (prod5 == "carnes") {
            NomProCarnes = "";
            cantidadCarnes = "";
            precioCarnes = "";
            impCarnes = 0;
            subtotalCarnes = 0;
            totalCarnes = 0;
            mDatabase.child("carrito").child(idUsuario).child("catCarnes").removeValue();
        } else if (prod5 == "granosB") {
            NomProGranosB = "";
            cantidadGranosB = "";
            precioGranosB = "";
            impGranosB = 0;
            subtotalGranosB = 0;
            totalGranosB = 0;
            mDatabase.child("carrito").child(idUsuario).child("catGranosB").removeValue();
        } else if (prod5 == "lacteos") {
            NomProLacteos = "";
            cantidadLacteos = "";
            precioLacteos = "";
            impLacteos = 0;
            subtotalLacteos = 0;
            totalLacteos = 0;
            mDatabase.child("carrito").child(idUsuario).child("catLacteos").removeValue();
        }

        calcularTotal();

        imgP5.setVisibility(View.GONE);
        btnCaEliminarP5.setVisibility(View.GONE);

        tvNomP5.setVisibility(View.GONE);
        tvPreP5.setVisibility(View.GONE);
        tvCantP5.setVisibility(View.GONE);
        tvSubP5.setVisibility(View.GONE);

        tvNomPdes5.setVisibility(View.GONE);
        tvPrePdes5.setVisibility(View.GONE);
        tvCantPdes5.setVisibility(View.GONE);
        tvSubPdes5.setVisibility(View.GONE);
    }

    public void calcularTotal(){
        impuesto = impBebes + impBebidas + impCarnes + impGranosB + impLacteos;
        subtotal = subtotalBebes + subtotalBebidas + subtotalCarnes + subtotalGranosB + subtotalLacteos;
        total = totalBebes + totalBebidas + totalCarnes + totalGranosB + totalLacteos;

        tvSubtotal.setText(subtotal + " Lps");
        tvImpuesto.setText(impuesto + " Lps");
        tvTotal.setText(total + " Lps");
    }
}