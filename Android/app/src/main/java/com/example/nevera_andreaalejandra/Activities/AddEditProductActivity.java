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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nevera_andreaalejandra.Fragments.Nevera_Fragment;
import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class AddEditProductActivity extends AppCompatActivity {
    //El boton para añadir
    private FloatingActionButton add;

    //Creamos las variables necesarias para asociarlas con el XML
    private EditText calendario;
    private EditText nombre;
    private EditText precio;
    private EditText cantidad;
    private Spinner  tipo;

    //Creamos el DatePicker para seleccionar una fecha
    private DatePickerDialog SeleccionarFecha;

    //Para realizar la conexon con la firebase
    private DatabaseReference mDataBase;

    //Para obtener los datos
    private Bundle extras;
    private String ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        //DB Firebase
        mDataBase = FirebaseDatabase.getInstance().getReference();

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
        
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                añadirProducto();
            }
        });

        setToolbar();
        
        

    }

    private void añadirProducto() {
        String productName = nombre.getText().toString().trim();
        String productFecha = calendario.getText().toString();
        int productCantidad = Integer.parseInt(cantidad.getText().toString().trim());
        double productPrecio = 0.0; //TODO Esto da error, no se porque
        if (!(precio.getText().toString().trim().isEmpty())){
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
        }else {
            Toast.makeText(AddEditProductActivity.this, "Rellena los campos necesarios", Toast.LENGTH_SHORT).show();
        }
    }

    private void addBBDD(String productName, String productFecha, int productCantidad, double productPrecio, String productTipo) {
        //TODO La ubicación vamos a tener que pasarla por el intent, igual que si es añadir o editar
        extras = getIntent().getExtras();
        if (extras != null) {
            ubicacion = extras.getString("ubicacion");
        }
        Toast.makeText(AddEditProductActivity.this, ubicacion + "hola", Toast.LENGTH_SHORT).show();
        ProductoModelo producto = new ProductoModelo(productName,productCantidad, productPrecio,ubicacion, productTipo, productFecha);
        mDataBase.child("Producto").push().setValue(producto).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddEditProductActivity.this, "Producto Creado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddEditProductActivity.this, "Error al crear", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Para volver al fragment donde nos encontramos
        Intent intent = new Intent(this, MainActivity.class);//Establecemos primero donde estamos y luego donde vamos

        //TODO AQUI DEPENDERA DE LO QUE PASEMOS POR EL INTENT IR A UN FRAGMENT U A OTRO
        //En este caso como hemos pulsado en el más, pasaremos la opcion de añadir
        //intent.putExtra("Tipo", "nevera"); //Para detectar en el AddEdit si es un añadir o un editar

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