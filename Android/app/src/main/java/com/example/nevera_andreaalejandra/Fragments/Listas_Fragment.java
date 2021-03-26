package com.example.nevera_andreaalejandra.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.nevera_andreaalejandra.Fragments.Listas.ListaCongelador_Fragment;
import com.example.nevera_andreaalejandra.Fragments.Listas.ListaNevera_Fragment;
import com.example.nevera_andreaalejandra.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Listas_Fragment extends Fragment {

    //Creamos el cuadro de dialogo
    private AlertDialog.Builder builder;

    //Creamos los campos necesarios para vincularlos con el xml
    private Button lista_nevera, lista_congelador, lista_personal;



    public Listas_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista, container, false);

        //Vinculamos nuestras variables con el XML
        lista_congelador = (Button) view.findViewById(R.id.lista_congelador);
        lista_nevera = (Button) view.findViewById(R.id.lista_nevera);
        lista_personal = (Button) view.findViewById(R.id.lista_personal);

        //Creamos los eventos onclick
        lista_congelador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new ListaCongelador_Fragment());
            }
        });

        lista_nevera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new ListaNevera_Fragment());
            }
        });

        lista_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }


    /*class OyenteFAB implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            builder = new AlertDialog.Builder(getContext()); //Getcontext porque es un fragment
            builder.setTitle("Nueva lista");
            builder.setMessage("Introduzca el nombre de la nueva lista");

            //Para añadir un edit text al dialogo
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_new_list, null);

            //Relacionamos con el xml
            nombre_correo = (EditText) dialogView.findViewById(R.id.editText_dialog);

            //Ponemos el edit text en el dialog
            builder.setView(dialogView);

            //Creamos los botones para confirmar y descartar
            //builder.setPositiveButton("OK", new OyenteBotonOK());
            builder.setNegativeButton("Cancelar",null); //null porque no hace falta hacer nada en cancelar

            builder.show();
        }
    }*/

    //Método para cambiar de fragment
    private void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, "NewFragmentTag");
        ft.commit();
    }
}