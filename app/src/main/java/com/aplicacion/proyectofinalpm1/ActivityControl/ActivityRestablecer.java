package com.aplicacion.proyectofinalpm1.ActivityControl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aplicacion.proyectofinalpm1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityRestablecer extends AppCompatActivity {

    EditText txtRestCorreo;
    EditText txtRestCo;
    Button btnRestContra;

    String correo = "";
    FirebaseAuth mAuth;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restablecer);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        txtRestCorreo = (EditText) findViewById(R.id.txtRestCorreo);

        btnRestContra = (Button) findViewById(R.id.btnRestContra);
        btnRestContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = txtRestCorreo.getText().toString();

                if (!correo.isEmpty()){
                    mDialog.setMessage("Espere un Momento...");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    resetContra();
                } else {
                    Toast.makeText(ActivityRestablecer.this, "Debe Ingresar una Dirección de Correo", Toast.LENGTH_LONG).show();
                }
                mDialog.dismiss();
            }
        });
    }

    private void resetContra() {
        mAuth.setLanguageCode("es");

        mAuth.sendPasswordResetEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ActivityRestablecer.this,
                            "Se a enviado un Correo para Restablecer tu Contraseña",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ActivityRestablecer.this, ActivityLogin.class));
                } else {
                    Toast.makeText(ActivityRestablecer.this,
                            "No se pudo enviar el correo de Restablecimiento de Contraseña",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}