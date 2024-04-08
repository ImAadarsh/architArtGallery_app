package com.example.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchInvoiceActivity extends AppCompatActivity {

    TextView search_invoic_title;
    private ImageView back_button;

    SharedPreferences sharedpreferences, sharedpreferences2;
    private static final String SHARED_PREFS2 = "login_cred";
    String SHARED_PREFS = "INPUT_QUERY_SHARE";
    private static final String INPUT_QUERY_KEY = "Input_Query_Key01";
    String input_query;

    RecyclerView recyclerView;
    ListAdapter invoiceAdaptor;
    ArrayList<ListData> fullDataList = new ArrayList<>();
    ListData listData;
    private static final String USER_LOGGED_IN_DATA_KEY = "Login_User_Info";
    final static String base_url = "https://api.architartgallery.in/public";
    String user_logged_in_data;
    JSONObject jsonResponse = new JSONObject();
    String Business_Id;
    ArrayList<ListData> dataListAfterFilter = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_invoice);

        search_invoic_title = findViewById(R.id.search_invoic_title);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        input_query = sharedpreferences.getString(INPUT_QUERY_KEY, "");

        sharedpreferences2 = getSharedPreferences(SHARED_PREFS2, Context.MODE_PRIVATE);
        user_logged_in_data = sharedpreferences2.getString(USER_LOGGED_IN_DATA_KEY, null);

        try {
            jsonResponse = new JSONObject(user_logged_in_data);
            Business_Id = jsonResponse.getJSONObject("user").getInt("business_id") + "";
            req(new HashMap<>(), "/api/getAllInvoices?business_id" + Business_Id, "Invoices retrieved successfully.", "fetch_invoice", "GET");
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Response get to failed!", Toast.LENGTH_SHORT).show();
        }

        fullDataList.add(new ListData("Arun kumar : 12345678", "11 Jan", 400));

        recyclerView = findViewById(R.id.recycler_view_search_invoice);
        invoiceAdaptor = new ListAdapter(dataListAfterFilter, this);
        recyclerView.setAdapter(invoiceAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(e -> {
            onBackPressed();
        });
    }

    void req(Map<String, String> data, String api_endpoint, String condition, String context, String method) {
        setProfileUpdateLoading(true, "", context);
        StringRequest stringRequest = new StringRequest(method.equals("GET") ? Request.Method.GET : Request.Method.POST, base_url + api_endpoint,
                response -> {
                    // Handle successful response
                    try {
                        // Convert response string to JSON object
                        JSONObject jsonResponse = new JSONObject(response);
                        // Now you can access data from the JSON object
                        String message = jsonResponse.getString("message");

                        if(message.equals(condition)) {
                            setProfileUpdateLoading(false, response, context);
                        }else {
                            // Handle the data accordingly
                            Toast.makeText(getApplicationContext(), "Invalid credential!", Toast.LENGTH_SHORT).show();
                            setProfileUpdateLoading(false, "", context);
                        }
                    } catch (JSONException e) {
                        // Handle JSON parsing error
                        Toast.makeText(getApplicationContext(), "Error: API response error!", Toast.LENGTH_SHORT).show();
                        setProfileUpdateLoading(false, "", context);
                    }
                },
                error -> {
                    // Handle error
                    Toast.makeText(getApplicationContext(), "Invalid credential!", Toast.LENGTH_LONG).show();
                    setProfileUpdateLoading(false, "", context);
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Construct form data
                return data;
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

    private void setProfileUpdateLoading(boolean loading, String response, String context) {
        if(context.equals("fetch_invoice") && loading) {
            search_invoic_title.setText("Wait....");
        }
        if(context.equals("fetch_invoice") && !loading) {
            if(response.length() >= 10){
                try {
                    JSONObject res;
                    res = new JSONObject(response);
                    JSONArray items = res.getJSONArray("data");
                    for(int i = 0; i < items.length(); i++ ) {
                        JSONObject item = items.getJSONObject(i);
                        String name = item.getString("name");
                        String id = item.getString("id");
                        String sn = item.getString("serial_no");
                        String date = item.getString("invoice_date");
                        String c_t = item.getString("customer_type");
                        String aadhaar = item.getString("doc_no");
                        String billling = item.getString("billing_address_id");
                        String shipping = item.getString("shipping_address_id");
                        String mobile = item.getString("mobile_number");
                        int cost = 0;
                        if(item.isNull("items_sum_price_of_all")) {
                            cost = 0;
                        }else {
                            cost = item.getInt("items_sum_price_of_all");
                        }
                        if(!name.equals("null")
                                && !id.equals("null")
                                && !sn.equals("null")
                                && !c_t.equals("null")
                        ) {
                            Log.d("ABC", name);
                            fullDataList.add(new ListData(
                                            id,
                                            sn,
                                            name,
                                            date,
                                            cost,
                                            "normal",
                                            c_t,
                                            aadhaar,
                                            billling,
                                            shipping,
                                            mobile
                                    )
                            );
                        }
                    }
                    invoiceAdaptor.notifyDataSetChanged();
                    for(ListData row: fullDataList) {
                        if(row.invoice_user_name.contains(input_query)) {
                            dataListAfterFilter.add(row);
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }

                search_invoic_title.setText("Search Invoice");
            }
        }
    }
}