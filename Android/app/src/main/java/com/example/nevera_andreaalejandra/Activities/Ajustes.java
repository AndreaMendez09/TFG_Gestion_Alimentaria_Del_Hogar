package com.example.nevera_andreaalejandra.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nevera_andreaalejandra.Models.UsuarioModelo;
import com.example.nevera_andreaalejandra.R;


public class Ajustes extends AppCompatActivity {
    private TextView nombre;
    private TextView correo;
    private TextView contraseña;
    private UsuarioModelo usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        nombre = (TextView) findViewById(R.id.nombreUsuario);
        correo = (TextView) findViewById(R.id.correoUsuario);
        contraseña = (TextView) findViewById(R.id.contraseñaUsuario);
        

        //nombre.setText(usuario.getNombre());
        //correo.setText(usuario.getCorreo());




    }
}
