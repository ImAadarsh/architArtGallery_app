package com.example.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class ForetPassword extends AppCompatActivity {

    private ImageView back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foret_password);

        back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(e -> {
            onBackPressed();
        });
    }
}