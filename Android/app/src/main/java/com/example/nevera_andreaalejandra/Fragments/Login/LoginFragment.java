package com.example.nevera_andreaalejandra.Fragments.Login;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.nevera_andreaalejandra.Activities.MainActivity;
import com.example.nevera_andreaalejandra.R;


public class LoginFragment extends Fragment {
    //Botones para relacionarlos con xml
    private Button login_login;

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
        
        login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToActivity();
                //Toast.makeText(getActivity(),"Text!",Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void changeToActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}