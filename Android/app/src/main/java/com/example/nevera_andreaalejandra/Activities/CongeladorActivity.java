package com.example.nevera_andreaalejandra.Activities;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nevera_andreaalejandra.Adapters.AdapterProducto;
import com.example.nevera_andreaalejandra.Interfaces.OnButtonClickListener;
import com.example.nevera_andreaalejandra.Interfaces.OnItemClickListener;
import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;
import com.example.nevera_andreaalejandra.Util.NotificationHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CongeladorActivity extends AppCompatActivity {
    //Partes del drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout logout;

    //Para la BBDD
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;

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

    //Notificaciones
    private NotificationHandler notificationHandler;
    private int counter = 0;
    private boolean isHighImportance = true;

    private Bundle extras;
    //Variable para detectar en que parte de la app está
    private int fragment_actual = 0;

    //El boton para añadir
    private FloatingActionButton add;
    private ConstraintLayout MensajeSinProductos;
    private EditText buscar;
    private ImageView imageView;

    //Creamos la lista para los productos de Nevera
    private List<ProductoModelo> lista_productos;
    //Creamos el adapter
    public AdapterProducto adapterEliminar;
    //Para el list view
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congelador);
        setToolbar();
        getSupportActionBar().setTitle("Mi congelador");


        //BBDD
        mAuth = FirebaseAuth.getInstance();//Para inicializar la instancia de autenticación
        mDataBase = FirebaseDatabase.getInstance().getReference();


        //Relacionamos con el xml
        add = findViewById(R.id.FABAddList);
        MensajeSinProductos = (ConstraintLayout) findViewById(R.id.MensajeSinProductos);
        imageView = (ImageView) findViewById(R.id.imageView);
        buscar = (EditText) findViewById(R.id.buscar);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navview);
        logout = (LinearLayout) findViewById(R.id.logout);

        recyclerView = (RecyclerView) findViewById(R.id.item_product_congelador);
        mLayoutManager = new LinearLayoutManager(this);
        notificationHandler = new NotificationHandler(this);


        lista_productos = new ArrayList<ProductoModelo>();

        //Dependiendo de lo que seleccionemos en el menu, iremos a un fragment u a otro
        navigationView.setNavigationItemSelectedListener(new OyenteNav());

        leerProductos();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Se pulsa en logout", Toast.LENGTH_SHORT).show();
                //TODO POR SI QUEREMOS METER UN DIALOG DE SI O NO
                mAuth.signOut(); //Desconectamos al usuario
                //Para volver al fragment donde nos encontramos
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);//Establecemos primero donde estamos y luego donde vamos
                startActivity(intent);//Iniciamos el intent
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Toast.makeText(getContext(), "Es necesario un nuevo nombre para el tablero", Toast.LENGTH_LONG).show();
                showAlertForCreatingBoard("Añadir nuevo producto", "Escribir el nombre del producto");*/
                //Con intent pasamos informacion al otro activity que vayamos a cambiar
                Intent intent = new Intent(getApplicationContext(), AddEditProductActivity.class);//Establecemos primero donde estamos y luego donde vamos

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
        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

    }

    private void filter(String textoBuscar) {
        ArrayList<ProductoModelo> filteredList = new ArrayList<ProductoModelo>();
        for (ProductoModelo producto : lista_productos) {
            if(producto.getNombre().toLowerCase().contains(textoBuscar.toLowerCase())) {
                filteredList.add(producto);
            }
        }

        adapterEliminar.filterList(filteredList);
    }

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

                        if (UbicacionProducto.equals("congelador")) {
                            ProductoModelo product = new ProductoModelo(IdProducto, NombreProducto, CantidadProducto, PrecioProducto, UbicacionProducto, TipoProducto, DateProducto, UID_usuario);
                            lista_productos.add(product);
                        }


                    }
                    adapterEliminar = new AdapterProducto(lista_productos, R.layout.item_principal, new OnItemClickListener() {
                        //Este click es al darle al producto
                        @Override
                        public void onItemClick(ProductoModelo productoModelo, int position) {
                            Intent intent = new Intent(getApplicationContext(), AddEditProductActivity.class);
                            intent.putExtra("tarea", "editar");
                            intent.putExtra("ubicacion", "congelador");
                            ProductoModelo productoModelo1 = lista_productos.get(position);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("objeto", productoModelo1);
                            intent.putExtras(bundle);

                            startActivity(intent);
                        }
                        //Este boton es al clickar en el boton eliminar que tiene cada cardview
                    }, new OnButtonClickListener() {
                        @Override
                        public void onButtonClick(ProductoModelo productoModelo, int position) {
                            //Aqui va el boton de eliminar del cardview
                            //Mostramos un dialogo emergente para comprobar si estas seguro de que quieres borrarlo
                            //TODO no tengo ni idea si esto esta bien

                            deleteProduct(productoModelo);
                        }

                    });


                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(adapterEliminar);


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

                //Collections.sort(lista_productos, ProductoModelo.ProductoAZ);
                //adapterEliminar.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        query1.addValueEventListener(valueEventListener);
    }



    //Para poner la imagen en el toolbar
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //He cambiado esto, porque ponia que el otro era para versiones de 30, y esta es la 26
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home); //Esto de aqui pone la imagen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    //Método para cambiar de fragment
    private void changeFragment(Fragment fragment, MenuItem item) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        item.setChecked(true);
        getSupportActionBar().setTitle(item.getTitle()); //Cambia el titulo de arriba
    }

    //--------Oyentes
    public class OyenteNav implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //Obtenemos la posicion del menu
            Activity activity = null;

            switch (item.getItemId()) {
                case R.id.menu_nevera: //Si coincide con el menumail es el fragment de Email
                    activity = new NeveraActivity();
                    cambiarActivity(activity);
                    item.setChecked(true);
                    break;
                case R.id.menu_congelador: //Si coincide con el menumail es el fragment de Email
                    activity = new CongeladorActivity();
                    cambiarActivity(activity);
                    item.setChecked(true);
                    break;
                case R.id.menu_lista: //Si coincide con el menumail es el fragment de Email
                    activity = new TabActivity();
                    cambiarActivity(activity);
                    item.setChecked(true);
                    break;
                case R.id.menu_ajustes:
                    activity = new AjustesActivity();
                    cambiarActivity(activity);
                    item.setChecked(true);
                    break;

            }
            return true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.more_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // abrir el menu lateral
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.opciones_ordenar:
                Collections.sort(lista_productos, ProductoModelo.ProductoAZ);
                adapterEliminar.notifyDataSetChanged();
                //Toast.makeText(MainActivity.this, "Has pulsado en ordenar", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.opciones_ordenar2:
                Collections.sort(lista_productos, ProductoModelo.ProductoZA);
                adapterEliminar.notifyDataSetChanged();
                return true;
            case R.id.opciones_ordenar3:
                Collections.sort(lista_productos, ProductoModelo.ProductoCantidadA);
                adapterEliminar.notifyDataSetChanged();
                return true;
            case R.id.opciones_ordenar4:
                Collections.sort(lista_productos, ProductoModelo.ProductoCantidadD);
                adapterEliminar.notifyDataSetChanged();
                return true;
            case R.id.buscar:
                /*ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(LayoutPadre);
                constraintSet.connect(R.id.linearLayout2, ConstraintSet.BOTTOM, R.id.toolbar, ConstraintSet.BOTTOM, 0);
                constraintSet.applyTo(LayoutPadre);*/
                if (buscar.getVisibility() == View.INVISIBLE)
                    buscar.setVisibility(View.VISIBLE);
                else {
                    buscar.setText("");
                    buscar.setVisibility(View.INVISIBLE);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void deleteProduct(final ProductoModelo productoModelo) {
        //Para el checkbox
        View checkBoxView = View.inflate(this, R.layout.dialog_checkbox, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);

        View edit_cantidad = View.inflate(this, R.layout.dialog_cantidad, null);
        EditText cantidad = (EditText) edit_cantidad.findViewById(R.id.CantidadModificar);

        //creamos el alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(CongeladorActivity.this);
                            builder2.setMessage("Introduce la cantidad a comprar")
                                    .setView(edit_cantidad)
                                    .setCancelable(false)
                                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Cambiamos la cantidad
                                            mDataBase.child("Producto").child(productoModelo.getId()).child("cantidad").setValue(Integer.parseInt(cantidad.getText().toString().trim()));

                                            //Si esta seleccionado el check, lo cambiamos de ubicacion
                                            mDataBase.child("Producto").child(productoModelo.getId()).child("ubicacion").setValue("lista_congelador").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        Toast.makeText(CongeladorActivity.this, "Se ha añadido satisfactoriamente", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    }).show();

                        }else {
                            Toast.makeText(CongeladorActivity.this, productoModelo.getId(), Toast.LENGTH_SHORT).show();
                            mDataBase.child("Producto").child(productoModelo.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //Creamos un toast, para informar de que se ha eliminado
                                        Toast.makeText(CongeladorActivity.this, "Se ha eliminado satisfactoriamente", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
        builder.setNegativeButton("Cancelar", null).show();




    }

    public void cambiarActivity(Activity activity){
        Intent intent = new Intent(this, activity.getClass());//Establecemos primero donde estamos y luego donde vamos
        startActivity(intent);
    }

}


