package com.aplicacion.proyectofinalpm1.ActivityRepartidor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.aplicacion.proyectofinalpm1.ActivityControl.ActivityLogin;
import com.aplicacion.proyectofinalpm1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityRepartidor extends AppCompatActivity {

    TextView textViewRepar, tvRepListadoP;
    Button btnReparCerrar, btnReparPerfil;
    String idUsuario = "";

    ListView listaPedidosNR;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor);

        mAuth = FirebaseAuth.getInstance();
        idUsuario = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        textViewRepar = findViewById(R.id.textViewRepar);
        tvRepListadoP = findViewById(R.id.tvRepListadoP);

        tipoUsuario();

        btnReparCerrar = (Button) findViewById(R.id.btnReparCerrar);
        btnReparCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(ActivityRepartidor.this, ActivityLogin.class));
                finish();
            }
        });

        //Ir al perfil del repartidor
        btnReparPerfil = (Button) findViewById(R.id.btnReparPerfil);
        btnReparPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityRepartidor.this, ActivityPerfilRepartidor.class));
            }
        });

        //Selecciona el pedido.
        listaPedidosNR = (ListView) findViewById(R.id.listaPedidosNR);
        listaPedidosNR.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int un = i;
                String textItemList = (String) listaPedidosNR.getItemAtPosition(un);

                Intent intent = new Intent(getApplicationContext(), ActivityPedidosN.class);
                intent.putExtra("identificador", textItemList);
                startActivity(intent);
            }
        });

        mostrarPedidos();
    }

    private void tipoUsuario(){

        String id = mAuth.getCurrentUser().getUid();

        ///Evalua los usuarios repartidores en la BD
        mDatabase.child("usuarios").child("repartidores").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child("nombre").getValue().toString();
                    textViewRepar.setText("Bienvenido: "+ nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void mostrarPedidos(){
        contarPedidos();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRootChild = mDatabase.child("pedidos").child("registroNue");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        listaPedidosNR = (ListView) findViewById(R.id.listaPedidosNR);
        listaPedidosNR.setAdapter(adapter);

        mRootChild.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s){
                String string = dataSnapshot.getValue(String.class);
                arrayList.add(string);
                adapter.notifyDataSetChanged();
                contarPedidos();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s){

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot){
                String string = dataSnapshot.getValue(String.class);
                arrayList.remove(string);
                adapter.notifyDataSetChanged();
                contarPedidos();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s){

            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });
    }

    public void contarPedidos(){
        mDatabase.child("pedidos").child("registroNue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvRepListadoP.setText("Listado de pedidos por ID (" + dataSnapshot.getChildrenCount() + ") Pendientes");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });

    }
}