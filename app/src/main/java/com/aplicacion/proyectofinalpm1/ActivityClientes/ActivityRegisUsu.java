package com.aplicacion.proyectofinalpm1.ActivityClientes;

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

import com.aplicacion.proyectofinalpm1.ActivityControl.ActivityLogin;
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

public class ActivityRegisUsu extends AppCompatActivity {

    EditText txtRegisCorreo, txtRegisNombre, txtRegisApellido, txtRegisTelefono, txtRegisDirec;

    TextInputLayout txtRegisContra;
    Button btnRegisCrear;
    Button btnRegisCancelar;

    AwesomeValidation awesomeValidation;
    FirebaseAuth firebaseAuth;

    //codigo para subida de imagen
    ImageView foto;
    Button btnRegisImagen;

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
        setContentView(R.layout.activity_regis_usu);

        //Codigo subida de imagen
        foto = (ImageView) findViewById(R.id.imgFoto);
        btnRegisImagen = (Button) findViewById(R.id.btnRegisImagen);

        imgref = FirebaseDatabase.getInstance().getReference().child("usuarios");
        storageReference = FirebaseStorage.getInstance().getReference().child("img_perfil_clientes");
        cargando = new ProgressDialog(this);

        btnRegisImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(ActivityRegisUsu.this);
            }
        }); //termina codigo subida de imagen

        //Codigo para base de datos
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //

        firebaseAuth = FirebaseAuth.getInstance();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.txtRegisCorreo, Patterns.EMAIL_ADDRESS, R.string.invalid_mail);
        awesomeValidation.addValidation(this, R.id.txtRegisContra, ".{8,}", R.string.invalid_password);

        txtRegisCorreo = (EditText) findViewById(R.id.txtRegisCorreo);
        txtRegisNombre = (EditText) findViewById(R.id.txtRegisNombre);
        txtRegisApellido = (EditText) findViewById(R.id.txtRegisApellido);
        txtRegisTelefono = (EditText) findViewById(R.id.txtRegisTelefono);
        txtRegisDirec = (EditText) findViewById(R.id.txtRegisDirec);
        txtRegisContra = (TextInputLayout) findViewById(R.id.txtRegisContra);

        //Cancela la creación del usuario
        btnRegisCancelar = (Button) findViewById(R.id.btnRegisCancelar);
        btnRegisCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityRegisUsu.this, ActivityLogin.class);
                startActivity(i);
            }
        });

        btnRegisCrear = (Button) findViewById(R.id.btnRegisCrear);
        btnRegisCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!correo.isEmpty() && !nombre.isEmpty() && !contra.isEmpty()){
                    if (contra.length() >= 8){
                        registrarUsuario();
                    } else {
                        Toast.makeText(ActivityRegisUsu.this, "La contraseña debe contener al menos 8 digitos", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                    //Toasterror(errorCode);
                    Toast.makeText(ActivityRegisUsu.this, "Complete todos los campos", Toast.LENGTH_LONG).show();
                }

                //
            }
        });
    }

    private void registrarUsuario() {
        //Codigo para base de datos

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
                    .setAspectRatio(1,1).start(ActivityRegisUsu.this);
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
                btnRegisCrear.setOnClickListener(new View.OnClickListener() {
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

                                correo = txtRegisCorreo.getText().toString();
                                nombre = txtRegisNombre.getText().toString();
                                apellido = txtRegisApellido.getText().toString();
                                telefono = txtRegisTelefono.getText().toString();
                                direccion = txtRegisDirec.getText().toString();
                                contra = txtRegisContra.getEditText().getText().toString().trim();

                                //Creación de usuario en la tabla de Authenticación
                                mAuth.createUserWithEmailAndPassword(correo, contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            //Registra los usuario en la BD
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("nombre", txtRegisNombre.getText().toString().trim());
                                            map.put("apellido", txtRegisApellido.getText().toString().trim());
                                            map.put("telefono", txtRegisTelefono.getText().toString().trim());
                                            map.put("direccion", txtRegisDirec.getText().toString().trim());
                                            map.put("correo", txtRegisCorreo.getText().toString().trim());
                                            map.put("imagenUrl", dowloaduri.toString());

                                            String id = mAuth.getCurrentUser().getUid();

                                            imgref.child("clientes").child(id).setValue(map);
                                            cargando.dismiss();
                                            Toast.makeText(ActivityRegisUsu.this, "Usuario Creado con Exito", Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(ActivityRegisUsu.this, "No se pudo registrar el usuario", Toast.LENGTH_LONG).show();
                                        }
                                        //Lleva al login despues de crear el usuario
                                        FirebaseAuth.getInstance().signOut();
                                        Intent i = new Intent(ActivityRegisUsu.this, ActivityLogin.class);
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
                Toast.makeText(ActivityRegisUsu.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(ActivityRegisUsu.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(ActivityRegisUsu.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(ActivityRegisUsu.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                txtRegisCorreo.setError("La dirección de correo electrónico está mal formateada.");
                txtRegisCorreo.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(ActivityRegisUsu.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                txtRegisContra.setError("la contraseña es incorrecta ");
                txtRegisContra.requestFocus();
                //txtRegisContra.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(ActivityRegisUsu.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(ActivityRegisUsu.this, "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(ActivityRegisUsu.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(ActivityRegisUsu.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                txtRegisCorreo.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                txtRegisCorreo.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(ActivityRegisUsu.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(ActivityRegisUsu.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(ActivityRegisUsu.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(ActivityRegisUsu.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(ActivityRegisUsu.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(ActivityRegisUsu.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(ActivityRegisUsu.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                txtRegisContra.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                txtRegisContra.requestFocus();
                break;
        }
    }
}