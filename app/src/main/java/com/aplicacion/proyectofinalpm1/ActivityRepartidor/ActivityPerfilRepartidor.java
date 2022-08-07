package com.aplicacion.proyectofinalpm1.ActivityRepartidor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aplicacion.proyectofinalpm1.ActivityControl.ActivityLogin;
import com.aplicacion.proyectofinalpm1.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class ActivityPerfilRepartidor extends AppCompatActivity {

    TextView txtRepartidorNombre;
    TextView txtRepartidorApellido;
    TextView txtRepartidorTelefono;
    TextView txtRepartidorDireccion;
    TextView txtRepartidorCorreo;

    ImageView imgRepartidorImg;

    Button btnRepartidorRCon;
    Button btnRepartidorGua;
    Button btnRepartidorRegrM;
    Button btnRepartidorImagen;

    String idUsuario = "";
    String email = "";

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    DatabaseReference imgref;
    StorageReference storageReference;
    ProgressDialog cargando;
    private Bitmap thumb_bitmap = null;
    //

    //Codigo para BD
    String nombre = "";
    String apellido = "";
    String telefono = "";
    String direccion = "";
    String correo = "";

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_repartidor);

        mAuth = FirebaseAuth.getInstance();

        idUsuario = mAuth.getCurrentUser().getUid();
        email = mAuth.getCurrentUser().getEmail();

        //Cuadros de Texto
        txtRepartidorNombre = (TextView) findViewById(R.id.txtRepartidorNombre);
        txtRepartidorApellido = (TextView) findViewById(R.id.txtRepartidorApellido);
        txtRepartidorTelefono = (TextView) findViewById(R.id.txtRepartidorTelefono);
        txtRepartidorDireccion = (TextView) findViewById(R.id.txtRepartidorDirec);
        txtRepartidorCorreo = (TextView) findViewById(R.id.txtRepartidorCorreo);

        //Botones
        btnRepartidorRCon = (Button) findViewById(R.id.btnRepartidorRCon);
        btnRepartidorGua = (Button) findViewById(R.id.btnRepartidorGua);
        btnRepartidorRegrM = (Button) findViewById(R.id.btnRepartidorRegrM);
        btnRepartidorImagen = (Button) findViewById(R.id.btnRepartidorImagen);

        //Barra de progreso
        mDialog = new ProgressDialog(this);

        //Imagen de Perfil
        imgRepartidorImg = (ImageView) findViewById(R.id.imgRepartidorImg);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("usuarios").child("repartidores").child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String nombre = dataSnapshot.child("nombre").getValue().toString();
                    String apellido = dataSnapshot.child("apellido").getValue().toString();
                    String telefono = dataSnapshot.child("telefono").getValue().toString();
                    String direccion = dataSnapshot.child("direccion").getValue().toString();
                    String correo = dataSnapshot.child("correo").getValue().toString();
                    String imagenURL = dataSnapshot.child("imagenUrl").getValue().toString();

                    //Carga de la imagen
                    Picasso.with(ActivityPerfilRepartidor.this).load(imagenURL).into(imgRepartidorImg);

                    txtRepartidorNombre.setText(nombre);
                    txtRepartidorApellido.setText(apellido);
                    txtRepartidorTelefono.setText(telefono);
                    txtRepartidorDireccion.setText(direccion);
                    txtRepartidorCorreo.setText(correo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imgref = FirebaseDatabase.getInstance().getReference().child("usuarios");
        storageReference = FirebaseStorage.getInstance().getReference().child("img_perfil_repartidores");
        cargando = new ProgressDialog(this);

        //Boton cambio de imagen
        btnRepartidorImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(ActivityPerfilRepartidor.this);
            }
        });

        //Codigo para base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Boton Cambio de Contraseña
        btnRepartidorRCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPerfilRepartidor.this);
                builder.setMessage("¿Estás Seguro que Deseas Cambiar tu Contraseña?")
                        .setTitle("Atención!!!");

                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Reseteo de la contraseña
                        if (!email.isEmpty()){
                            mDialog.setMessage("Espere un Momento...");
                            mDialog.setCanceledOnTouchOutside(false);
                            mDialog.show();
                            resetContra();
                        }
                        mDialog.dismiss();
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

        //Boton Guardar Cambios
        btnRepartidorGua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = txtRepartidorCorreo.getText().toString().trim();
                nombre = txtRepartidorNombre.getText().toString().trim();
                apellido = txtRepartidorApellido.getText().toString().trim();
                telefono = txtRepartidorTelefono.getText().toString().trim();
                direccion = txtRepartidorDireccion.getText().toString().trim();

                if (!nombre.isEmpty() && !apellido.isEmpty() && !telefono.isEmpty() && !direccion.isEmpty()
                    && !correo.isEmpty()){

                    //Actualización los datos del usuario
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre", txtRepartidorNombre.getText().toString().trim());
                    map.put("apellido", txtRepartidorApellido.getText().toString().trim());
                    map.put("telefono", txtRepartidorTelefono.getText().toString().trim());
                    map.put("direccion", txtRepartidorDireccion.getText().toString().trim());

                    String id = mAuth.getCurrentUser().getUid();

                    imgref.child("repartidores").child(id).updateChildren(map);
                    cargando.dismiss();
                    Toast.makeText(ActivityPerfilRepartidor.this, "Datos Actualizados", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ActivityPerfilRepartidor.this, "Complete todos los campos", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Boton Regresar al Menu
        btnRepartidorRegrM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityPerfilRepartidor.this, ActivityRepartidor.class));
            }
        });
    }

    private void resetContra() {
        mAuth.setLanguageCode("es");

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ActivityPerfilRepartidor.this,
                            "Se a enviado un Correo para Restablecer tu Contraseña",
                            Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    startActivity(new Intent(ActivityPerfilRepartidor.this, ActivityLogin.class));
                    finish();
                } else {
                    Toast.makeText(ActivityPerfilRepartidor.this,
                            "No se pudo enviar el correo de Restablecimiento de Contraseña",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Subida de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri imageuri = CropImage.getPickImageResultUri(this, data);

            //Recortar imagen
            CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(250, 250)
                    .setAspectRatio(1,1).start(ActivityPerfilRepartidor.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();

                File url = new File(resultUri.getPath());

                Picasso.with(this).load(url).into(imgRepartidorImg);

                //Comprimir la Imagen
                try {
                    thumb_bitmap = new Compressor(this).setMaxWidth(250).setMaxHeight(250).setQuality(90).compressToBitmap(url);
                } catch (IOException e){
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                final byte [] thumb_byte = byteArrayOutputStream.toByteArray();
                //Finaliza el compresor

                int p = (int) (Math.random() * 25 + 1); int s = (int) (Math.random() * 25 + 1);
                int t = (int) (Math.random() * 25 + 1); int c = (int) (Math.random() * 25 + 1);
                int numero1 = (int) (Math.random() * 1012 +2111);
                int numero2 = (int) (Math.random() * 1012 +2111);

                String[] elementos = {"a", "b", "c", "d","e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                        "q", "r","s", "t","u", "v","w", "x", "y", "x"};

                final String aleatorio = elementos[p] + elementos[s] + numero1 + elementos[t] + elementos[c] +
                        numero2 + "app.jpg";

                //Subir la imagen
                btnRepartidorGua.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cargando.setTitle("Guardando Información del Usuario");
                        cargando.setMessage("Espere por favor...");
                        cargando.show();

                        StorageReference ref = storageReference.child(aleatorio);
                        UploadTask uploadTask = ref.putBytes(thumb_byte);

                        //Subir la imagen a la Base de Datos
                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()){
                                    throw Objects.requireNonNull(task.getException());
                                }
                                return ref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri dowloaduri = task.getResult();

                                correo = txtRepartidorCorreo.getText().toString().trim();
                                nombre = txtRepartidorNombre.getText().toString().trim();
                                apellido = txtRepartidorApellido.getText().toString().trim();
                                telefono = txtRepartidorTelefono.getText().toString().trim();
                                direccion = txtRepartidorDireccion.getText().toString().trim();

                                if (!nombre.isEmpty() && !apellido.isEmpty() && !telefono.isEmpty() && !direccion.isEmpty()
                                        && !correo.isEmpty()) {
                                    //Actualización los datos del usuario
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("nombre", txtRepartidorNombre.getText().toString().trim());
                                    map.put("apellido", txtRepartidorApellido.getText().toString().trim());
                                    map.put("telefono", txtRepartidorTelefono.getText().toString().trim());
                                    map.put("direccion", txtRepartidorDireccion.getText().toString().trim());
                                    map.put("imagenUrl", dowloaduri.toString());

                                    String id = mAuth.getCurrentUser().getUid();

                                    imgref.child("repartidores").child(id).updateChildren(map);
                                    cargando.dismiss();
                                    Toast.makeText(ActivityPerfilRepartidor.this, "Datos Actualizados", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        }
    }
}