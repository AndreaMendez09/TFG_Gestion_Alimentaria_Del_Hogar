package com.example.nevera_andreaalejandra.Activities;

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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.nevera_andreaalejandra.Adapters.AdapterTab;
import com.example.nevera_andreaalejandra.Fragments.Congelador_Fragment;
import com.example.nevera_andreaalejandra.Fragments.Nevera_Fragment;
import com.example.nevera_andreaalejandra.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class TabActivity extends AppCompatActivity {

    //Para el drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout logout;

    //Para la BBDD
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;

    private Bundle extras;
    private String fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        //Ponemos el toolbar
        setToolbar();

        //Relacionamos con el xml
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navview);
        logout = (LinearLayout) findViewById(R.id.logout);

        //BBDD
        mAuth = FirebaseAuth.getInstance();

        //Establecemos el tabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //tabLayout.addTab(tabLayout.newTab().setText("Nevera"));
        //tabLayout.addTab(tabLayout.newTab().setText("Congelador"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Establecemos el adapter
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        AdapterTab adapter = new AdapterTab(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Toast.makeText(TabActivity.this, "Seleccionado -> "+tab.getText(), Toast.LENGTH_SHORT).show();
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //Toast.makeText(TabActivity.this, "Deseleccionado -> "+tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Toast.makeText(TabActivity.this, "Reseleccioando -> "+tab.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        //Dependiendo de lo que seleccionemos en el menu, iremos a un fragment u a otro
        navigationView.setNavigationItemSelectedListener(new TabActivity.OyenteNav());

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
        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
    class OyenteNav implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            boolean fragmentTransaction = false;
            Fragment fragment = null;
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

    public void cambiarActivityAjustes(){
        Intent intent = new Intent(this, AjustesActivity.class);//Establecemos primero donde estamos y luego donde vamos
        startActivity(intent);
    }
    public void cambiarActivityTab(){
        Intent intent = new Intent(this, TabActivity.class);//Establecemos primero donde estamos y luego donde vamos
        startActivity(intent);
    }

}
