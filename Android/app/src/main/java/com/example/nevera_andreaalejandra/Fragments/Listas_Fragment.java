package com.example.nevera_andreaalejandra.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nevera_andreaalejandra.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Listas_Fragment extends Fragment {
    //Creamos el boton flotante para relacionarlo con el xml
    private FloatingActionButton add;

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
        View view = inflater.inflate(R.layout.activity_lista, container, false);
        add = view.findViewById(R.id.FABAddList);


        return view;
    }
}