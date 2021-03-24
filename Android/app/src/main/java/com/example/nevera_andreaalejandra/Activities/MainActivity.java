package com.example.nevera_andreaalejandra.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.nevera_andreaalejandra.Fragments.Congelador_Fragment;
import com.example.nevera_andreaalejandra.Fragments.Listas_Fragment;
import com.example.nevera_andreaalejandra.Fragments.Nevera_Fragment;
import com.example.nevera_andreaalejandra.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    //private FirebaseUser mFirebaseUser;
    //private FirebaseAuth mFirebaseAuth;
    private LinearLayout logout;
    private LinearLayout ajustes;

    //Para la BBDD
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;

    private Bundle extras;
    private String fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();

        //BBDD
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();

        //Para inicializar la instancia de autenticación
        /*mAuth = FirebaseAuth.getInstance();
        String ID_user = mAuth.getCurrentUser().getUid();
        Toast.makeText(MainActivity.this, "El usuario: " + ID_user, Toast.LENGTH_SHORT).show();*/

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
            if (fragment.equals("nevera")) {
                changeFragment(new Nevera_Fragment(), navigationView.getMenu().getItem(0));
            } else if (fragment.equals("congelador")) {
                changeFragment(new Congelador_Fragment(), navigationView.getMenu().getItem(1));
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
            Activity activity = null;
            //Obtenemos la posicion del menu
            switch (item.getItemId()) {
                case R.id.menu_nevera: //Si coincide con el menumail es el fragment de Email
                    fragment = new Nevera_Fragment();
                    fragmentTransaction = true;
                    break;
                case R.id.menu_congelador: //Si coincide con el menumail es el fragment de Email
                    fragment = new Congelador_Fragment();
                    fragmentTransaction = true;
                    break;
                case R.id.menu_lista: //Si coincide con el menumail es el fragment de Email
                    fragment = new Listas_Fragment();
                    fragmentTransaction = true;
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
                //Toast.makeText(MainActivity.this, "Has pulsado en borrar", Toast.LENGTH_SHORT).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void cambiarActivityAjustes(){
        Intent intent = new Intent(this, Ajustes.class);//Establecemos primero donde estamos y luego donde vamos
        startActivity(intent);
    }

    /*private void BorrarTodos() {
        mDataBase.child("Producto").child().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Creamos un toast, para informar de que se ha eliminado
                    Toast.makeText(getApplicationContext(), "Se ha eliminado satisfactoriamente", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }*/

}