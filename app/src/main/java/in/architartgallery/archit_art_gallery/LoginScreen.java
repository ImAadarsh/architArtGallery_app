package in.architartgallery.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity {

    private static final String SHARED_PREFS = "login_cred";
    private static final String PHONE_KEY = "Phone_Key";
    private static final String PASSWORD_KEY = "Password_Key";
    private static final String USER_LOGGED_IN_DATA_KEY = "Login_User_Info";

    private TextInputEditText phoneNumber;
    private TextInputEditText userPassword;

    SharedPreferences sharedpreferences;
    String phone, password;

    public Button frogot_password;
    public Button login_button;
    public ProgressBar login_spinner;

    // Manage LOGIN API
    final static String base_url = "https://architartgallery.in/public";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        phoneNumber = findViewById(R.id.phone_number);
        userPassword = findViewById(R.id.password);
        login_button = findViewById(R.id.login_button);
        login_spinner = findViewById(R.id.loading_spinner);

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
            if (TextUtils.isEmpty(phoneNumber.getText().toString()) || TextUtils.isEmpty(userPassword.getText().toString())) {
                // this method will call when email and password fields are empty.
                Toast.makeText(LoginScreen.this, "Please Enter Phone and Password", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                getCred(phoneNumber.getText().toString(),  userPassword.getText().toString(), editor);
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

    void getCred(String phone, String password, SharedPreferences.Editor editor) {

        String API_ENDPOINT = "/api/login";
        setLoading(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, base_url + API_ENDPOINT,
                response -> {
                    // Handle successful response
                    try {
                        Log.d("TEES", response);
                        // Convert response string to JSON object
                        JSONObject jsonResponse = new JSONObject(response);
                        // Now you can access data from the JSON object
                        String message = jsonResponse.getString("message");

                        if(message.equals("User is Logged IN")) {
                            // below two lines will put values for
                            // email and password in shared preferences.
                            editor.putString(PHONE_KEY, phoneNumber.getText().toString());
                            editor.putString(PASSWORD_KEY, userPassword.getText().toString());
                            editor.putString(USER_LOGGED_IN_DATA_KEY, response);
                            // to save our data with key and value.
                            editor.apply();

                            // starting new activity.
                            Intent i = new Intent(getApplicationContext(), Dashboard.class);
                            startActivity(i);
                            setLoading(false);
                            finish();
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
                params.put("password", password);
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
            login_button.setEnabled(false);
            login_spinner.setVisibility(View.VISIBLE);
        } else {
            login_button.setEnabled(true);
            login_spinner.setVisibility(View.GONE);
        }
    }
}