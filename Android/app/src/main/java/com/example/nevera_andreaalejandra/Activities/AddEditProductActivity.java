package com.example.nevera_andreaalejandra.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nevera_andreaalejandra.Fragments.Nevera_Fragment;
import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEditProductActivity extends AppCompatActivity implements Serializable {
    //El boton para añadir
    private FloatingActionButton add;

    //Creamos las variables necesarias para asociarlas con el XML
    private EditText calendario;
    private EditText nombre;
    private EditText precio;
    private EditText cantidad;
    private Spinner tipo;
    private List<ProductoModelo> producto;

    //Creamos el DatePicker para seleccionar una fecha
    private DatePickerDialog SeleccionarFecha;

    //Para realizar la conexon con la firebase
    private DatabaseReference mDataBase;
    private FirebaseAuth mAuth;

    //Para obtener los datos
    private Bundle extras;
    private String ubicacion;
    private Boolean esCreado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        //DB Firebase
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Producto");
        mAuth = FirebaseAuth.getInstance();

        //Vinculamos con el XML
        add = findViewById(R.id.Guardar);
        calendario = (EditText) findViewById(R.id.ECalendarioProducto);
        nombre = (EditText) findViewById(R.id.ENombreProducto);
        precio = (EditText) findViewById(R.id.EPrecioProducto);
        cantidad = (EditText) findViewById(R.id.ECantidadProducto);
        tipo = (Spinner) findViewById(R.id.ETipoProducto);


        //Creamos los métodos on click
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });


        setToolbar();

        //Obtenemos el intent
        Bundle bundle = getIntent().getExtras();

        //Obtenemos el objeto
        ProductoModelo producto = (ProductoModelo) bundle.getSerializable("objeto");

        //Llamamos al metodo
        Edit_O_Add(producto, bundle);


        //Este es el boton de guardar
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean cambiar = false;
                //Obtenemos el valor del intent
                if (bundle != null) { //Si ha encontrado algun dato
                    String tarea = bundle.getString("tarea"); //Obtenemos el dato
                    if (tarea.equals("añadir")) {
                        añadirProducto();
                    }else if (tarea.equals("editar")){
                        Toast.makeText(AddEditProductActivity.this, "Se edita el producto", Toast.LENGTH_SHORT).show();
                        cambiar = editProduct(producto);
                    }

                    //Para saber cuando cambiar de activity
                    if (cambiar){
                        changeActivity();
                    }
                }
            }
        });


    }
    private void Edit_O_Add(ProductoModelo producto, Bundle valor) {
        if (valor != null) {//Comprobamos que nos ha llegado algo con la tag añadir del BoardActivity
            String tarea = valor.getString("tarea"); //Obtenemos el dato
            if (tarea.equals("añadir")) {//Comprobamos que nos ha llegado algo con la tag añadir del BoardActivity
                AddEditProductActivity.this.setTitle("Añadir un nuevo producto");//Cambiamos el título
                Toast.makeText(AddEditProductActivity.this, "Estamos en añadir", Toast.LENGTH_SHORT).show();
                esCreado = true;
            } else if (tarea.equals("editar")){//esto significa editar
                AddEditProductActivity.this.setTitle("Editar el producto");//Cambiamos el título
                Toast.makeText(AddEditProductActivity.this, "Estamos en editar", Toast.LENGTH_SHORT).show();
            }

            if (esCreado == false) {//Es decir, lo vamos a editar
                //Toast.makeText(getApplication(), "Estoy en editar", Toast.LENGTH_SHORT).show();
                //Al estar dentro del cardview de editar, queremos que al cargar el xml tenga los datos por defecto que tiene en la base de datos
                nombre.setText(producto.getNombre());
                //precio.setText((int) producto.getPrecio());
                //cantidad.setText((int) producto.getCantidad());
                //calendario.setText(producto.getFecha());
            }
        }
    }
    //Para editar el producto
    private Boolean editProduct(ProductoModelo producto_eliminar) {//Pasamos el producto a "eliminar" para poder obtener el id
        String productNameEdit = nombre.getText().toString().trim();
        String productFechaEdit = calendario.getText().toString();
        int productCantidadEdit = Integer.parseInt(cantidad.getText().toString().trim());
        double productPrecioEdit = 0.0; //TODO Esto da error, no se porque
        if (!(precio.getText().toString().trim().isEmpty())) {
            productPrecioEdit = Double.parseDouble(precio.getText().toString().trim());
        }
        String productTipoEdit = tipo.getSelectedItem().toString();
        ProductoModelo objeto_producto = new ProductoModelo(productNameEdit, productCantidadEdit,productPrecioEdit, productTipoEdit, productFechaEdit);//Creamos una productp vacia


        boolean esVacio = true;
        //Utilizo la misma booleana para no complicarme
        if (productNameEdit.isEmpty()) {
            nombre.setError("El nombre es obligatorio");
            esVacio= false;
        }
        if (cantidad.getText().toString().isEmpty()) {
            cantidad.setError("La cantidad es obligatoria");
            esVacio = false;
        }

        if (esVacio) {
            //Actualizamos los campos
            mDataBase.child(producto_eliminar.getId()).child("nombre").setValue(objeto_producto.getNombre());
            mDataBase.child(producto_eliminar.getId()).child("precio").setValue(objeto_producto.getPrecio());
            mDataBase.child(producto_eliminar.getId()).child("cantidad").setValue(objeto_producto.getCantidad());
            mDataBase.child(producto_eliminar.getId()).child("tipo").setValue(objeto_producto.getTipo());
            mDataBase.child(producto_eliminar.getId()).child("fecha").setValue(objeto_producto.getFecha());
        } else {
            Toast.makeText(AddEditProductActivity.this, "Rellena los campos necesarios", Toast.LENGTH_SHORT).show();
        }

        return esVacio;//Devolvemos para saber si debemos cambiar de activity o no
    }

    private boolean añadirProducto() {
        String productName = nombre.getText().toString().trim();
        String productFecha = calendario.getText().toString();
        int productCantidad = Integer.parseInt(cantidad.getText().toString().trim());
        double productPrecio = 0.0; //TODO Esto da error, no se porque
        if (!(precio.getText().toString().trim().isEmpty())) {
            productPrecio = Double.parseDouble(precio.getText().toString().trim());
        }
        String productTipo = tipo.getSelectedItem().toString();

        //Vamos a comprobar si los campos que son obligatorios, estan rellenos
        //En este caso nombre y cantidad, porque tipo siempre tiene uno
        boolean isAble = true;
        if (productName.isEmpty()) {
            nombre.setError("El nombre es obligatorio");
            isAble = false;
        }
        if (cantidad.getText().toString().isEmpty()) {
            cantidad.setError("La cantidad es obligatoria");
            isAble = false;
        }

        if (isAble) {
            addBBDD(productName, productFecha, productCantidad, productPrecio, productTipo);
        } else {
            Toast.makeText(AddEditProductActivity.this, "Rellena los campos necesarios", Toast.LENGTH_SHORT).show();
        }
        return isAble;

    }

    private void addBBDD(String productName, String productFecha, int productCantidad, double productPrecio, String productTipo) {
        extras = getIntent().getExtras();
        if (extras != null) {
            ubicacion = extras.getString("ubicacion");
        }        //Para inicializar la instancia de autenticación

        //Obtenemos el id del usuario conectado
        String ID_user = mAuth.getCurrentUser().getUid();
        Toast.makeText(AddEditProductActivity.this, "El usuario: " + ID_user, Toast.LENGTH_SHORT).show();
        //IdProducto = ds.getKey();

        ProductoModelo product = new ProductoModelo(productName,productCantidad, productPrecio,ubicacion, productTipo, productFecha, ID_user);
        mDataBase.push().setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddEditProductActivity.this, "Producto Creado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddEditProductActivity.this, "Error al crear", Toast.LENGTH_SHORT).show();
                }
            }
        });
        changeActivity();

    }

    private void changeActivity() {
        extras = getIntent().getExtras();
        if (extras != null) {
            ubicacion = extras.getString("ubicacion");
        }

        //Para volver al fragment donde nos encontramos
        Intent intent = new Intent(this, MainActivity.class);//Establecemos primero donde estamos y luego donde vamos
        intent.putExtra("fragment", ubicacion); //Para detectar en el AddEdit si es un añadir o un editar
        startActivity(intent);//Iniciamos el intent
    }

    private void mostrarCalendario() {
        //Toast.makeText(this, "Has pulsado para mostrar el calendario", Toast.LENGTH_SHORT).show();
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        SeleccionarFecha = new DatePickerDialog(AddEditProductActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendario.setText(dayOfMonth + "/" + (month+1) + "/" + year);
            }
        },  year, month, day);
        SeleccionarFecha.show();


    }

    //Para poner la imagen en el toolbar
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //He cambiado esto, porque ponia que el otro era para versiones de 30, y esta es la 26
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_undo); //Esto de aqui pone la imagen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}