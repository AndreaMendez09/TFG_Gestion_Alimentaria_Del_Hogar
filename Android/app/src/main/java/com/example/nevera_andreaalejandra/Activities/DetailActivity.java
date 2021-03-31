package com.example.nevera_andreaalejandra.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nevera_andreaalejandra.R;


public class DetailActivity  extends AppCompatActivity {

    TextView textViewTitle;
    TextView textViewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setIntentValues();
    }

    private void setIntentValues() {
        if (getIntent() != null) {
            textViewTitle.setText(getIntent().getStringExtra("title"));
            textViewMessage.setText(getIntent().getStringExtra("message"));
        }
    }
}
