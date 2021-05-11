package com.example.nevera_andreaalejandra.Activities;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;
import com.example.nevera_andreaalejandra.Util.NotificationHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class AddEditProductActivity extends AppCompatActivity{
    //El boton para añadir
    private FloatingActionButton add;

    //Creamos las variables necesarias para asociarlas con el XML
    private EditText calendario;
    private EditText nombre;
    private EditText precio;
    private EditText cantidad;
    private Spinner tipo;
    private Switch switchCalendario;

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
        switchCalendario = (Switch) findViewById(R.id.switchCalendario);

        //Para quitar las funciones de copiar y pegar
        calendario.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
            public void onDestroyActionMode(ActionMode mode) {
            }
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });


        //Creamos los métodos on click
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });

        //Añadimos la toolbar
        setToolbar();

        //Obtenemos el intent
        Bundle bundle = getIntent().getExtras();

        //Obtenemos el producto
        ProductoModelo producto = (ProductoModelo) bundle.getSerializable("objeto");

        //Llamamos al metodo
        Edit_O_Add(producto, bundle);



        //Este es el boton de guardar
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean cambiar = false;
                //Obtenemos el valor del intent, para detectar si es añadir o editar
                if (bundle != null) { //Si ha encontrado algun dato
                    String tarea = bundle.getString("tarea"); //Obtenemos el dato
                    if (tarea.equals("añadir")) {
                        añadirProducto();
                    }else if (tarea.equals("editar")){
                        cambiar = editProduct(producto);
                    }

                    //Para saber cuando cambiar de activity
                    if (cambiar)
                        changeActivity();

                    //En caso de que el usuario quiera añadir el evento de calendario
                    if (switchCalendario.isChecked() && (!(calendario.getText().toString().equals(""))))
                        CrearEvento();

                    if (switchCalendario.isChecked() && calendario.getText().toString().equals(""))
                        Toast.makeText(AddEditProductActivity.this, "No se puede crear el evento sin fecha de caducidad.", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
    private void Edit_O_Add(ProductoModelo producto, Bundle valor) {
        if (valor != null) {//Comprobamos que nos ha llegado algo con la tag tarea
            String tarea = valor.getString("tarea"); //Obtenemos el dato
            if (tarea.equals("añadir")) {//Comprobamos si es añadir
                AddEditProductActivity.this.setTitle("Añadir un nuevo producto");//Cambiamos el título
                esCreado = true; //Establecemos la booleana a true para detectar que debe crear un nuevo producto
            } else if (tarea.equals("editar")){//Comprobamos si es editar
                AddEditProductActivity.this.setTitle("Editar el producto");//Cambiamos el título
            }

            if (esCreado == false) {//Es decir, lo vamos a editar

                //Ponemos por defecto los datos que obtenemos del producto
                nombre.setText(producto.getNombre());
                precio.setText(String.valueOf(producto.getPrecio()));
                cantidad.setText(String.valueOf(producto.getCantidad()));

                //En caso de que no exista fecha, no rellenara el campo
                if (!(producto.getFecha().equals("--/--/----"))) {
                    calendario.setText(String.valueOf(producto.getFecha()));
                }

                //Para detectar el tipo de producto y ponerlo por defecto
                for (int i = 0; i < tipo.getCount(); i++) {
                    if (tipo.getItemAtPosition(i).equals(producto.getTipo())) {
                        tipo.setSelection(i);
                        break;
                    }
                }

            }
        }
    }

    //Método para editar el producto
    private Boolean editProduct(ProductoModelo producto_eliminar) {
        //Obtenemos los datos de los editText
        String productNameEdit = nombre.getText().toString().trim();
        String productFechaEdit = calendario.getText().toString();
        int productCantidadEdit = Integer.parseInt(cantidad.getText().toString().trim());
        double productPrecioEdit = -1;
        try {
            productPrecioEdit = Double.parseDouble(precio.getText().toString().trim()); //TODO Esto da error, no se porque
        } catch (NumberFormatException e) {

        }
        String productTipoEdit = tipo.getSelectedItem().toString();

        //Creamos el nuevo producto
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

        //Si todos los campos estan rellenos, procedemos a editar en la BBDD
        if (esVacio) {
            //Actualizamos los campos
            mDataBase.child(producto_eliminar.getId()).child("nombre").setValue(objeto_producto.getNombre());
            mDataBase.child(producto_eliminar.getId()).child("precio").setValue(objeto_producto.getPrecio());
            mDataBase.child(producto_eliminar.getId()).child("cantidad").setValue(objeto_producto.getCantidad());
            mDataBase.child(producto_eliminar.getId()).child("tipo").setValue(objeto_producto.getTipo());
            if (!(objeto_producto.getFecha().equals(""))) //Para que no añada un campo vacio si la fecha no ha sido seleccionada
                mDataBase.child(producto_eliminar.getId()).child("fecha").setValue(objeto_producto.getFecha());
        } else { //Informamos al usuario que debe rellenar todos los campos
            Toast.makeText(AddEditProductActivity.this, "Rellena los campos necesarios", Toast.LENGTH_SHORT).show();
        }

        return esVacio;//Devolvemos para saber si debemos cambiar de activity o no
    }

    //Método para cuando deseamos crear un nuevo producto
    private boolean añadirProducto() {

        //Creamos y rellenamos algunas variables
        int productCantidad = 0;
        String productName = null;
        String productFecha = calendario.getText().toString();
        String productTipo = tipo.getSelectedItem().toString();
        double productPrecio;
        try {
            productPrecio = Double.parseDouble(precio.getText().toString().trim());
        } catch (NumberFormatException e) {
            productPrecio = -1;
        }


        //Vamos a comprobar si los campos que son obligatorios, estan rellenos
        //En este caso nombre y cantidad, porque tipo siempre tiene uno
        boolean isAble = true;
        if (nombre.getText().toString().isEmpty()) {
            nombre.setError("El nombre es obligatorio");
            isAble = false;
        }else {
            productName = nombre.getText().toString().trim();
        }
        if (cantidad.getText().toString().equals("")) {
            cantidad.setError("La cantidad es obligatoria");
            isAble = false;
        }else {
            productCantidad = Integer.parseInt(cantidad.getText().toString().trim());
        }

        if (isAble) {//Si los campos obligatorios estan rellenos, lo añadimos a la BBDD
            addBBDD(productName, productFecha, productCantidad, productPrecio, productTipo);
        } else {
            Toast.makeText(AddEditProductActivity.this, "Rellena los campos necesarios", Toast.LENGTH_SHORT).show();
        }
        return isAble;

    }

    //Método para añadir a la BBDD
    private void addBBDD(String productName, String productFecha, int productCantidad, double productPrecio, String productTipo) {
        extras = getIntent().getExtras();
        //Obtenemos la ubicacion desde donde hemos llamado a añadir
        if (extras != null) {
            ubicacion = extras.getString("ubicacion");
        }

        //Obtenemos el id del usuario conectado
        String ID_user = mAuth.getCurrentUser().getUid();

        //Creamos el producto
        ProductoModelo product = new ProductoModelo(productName,productCantidad,ubicacion, productTipo, ID_user);

        //Comprobamos si los datos se han rellenado
        if (!(productFecha.equals(""))) {
            product.setFecha(productFecha);
        }

        if (productPrecio > 0) {
            product.setPrecio(productPrecio);
        }

        //Lo añadimos a la BBDD
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
        //Cambiamos el activity
        changeActivity();

    }

    private void changeActivity() {
        Intent intent = null;
        extras = getIntent().getExtras();
        if (extras != null) {
            ubicacion = extras.getString("ubicacion");
        }
        if(ubicacion.equals("nevera")) {
            //Para volver al fragment donde nos encontramos
            intent = new Intent(this, NeveraActivity.class);//Establecemos primero donde estamos y luego donde vamos
        }else{
            intent = new Intent(this, CongeladorActivity.class);
        }
            intent.putExtra("fragment", ubicacion); //Para detectar en el AddEdit si es un añadir o un editar

            startActivity(intent);//Iniciamos el intent

    }

    private void mostrarCalendario() {
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
    public void CrearEvento(){
        //Creamos la instancia del calendario, para poder establecer la fecha del evento
        Calendar cal = Calendar.getInstance();
        Intent intent = null;
            try{
                //Obtenemos la fecha
                String[] fecha = calendario.getText().toString().split("/");

                //Establecemos la fecha
                cal.set(Calendar.YEAR, Integer.parseInt(fecha[2]));
                cal.set(Calendar.MONTH, Integer.parseInt(fecha[1]) - 1); //Es menos 1, debido a que enero es 0
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fecha[0]));

                //Creamos el evento
                intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.ALL_DAY,true);
                intent.putExtra(CalendarContract.Events.TITLE,"El producto " + nombre.getText() + " se caduca pronto");
                intent.putExtra(CalendarContract.Events.DESCRIPTION, "Comer antes de que se ponga malo.");
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis()); //Establecemos el día del evento

                //Comprobamos que tenga aplicación para ello
                if(intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
                else
                    Toast.makeText(getApplicationContext(), "No hay aplicacion para crear un evento de calendario", Toast.LENGTH_LONG).show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Fecha invalida", Toast.LENGTH_LONG).show();
            }
        //}
    }

    //Para poner la imagen en el toolbar
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //He cambiado esto, porque ponia que el otro era para versiones de 30, y esta es la 26
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_undo); //Esto de aqui pone la imagen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}