package com.aplicacion.proyectofinalpm1.ActivityClientes.Notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.aplicacion.proyectofinalpm1.ActivityClientes.ActivityMenu;
import com.aplicacion.proyectofinalpm1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class Fcm extends FirebaseMessagingService {

    //Nos da el token del telefono el id del dispositivo, con este token se puede enviar una notificacion especifica
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.e("token","mi token es: "+s);
        guardartoken(s);

    }

    private void guardartoken(String s){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("token");
        ref.child("tipoUsuario()").setValue(s);
        SharedPreferences.Editor editor = getSharedPreferences("unique_name", MODE_PRIVATE).edit();
        editor.putString("token", s);
        editor.commit();
    }


    //recibimos todas las notificaciones o todos los datos que lleguen sera aqui
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from= remoteMessage.getFrom();
        /* Log.e("TAG", "Mensaje recibido de" +from);
       if (remoteMessage.getNotification() !=null){
            Log.e( "TAG", "el titulo es: "+remoteMessage.getNotification().getTitle());
            Log.e( "TAG", "el detalle es: "+remoteMessage.getNotification().getBody());
        }*/

        if (remoteMessage.getData().size() >0){

  /*          Log.e("TAG", "Titulo es: "+remoteMessage.getData().get("titulo"));
            Log.e("TAG", "Detalle es: "+remoteMessage.getData().get("detalle"));
            Log.e("TAG", "el color es: "+remoteMessage.getData().get("color"));
*/
            String titulo = remoteMessage.getData().get("titulo");
            String detalle = remoteMessage.getData().get("detalle");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                mayorqueoreo(titulo,detalle);
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O){
                menorqueoreo();
            }



        }


    }

    private void menorqueoreo() {
        String id= "mensaje";
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc= new NotificationChannel(id, "nuevo",NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            nm.createNotificationChannel(nc);

        }

        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("")
                .setContentIntent(clicknoti())
                .setContentInfo("nuevo");

        Random random =new Random();
        int idNotify = random.nextInt(8000);

        assert nm != null;
        nm.notify(idNotify,builder.build());
    }


    private void mayorqueoreo(String titulo, String detalle) {

        String id= "mensaje";
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc= new NotificationChannel(id, "nuevo",NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            nm.createNotificationChannel(nc);

        }

        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(detalle)
                .setContentIntent(clicknoti())
                .setContentInfo("nuevo");

        Random random =new Random();
        int idNotify = random.nextInt(8000);

        assert nm != null;
        nm.notify(idNotify,builder.build());

    }

    public PendingIntent clicknoti (){
        Intent nf =new Intent(getApplicationContext(), ActivityMenu.class);
        nf.putExtra("color", "rojo");
        nf.setFlags((Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        return PendingIntent.getActivity(this,0, nf, 0);
    }


}
