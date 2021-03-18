package com.example.nevera_andreaalejandra.Fragments;

import
        android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class Nevera_Fragment extends Fragment {

    //El boton para añadir
    private FloatingActionButton add;

    //Creamos el adapter
    private AdapterProducto adapterProducto;
    private AdapterProducto adapterEliminar;


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
    private RecyclerView.LayoutManager mLayoutManager;

    public Nevera_Fragment() {
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
        View view = inflater.inflate(R.layout.fragment_nevera_, container, false);

        //DB Firebase
        mDataBase = FirebaseDatabase.getInstance().getReference();

        //Enlazar con el xml
        add = view.findViewById(R.id.FABAddList);
        lista_productos = new ArrayList<ProductoModelo>();
        recyclerView = (RecyclerView) view.findViewById(R.id.item_product_nevera);
        mLayoutManager = new LinearLayoutManager(getContext());

        leerProductos();

        //Para que se visualize
        registerForContextMenu(recyclerView);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Toast.makeText(getContext(), "Es necesario un nuevo nombre para el tablero", Toast.LENGTH_LONG).show();
                showAlertForCreatingBoard("Añadir nuevo producto", "Escribir el nombre del producto");*/
                //Con intent pasamos informacion al otro activity que vayamos a cambiar
                Intent intent = new Intent(getActivity(), AddEditProductActivity.class);//Establecemos primero donde estamos y luego donde vamos

                //En este caso como hemos pulsado en el más, pasaremos la opcion de añadir
                intent.putExtra("añadir", IdProducto); //Para detectar en el AddEdit si es un añadir o un editar
                intent.putExtra("ubicacion", "nevera"); //Para detectar en el AddEdit si es de congelador o de nevera

                startActivity(intent);//Iniciamos el intent
            }
        });
        return view;
    }
    // CRUD Actions
    private void leerProductos() {
        mDataBase.child("Producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    lista_productos.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) { //Añadimos los campos a las variables creadas anteriormente
                        NombreProducto = ds.child("nombre").getValue().toString(); //Lo que hay entre parentesis es el nombre de como lo guarda la base de datos
                        TipoProducto = ds.child("tipo").getValue().toString();
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

                        if (UbicacionProducto.equals("nevera")) {
                            ProductoModelo product = new ProductoModelo(IdProducto, CantidadProducto, TipoProducto, NombreProducto, UbicacionProducto);
                            lista_productos.add(product);
                        }
                    }
                    adapterEliminar = new AdapterProducto(lista_productos, R.layout.item_product, new AdapterProducto.OnItemClickListener() {
                        //Este click es al darle a la ciudad
                        @Override
                        public void onItemClick(ProductoModelo productoModelo, int position) {
                            Intent intent = new Intent(getContext(), AddEditProductActivity.class);
                            ProductoModelo productoModelo1 = lista_productos.get(position);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("objeto", productoModelo1);
                            intent.putExtras(bundle);

                            startActivity(intent);
                        }
                        //Este boton es al clickar en el boton eliminar que tiene cada cardview de ciudad
                    }, new AdapterProducto.OnButtonClickListener() {
                        @Override
                        public void onButtonClick(ProductoModelo productoModelo, int position) {
                            //Aqui va el boton de eliminar del cardview
                            //Mostramos un dialogo emergente para comprobar si estas seguro de que quieres borrarlo
                            //TODO no tengo ni idea si esto esta bien
                            //showAlertForErasingCity(city.getName(),city.getDescription(),city);
                            deleteProduct(productoModelo);
                        }

                    });


                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(adapterEliminar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void deleteProduct(final ProductoModelo productoModelo) {

        //creamos el alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("¿Seguro que quiere eliminar?")//es como la partesita de arriba
                .setTitle("Aviso")//es el texto
                .setCancelable(false)//es para que no se salga  si oprime cualquier cosa
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface builder, int id) {
                        //Comprobamos si exite el producto
                        if(lista_productos.size()==1){
                            lista_productos.clear();//La limpiamos
                        }
                        mDataBase.child(productoModelo.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //Creamos un toast, para informar de que se ha eliminado
                                    Toast.makeText(getContext(), "Se ha eliminado satisfactoriamente", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });
        builder.setNegativeButton("Cancelar", null).show();


    }

    /*private void showAlertForCreatingBoard(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_list, null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editText_dialog);
        final EditText input2 = (EditText) viewInflated.findViewById(R.id.cantidad_dialog);
        final EditText input3 = (EditText) viewInflated.findViewById(R.id.precio_dialog);

        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String productName = input.getText().toString().trim();
                int productCantidad = Integer.parseInt(input2.getText().toString().trim());
                double productPrecio = Double.parseDouble(input3.getText().toString().trim());
                if (productName.length() > 0)
                    createNewBoard(productName,productPrecio,productCantidad);
                else
                    Toast.makeText(getContext(), "Es necesario un nuevo nombre para el tablero", Toast.LENGTH_LONG).show();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }*/

    /*private void createNewBoard(String productName, double precio, int cantidad) {
        ProductoModelo producto = new ProductoModelo(productName,cantidad, precio,"nevera");
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