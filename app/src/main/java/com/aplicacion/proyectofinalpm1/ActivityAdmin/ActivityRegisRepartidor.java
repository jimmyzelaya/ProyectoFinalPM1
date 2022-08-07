package com.aplicacion.proyectofinalpm1.ActivityAdmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aplicacion.proyectofinalpm1.R;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class ActivityRegisRepartidor extends AppCompatActivity {

    EditText txtReparCorreo, txtReparNombre, txtReparApellido, txtReparTelefono, txtReparDirec;
    TextInputLayout txtReparContra;
    Button btnReparCrear;
    Button btnReparCancelar;

    AwesomeValidation awesomeValidation;
    FirebaseAuth firebaseAuth;

    ///codigo para subida de imagen
    ImageView foto;
    Button btnReparImagen;

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
    String contra = "";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis_repartidor);

        //Regresa al menu Administrador
        btnReparCancelar = (Button) findViewById(R.id.btnReparCancelar);
        btnReparCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityRegisRepartidor.this, ActivityAdministrador.class));
                finish();
            }
        });

        //Codigo subida de imagen
        foto = (ImageView) findViewById(R.id.imgFotoRepartidor);
        btnReparImagen = (Button) findViewById(R.id.btnRepartImagen);

        imgref = FirebaseDatabase.getInstance().getReference().child("usuarios");
        storageReference = FirebaseStorage.getInstance().getReference().child("img_perfil_repartidores");
        cargando = new ProgressDialog(this);

        btnReparImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(ActivityRegisRepartidor.this);
            }
        }); //termina codigo subida de imagen

        //Codigo para base de datos
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //

        firebaseAuth = FirebaseAuth.getInstance();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.txtReparCorreo, Patterns.EMAIL_ADDRESS, R.string.invalid_mail);
        awesomeValidation.addValidation(this, R.id.txtReparContra, ".{8,}", R.string.invalid_password);

        txtReparCorreo = (EditText) findViewById(R.id.txtReparCorreo);
        txtReparNombre = (EditText) findViewById(R.id.txtReparNombre);
        txtReparApellido = (EditText) findViewById(R.id.txtReparApellido);
        txtReparTelefono = (EditText) findViewById(R.id.txtReparTelefono);
        txtReparDirec = (EditText) findViewById(R.id.txtReparDirec);
        txtReparContra = (TextInputLayout) findViewById(R.id.txtReparContra);

        btnReparCrear = (Button) findViewById(R.id.btnReparCrear);
        btnReparCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!correo.isEmpty() && !nombre.isEmpty() && !contra.isEmpty()){
                    if (contra.length() >= 8){
                        //registrarUsuario();
                    } else {
                        Toast.makeText(ActivityRegisRepartidor.this, "La contraseña debe contener al menos 8 digitos", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ActivityRegisRepartidor.this, "COmplete todos los campos", Toast.LENGTH_LONG).show();
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
                    .setAspectRatio(1,1).start(ActivityRegisRepartidor.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();

                File url = new File(resultUri.getPath());

                Picasso.with(this).load(url).into(foto);

                //COmprimir la Imagen
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
                btnReparCrear.setOnClickListener(new View.OnClickListener() {
                    //btnSubirFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cargando.setTitle("Creando Usuario");
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

                                correo = txtReparCorreo.getText().toString();
                                nombre = txtReparNombre.getText().toString();
                                apellido = txtReparApellido.getText().toString();
                                telefono = txtReparTelefono.getText().toString();
                                direccion = txtReparDirec.getText().toString();
                                contra = txtReparContra.getEditText().getText().toString();

                                //Creación de usuario en la tabla de Authenticación
                                mAuth.createUserWithEmailAndPassword(correo, contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            //Registra los usuario en la BD
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("nombre", txtReparNombre.getText().toString().trim());
                                            map.put("apellido", txtReparApellido.getText().toString().trim());
                                            map.put("telefono", txtReparTelefono.getText().toString().trim());
                                            map.put("direccion", txtReparDirec.getText().toString().trim());
                                            map.put("correo", txtReparCorreo.getText().toString().trim());
                                            map.put("imagenUrl", dowloaduri.toString());

                                            String id = mAuth.getCurrentUser().getUid();

                                            imgref.child("repartidores").child(id).setValue(map);
                                            cargando.dismiss();
                                            Toast.makeText(ActivityRegisRepartidor.this, "Usuario Creado con Exito", Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(ActivityRegisRepartidor.this, "No se pudo registrar el usuario", Toast.LENGTH_LONG).show();
                                        }
                                        //Lleva al login despues de crear el usuario
                                        FirebaseAuth.getInstance().signOut();
                                        Intent i = new Intent(ActivityRegisRepartidor.this, ActivityAdministrador.class);
                                        startActivity(i);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    }

    private void Toasterror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(ActivityRegisRepartidor.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(ActivityRegisRepartidor.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(ActivityRegisRepartidor.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(ActivityRegisRepartidor.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                txtReparCorreo.setError("La dirección de correo electrónico está mal formateada.");
                txtReparCorreo.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(ActivityRegisRepartidor.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                txtReparContra.setError("la contraseña es incorrecta ");
                txtReparContra.requestFocus();
                txtReparContra.getEditText().setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(ActivityRegisRepartidor.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(ActivityRegisRepartidor.this, "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(ActivityRegisRepartidor.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(ActivityRegisRepartidor.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                txtReparCorreo.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                txtReparCorreo.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(ActivityRegisRepartidor.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(ActivityRegisRepartidor.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(ActivityRegisRepartidor.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(ActivityRegisRepartidor.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(ActivityRegisRepartidor.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(ActivityRegisRepartidor.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(ActivityRegisRepartidor.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                txtReparContra.setError("La contraseña no es válida, debe tener al menos 8 caracteres");
                txtReparContra.requestFocus();
                break;
        }
    }
}