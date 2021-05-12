package com.example.nevera_andreaalejandra.Fragments.Login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.nevera_andreaalejandra.R;


public class HomeFragment extends Fragment {
    //Botones para relacionarlos con xml
    private Button login_home;
    private Button register_home;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Relacionamos con el xml
        login_home = (Button) view.findViewById(R.id.buttonLogin_home);
        register_home = (Button) view.findViewById(R.id.buttonRegister_home);

        //Creamos los métodos para el onclick, que iran al fragment necesario
        login_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new LoginFragment());
            }
        });

        register_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new RegisterFragment());
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    //Método para cambiar de fragment
    private void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame_login, fragment, "NewFragmentTag");
        ft.commit();
    }
}