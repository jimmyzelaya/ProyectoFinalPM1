package com.aplicacion.proyectofinalpm1.ActivityClientes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.aplicacion.proyectofinalpm1.ActivityRepartidor.ActivityPedidosN;
import com.aplicacion.proyectofinalpm1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityDashboard extends AppCompatActivity {

    Button btnPedidoActC, btnPedidosHisC;

    ListView listaDashboarC, listaDashboarHis;

    String idUsuario;
    TextView tvTipoPedidoP;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        idUsuario = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        listaDashboarC = findViewById(R.id.listaDashboarC);
        listaDashboarHis = findViewById(R.id.listaDashboarHis);
        tvTipoPedidoP = findViewById(R.id.tvTipoPedidoP);

        //Muestra los pedidos Pendientes
        btnPedidoActC = (Button) findViewById(R.id.btnPedidoActC);
        btnPedidoActC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPedientes();
            }
        });

        //Muestra el historial de compras
        btnPedidosHisC = (Button) findViewById(R.id.btnPedidosHisC);
        btnPedidosHisC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarHistorial();
            }
        });

        //Selecciona el pedido Activo
        listaDashboarC.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int un = i;
                String textItemList = (String) listaDashboarC.getItemAtPosition(un);

                Intent intent = new Intent(getApplicationContext(), ActivityPedidosRegistro.class);
                intent.putExtra("identificador", textItemList);
                startActivity(intent);
            }
        });

        //Selecciona el pedido desde el Historial
        listaDashboarHis.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int un = i;
                String textItemList = (String) listaDashboarHis.getItemAtPosition(un);

                Intent intent = new Intent(getApplicationContext(), ActivityEvaluarStars.class);
                intent.putExtra("identificador", textItemList);
                startActivity(intent);
            }
        });

        //Pedidos pendientes - carga de inicio
       mostrarPedidos();
    }

    private void mostrarPedidos() {

        contarPendientes();

        listaDashboarHis.setVisibility(View.GONE);
        listaDashboarC.setVisibility(View.VISIBLE);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRootChild = mDatabase.child("pedidos").child("registroNueID").child(idUsuario);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        listaDashboarC = (ListView) findViewById(R.id.listaDashboarC);
        listaDashboarC.setAdapter(adapter);

        mRootChild.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String string = dataSnapshot.getValue(String.class);
                arrayList.add(string);
                adapter.notifyDataSetChanged();
                contarPendientes();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String string = dataSnapshot.getValue(String.class);
                arrayList.remove(string);
                adapter.notifyDataSetChanged();
                contarPendientes();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void mostrarPedientes() {
        contarPendientes();

        listaDashboarHis.setVisibility(View.GONE);
        listaDashboarC.setVisibility(View.VISIBLE);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRootChild = mDatabase.child("pedidos").child("registroNueID").child(idUsuario);

        adapter.clear(); adapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        listaDashboarC = (ListView) findViewById(R.id.listaDashboarC);
        listaDashboarC.setAdapter(adapter);

        mRootChild.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s){
                String string = dataSnapshot.getValue(String.class);
                arrayList.add(string);
                adapter.notifyDataSetChanged();
                contarPendientes();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s){

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot){
                String string = dataSnapshot.getValue(String.class);
                arrayList.remove(string);
                adapter.notifyDataSetChanged();
                contarPendientes();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s){

            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });
    }

    private void mostrarHistorial() {
        contarCerrados();

        listaDashboarHis.setVisibility(View.VISIBLE);
        listaDashboarC.setVisibility(View.GONE);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRootChild = mDatabase.child("pedidos").child("registroCli").child(idUsuario);

        adapter.clear(); adapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        listaDashboarHis = (ListView) findViewById(R.id.listaDashboarHis);
        listaDashboarHis.setAdapter(adapter);

        mRootChild.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s){
                String string = dataSnapshot.getValue(String.class);
                arrayList.add(string);
                adapter.notifyDataSetChanged();
                contarCerrados();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s){

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot){
                String string = dataSnapshot.getValue(String.class);
                arrayList.remove(string);
                adapter.notifyDataSetChanged();
                contarCerrados();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s){

            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });
    }

    private void contarPendientes(){
        mDatabase.child("pedidos").child("registroNueID").child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvTipoPedidoP.setText("(" + dataSnapshot.getChildrenCount() + ") Pedidos Pendientes");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }

    private void contarCerrados(){
        mDatabase.child("pedidos").child("registroCli").child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvTipoPedidoP.setText("(" + dataSnapshot.getChildrenCount() + ") Historial de Pedidos Recibidos");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }
}