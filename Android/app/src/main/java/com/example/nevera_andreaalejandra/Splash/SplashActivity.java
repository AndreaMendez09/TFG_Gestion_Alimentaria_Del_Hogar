package com.example.nevera_andreaalejandra.Splash;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nevera_andreaalejandra.Activities.LoginActivity;
import com.example.nevera_andreaalejandra.Activities.NeveraActivity;
import com.example.nevera_andreaalejandra.Models.ProductoModelo;
import com.example.nevera_andreaalejandra.Util.LoginUtil;
import com.example.nevera_andreaalejandra.Util.NotificationHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        /*for(int i=0; i==lista_productos.size(); i++ ){
            if(lista_productos.get(i).getCantidad()<2){
                sendNotificationProductos(lista_productos.get(i).getNombre(),isHighImportance);
            }
        }*/



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
    private void sendNotificationProductos(String nombreProducto, boolean isHighImportance) {
        // Toast.makeText(getApplicationContext(), "notificacion", Toast.LENGTH_LONG).show();

        String title = "Quedan pocos productos" ;
        String message = "Añade a tu lista de compra "+ nombreProducto + " que quedan pocas unidades";

        if (isHighImportance==true) {
            //TODO MIRAR ESTO
            Notification.Builder nb = notificationHandler.createNotification(title, message, isHighImportance, null);
            notificationHandler.getManager().notify(++counter, nb.build());
            notificationHandler.publishNotificationSummaryGroup(isHighImportance);
        }
    }
}
