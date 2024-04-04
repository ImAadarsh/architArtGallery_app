package com.example.archit_art_gallery;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForetPassword extends AppCompatActivity {

    private ImageView back_button;
    final static String base_url = "https://api.architartgallery.in/public";
    MaterialButton forget_password_btn;
    TextInputEditText phone_number_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foret_password);

        forget_password_btn = findViewById(R.id.forget_password_btn);
        phone_number_edit = findViewById(R.id.phone_number_edit);
        back_button = findViewById(R.id.back_button);

        forget_password_btn.setOnClickListener(e -> {
            getCred(phone_number_edit.getText().toString());
        });

        back_button.setOnClickListener(e -> {
            onBackPressed();
        });
    }

    void getCred(String phone) {

        String API_ENDPOINT = "/api/forgot";
        setLoading(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, base_url + API_ENDPOINT,
                response -> {
                    // Handle successful response
                    try {
                        Log.d("TEES", response);
                        // Convert response string to JSON object
                        JSONObject jsonResponse = new JSONObject(response);
                        // Now you can access data from the JSON object
                        String password = jsonResponse.getString("password");
                        Boolean status = jsonResponse.getBoolean("status");

                        if(status) {
                            setLoading(false);
                            AlertDialog.Builder builder = new AlertDialog.Builder(ForetPassword.this);

                            // set title
                            builder.setTitle("Your Password");

                            // set message
                            builder.setMessage(password);

                            // set two buttons.
                            builder.setPositiveButton("Copy", (dialogInterface, i) -> {
                                copyToClipboard(ForetPassword.this, password);
                            });
                            builder.setNegativeButton("Ok", (dialogInterface, i) -> {
                                // no activity for back.
                            });

                            // show the alert.
                            builder.show();
                        }else {
                            // Handle the data accordingly
                            Toast.makeText(getApplicationContext(), "Invalid credential!", Toast.LENGTH_SHORT).show();
                            setLoading(false);
                        }
                    } catch (JSONException e) {
                        // Handle JSON parsing error
                        Toast.makeText(getApplicationContext(), "Error: API response error!", Toast.LENGTH_SHORT).show();
                        setLoading(false);
                    }
                },
                error -> {
                    // Handle error
                    Toast.makeText(getApplicationContext(), "Invalid credential!", Toast.LENGTH_LONG).show();
                    setLoading(false);
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Construct form data
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // You can add headers here if needed
                Map<String, String> headers = new HashMap<>();
                // Add headers if needed
                return headers;
            }
        };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void setLoading(boolean loading) {
        if (loading) {
            forget_password_btn.setText("Wait....");
            forget_password_btn.setEnabled(false);
        } else {
            forget_password_btn.setText("Enter");
            forget_password_btn.setEnabled(true);
        }
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }
}