package com.example.nevera_andreaalejandra.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nevera_andreaalejandra.Adapters.AdapterProducto;
import com.example.nevera_andreaalejandra.Interfaces.OnButtonClickListener;
import com.example.nevera_andreaalejandra.Interfaces.OnItemClickListener;
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
    private String DateProducto;
    private String UID_usuario;

    //El boton para añadir y datos del xml
    private FloatingActionButton add;
    private ConstraintLayout MensajeSinProductos;
    private EditText buscar;

    //Creamos la lista para los productos de Congelador
    private List<ProductoModelo> lista_productos;
    private List<ProductoModelo> lista_auxiliar;


    //Creamos el adapter
    public AdapterProducto adapterEliminar;
    //Para el list view
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congelador);

        //Establecemos la toolbar y el mensaje que aparece en ella
        setToolbar();
        getSupportActionBar().setTitle("Mi congelador");

        //Variables para comprobar si el usuario esta conectado a internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Comprobamos el estado de internet
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {//Entrará cuando no consiga acceder al internet
            //Creamos un dialog para avisar al usuario de que necesita conectase a internet
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_nointernet);
            dialog.setCanceledOnTouchOutside(false);//No podrá salir al pulsar fuera
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //Para poner el fondo transparente, sino sale un recuadro gris
            dialog.show(); //Mostramos el dialogo

            //Creamos el evento del boton
            Button btnWifi = dialog.findViewById(R.id.btn_intentar);
            btnWifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate(); //Volvemos a cargar
                }
            });
        }


        //Inicializamos las variables para la BBDD
        mAuth = FirebaseAuth.getInstance();//Para inicializar la instancia de autenticación
        mDataBase = FirebaseDatabase.getInstance().getReference();


        //****Relacionamos con el xml***
        add = findViewById(R.id.FABAddList);
        MensajeSinProductos = (ConstraintLayout) findViewById(R.id.MensajeSinProductos);
        buscar = (EditText) findViewById(R.id.buscar);

        //Partes de drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navview);
        logout = (LinearLayout) findViewById(R.id.logout);

        //Variables necesarias para visualizar la lista
        recyclerView = (RecyclerView) findViewById(R.id.item_product_congelador);
        mLayoutManager = new LinearLayoutManager(this);
        lista_productos = new ArrayList<ProductoModelo>();

        //Dependiendo de lo que seleccionemos en el menu, iremos a un fragment u a otro
        navigationView.setNavigationItemSelectedListener(new OyenteNav());

        leerProductos();

        //Esta lista auxiliar es para poder recuperarla en caso de que busque
        lista_auxiliar = lista_productos;

        //*****CREAMOS LISTENERS****
        //Función para cuando pulse el logout, localizado en el drawer abajo a la izquierda
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut(); //Desconectamos al usuario

                //Para volver al fragment de login
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);//Establecemos primero donde estamos y luego donde vamos
                startActivity(intent);//Iniciamos el intent
            }
        });

        //Función para añadir un producto
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Con intent pasamos informacion al otro activity que vayamos a cambiar
                Intent intent = new Intent(getApplicationContext(), AddEditProductActivity.class);//Establecemos primero donde estamos y luego donde vamos

                //En este caso como hemos pulsado en el añador, pasaremos la opcion de "añadir"
                intent.putExtra("tarea", "añadir"); //Para detectar en el AddEdit si es un añadir o un editar
                intent.putExtra("ubicacion", "congelador"); //Para detectar en el AddEdit si es de congelador o de nevera

                startActivity(intent);//Iniciamos el intent
            }
        });

        //Función para aparecer o desaparecer el boton flotante al scrollear la lista
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

        //Función para buscar en la lista de productos
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
                lista_productos = lista_auxiliar;
            }
        });

    }


    //********** METODOS *************
    //Método para buscar, se activará cuando pulse el usuario en la opción localizada en los tres puntitos
    private void filter(String textoBuscar) {
        ArrayList<ProductoModelo> filteredList = new ArrayList<ProductoModelo>(); //Se crea la lista donde se guardan los productos filtrados
        for (ProductoModelo producto : lista_productos) {
            if(producto.getNombre().toLowerCase().contains(textoBuscar.toLowerCase())) { //Filtramos por el nombre
                filteredList.add(producto); //Añadimos los productos resultantes del filtrar
            }
        }
        //Actualizamos
        lista_productos = filteredList;
        try {
            adapterEliminar.notifyDataSetChanged();
            adapterEliminar.filterList(filteredList);
        } catch (NullPointerException e) {

        }
    }

    //Método para leer los productos de la BBDD
    private void leerProductos() {

        //Comprobamos el usuario que esta conectado
        String idUsuario = mAuth.getCurrentUser().getUid();
        Query query1 = FirebaseDatabase.getInstance().getReference("Producto").orderByChild("uid_usuario").equalTo(idUsuario);

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

                        //Para visualizar de manera distinta si en la BBDD no existe fecha
                        try {
                            DateProducto = ds.child("fecha").getValue().toString();
                        } catch (NullPointerException e) {
                            DateProducto = "--/--/----";
                        }

                        //Vinculamos el id
                        IdProducto = ds.getKey();

                        //Mostramos solo aquellos de congelador
                        if (UbicacionProducto.equals("congelador")) {
                            ProductoModelo product = new ProductoModelo(IdProducto, NombreProducto, CantidadProducto, PrecioProducto, UbicacionProducto, TipoProducto, DateProducto, UID_usuario);
                            lista_productos.add(product);
                        }
                    }
                    //Creamos el adapter
                    adapterEliminar = new AdapterProducto(lista_productos, R.layout.item_principal, new OnItemClickListener() {
                        //Este click es al darle al producto
                        @Override
                        public void onItemClick(ProductoModelo productoModelo, int position) {
                            Intent intent = new Intent(getApplicationContext(), AddEditProductActivity.class);
                            intent.putExtra("tarea", "editar"); //Al pulsar en un producto es editar
                            intent.putExtra("ubicacion", "congelador");
                            ProductoModelo productoModelo1 = lista_productos.get(position); //Obtenemos el producto pulsado
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("objeto", productoModelo1); //Pasamos el producto al activity de editar
                            intent.putExtras(bundle);

                            startActivity(intent); //Iniciamos editar
                        }
                        //Este boton es al clickar en la papelera eliminar que tiene cada cardview
                    }, new OnButtonClickListener() {
                        @Override
                        public void onButtonClick(ProductoModelo productoModelo, int position) {
                            deleteProduct(productoModelo);
                        }
                    });
                }

                //Atributos del recycler view
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(adapterEliminar);


                //Ponemos la animación
                Context context = recyclerView.getContext();
                LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_caer);
                recyclerView.setLayoutAnimation(layoutAnimationController);
                recyclerView.scheduleLayoutAnimation();

                //En caso de que no haya productos, visualizaremos una foto con mensaje
                if (adapterEliminar.getItemCount() > 0 ) {
                    MensajeSinProductos.setVisibility(View.INVISIBLE);
                }else {
                    MensajeSinProductos.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        //Ejecutamos la query
        query1.addValueEventListener(valueEventListener);
    }

    //Método para establecer el toolbar
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home); //Esto de aqui pone la imagen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Método para borrar un producto
    private void deleteProduct(final ProductoModelo productoModelo) { //Pasamos el producto a borrar

        //Para el checkbox del dialog
        View checkBoxView = View.inflate(this, R.layout.dialog_checkbox, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);

        //Si marcamos el checkbox saldrá otro dialog con un edittext para introducir la cantidad
        View edit_cantidad = View.inflate(this, R.layout.dialog_cantidad, null);
        EditText cantidad = (EditText) edit_cantidad.findViewById(R.id.CantidadModificar);

        //Creamos el primer alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro que quiere eliminar?")//Es el titulo
                .setTitle("Aviso")//Es el cuerpo
                .setView(checkBoxView) //Ponemos el checkbox
                .setCancelable(false)//Con esta opción no se puede salir aunque pulse atras
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() { //En caso de que de a confirmar
                    public void onClick(DialogInterface builder, int id) {
                        //Comprobamos si exite el producto
                        if(lista_productos.size()==1){
                            lista_productos.clear();//La limpiamos
                        }

                        //Oyente del check, si esta checkeado se creará otro alertdialog
                        if (checkBox.isChecked()) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(CongeladorActivity.this);
                            builder2.setMessage("Introduce la cantidad a comprar")
                                    .setView(edit_cantidad)
                                    .setCancelable(false)
                                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Cambiamos la cantidad
                                            try {
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
                                            }catch (NumberFormatException e){
                                                Toast.makeText(getApplicationContext(), "Debe introducir una cantidad", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }).show();

                        }else { //Si no esta seleccionado el check
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

    //Método para cambiar de activity
    public void cambiarActivity(Activity activity){
        Intent intent = new Intent(this, activity.getClass());//Establecemos primero donde estamos y luego donde vamos
        startActivity(intent);
    }


    //***** OYENTES MENUS ****
    //Este oyente es el del drawer
    public class OyenteNav implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //Obtenemos la posicion del menu
            Activity activity = null;
            //Se usa un switch para hacer mas eficiente el codigo
            switch (item.getItemId()) {
                case R.id.menu_nevera:
                    activity = new NeveraActivity();
                    cambiarActivity(activity);
                    break;
                case R.id.menu_congelador:
                    activity = new CongeladorActivity();
                    cambiarActivity(activity);
                    break;
                case R.id.menu_lista:
                    activity = new TabActivity();
                    cambiarActivity(activity);
                    break;
                case R.id.menu_ajustes:
                    activity = new AjustesActivity();
                    cambiarActivity(activity);
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

    //Oyente para los tres puntitos localizados en el toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Abre el drawer
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.opciones_ordenar: //Ordena de la AZ
                Collections.sort(lista_productos, ProductoModelo.ProductoAZ);
                adapterEliminar.notifyDataSetChanged();
                return true;
            case R.id.opciones_ordenar2: //Ordena de la ZA
                Collections.sort(lista_productos, ProductoModelo.ProductoZA);
                adapterEliminar.notifyDataSetChanged();
                return true;
            case R.id.opciones_ordenar3: //Ordena por cantidad ascendente
                Collections.sort(lista_productos, ProductoModelo.ProductoCantidadA);
                adapterEliminar.notifyDataSetChanged();
                return true;
            case R.id.opciones_ordenar4: //Ordena por cantidad descendente
                Collections.sort(lista_productos, ProductoModelo.ProductoCantidadD);
                adapterEliminar.notifyDataSetChanged();
                return true;
            case R.id.buscar: //Habilida/dehabilita la opción de buscar
                leerProductos();
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
}