package com.example.nevera_andreaalejandra.Activities;

import android.app.Notification;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.Models.UsuarioModelo;
import com.example.nevera_andreaalejandra.R;
import com.example.nevera_andreaalejandra.Util.NotificationHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class AjustesActivity extends AppCompatActivity {
    private TextView nombre;

    private TextView correo;
    private TextView contraseña;
    private UsuarioModelo usuario;
    private String NombreUsuario;
    private String ApellidoUsuario;
    private String CorreoUsuario;
    private String IdUsuario;
    private NotificationHandler notificationHandler;
    private int counter = 0;
    @BindView(R.id.switchNotificaciones)
    Switch switchNotificaciones;
    @BindString(R.string.switch_notifications_on) String switchTextOn;
    @BindString(R.string.switch_notifications_off) String switchTextOff;

    private boolean isHighImportance = false;

    //Para realizar la conexon con la firebase
    private DatabaseReference mDataBase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        nombre = (TextView) findViewById(R.id.nombreUsuario);
        correo = (TextView) findViewById(R.id.correoUsuario);
        //Para las notifcaciones
        ButterKnife.bind(this); // Right after setContentView
        notificationHandler = new NotificationHandler(this);

        //1.Las referencias de auntenticacion de la base de dato
        //leeemos la lista
        mDataBase = FirebaseDatabase.getInstance().getReference();
        //Para inicializar la instancia de autenticación
        mAuth = FirebaseAuth.getInstance();

        //
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

    @OnClick(R.id.buttonNotificacion)
    public void click() {
        sendNotification();
    }
    @OnCheckedChanged(R.id.switchNotificaciones)
    public void change(CompoundButton buttonView, boolean isChecked) {
        isHighImportance = isChecked;
        switchNotificaciones.setText((isChecked) ? switchTextOn : switchTextOff);
    }

    private void sendNotification() {
        String title = "Probando";
        String message = "Desde ajustes";

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(message)) {
            Notification.Builder nb = notificationHandler.createNotification(title, message, isHighImportance);
            notificationHandler.getManager().notify(++counter, nb.build());
            notificationHandler.publishNotificationSummaryGroup(isHighImportance);
        }
    }
}
