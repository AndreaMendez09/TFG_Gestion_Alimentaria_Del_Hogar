package com.example.nevera_andreaalejandra.Fragments.Login;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.nevera_andreaalejandra.Activities.AddEditProductActivity;
import com.example.nevera_andreaalejandra.Models.UsuarioModelo;
import com.example.nevera_andreaalejandra.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegisterFragment extends Fragment {
    //Elementos en el fragment
    private EditText nombre_edit, apellido_edit, mail_edit, pass_edit;
    private Button btnregister;
    private ImageButton irAtras;

    //Para la BBDD
    private DatabaseReference mDataBase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;

    //Creamos las variables para obtener los datos de los edit text
    private String nombre, apellido, mail, pass;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Creamos la vista
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        //Referenciamos los elementos con el xml
        btnregister = (Button) view.findViewById(R.id.buttonRegister);
        nombre_edit = (EditText) view.findViewById(R.id.name_register);
        apellido_edit = (EditText) view.findViewById(R.id.surname_register);
        mail_edit = (EditText) view.findViewById(R.id.mail_register);
        pass_edit = (EditText) view.findViewById(R.id.password_register);
        irAtras = (ImageButton) view.findViewById(R.id.irAtras);

        //Para inicializar la instancia de autenticación
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        btnregister.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Boton", Toast.LENGTH_SHORT).show();
            addUser();
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

    //Creamos un método para registrar al usuario
    private void addUser() {
        //Obtenemos lo que ha escrito el usuario
        nombre = nombre_edit.getText().toString();
        apellido = apellido_edit.getText().toString();
        mail = mail_edit.getText().toString();
        pass = pass_edit.getText().toString();

        //Como todos los datos deben ser not null, debemos primero comprobar si no estan vacios
        if (!nombre.isEmpty() && !apellido.isEmpty() && !mail.isEmpty() && !pass.isEmpty()) {

            //Comprobamos que la contraseña tiene más de 6 caracteres
            if (pass.length() >= 6) {
                registro();
            }else {
                //Ponemos un aviso al usuario de que debe cambiar la contraseña
                pass_edit.setError("La contraseña debe tener como mínimo 6 carácteres");
            }
        }else {
            //Avisamos al usuario si no se encuentran todos los campos
            Toast.makeText(getContext(), "Rellena los campos necesarios", Toast.LENGTH_SHORT).show();
        }
    }

    private void registro() {
        mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //GUARDAMOS LA ID DEL USUARIO CREADO
                String id = mAuth.getCurrentUser().getUid();

                //Creamos un usario
                UsuarioModelo user = new UsuarioModelo(id, nombre, apellido, pass, mail);

                mDataBase.child("Usuario").push().setValue(user).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(getContext(), "Usuario registrado", Toast.LENGTH_SHORT).show();
                        //Nos movemos al fragment de login
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame_login, new LoginFragment(), "NewFragmentTag");
                        ft.commit();
                    }else {
                        Toast.makeText(getContext(), "ERROR EN EL REGISTRO", Toast.LENGTH_SHORT).show();
                    }
                });
                mAuth.signOut();
            }else {
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(getContext(), "EMAIL YA EN USO", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "ERROR EN EL REGISTRO", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "COMPRUEBE SI LOS DATOS SON CORRECTOS", Toast.LENGTH_SHORT).show();
        });
    }

    //Método para cambiar de fragment
    private void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame_login, fragment, "NewFragmentTag");
        ft.commit();
    }
}