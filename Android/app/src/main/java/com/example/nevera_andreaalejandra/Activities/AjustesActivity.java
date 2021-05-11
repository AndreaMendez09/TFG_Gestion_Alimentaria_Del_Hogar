package com.example.nevera_andreaalejandra.Activities;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    private EditText nombreedit;
    private EditText correoEdit;
    private EditText apellidosEdit;

    private TextView sugerencia;
    private UsuarioModelo usuario;
    private String NombreUsuario;
    private String ApellidoUsuario;
    private String CorreoUsuario;
    private String IdUsuario;
    private Button enviar;
    private Button editarUsuario;
    private Button editarContraseña;
    //Notificaciones
    private NotificationHandler notificationHandler;
    private int counter = 0;
    private Switch switchNotificaciones;
    private String switchTextOn = "Activado";
    private String switchTextOff = "Desactivado";
    private boolean isHighImportance = true;

    private LinearLayout logout;


    //Para realizar la conexon con la firebase
    private DatabaseReference mDataBase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        //Enlazamos con el xml

        sugerencia =  (TextView) findViewById(R.id.sugerencia);
        enviar = (Button) findViewById(R.id.buttonEnviarSugerencia);
        editarUsuario = (Button) findViewById(R.id.buttonEditarPerfil);
        editarContraseña = (Button) findViewById(R.id.buttonCambiarContraseña);
        nombreedit = (EditText) findViewById(R.id.nombreUsuario);
        correoEdit = (EditText) findViewById(R.id.correoUsuario);
        apellidosEdit = (EditText) findViewById(R.id.apellidosUsuario);
        logout = (LinearLayout) findViewById(R.id.logout);
        setToolbar();


        notificationHandler = new NotificationHandler(this);

        //1.Las referencias de auntenticacion de la base de datos
        //leeemos la lista
        mDataBase = FirebaseDatabase.getInstance().getReference();
        //Para inicializar la instancia de autenticación
        mAuth = FirebaseAuth.getInstance();

        //Visualizamos los datos del usuario
        DatosUsuario();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Se pulsa en logout", Toast.LENGTH_SHORT).show();
                //TODO POR SI QUEREMOS METER UN DIALOG DE SI O NO
                mAuth.signOut(); //Desconectamos al usuario
                //Para volver al fragment donde nos encontramos
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);//Establecemos primero donde estamos y luego donde vamos
                startActivity(intent);//Iniciamos el intent
            }
        });
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarEmail();
                Toast.makeText(AjustesActivity.this, "Se pulsa en enviar", Toast.LENGTH_SHORT).show();

            }
        });
        editarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarUsuario(usuario);
                Toast.makeText(AjustesActivity.this, "Se ha editado el Usuario", Toast.LENGTH_SHORT).show();

            }
        });
        editarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(AjustesActivity.this, "Se ha pulsado en contraseña", Toast.LENGTH_SHORT).show();

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

                            nombreedit.setText(usuario.getNombre());
                            correoEdit.setText(usuario.getCorreo());
                            apellidosEdit.setText(usuario.getApellido());
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
        //sendNotification(isHighImportance, act);
    }


    //Para poner la imagen en el toolbar
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //He cambiado esto, porque ponia que el otro era para versiones de 30, y esta es la 26
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_undo); //Esto de aqui pone la imagen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void enviarEmail(){

        //Instanciamos un Intent del tipo ACTION_SEND
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        //Aqui definimos la tipologia de datos del contenido dle Email en este caso text/html
        emailIntent.setType("text/html");

        // Indicamos con un Array de tipo String las direcciones de correo a las cuales enviar
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jakaleja5@gmail.com"});
        // Aqui definimos un titulo para el Email
        emailIntent.putExtra(android.content.Intent.EXTRA_TITLE, "Sugerencias");
        // Aqui definimos un Asunto para el Email
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "El Asunto");
        // Aqui obtenemos la referencia al texto y lo pasamos al Email Intent
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.my_text));
        try {
            //Enviamos el Correo iniciando una nueva Activity con el emailIntent.
            startActivity(Intent.createChooser(emailIntent, "Enviar Correo..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AjustesActivity.this, "No hay ningun cliente de correo instalado.", Toast.LENGTH_SHORT).show();
        }
    }
    private void editarUsuario(UsuarioModelo usuario_eliminar){
        //Obtenemos los datos del edit text
        String nombreUsuario = nombreedit.getText().toString().trim();
        String correoUsuario = correoEdit.getText().toString().trim();
        String apellidoUsuario = apellidosEdit.getText().toString();

        //Creamos un usuario nuevo
        UsuarioModelo usuario = new UsuarioModelo(nombreUsuario,apellidoUsuario, correoUsuario);

        boolean Vacio = true;
        if(nombreUsuario.isEmpty()){
            nombreedit.setError("El nombre es obligatorio");
            Vacio = false;
        }
        if(correoUsuario.isEmpty()){
            correoEdit.setError("El correo es obligatorio");
            Vacio = false;
        }
        if(nombreUsuario.isEmpty()){
            apellidosEdit.setError("El apellido es obligatorio");
            Vacio = false;
        }
        //Si todos los campos estan rellenos, procedemos a editar en la BBDD
        if(Vacio){
            mDataBase.child(usuario_eliminar.getId_usuario()).child("nombre").setValue(usuario.getNombre());
            mDataBase.child(usuario_eliminar.getId_usuario()).child("apellido").setValue(usuario.getApellido());
            mDataBase.child(usuario_eliminar.getId_usuario()).child("correo").setValue(usuario.getCorreo());
        }else {//Informamos al usuario que debe rellenar todos los campos
            Toast.makeText(AjustesActivity.this, "Rellena los campos necesarios", Toast.LENGTH_SHORT).show();
        }

    }


}
