package com.example.nevera_andreaalejandra.Fragments.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nevera_andreaalejandra.Activities.NeveraActivity;
import com.example.nevera_andreaalejandra.R;
import com.example.nevera_andreaalejandra.Util.LoginUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.MODE_PRIVATE;


public class LoginFragment extends Fragment {
    //Botones para relacionarlos con xml
    private Button login_login;
    private EditText correo_login, pass_login;
    private Button recordarPass;
    private ImageButton irAtras;
    private LinearLayout caja_mail, caja_pass, caja_abajo;
    private TextView texto_login;
    private Switch remember;

    private SharedPreferences preferences;
    //Para el color
    private float alpha = 0;

    //Para la BBDD
    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //Relacionamos con el xml
        login_login = (Button) view.findViewById(R.id.buttonLogin_login);
        correo_login = (EditText) view.findViewById(R.id.mail_login);
        pass_login = (EditText) view.findViewById(R.id.password_login);
        recordarPass = (Button) view.findViewById(R.id.textPassRecover);
        irAtras = (ImageButton) view.findViewById(R.id.irAtras);
        caja_mail = (LinearLayout) view.findViewById(R.id.caja_mail);
        caja_pass = (LinearLayout) view.findViewById(R.id.caja_password);
        caja_abajo = (LinearLayout) view.findViewById(R.id.caja_abajo);
        texto_login = (TextView) view.findViewById(R.id.texto_login);
        remember = (Switch) view.findViewById(R.id.switchRecordar) ;
        //Para inicializar la instancia de autenticación
        mAuth = FirebaseAuth.getInstance();

        //Eventos on click
        login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
                //Toast.makeText(getActivity(),"Text!",Toast.LENGTH_SHORT).show();
            }
        });

        recordarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new RecuperarFragment());
            }
        });

        irAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new HomeFragment());
            }
        });

        //se auto-rellenan el email y contraseña en caso de haberse guardado
        preferences = getContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        setCredentialsIfExist();

        //Método para las transicciones
        movimiento();

        // Inflate the layout for this fragment
        return view;


    }

    private void movimiento() {
        texto_login.setTranslationY(300);
        caja_mail.setTranslationY(300);
        caja_pass.setTranslationY(300);
        caja_abajo.setTranslationY(300);
        remember.setTranslationY(300);

        texto_login.setAlpha(alpha);
        caja_mail.setAlpha(alpha);
        caja_pass.setAlpha(alpha);
        caja_abajo.setAlpha(alpha);
        remember.setAlpha(alpha);

        texto_login.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        caja_mail.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        caja_pass.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        caja_abajo.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(1200).start();
        remember.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(1000).start();
    }

    private void changeToActivity() {
        Intent intent = new Intent(getActivity(), NeveraActivity.class);
        startActivity(intent);
    }

    //Método para cambiar de fragment
    private void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame_login, fragment, "NewFragmentTag");
        ft.commit();
    }

    private void LoginUser() {
        //Obtenemos lo que ha escrito el usuario
        String email = correo_login.getText().toString();
        String pass = pass_login.getText().toString();
        if(login(email,pass)) {
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(getActivity(),new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) { //Si el usuario y contraseña son correctos, se carga el UserActivity.
                        String id = mAuth.getCurrentUser().getUid();
                        Toast.makeText(getContext(), "Bienvenido" + id, Toast.LENGTH_SHORT).show();
                        saveOnPreferences(correo_login.getText().toString().trim(),pass_login.getText().toString().trim());
                        changeToActivity();
                    } else {
                        Toast.makeText(getContext(), "Error, compruebe el usuario o contraseña", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean login(String email, String password) {
        if (!isValidEmail(email)) {
            Toast.makeText(getContext(), "Email no válido,", Toast.LENGTH_LONG).show();
            correo_login.setError("El correo es incorrecto");
            return false;
        } else if (!isValidPassword(password)) {
            Toast.makeText(getContext(), "Password incorrecta", Toast.LENGTH_LONG).show();
            pass_login.setError("La contraseña no coincide");
            return false;
        } else {
            return true;
        }
    }
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 4;
    }

    //método que guarda el email y contraseña introducidos
    private void saveOnPreferences(String email, String password) {
        if (remember.isChecked()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("email", email);
            editor.putString("pass", password);
            editor.apply();


        } else {
            LoginUtil.removeSharedPreferences(preferences);
        }
    }
    //método que fija el email y contraseña que se hayan guardado
    private void setCredentialsIfExist() {
        String email = LoginUtil.getUserMailPrefs(preferences);
        String password = LoginUtil.getUserPassPrefs(preferences);
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            correo_login.setText(email);
            pass_login.setText(password);
            remember.setChecked(true);
            //LoginUser();
        }
    }
}