package com.example.nevera_andreaalejandra.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nevera_andreaalejandra.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity  extends AppCompatActivity {

    @BindView(R.id.textViewTitle)
    TextView textViewTitle;
    @BindView(R.id.textViewMessage)
    TextView textViewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setIntentValues();
    }

    private void setIntentValues() {
        if (getIntent() != null) {
            textViewTitle.setText(getIntent().getStringExtra("title"));
            textViewMessage.setText(getIntent().getStringExtra("message"));
        }
    }
}
