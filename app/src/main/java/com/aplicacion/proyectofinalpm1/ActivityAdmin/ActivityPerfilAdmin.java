package com.aplicacion.proyectofinalpm1.ActivityAdmin;

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
import com.aplicacion.proyectofinalpm1.ActivityRepartidor.ActivityRepartidor;
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

public class ActivityPerfilAdmin extends AppCompatActivity {

    TextView txtAdminNombre;
    TextView txtAdminApellido;
    TextView txtAdminTelefono;
    TextView txtAdminDireccion;
    TextView txtAdminCorreo;

    ImageView imgAdminImg;

    Button btnAdminRCon;
    Button btnAdminGua;
    Button btnAdminRegrM;
    Button btnAdminImagen;

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
        setContentView(R.layout.activity_perfil_admin);


        mAuth = FirebaseAuth.getInstance();

        idUsuario = mAuth.getCurrentUser().getUid();
        email = mAuth.getCurrentUser().getEmail();

        //Cuadros de Texto
        txtAdminNombre = (TextView) findViewById(R.id.txtAdminNombre);
        txtAdminApellido = (TextView) findViewById(R.id.txtAdminApellido);
        txtAdminTelefono = (TextView) findViewById(R.id.txtAdminTelefono);
        txtAdminDireccion = (TextView) findViewById(R.id.txtAdminDirec);
        txtAdminCorreo = (TextView) findViewById(R.id.txtAdminCorreo);

        //Botones
        btnAdminRCon = (Button) findViewById(R.id.btnAdminRCon);
        btnAdminGua = (Button) findViewById(R.id.btnAdminGua);
        btnAdminRegrM = (Button) findViewById(R.id.btnAdminRegrM);
        btnAdminImagen = (Button) findViewById(R.id.btnAdminImagen);

        //Barra de progreso
        mDialog = new ProgressDialog(this);

        //Imagen de Perfil
        imgAdminImg = (ImageView) findViewById(R.id.imgAdminImg);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("usuarios").child("administradores").child(idUsuario).addValueEventListener(new ValueEventListener() {
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
                    Picasso.with(ActivityPerfilAdmin.this).load(imagenURL).into(imgAdminImg);

                    txtAdminNombre.setText(nombre);
                    txtAdminApellido.setText(apellido);
                    txtAdminTelefono.setText(telefono);
                    txtAdminDireccion.setText(direccion);
                    txtAdminCorreo.setText(correo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imgref = FirebaseDatabase.getInstance().getReference().child("usuarios");
        storageReference = FirebaseStorage.getInstance().getReference().child("img_perfil_administradores");
        cargando = new ProgressDialog(this);

        //Boton cambio de imagen
        btnAdminImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(ActivityPerfilAdmin.this);
            }
        });

        //Codigo para base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Boton Cambio de Contraseña
        btnAdminRCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPerfilAdmin.this);
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
        btnAdminGua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = txtAdminCorreo.getText().toString().trim();
                nombre = txtAdminNombre.getText().toString().trim();
                apellido = txtAdminApellido.getText().toString().trim();
                telefono = txtAdminTelefono.getText().toString().trim();
                direccion = txtAdminDireccion.getText().toString().trim();

                if (!nombre.isEmpty() && !apellido.isEmpty() && !telefono.isEmpty() && !direccion.isEmpty()
                        && !correo.isEmpty()){

                    //Actualización los datos del usuario
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre", txtAdminNombre.getText().toString().trim());
                    map.put("apellido", txtAdminApellido.getText().toString().trim());
                    map.put("telefono", txtAdminTelefono.getText().toString().trim());
                    map.put("direccion", txtAdminDireccion.getText().toString().trim());

                    String id = mAuth.getCurrentUser().getUid();

                    imgref.child("administradores").child(id).updateChildren(map);
                    cargando.dismiss();
                    Toast.makeText(ActivityPerfilAdmin.this, "Datos Actualizados", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ActivityPerfilAdmin.this, "Complete todos los campos", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Boton Regresar al Menu
        btnAdminRegrM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityPerfilAdmin.this, ActivityAdministrador.class));
            }
        });
    }

    private void resetContra() {
        mAuth.setLanguageCode("es");

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ActivityPerfilAdmin.this,
                            "Se a enviado un Correo para Restablecer tu Contraseña",
                            Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    startActivity(new Intent(ActivityPerfilAdmin.this, ActivityLogin.class));
                    finish();
                } else {
                    Toast.makeText(ActivityPerfilAdmin.this,
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
                    .setAspectRatio(1,1).start(ActivityPerfilAdmin.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();

                File url = new File(resultUri.getPath());

                Picasso.with(this).load(url).into(imgAdminImg);

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
                btnAdminGua.setOnClickListener(new View.OnClickListener() {
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

                                correo = txtAdminCorreo.getText().toString().trim();
                                nombre = txtAdminNombre.getText().toString().trim();
                                apellido = txtAdminApellido.getText().toString().trim();
                                telefono = txtAdminTelefono.getText().toString().trim();
                                direccion = txtAdminDireccion.getText().toString().trim();

                                if (!nombre.isEmpty() && !apellido.isEmpty() && !telefono.isEmpty() && !direccion.isEmpty()
                                        && !correo.isEmpty()) {
                                    //Actualización los datos del usuario
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("nombre", txtAdminNombre.getText().toString().trim());
                                    map.put("apellido", txtAdminApellido.getText().toString().trim());
                                    map.put("telefono", txtAdminTelefono.getText().toString().trim());
                                    map.put("direccion", txtAdminDireccion.getText().toString().trim());
                                    map.put("imagenUrl", dowloaduri.toString());

                                    String id = mAuth.getCurrentUser().getUid();

                                    imgref.child("administradores").child(id).updateChildren(map);
                                    cargando.dismiss();
                                    Toast.makeText(ActivityPerfilAdmin.this, "Datos Actualizados", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        }
    }
}