package com.example.nevera_andreaalejandra.Splash;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nevera_andreaalejandra.Activities.AddEditProductActivity;
import com.example.nevera_andreaalejandra.Activities.LoginActivity;
import com.example.nevera_andreaalejandra.Activities.NeveraActivity;
import com.example.nevera_andreaalejandra.Adapters.AdapterProducto;
import com.example.nevera_andreaalejandra.Interfaces.OnButtonClickListener;
import com.example.nevera_andreaalejandra.Interfaces.OnItemClickListener;
import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.R;
import com.example.nevera_andreaalejandra.Util.LoginUtil;
import com.example.nevera_andreaalejandra.Util.NotificationHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;


    //Notificaciones
    private NotificationHandler notificationHandler;
    private int counter = 0;
    private boolean isHighImportance = true;

    //Creamos la lista para los productos de Nevera
    private List<ProductoModelo> lista_productos;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();



        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        notificationHandler = new NotificationHandler(this);
        lista_productos = new ArrayList<ProductoModelo>();
        //lista_productos.clear();

        Intent intentLogin = new Intent(this, LoginActivity.class);

        if (!TextUtils.isEmpty(LoginUtil.getUserMailPrefs(prefs)) && !TextUtils.isEmpty(LoginUtil.getUserPassPrefs(prefs))) {
            login();
            //Toast.makeText(this, "user" + idUsuario, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(intentLogin);
        }

        leerProductos();

        /*if (!TextUtils.isEmpty(LoginUtil.getUserMailPrefs(prefs)) && !TextUtils.isEmpty(LoginUtil.getUserPassPrefs(prefs))) {
            for (ProductoModelo item : lista_productos) {
                Toast.makeText(this, "hola", Toast.LENGTH_SHORT).show();
                if (item.getCantidad() < 4) {
                    sendNotificationProductos(item.getNombre(), isHighImportance);
                }
            }
        }*/



    }

    private void leerProductos() {
        String idUsuario = "";
        try {
            idUsuario = mAuth.getCurrentUser().getUid();
            Query query1 = FirebaseDatabase.getInstance().getReference("Producto").orderByChild("uid_usuario").equalTo(idUsuario);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) { //Añadimos los campos a las variables creadas anteriormente
                            NombreProducto = ds.child("nombre").getValue().toString(); //Lo que hay entre parentesis es el nombre de como lo guarda la base de datos
                            TipoProducto = ds.child("tipo").getValue().toString();
                            UbicacionProducto = ds.child("ubicacion").getValue().toString();
                            PrecioProducto = Double.valueOf(ds.child("precio").getValue().toString());
                            CantidadProducto = Integer.parseInt(ds.child("cantidad").getValue().toString());
                            UID_usuario = ds.child("uid_usuario").getValue().toString();
                            //Toast.makeText(SplashActivity.this, "product" + NombreProducto, Toast.LENGTH_SHORT).show();

                            try {
                                DateProducto = ds.child("fecha").getValue().toString();
                            } catch (NullPointerException e) {
                                DateProducto = "--/--/----";
                            }

                            //Vinculamos el id
                            IdProducto = ds.getKey();

                            ProductoModelo product = new ProductoModelo(IdProducto, NombreProducto, CantidadProducto, PrecioProducto, UbicacionProducto, TipoProducto, DateProducto, UID_usuario);
                            lista_productos.add(product);

                            if (product.getCantidad() < 2) {
                                if (product.getUbicacion().equals("nevera") || product.getUbicacion().equals("congelador"))
                                    sendNotificationProductos(product.getNombre(),product.getUbicacion(), isHighImportance);
                            }

                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            query1.addValueEventListener(valueEventListener);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        //Se recogen las credenciales para loguear al usuario
        String email= LoginUtil.getUserMailPrefs(prefs);
        String password= LoginUtil.getUserPassPrefs(prefs);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //Si el usuario y contraseña son correctos, se carga el UserActivity.
                            Intent intent = new Intent(getApplication(), NeveraActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }
    private void sendNotificationProductos(String nombreProducto, String ubicacionProducto, boolean isHighImportance) {
        // Toast.makeText(getApplicationContext(), "notificacion", Toast.LENGTH_LONG).show();

        String title = "Quedan pocos productos" ;
        String message = "Añade a tu lista de compra "+ nombreProducto + " que quedan pocas unidades, se encuentra en " + ubicacionProducto;

        if (isHighImportance==true) {
            //TODO MIRAR ESTO
            Notification.Builder nb = notificationHandler.createNotification(title, message, isHighImportance, null);
            notificationHandler.getManager().notify(++counter, nb.build());
            notificationHandler.publishNotificationSummaryGroup(isHighImportance);
        }
    }
}
