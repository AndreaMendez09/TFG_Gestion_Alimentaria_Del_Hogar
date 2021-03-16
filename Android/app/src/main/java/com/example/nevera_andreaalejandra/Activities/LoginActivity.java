package com.example.nevera_andreaalejandra.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nevera_andreaalejandra.Fragments.Login.HomeFragment;
import com.example.nevera_andreaalejandra.Fragments.Login.LoginFragment;
import com.example.nevera_andreaalejandra.Fragments.Nevera_Fragment;
import com.example.nevera_andreaalejandra.R;

import java.util.EventListener;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        changeFragment(new HomeFragment());




    }

    //Método para cambiar de fragment
    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame_login, fragment).commit();
    }
}

