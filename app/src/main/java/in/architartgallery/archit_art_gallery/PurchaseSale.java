package in.architartgallery.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.RangeSlider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PurchaseSale extends AppCompatActivity {

    private ImageView back_button, purchase_sale_config, cross_dialog;
    BottomSheetDialog sheetDialog;

    RangeSlider rangeSlider;
    // filters
    MaterialButtonToggleGroup toggleButton;

    RecyclerView purchase_recycler_view;
    PurchaseSaleAdapter purchaseSaleAdapter;
    ArrayList<PurchaseSaleData> dataArrayList = new ArrayList<>();
    ArrayList<PurchaseSaleData> caseList = new ArrayList<>();
    ArrayList<PurchaseSaleData> invoicedList = new ArrayList<>();
    ArrayList<PurchaseSaleData> dummyList = new ArrayList<>();
    ArrayList<PurchaseSaleData> onlineList = new ArrayList<>();
    TextView total_text_view, tnx_count;

    JSONObject PurchaseSaleData;
    double full_total = 0, invoiced_total = 0, dummy_total = 0, case_total = 0, online_total = 0;
    int tnx_count_no = 0;
    final static String base_url = "https://architartgallery.in/public";
    double MAX_VALUE = 1000;

    int select_choice_number = 1;
    String SELECTED = "DAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_sale);

        back_button = findViewById(R.id.back_button);
        purchase_sale_config = findViewById(R.id.purchase_sale_config);
        total_text_view = findViewById(R.id.amount_total);
        tnx_count = findViewById(R.id.tnx_count);
        toggleButton = findViewById(R.id.toggleButton);

        back_button.setOnClickListener(e -> {
            onBackPressed();
        });

        // Manage Config purchase sale
        purchase_sale_config.setOnClickListener(e -> {
            sheetDialog = new BottomSheetDialog(this, R.style.BottomSheetStyle);
            View view = LayoutInflater.from(this).inflate(R.layout.report_config_bottom_dialog,
                    (LinearLayout)findViewById(R.id.sheet));
            sheetDialog.setContentView(view);
            sheetDialog.show();

            cross_dialog = view.findViewById(R.id.cross_dialog);
            cross_dialog.setOnClickListener(ev -> {
                sheetDialog.hide();
            });

            rangeSlider = findViewById(R.id.range_slider);

            AutoCompleteTextView multi_select_names = view.findViewById(R.id.multi_select_names);
            String[] indianStates = getResources().getStringArray(R.array.indian_states);
            ArrayAdapter<String> state_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, indianStates);
            multi_select_names.setAdapter(state_adapter);
        });

        purchase_recycler_view = findViewById(R.id.purchase_recycler_view);
        purchaseSaleAdapter = new PurchaseSaleAdapter(dataArrayList, this);
        purchase_recycler_view.setAdapter(purchaseSaleAdapter);
        purchase_recycler_view.setLayoutManager(new LinearLayoutManager(this));

        req(new HashMap<>(), "/api/getSaleReport", "Data fetched.", "fetch_old_item", "GET");

        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
//                    filter_individual_selector.setVisibility(View.VISIBLE);

                    Button button = findViewById(checkedId);
//                    Toast.makeText(getApplicationContext(), button.getText().toString(), Toast.LENGTH_SHORT).show();
                    String query = "";
                    String currentDate = null;
                    String dateYesterday = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        currentDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
                        dateYesterday = LocalDate.now().minusDays(6).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                    if(button.getText().toString().equals("Day")) {
                        // Day
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                        }
                        query = "day=" + currentDate + "&";
                        SELECTED = "DAY";
                        select_choice_number = Integer.parseInt(currentDate.split("-")[2]);
