package com.example.nevera_andreaalejandra.Fragments.Login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.nevera_andreaalejandra.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class RecuperarFragment extends Fragment {
    //Para enlazarlo con el XML
    private EditText mail_recuperar;
    private Button ButtonEnviar;
    private ImageButton irAtras;

    //Para la BBDD
    private FirebaseAuth mAuth;


    public RecuperarFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Creamos la view
        View view = inflater.inflate(R.layout.fragment_recuperar, container, false);

        //Enlazamos con el xml
        mail_recuperar = (EditText) view.findViewById(R.id.recuperar_mail_login);
        ButtonEnviar = (Button) view.findViewById(R.id.buttonEnviarRecuperar);
        irAtras = (ImageButton) view.findViewById(R.id.irAtras);

        //Para inicializar la instancia de autenticación
        mAuth=FirebaseAuth.getInstance();
        
        ButtonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarEmail();
            }
        });

        irAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new HomeFragment());
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    private void enviarEmail() {
        //Obtenemos el email que ha escrito el usuario
        String email= mail_recuperar.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getContext(),"Debe escribir una dirección",Toast.LENGTH_SHORT).show();
        }else{

            //método que manda un email automático a la dirección proporcionada donde se
            // escribe la nueva contraseña, siempre que exista una cuenta con dicha dirección
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){//En caso exitoso, volvemos al activity principal
                        Toast.makeText(getContext(),"Se ha enviado un email a tu dirección",Toast.LENGTH_SHORT).show();
                        changeFragment(new HomeFragment());
                    }else{ //Si la dirección de la cuenta no existe
                        Toast.makeText(getContext(),"Error: no hay ninguna cuenta registrada con la dirección proporcionada",Toast.LENGTH_SHORT).show();
                        mail_recuperar.setError("Escriba una dirección registrada de email");
                    }
                }
            });
        }
    }

    //Método para cambiar de fragment
    private void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame_login, fragment, "NewFragmentTag");
        ft.commit();
    }
}