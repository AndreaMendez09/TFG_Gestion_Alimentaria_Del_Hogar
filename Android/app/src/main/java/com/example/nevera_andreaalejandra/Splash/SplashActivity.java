package com.example.nevera_andreaalejandra.Splash;

import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
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
    private String DateProducto;
    private String UID_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        } else {

            mAuth = FirebaseAuth.getInstance();


            prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            notificationHandler = new NotificationHandler(this);
            lista_productos = new ArrayList<ProductoModelo>();

            Intent intentLogin = new Intent(this, LoginActivity.class);

            if (!TextUtils.isEmpty(LoginUtil.getUserMailPrefs(prefs)) && !TextUtils.isEmpty(LoginUtil.getUserPassPrefs(prefs))) {
                login();
            } else {
                startActivity(intentLogin);
            }

            leerProductos();
        }
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
        String title = "Quedan pocos productos" ;
        String message = "Añade a tu lista de compra "+ nombreProducto + " que quedan pocas unidades, se encuentra en " + ubicacionProducto;

        if (isHighImportance==true) {
            Notification.Builder nb = notificationHandler.createNotification(title, message, isHighImportance, null);
            notificationHandler.getManager().notify(++counter, nb.build());
            notificationHandler.publishNotificationSummaryGroup(isHighImportance);
        }
    }
}
