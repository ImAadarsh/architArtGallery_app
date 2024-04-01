package com.example.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class LoginScreen extends AppCompatActivity {

    private static final String SHARED_PREFS = "login_cred";
    private static final String PHONE_KEY = "Phone_Key";
    private static final String PASSWORD_KEY = "Password_Key";

    private TextInputEditText phoneNumber;
    private TextInputEditText userPassword;

    SharedPreferences sharedpreferences;
    String phone, password;

    public Button frogot_password;
    public Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        phoneNumber = findViewById(R.id.phone_number);
        userPassword = findViewById(R.id.password);
        login_button = findViewById(R.id.login_button);

        frogot_password = findViewById(R.id.forgot_password);
        frogot_password.setOnClickListener(e -> {
            Intent intent = new Intent(getApplicationContext(), ForetPassword.class);
            startActivity(intent);
        });

        try {
            sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            phone = sharedpreferences.getString("Phone_Key", null);
            password = sharedpreferences.getString("Password_Key", null);
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        login_button.setOnClickListener(e -> {
            if (TextUtils.isEmpty(phoneNumber.getText().toString()) && TextUtils.isEmpty(userPassword.getText().toString())) {
                // this method will call when email and password fields are empty.
                Toast.makeText(LoginScreen.this, "Please Enter Phone and Password", Toast.LENGTH_SHORT).show();
            } else {
                if(phoneNumber.getText().toString().equals("0000000000") && userPassword.getText().toString().equals("abcde")) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    // below two lines will put values for
                    // email and password in shared preferences.
                    editor.putString(PHONE_KEY, phoneNumber.getText().toString());
                    editor.putString(PASSWORD_KEY, userPassword.getText().toString());

                    // to save our data with key and value.
                    editor.apply();

                    // starting new activity.
                    Intent i = new Intent(getApplicationContext(), Dashboard.class);
                    startActivity(i);
                    finish();
                }else {
                    Toast.makeText(LoginScreen.this, "Login Credential are invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (phone != null && password != null) {
            Intent i = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(i);
            finish();
        }
    }
}