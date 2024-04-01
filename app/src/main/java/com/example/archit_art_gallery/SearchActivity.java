package com.example.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class SearchActivity extends AppCompatActivity {

    private ImageView back_button;
    private Button search_button;

    private TextInputEditText search_input;

    SharedPreferences sharedpreferences;
    String SHARED_PREFS = "INPUT_QUERY_SHARE";
    private static final String INPUT_QUERY_KEY = "Input_Query_Key01";
    String input_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        back_button = findViewById(R.id.back_button);
        search_button = findViewById(R.id.search_button);
        search_input = findViewById(R.id.search_input);

        back_button.setOnClickListener(e -> {
            onBackPressed();
        });

        search_button.setOnClickListener(e -> {
            Intent search_invoice = new Intent(getApplicationContext(), SearchInvoiceActivity.class);
            input_query = search_input.getText().toString();
            if(input_query.equals("") || input_query.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please input query for search", Toast.LENGTH_SHORT).show();
            }else {
                try {
                    sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(INPUT_QUERY_KEY, input_query);
                    // to save our data with key and value.
                    editor.apply();
                }catch (Exception error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
                startActivity(search_invoice);
            }
        });
    }
}