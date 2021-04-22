package com.example.nevera_andreaalejandra.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.nevera_andreaalejandra.Fragments.Login.HomeFragment;
import com.example.nevera_andreaalejandra.R;

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

