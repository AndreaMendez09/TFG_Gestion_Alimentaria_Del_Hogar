package com.example.nevera_andreaalejandra.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.example.nevera_andreaalejandra.Activities.AddEditProductActivity;
import com.example.nevera_andreaalejandra.Adapters.AdapterProducto;
import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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
    private FirebaseAuth mAuth;

    //Los datos para vincularlos con la base de datos
    private String IdProducto;
    private String NombreProducto;
    private String TipoProducto;
    private String UbicacionProducto;
    private Double PrecioProducto;
    private int CantidadProducto;
    private Date FechaProducto = new Date();
    private String UID_usuario;
    private String  DateProducto;

    //Para el list view
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

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


        //DB Firebase
        mDataBase = FirebaseDatabase.getInstance().getReference();
        //Para inicializar la instancia de autenticación
        mAuth = FirebaseAuth.getInstance();

        //Creamos la lista
        lista_productos = new ArrayList<ProductoModelo>();



        //Enlazamos con el xml
        add = view.findViewById(R.id.FABAddList);
        recyclerView = (RecyclerView) view.findViewById(R.id.item_product_congelador);
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
                intent.putExtra("tarea", "añadir"); //Para detectar en el AddEdit si es un añadir o un editar
                intent.putExtra("ubicacion", "congelador"); //Para detectar en el AddEdit si es de congelador o de nevera

                startActivity(intent);//Iniciamos el intent
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if(lista_productos.size()<=2)
                    add.show();
                if (dy > 0)
                    add.hide();
                else if (dy < 0)
                    add.show();
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
                        TipoProducto = ds.child("tipo").getValue().toString();
                        UbicacionProducto = ds.child("ubicacion").getValue().toString();
                        PrecioProducto = Double.valueOf(ds.child("precio").getValue().toString());
                        CantidadProducto = Integer.parseInt(ds.child("cantidad").getValue().toString());
                        UID_usuario = ds.child("uid_usuario").getValue().toString();
                        DateProducto = ds.child("fecha").getValue().toString();

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

                        //Comprobamos el usuario que esta conectado
                        String id = mAuth.getCurrentUser().getUid();

                        if (UbicacionProducto.equals("congelador")) {
                            if (UID_usuario.equals(id)) {
                                ProductoModelo product = new ProductoModelo(IdProducto,NombreProducto, CantidadProducto, PrecioProducto,UbicacionProducto ,TipoProducto,DateProducto,UID_usuario);
                                lista_productos.add(product);
                            }
                        }
                    }
                    adapterProducto = new AdapterProducto(lista_productos, R.layout.item_product, new AdapterProducto.OnItemClickListener() {
                        //Este click es al darle a la ciudad
                        @Override
                        public void onItemClick(ProductoModelo productoModelo, int position) {
                            Intent intent = new Intent(getContext(), AddEditProductActivity.class);
                            intent.putExtra("tarea", "editar");
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
                            deleteProduct(productoModelo);
                        }

                    });


                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(adapterProducto);

                /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
                dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
                recyclerView.addItemDecoration(dividerItemDecoration);*/

                //Ponemos la animacion
                Context context = recyclerView.getContext();
                LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_caer);
                recyclerView.setLayoutAnimation(layoutAnimationController);
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
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
                        mDataBase.child("Producto").child(productoModelo.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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