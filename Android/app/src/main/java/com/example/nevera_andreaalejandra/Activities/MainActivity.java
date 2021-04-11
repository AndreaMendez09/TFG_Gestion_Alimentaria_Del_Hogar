package com.example.nevera_andreaalejandra.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.nevera_andreaalejandra.Fragments.Congelador_Fragment;
import com.example.nevera_andreaalejandra.Fragments.Nevera_Fragment;
import com.example.nevera_andreaalejandra.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    //Partes del drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout logout;

    //Para la BBDD
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;

    private Bundle extras;
    private String fragment;

    //Variable para detectar en que parte de la app está
    private int fragment_actual = 0;

    //Variables para detectar el borrar
    private String IdProducto;
    private String UbicacionProducto;
    private String UID_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();

        //BBDD
        mAuth = FirebaseAuth.getInstance();//Para inicializar la instancia de autenticación
        mDataBase = FirebaseDatabase.getInstance().getReference();


        //Relacionamos con el xml
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navview);
        logout = (LinearLayout) findViewById(R.id.logout);


        setFragmentByDefault(); //Para poner el fragment por defecto. En este caso email

        //Dependiendo de lo que seleccionemos en el menu, iremos a un fragment u a otro
        navigationView.setNavigationItemSelectedListener(new OyenteNav());

        extras = getIntent().getExtras();
        if (extras != null) {
            fragment = extras.getString("fragment");
            try {
                if (fragment.equals("nevera")) {
                    changeFragment(new Nevera_Fragment(), navigationView.getMenu().getItem(0));
                } else if (fragment.equals("congelador")) {
                    changeFragment(new Congelador_Fragment(), navigationView.getMenu().getItem(1));
                }
            }catch (NullPointerException e) {

            }
        }

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

    //Método para poner el fragment por defecto al iniciar
    private void setFragmentByDefault() {
        /* Esto eran mis pruebas pero es que simplemente soy idiota y estaba llamando al método antes de enlazar el menu
        Fragment fragment = new Emails_Fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        navigationView.setCheckedItem(R.id.menu_mail);*/
        changeFragment(new Nevera_Fragment(), navigationView.getMenu().getItem(0));
    }

    //Método para cambiar de fragment
    private void changeFragment(Fragment fragment, MenuItem item) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        item.setChecked(true);
        getSupportActionBar().setTitle(item.getTitle()); //Cambia el titulo de arriba
    }

    //--------Oyentes
    class OyenteNav implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            boolean fragmentTransaction = false;
            Fragment fragment = null;
            //Obtenemos la posicion del menu
            switch (item.getItemId()) {
                case R.id.menu_nevera: //Si coincide con el menumail es el fragment de Email
                    fragment_actual = 0;
                    fragment = new Nevera_Fragment();
                    fragmentTransaction = true;
                    break;
                case R.id.menu_congelador: //Si coincide con el menumail es el fragment de Email
                    fragment_actual = 1;
                    fragment = new Congelador_Fragment();
                    fragmentTransaction = true;
                    break;
                case R.id.menu_lista: //Si coincide con el menumail es el fragment de Email
                    cambiarActivityTab();
                    break;
                case R.id.menu_ajustes:
                    cambiarActivityAjustes();
                    break;

            }
            if (fragmentTransaction) {
                changeFragment(fragment, item);
                drawerLayout.closeDrawers();
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
        }
        switch (item.getItemId()) {
            case R.id.opciones_ordenar:
                Toast.makeText(MainActivity.this, "Has pulsado en ordenar", Toast.LENGTH_SHORT).show();
                break;
            case R.id.opciones_borrar:
                //Toast.makeText(MainActivity.this, "Has pulsado en borrar " +  fragment_actual, Toast.LENGTH_SHORT).show();
                BorrarTodos();
                break;
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

    private void BorrarTodos() {
        //Para el checkbox
        View checkBoxView = View.inflate(this, R.layout.dialog_checkbox, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);

        //creamos el alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro que quiere eliminar todos?")//es como la partesita de arriba
                .setTitle("Aviso")//es el texto
                .setView(checkBoxView)
                .setCancelable(false)//es para que no se salga  si oprime cualquier cosa
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface builder, int id) {
                        mDataBase.child("Producto").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        UbicacionProducto = ds.child("ubicacion").getValue().toString();
                                        UID_usuario = ds.child("uid_usuario").getValue().toString();

                                        //Vinculamos el id
                                        IdProducto = ds.getKey();

                                        //Comprobamos el usuario que esta conectado
                                        String id = mAuth.getCurrentUser().getUid();

                                        //Obtenemos la posicion
                                        String borrarDe = obtenerUbicacion();
                                        String compra;
                                        if (UbicacionProducto.equals(borrarDe)) { //Comprobamos que esta en esa lista
                                            if (UID_usuario.equals(id)) { //Comprobamos que solo borra los del usuario
                                                if (checkBox.isChecked()) {
                                                    if (borrarDe.equals("nevera"))
                                                        compra="lista_nevera";
                                                    else
                                                        compra="lista_congelador";

                                                    //Movemos los productos
                                                    //Si esta seleccionado el check, lo cambiamos de ubicacion
                                                    mDataBase.child("Producto").child(IdProducto).child("ubicacion").setValue(compra).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), "Se ha añadido satisfactoriamente", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }else {
                                                    //Borramos el producto
                                                    mDataBase.child("Producto").child(IdProducto).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                //Creamos un toast, para informar de que se ha eliminado
                                                                Toast.makeText(getApplicationContext(), "Se han eliminado todos", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
        builder.setNegativeButton("Cancelar", null).show();

    }

    private String obtenerUbicacion() {
        String borrarDe = "";
        if (fragment_actual == 0) {
            borrarDe = "nevera";
        }else if (fragment_actual == 1) {
            borrarDe = "congelador";
        }
        return borrarDe;
    }

}