package com.example.nevera_andreaalejandra.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.nevera_andreaalejandra.Adapters.AdapterProductoLista;
import com.example.nevera_andreaalejandra.Adapters.AdapterTab;
import com.example.nevera_andreaalejandra.Fragments.Listas.ListaCongelador_Fragment;
import com.example.nevera_andreaalejandra.Fragments.Listas.ListaNevera_Fragment;
import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;

public class TabActivity extends AppCompatActivity {

    //Para el drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout logout;

    //Para la BBDD
    private FirebaseAuth mAuth;

    //Declaramos el adapter
    AdapterTab adapter;

    //Creamos las listas
    private ListaNevera_Fragment lista_nevera = new ListaNevera_Fragment();
    private ListaCongelador_Fragment lista_congelador = new ListaCongelador_Fragment();

    //Creamos la posicion
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        //Ponemos el toolbar
        setToolbar();
        getSupportActionBar().setTitle("Mis listas de la compra");

        //Relacionamos con el xml
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navTabs);
        logout = (LinearLayout) findViewById(R.id.logout);

        //Inicializamos las variables para la BBDD
        mAuth = FirebaseAuth.getInstance();

        //Establecemos el tabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Establecemos el adapter para el tablayout
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new AdapterTab(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //Dependiendo de lo que seleccionemos en el menu, iremos a un fragment u a otro
        navigationView.setNavigationItemSelectedListener(new TabActivity.OyenteNav());


        //Funcion de logout
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

        lista_nevera = (ListaNevera_Fragment) adapter.getItem(0);
        lista_congelador = (ListaCongelador_Fragment) adapter.getItem(1);
    }
    //Para poner la imagen en el toolbar
    private void setToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //He cambiado esto, porque ponia que el otro era para versiones de 30, y esta es la 26
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home); //Esto de aqui pone la imagen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                    break;
                case R.id.menu_congelador: //Si coincide con el menumail es el fragment de Email
                    activity = new CongeladorActivity();
                    cambiarActivity(activity);
                    break;
                case R.id.menu_lista: //Si coincide con el menumail es el fragment de Email
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

    public void cambiarActivity(Activity activity){
        Intent intent = new Intent(this, activity.getClass());//Establecemos primero donde estamos y luego donde vamos
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.more_options_listas, menu);
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
            //0 es el item de nevera y 1 es el item de congelador
            case R.id.opciones_ordenar:
                if (position == 0) {
                    lista_nevera.UpdateAZ();
                }else if (position == 1) {
                    lista_congelador.UpdateAZ();
                }
                return true;
            case R.id.opciones_ordenar2:
                if (position == 0) {
                    lista_nevera.UpdateZA();
                }else if (position == 1) {
                    lista_congelador.UpdateZA();
                }
                return true;
            case R.id.opciones_ordenar3:
                if (position == 0) {
                    lista_nevera.Update19();
                }else if (position == 1) {
                    lista_congelador.Update19();
                }
                return true;
            case R.id.opciones_ordenar4:
                if (position == 0) {
                    lista_nevera.Update91();
                }else if (position == 1) {
                    lista_congelador.Update91();
                }
                return true;
            case R.id.opciones_comprar: //Compra todos
                if (position == 0) {
                    lista_nevera.comprar_todos();
                }else if (position == 1) {
                    lista_congelador.comprar_todos();
                }
                break;
            case R.id.opciones_deseleccionar:
                Toast.makeText(TabActivity.this, "Has pulsado en deseleccionar", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
