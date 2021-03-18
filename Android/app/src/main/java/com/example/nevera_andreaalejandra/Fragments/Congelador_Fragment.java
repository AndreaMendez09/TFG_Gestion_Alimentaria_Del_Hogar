package com.example.nevera_andreaalejandra.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import com.example.nevera_andreaalejandra.Activities.AddEditProductActivity;
import com.example.nevera_andreaalejandra.Adapters.AdapterProducto;
import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Congelador_Fragment extends Fragment {
    //El boton para añadir
    private FloatingActionButton add;

    //Creamos el adapter
    private AdapterProducto adapterProducto;

    //Creamos nuestra lista para guardar los productos
    private List<ProductoModelo> lista_productos;

    //Para realizar la conexon con la firebase
    private DatabaseReference mDataBase;

    //Los datos para vincularlos con la base de datos
    private String IdProducto;
    private String NombreProducto;
    private String TipoProducto;
    private String UbicacionProducto;
    private Double PrecioProducto;
    private int CantidadProducto;
    private Date FechaProducto = new Date();

    //Para el list view
    private RecyclerView recyclerView;

    public Congelador_Fragment() {
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
        View view = inflater.inflate(R.layout.fragment_congelador_, container, false);
        add = view.findViewById(R.id.FABAddList);

        //DB Firebase
        mDataBase = FirebaseDatabase.getInstance().getReference();
        lista_productos = new ArrayList<ProductoModelo>();
        recyclerView = (RecyclerView) view.findViewById(R.id.item_product_congelador);
        leerProductos();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Toast.makeText(getContext(), "Es necesario un nuevo nombre para el tablero", Toast.LENGTH_LONG).show();
                showAlertForCreatingBoard("Añadir nuevo producto", "Escribir el nombre del producto");*/
                //Con intent pasamos informacion al otro activity que vayamos a cambiar
                Intent intent = new Intent(getActivity(), AddEditProductActivity.class);//Establecemos primero donde estamos y luego donde vamos

                //En este caso como hemos pulsado en el más, pasaremos la opcion de añadir
                intent.putExtra("añadir", IdProducto); //Para detectar en el AddEdit si es un añadir o un editar
                intent.putExtra("ubicacion", "congelador"); //Para detectar en el AddEdit si es de congelador o de nevera

                startActivity(intent);//Iniciamos el intent
            }
        });

        return view;
    }

    private void leerProductos() {
        mDataBase.child("Producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    lista_productos.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) { //Añadimos los campos a las variables creadas anteriormente
                        NombreProducto = ds.child("nombre").getValue().toString(); //Lo que hay entre parentesis es el nombre de como lo guarda la base de datos
                        //TipoProducto = ds.child("tipo").getValue().toString();
                        UbicacionProducto = ds.child("ubicacion").getValue().toString();
                        PrecioProducto = Double.valueOf(ds.child("precio").getValue().toString());
                        CantidadProducto = Integer.parseInt(ds.child("cantidad").getValue().toString());

                        //Creamos la fecha
                        /*String date = ds.child("fecha").child("date").getValue().toString();
                        String month = ds.child("fecha").child("month").getValue().toString();
                        int monthInt = Integer.parseInt(month)+1;
                        String year = ds.child("fecha").child("year").getValue().toString();
                        int yearInt = (Integer.parseInt(year)+1900);
                        String fecha=null;
                        if(Integer.parseInt(month)<10){
                            fecha=date+"/0"+monthInt+"/"+yearInt;
                        }else{
                            fecha=date+"/"+monthInt+"/"+yearInt;

                        }
                        FechaProducto = null;
                        try {
                            FechaProducto=new SimpleDateFormat("dd/MM/yyyy").parse(fecha);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/

                        //Vinculamos el id
                        IdProducto = ds.getKey();

                        if (UbicacionProducto.equals("congelador")) {
                            ProductoModelo product = new ProductoModelo(IdProducto, CantidadProducto, TipoProducto, NombreProducto, UbicacionProducto);
                            lista_productos.add(product);
                        }
                    }



                }
                // Observa como pasamos el activity, con this. Podríamos declarar
                // Activity o Context en el constructor y funcionaría pasando el mismo valor, this
                adapterProducto = new AdapterProducto(getContext(), R.layout.item_product,lista_productos);
                recyclerView.setAdapter(adapterProducto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*private void createNewBoard(String productName, double precio, int cantidad) {
        ProductoModelo producto = new ProductoModelo(productName,cantidad, precio, "congelador");
        mDataBase.child("Producto").push().setValue(producto).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Board Creado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al crear", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/
}