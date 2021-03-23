package com.example.nevera_andreaalejandra.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.nevera_andreaalejandra.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Listas_Fragment extends Fragment {
    //Creamos el boton flotante para relacionarlo con el xml
    private FloatingActionButton add;
    //Creamos el cuadro de dialogo
    private AlertDialog.Builder builder;

    //Creamos el listview para enlazarlo con el xml
    private EditText nombre_correo;


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


        //listView.setAdapter(myAdapter);

        return view;
    }


    class OyenteFAB implements View.OnClickListener {

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
    }
}