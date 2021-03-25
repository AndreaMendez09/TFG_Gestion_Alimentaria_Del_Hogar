package com.example.nevera_andreaalejandra.Activities;

import android.os.Bundle;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.Models.UsuarioModelo;
import com.example.nevera_andreaalejandra.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Ajustes extends AppCompatActivity {
    private TextView nombre;

    private TextView correo;
    private TextView contraseña;
    private UsuarioModelo usuario;
    private String NombreUsuario;
    private String ApellidoUsuario;
    private String CorreoUsuario;
    private String IdUsuario;

    //Para realizar la conexon con la firebase
    private DatabaseReference mDataBase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        nombre = (TextView) findViewById(R.id.nombreUsuario);
        correo = (TextView) findViewById(R.id.correoUsuario);

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
}
