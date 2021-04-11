package com.example.nevera_andreaalejandra.Activities;

import android.app.Notification;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nevera_andreaalejandra.Models.UsuarioModelo;
import com.example.nevera_andreaalejandra.R;
import com.example.nevera_andreaalejandra.Util.NotificationHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class AjustesActivity extends AppCompatActivity {
    //Valores de la BBDD
    private TextView nombre;
    private TextView correo;
    private UsuarioModelo usuario;
    private String NombreUsuario;
    private String ApellidoUsuario;
    private String CorreoUsuario;
    private String IdUsuario;

    //Notificaciones
    private NotificationHandler notificationHandler;
    private int counter = 0;
    private Switch switchNotificaciones;
    private String switchTextOn = "Activado";
    private String switchTextOff = "Desactivado";
    private boolean isHighImportance = true;


    //Para realizar la conexon con la firebase
    private DatabaseReference mDataBase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        //Enlazamos con el xml
        nombre = (TextView) findViewById(R.id.nombreUsuario);
        correo = (TextView) findViewById(R.id.correoUsuario);
        switchNotificaciones = (Switch) findViewById(R.id.switchNotificaciones);

        setToolbar();

        notificationHandler = new NotificationHandler(this);

        //1.Las referencias de auntenticacion de la base de datos
        //leeemos la lista
        mDataBase = FirebaseDatabase.getInstance().getReference();
        //Para inicializar la instancia de autenticación
        mAuth = FirebaseAuth.getInstance();

        //Visualizamos los datos del usuario
        DatosUsuario();

        //Oyentes
        switchNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchNotificaciones.setText((switchNotificaciones.isChecked()) ? switchTextOn : switchTextOff);
                if (switchNotificaciones.isChecked()) {
                    sendNotification(isHighImportance);
                    Toast.makeText(getApplicationContext(), "Activado", Toast.LENGTH_LONG).show();
                }else {
                    sendNotification(isHighImportance);
                    Toast.makeText(getApplicationContext(), "Desactivado", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void DatosUsuario() {
        mDataBase.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        NombreUsuario = ds.child("nombre").getValue().toString();
                        ApellidoUsuario = ds.child("apellido").getValue().toString();
                        CorreoUsuario = ds.child("correo").getValue().toString();
                        IdUsuario = ds.getKey();


                        //Combrabamos que usuario esta conectado
                        String id = mAuth.getCurrentUser().getUid();

                        if (IdUsuario.equals(id)) {
                          usuario = new UsuarioModelo(IdUsuario, NombreUsuario, ApellidoUsuario, CorreoUsuario);

                            nombre.setText(usuario.getNombre() + " " + usuario.getApellido());
                            correo.setText(usuario.getCorreo());
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    public void change(CompoundButton buttonView, boolean isChecked) {
        isHighImportance = isChecked;
        switchNotificaciones.setText((isChecked) ? switchTextOn : switchTextOff);
        sendNotification(isHighImportance);
    }

    private void sendNotification(boolean isHighImportance) {
        Toast.makeText(getApplicationContext(), "notificacion", Toast.LENGTH_LONG).show();

        String title = "Notificaciones";
        String messageActivada = "Las notificaciones estan activadas";
        String messageDesactivadas = "Las notificaciones estan Desactivadas";


        if (isHighImportance==true) {
            Notification.Builder nb = notificationHandler.createNotification(title, messageActivada, isHighImportance);
            notificationHandler.getManager().notify(++counter, nb.build());
            notificationHandler.publishNotificationSummaryGroup(isHighImportance);
        }if(isHighImportance==false) {
            Notification.Builder nb = notificationHandler.createNotification(title, messageDesactivadas, isHighImportance);
            notificationHandler.getManager().notify(++counter, nb.build());
            notificationHandler.publishNotificationSummaryGroup(isHighImportance);
        }
    }

    //Para poner la imagen en el toolbar
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //He cambiado esto, porque ponia que el otro era para versiones de 30, y esta es la 26
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_undo); //Esto de aqui pone la imagen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
