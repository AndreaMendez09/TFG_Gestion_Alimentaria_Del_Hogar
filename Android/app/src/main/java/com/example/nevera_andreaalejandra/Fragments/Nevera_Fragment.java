package com.example.nevera_andreaalejandra.Fragments;

import android.content.Context;
import
        android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nevera_andreaalejandra.Activities.AddEditProductActivity;
import com.example.nevera_andreaalejandra.Adapters.AdapterProducto;
import com.example.nevera_andreaalejandra.Interfaces.OnButtonClickListener;
import com.example.nevera_andreaalejandra.Interfaces.OnItemClickListener;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Nevera_Fragment extends Fragment {

    //El boton para añadir
    private FloatingActionButton add;
    private ConstraintLayout MensajeSinProductos;
    private ImageView imageView;

    //Creamos el adapter
    public AdapterProducto adapterEliminar;

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
    private String DateProducto;
    private String UID_usuario;

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
        //Para inicializar la instancia de autenticación
        mAuth = FirebaseAuth.getInstance();

        //Enlazar con el xml
        add = view.findViewById(R.id.FABAddList);
        MensajeSinProductos = (ConstraintLayout) view.findViewById(R.id.MensajeSinProductos);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        /*Display display = getWindowManager().getDefaultDisplay();
        imageView.getLayoutParams().height = display.getHeight()/2;*/

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
                intent.putExtra("tarea", "añadir"); //Para detectar en el AddEdit si es un añadir o un editar
                intent.putExtra("ubicacion", "nevera"); //Para detectar en el AddEdit si es de congelador o de nevera

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
    // CRUD Actions
    private void leerProductos() {
        //Comprobamos el usuario que esta conectado
        String idUsuario = mAuth.getCurrentUser().getUid();


        Query query1 = FirebaseDatabase.getInstance().getReference("Producto").orderByChild("uid_usuario").equalTo(idUsuario);


        //mDataBase.child("Producto").addValueEventListener();


        ValueEventListener valueEventListener = new ValueEventListener() {
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
                        try {
                            DateProducto = ds.child("fecha").getValue().toString();
                        } catch (NullPointerException e) {
                            DateProducto = "--/--/----";
                        }

                        //Vinculamos el id
                        IdProducto = ds.getKey();

                        if (UbicacionProducto.equals("nevera")) {
                            ProductoModelo product = new ProductoModelo(IdProducto, NombreProducto, CantidadProducto, PrecioProducto, UbicacionProducto, TipoProducto, DateProducto, UID_usuario);
                            lista_productos.add(product);
                        }

                    }
                    adapterEliminar = new AdapterProducto(lista_productos, R.layout.item_principal, new OnItemClickListener() {
                        //Este click es al darle a la ciudad
                        @Override
                        public void onItemClick(ProductoModelo productoModelo, int position) {
                            Intent intent = new Intent(getContext(), AddEditProductActivity.class);
                            intent.putExtra("tarea", "editar");
                            intent.putExtra("ubicacion", "nevera");
                            ProductoModelo productoModelo1 = lista_productos.get(position);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("objeto", productoModelo1);
                            intent.putExtras(bundle);

                            startActivity(intent);
                        }
                        //Este boton es al clickar en el boton eliminar que tiene cada cardview de ciudad
                    }, new OnButtonClickListener() {
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

                //Ponemos el espacio entre los items
                /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
                dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
                recyclerView.addItemDecoration(dividerItemDecoration);*/

                //Ponemos la animacion
                Context context = recyclerView.getContext();
                LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_caer);
                recyclerView.setLayoutAnimation(layoutAnimationController);
                //recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();

                if (adapterEliminar.getItemCount() > 0 ) {
                    //Toast.makeText(getContext(), "Hay productos", Toast.LENGTH_SHORT).show();
                    MensajeSinProductos.setVisibility(View.INVISIBLE);
                }else {
                    //Toast.makeText(getContext(), "No hay productos", Toast.LENGTH_SHORT).show();
                    MensajeSinProductos.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        query1.addValueEventListener(valueEventListener);
    }
    private void deleteProduct(final ProductoModelo productoModelo) {
        //Para el checkbox
        View checkBoxView = View.inflate(getContext(), R.layout.dialog_checkbox, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);

        View edit_cantidad = View.inflate(getContext(), R.layout.dialog_cantidad, null);
        EditText cantidad = (EditText) edit_cantidad.findViewById(R.id.CantidadModificar);

        //creamos el alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("¿Seguro que quiere eliminar?")//es como la partesita de arriba
                .setTitle("Aviso")//es el texto
                .setView(checkBoxView)
                .setCancelable(false)//es para que no se salga  si oprime cualquier cosa
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface builder, int id) {
                        //Comprobamos si exite el producto
                        if(lista_productos.size()==1){
                            lista_productos.clear();//La limpiamos
                        }

                        //Oyente del check
                        if (checkBox.isChecked()) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                            builder2.setMessage("Introduce la cantidad a comprar")
                                    .setView(edit_cantidad)
                                    .setCancelable(false)
                                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Cambiamos la cantidad
                                            mDataBase.child("Producto").child(productoModelo.getId()).child("cantidad").setValue(Integer.parseInt(cantidad.getText().toString().trim()));

                                            //Si esta seleccionado el check, lo cambiamos de ubicacion
                                            mDataBase.child("Producto").child(productoModelo.getId()).child("ubicacion").setValue("lista_nevera").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Se ha añadido satisfactoriamente", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    }).show();

                        }else {
                            Toast.makeText(getContext(), productoModelo.getId(), Toast.LENGTH_SHORT).show();
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