//                        select_choice.setText(select_choice_number + "");
                    }else if(button.getText().toString().equals("Week")) {
                        // Week
                        query = "week_start=" + dateYesterday + "&" + "week_end=" + currentDate + "&";
                        SELECTED = "WEEK";
                        select_choice_number = 0;
                        if(select_choice_number == 0) {
//                            select_choice.setText("Past Week");
                        }
                    }else if(button.getText().toString().equals("Month")) {
                        // Month
                        query = "month=" + currentDate.split("-")[1] + "&";
                        SELECTED = "MONTH";
                        select_choice_number = 6;
//                        select_choice.setText(myMonths.get(select_choice_number - 1));
                    }else if(button.getText().toString().equals("Year")) {
                        // Year
                        query = "year=" + currentDate.split("-")[0] + "&";
                        SELECTED = "YEAR";
                        select_choice_number = 2024;
//                        select_choice.setText(select_choice_number + "");
                    }
                    req(new HashMap<>(), "/api/getSaleReport/?" + query, "Data fetched.", "fetch_old_item", "GET");
                }else {
//                    filter_individual_selector.setVisibility(View.GONE);
                    req(new HashMap<>(), "/api/getSaleReport", "Data fetched.", "fetch_old_item", "GET");
                }
            }
        });
    }

    void req(Map<String, String> data, String api_endpoint, String condition, String context, String method) {
        setLoading(true, "", context);
        StringRequest stringRequest = new StringRequest(method.equals("GET") ? Request.Method.GET : Request.Method.POST, base_url + api_endpoint,
                response -> {
                    // Handle successful response
                    try {
                        // Convert response string to JSON object
                        JSONObject jsonResponse = new JSONObject(response);
                        // Now you can access data from the JSON object
                        String message = jsonResponse.getString("message");

                        if(message.equals(condition)) {
                            setLoading(false, response, context);
                        }else {
                            // Handle the data accordingly
                            Toast.makeText(getApplicationContext(), "Invalid credential!", Toast.LENGTH_SHORT).show();
                            setLoading(false, "", context);
                        }
                    } catch (JSONException e) {
                        // Handle JSON parsing error
                        Toast.makeText(getApplicationContext(), "Error: API response error!", Toast.LENGTH_SHORT).show();
                        setLoading(false, "", context);
                    }
                },
                error -> {
                    // Handle error
                    Toast.makeText(getApplicationContext(), "Invalid credential!", Toast.LENGTH_LONG).show();
                    setLoading(false, "", context);
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

    private void setLoading(boolean loading, String response, String context) {
        if (context.equals("fetch_old_item") && loading) {
            //
        }
        if (context.equals("fetch_old_item") && !loading) {
            full_total = 0;
            tnx_count_no = 0;
            dataArrayList.clear();
            if (response.length() >= 10) {
                try {
                    PurchaseSaleData = new JSONObject(response);
                    try {
                        JSONObject myRes = new JSONObject(response);
                        JSONArray itemsArray = myRes.getJSONArray("data");
                        Log.d("DATA", itemsArray.toString());
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemObject = itemsArray.getJSONObject(i);
                            Log.d("ROW", itemObject.toString());
                            String inputDate = itemObject.getString("invoice_date");
//                            ZonedDateTime utcDateTime = null;
//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                                utcDateTime = ZonedDateTime.parse(inputDate);
//                            }
//                            ZonedDateTime kolkataDateTime = null;
//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                                kolkataDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
//                            }
//                            DateTimeFormatter outputFormatter = null;
//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                                    outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy | hh:mm a");
//                            }
//                            String formattedDate = null;
//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                                formattedDate = kolkataDateTime.format(outputFormatter);
//                            }
//                            if (itemObject.getDouble("total_amount") > MAX_VALUE)
//                                MAX_VALUE = itemObject.getDouble("amount");
//                            formattedDate = formattedDate.replaceAll("am", "AM").replaceAll("pm", "PM");
                            dataArrayList.add(new PurchaseSaleData(
                                    itemObject.getString("id"),
                                    itemObject.getString("name"),
                                    itemObject.getString("type"),
                                    inputDate,
                                    itemObject.getDouble("total_amount")
                            ));
                            full_total += itemObject.getDouble("total_amount");
                            tnx_count_no += 1;
                        }
                        total_text_view.setText("₹" + String.format("%.2f", full_total));
                        tnx_count.setText(tnx_count_no + " Transactions");
                        purchaseSaleAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}