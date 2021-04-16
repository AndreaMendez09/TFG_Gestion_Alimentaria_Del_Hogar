package com.example.nevera_andreaalejandra.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
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
import com.example.nevera_andreaalejandra.Fragments.Congelador_Fragment;
import com.example.nevera_andreaalejandra.Fragments.Nevera_Fragment;
import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;
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

public class NeveraActivity extends AppCompatActivity {
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

    private Bundle extras;

    //Creamos la lista para los productos de Nevera
    private List<ProductoModelo> lista_productos;

    //El boton para añadir
    private FloatingActionButton add;
    private ConstraintLayout MensajeSinProductos;
    private ImageView imageView;

    //Para el list view
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    //Creamos el adapter
    public AdapterProducto adapterEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_nevera_);
        setToolbar();

        //BBDD
        mAuth = FirebaseAuth.getInstance();//Para inicializar la instancia de autenticación
        mDataBase = FirebaseDatabase.getInstance().getReference();


        //Relacionamos con el xml
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navview);
        logout = (LinearLayout) findViewById(R.id.logout);

        //Enlazar con el xml
        add = findViewById(R.id.FABAddList);
        MensajeSinProductos = (ConstraintLayout) findViewById(R.id.MensajeSinProductos);
        imageView = (ImageView) findViewById(R.id.imageView);

        lista_productos = new ArrayList<ProductoModelo>();
        recyclerView = (RecyclerView) findViewById(R.id.item_product_nevera);
        mLayoutManager = new LinearLayoutManager(this);


        //Dependiendo de lo que seleccionemos en el menu, iremos a un fragment u a otro
        navigationView.setNavigationItemSelectedListener(new OyenteNav());

        leerProductos();

        //Para que se visualize
        registerForContextMenu(recyclerView);


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


    }

    //Para poner la imagen en el toolbar
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //He cambiado esto, porque ponia que el otro era para versiones de 30, y esta es la 26
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home); //Esto de aqui pone la imagen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

                        if (UbicacionProducto.equals("nevera")) {
                            ProductoModelo product = new ProductoModelo(IdProducto, NombreProducto, CantidadProducto, PrecioProducto, UbicacionProducto, TipoProducto, DateProducto, UID_usuario);
                            lista_productos.add(product);
                        }

                    }
                    adapterEliminar = new AdapterProducto(lista_productos, R.layout.item_principal, new AdapterProducto.OnItemClickListener() {
                        //Este click es al darle a la ciudad
                        @Override
                        public void onItemClick(ProductoModelo productoModelo, int position) {
                            Intent intent = new Intent(getApplicationContext(), AddEditProductActivity.class);
                            intent.putExtra("tarea", "editar");
                            intent.putExtra("ubicacion", "nevera");
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
                            //deleteProduct(productoModelo);
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

                //Collections.sort(lista_productos, ProductoModelo.ProductoAZ);
                //adapterEliminar.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        query1.addValueEventListener(valueEventListener);
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
            switch (item.getItemId()) {
                case R.id.menu_nevera: //Si coincide con el menumail es el fragment de Email
                    cambiarActivityNevera();
                    break;
                case R.id.menu_congelador: //Si coincide con el menumail es el fragment de Email
                    cambiarActivityCongelador();
                    break;
                case R.id.menu_lista: //Si coincide con el menumail es el fragment de Email
                    cambiarActivityTab();
                    break;
                case R.id.menu_ajustes:
                    cambiarActivityAjustes();
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
            case R.id.opciones_borrar:
                //Toast.makeText(MainActivity.this, "Has pulsado en borrar " +  fragment_actual, Toast.LENGTH_SHORT).show();
                //BorrarTodos();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void cambiarActivityAjustes(){
        Intent intent = new Intent(this, AjustesActivity.class);//Establecemos primero donde estamos y luego donde vamos
        startActivity(intent);
    }
    public void cambiarActivityTab(){
        Intent intent = new Intent(this, TabActivity.class);//Establecemos primero donde estamos y luego donde vamos
        startActivity(intent);
    }
    public void cambiarActivityNevera(){
        Intent intent = new Intent(this, NeveraActivity.class);//Establecemos primero donde estamos y luego donde vamos
        startActivity(intent);
    }
    public void cambiarActivityCongelador(){
        Intent intent = new Intent(this, CongeladorActivity.class);//Establecemos primero donde estamos y luego donde vamos
        startActivity(intent);
    }


}
