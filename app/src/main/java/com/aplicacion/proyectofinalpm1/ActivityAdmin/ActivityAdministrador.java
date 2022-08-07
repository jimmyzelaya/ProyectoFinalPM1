package com.aplicacion.proyectofinalpm1.ActivityAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aplicacion.proyectofinalpm1.ActivityControl.ActivityLogin;
import com.aplicacion.proyectofinalpm1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityAdministrador extends AppCompatActivity {

    TextView textViewAdmin, tvAdminCantClientes, tvAdminCantRepartidores, tvAdminCantAdmin;
    Button btnAdminCerrar, btnRegisRepartidor, btnRegisAdmin, btnPedidosPendientes, btnAdminPerfil;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        textViewAdmin = findViewById(R.id.textViewAdmin);
        tvAdminCantClientes = (TextView) findViewById(R.id.tvAdminCantClientes);
        tvAdminCantRepartidores = (TextView) findViewById(R.id.tvAdminCantRepartidores);
        tvAdminCantAdmin = (TextView) findViewById(R.id.tvAdminCantAdmin);

        //Vista de Pedidos que estan pendientes de entregar
        btnPedidosPendientes = (Button) findViewById(R.id.btnPedidosPendientes);
        btnPedidosPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityAdministrador.this, ActivityListadoPedidos.class));
            }
        });

        /////////Creación de usuarios//////////////////

        //Carga la opción de creación de un usuario Repartidor
        btnRegisRepartidor = (Button) findViewById(R.id.btnRegisRepartidor);
        btnRegisRepartidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityAdministrador.this, ActivityRegisRepartidor.class));
            }
        });

        //Carga la opción de creación de un usuario Administrador
        btnRegisAdmin = (Button) findViewById(R.id.btnRegisAdmin);
        btnRegisAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityAdministrador.this, ActivityRegisAdmin.class));
            }
        });

        //Boton Perfil del Administrador
        btnAdminPerfil = (Button) findViewById(R.id.btnAdminPerfil);
        btnAdminPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityAdministrador.this, ActivityPerfilAdmin.class));
            }
        });

        //Cierra la sesión del usuario Administrador
        btnAdminCerrar = (Button) findViewById(R.id.btnAdminCerrar);
        btnAdminCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(ActivityAdministrador.this, ActivityLogin.class));
                finish();
            }
        });

        tipoUsuario();
        contarClientes();
        contarRepartidores();
        contarAdministradores();
    }

    private void tipoUsuario(){

        String id = mAuth.getCurrentUser().getUid();

        //Evalua los usuarios Administradores dentro de la BD
        mDatabase.child("usuarios").child("administradores").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child("nombre").getValue().toString();
                    textViewAdmin.setText("Bienvenido: "+ nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void contarClientes(){
        mDatabase.child("usuarios").child("clientes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvAdminCantClientes.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }

    private void contarRepartidores(){
        mDatabase.child("usuarios").child("repartidores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvAdminCantRepartidores.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }

    private void contarAdministradores(){
        mDatabase.child("usuarios").child("administradores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvAdminCantAdmin.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }
}