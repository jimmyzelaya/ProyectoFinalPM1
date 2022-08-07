package com.aplicacion.proyectofinalpm1.ActivityAdmin;

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

public class ActivityListadoPedidos extends AppCompatActivity {

    ListView listaPedidosPenA, listaPedidosCerrA, listaPedidosEvaA;

    String idUsuario;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    Button btnAdminPPen, btnAdminPCer, btnAdminPEva;
    TextView tvTipoPedido;
    long cantPendiente, cantCerrados, cantEvaluados;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_pedidos);

        mAuth = FirebaseAuth.getInstance();
        idUsuario = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        listaPedidosPenA = findViewById(R.id.listaPedidosPenA);
        listaPedidosCerrA = findViewById(R.id.listaPedidosCerrA);
        listaPedidosEvaA = findViewById(R.id.listaPedidosEvaA);
        tvTipoPedido = findViewById(R.id.tvTipoPedido);

        //Muestra los pedidos Pendientes
        btnAdminPPen = (Button) findViewById(R.id.btnAdminPPen);
        btnAdminPPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPedidosPend();
            }
        });

        //Muestra los pedidos cerrados
        btnAdminPCer = (Button) findViewById(R.id.btnAdminPCer);
        btnAdminPCer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPedidosCerrados();
            }
        });

        //Muestra los pedidos Evaluados
        btnAdminPEva = (Button) findViewById(R.id.btnAdminPEva);
        btnAdminPEva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPedidosEvaluados();
            }
        });

        //ListView
        //Pedidos Pendientes.
        listaPedidosPenA = (ListView) findViewById(R.id.listaPedidosPenA);
        listaPedidosPenA.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int un = i;
                String textItemList = (String) listaPedidosPenA.getItemAtPosition(un);

                Intent intent = new Intent(getApplicationContext(), ActivityDetallePedidos.class);
                intent.putExtra("identificador", textItemList);
                intent.putExtra("tipoPedido", "nuevos");
                intent.putExtra("estadoPedido", "Pendiente");
                startActivity(intent);
            }
        });

        //Pedidos Cerrados.
        listaPedidosCerrA = (ListView) findViewById(R.id.listaPedidosCerrA);
        listaPedidosCerrA.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int un = i;
                String textItemList = (String) listaPedidosCerrA.getItemAtPosition(un);

                Intent intent = new Intent(getApplicationContext(), ActivityDetallePedidos.class);
                intent.putExtra("identificador", textItemList);
                intent.putExtra("tipoPedido", "cerrados");
                intent.putExtra("estadoPedido", "Entregado");
                startActivity(intent);
            }
        });

        //Pedidos Evaluados.
        listaPedidosEvaA = (ListView) findViewById(R.id.listaPedidosEvaA);
        listaPedidosEvaA.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int un = i;
                String textItemList = (String) listaPedidosEvaA.getItemAtPosition(un);

                Intent intent = new Intent(getApplicationContext(), ActivityDetallePedidos.class);
                intent.putExtra("identificador", textItemList);
                intent.putExtra("tipoPedido", "evaluados");
                intent.putExtra("estadoPedido", "Cerrado con Evaluación");
                intent.putExtra("tip", "3");
                startActivity(intent);
            }
        });

        //Craga inicial de los productos pendientes
        mostrarPedidos();
    }

    public void mostrarPedidos(){

        contarPendientes();

        listaPedidosCerrA.setVisibility(View.GONE);
        listaPedidosPenA.setVisibility(View.VISIBLE);
        listaPedidosEvaA.setVisibility(View.GONE);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRootChild = mDatabase.child("pedidos").child("registroNue");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        listaPedidosPenA = (ListView) findViewById(R.id.listaPedidosPenA);
        listaPedidosPenA.setAdapter(adapter);

        mRootChild.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s){
                String string = dataSnapshot.getValue(String.class);
                arrayList.add(string);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s){

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot){
                String string = dataSnapshot.getValue(String.class);
                arrayList.remove(string);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s){

            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });
    }

    public void mostrarPedidosPend(){

        contarPendientes();

        listaPedidosCerrA.setVisibility(View.GONE);
        listaPedidosPenA.setVisibility(View.VISIBLE);
        listaPedidosEvaA.setVisibility(View.GONE);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRootChild = mDatabase.child("pedidos").child("registroNue");

        adapter.clear(); adapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        listaPedidosPenA = (ListView) findViewById(R.id.listaPedidosPenA);
        listaPedidosPenA.setAdapter(adapter);

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

    public void mostrarPedidosCerrados(){

        contarCerrados();

        listaPedidosCerrA.setVisibility(View.VISIBLE);
        listaPedidosPenA.setVisibility(View.GONE);
        listaPedidosEvaA.setVisibility(View.GONE);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRootChild = mDatabase.child("pedidos").child("registroCer");

        adapter.clear(); adapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        listaPedidosCerrA = (ListView) findViewById(R.id.listaPedidosCerrA);
        listaPedidosCerrA.setAdapter(adapter);

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

    public void mostrarPedidosEvaluados(){

        contarEvaluados();

        listaPedidosCerrA.setVisibility(View.GONE);
        listaPedidosPenA.setVisibility(View.GONE);
        listaPedidosEvaA.setVisibility(View.VISIBLE);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRootChild = mDatabase.child("pedidos").child("registroEva");

        adapter.clear(); adapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        listaPedidosEvaA = (ListView) findViewById(R.id.listaPedidosEvaA);
        listaPedidosEvaA.setAdapter(adapter);

        mRootChild.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s){
                String string = dataSnapshot.getValue(String.class);
                arrayList.add(string);
                adapter.notifyDataSetChanged();
                contarEvaluados();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s){

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot){
                String string = dataSnapshot.getValue(String.class);
                arrayList.remove(string);
                adapter.notifyDataSetChanged();
                contarEvaluados();
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
        mDatabase.child("pedidos").child("registroNue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cantPendiente = dataSnapshot.getChildrenCount();
                tvTipoPedido.setText(cantPendiente + " Pedidos Pendientes");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }

    private void contarCerrados(){
        mDatabase.child("pedidos").child("registroCer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cantCerrados = dataSnapshot.getChildrenCount();
                tvTipoPedido.setText(cantCerrados + " Pedidos Cerrados");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }

    private void contarEvaluados(){
        mDatabase.child("pedidos").child("registroEva").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cantEvaluados = dataSnapshot.getChildrenCount();
                tvTipoPedido.setText(cantEvaluados + " Pedidos Evaluados");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }
}