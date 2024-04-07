package com.example.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class InvoicePage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ImageView back_button;
    TextView invoice_serial_no;
    EditText invoice_to_text;
    AutoCompleteTextView customer_type_options;
    EditText aadhaar_no_text;
    TextView billing_address_text;
    TextView shipping_address_text;
    CheckBox same_address_checked;
    EditText mobile_no_text;

    ConstraintLayout billing_address_edit;
    ConstraintLayout shipping_address_edit;

    BottomSheetDialog sheetDialog;

    ImageView cross_dialog;

    MaterialButton next_invoice_button, save_address_button;

    // For date
    LinearLayout today_date_piker;
    TextView invoice_pike_date;
    ArrayList<String> monthNames;

    final static String base_url = "https://api.architartgallery.in/public";
    private static final String SHARED_PREFS = "login_cred";
    private static final String PHONE_KEY = "Phone_Key";
    private static final String PASSWORD_KEY = "Password_Key";
    private static final String USER_LOGGED_IN_DATA_KEY = "Login_User_Info";
    SharedPreferences sharedpreferences;
    String phone, password, user_logged_in_data, location_id, business_id;
    JSONObject jsonResponse;
    int Invoice_ID = 0;
    int Serial_NO = 0;
    int Billing_Address_ID = 0;
    int Shipping_Address_ID = 0;
    String bill_type = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_page);
        Map<String, String> typeRequestData = new HashMap<>();
        typeRequestData.put("type", "normal");
        if(!(getIntent().getStringExtra("INVOICE_SERIAL_NO").length() >= 1)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(InvoicePage.this);
            // set title
            builder.setTitle("Choose One of them?");
            // set message
            builder.setMessage("- Invoice(normal)\n- Dummy\n- Performa");
            // set two buttons.
            builder.setPositiveButton("Performa", (dialogInterface, i) -> {
                typeRequestData.put("type", "performa");
                req(typeRequestData, "/api/createInvoice", "Invoice created successfully.", "first_time_invoice_create");
            });
            builder.setNegativeButton("Dummy", (dialogInterface, i) -> {
                typeRequestData.put("type", "dummy");
                req(typeRequestData, "/api/createInvoice", "Invoice created successfully.", "first_time_invoice_create");
            });
            builder.setNeutralButton("Invoice", (dialogInterface, i) -> {
                typeRequestData.put("type", "normal");
                req(typeRequestData, "/api/createInvoice", "Invoice created successfully.", "first_time_invoice_create");
            });
            // show the alert.
            bill_type = typeRequestData.get("type");
            builder.show();
        }

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        phone = sharedpreferences.getString(PHONE_KEY, null);
        password = sharedpreferences.getString(PASSWORD_KEY, null);
        user_logged_in_data = sharedpreferences.getString(USER_LOGGED_IN_DATA_KEY, null);

        try {
            jsonResponse = new JSONObject(user_logged_in_data);
            location_id = jsonResponse.getJSONObject("user").getString("location_id");
            business_id = jsonResponse.getJSONObject("user").getString("business_id");
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }

        // Select Default Date
        monthNames = new ArrayList<>();
        monthNames.add("January");
        monthNames.add("February");
        monthNames.add("March");
        monthNames.add("April");
        monthNames.add("May");
        monthNames.add("June");
        monthNames.add("July");
        monthNames.add("August");
        monthNames.add("September");
        monthNames.add("October");
        monthNames.add("November");
        monthNames.add("December");

        today_date_piker = findViewById(R.id.total_actual_date_piker_layout);
        invoice_pike_date = findViewById(R.id.total_actual_sale_date);

        LocalDate date = LocalDate.now();
        String selectedDate = formatDate(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        // sate today date if current date are not given.
        invoice_pike_date.setText(selectedDate);

        today_date_piker.setOnClickListener(e -> {
            showDatePickerDialog();
        });

        // Take Fields from the intent and mange in invoice
        customer_type_options = findViewById(R.id.customer_type_options);
        invoice_serial_no = findViewById(R.id.invoice_serial_no);
        invoice_to_text = findViewById(R.id.invoice_to_text);
        aadhaar_no_text = findViewById(R.id.aadhaar_no_text);
        billing_address_text = findViewById(R.id.billing_address_text);
        shipping_address_text = findViewById(R.id.shipping_address_text);
        same_address_checked = findViewById(R.id.same_address_checked);
        mobile_no_text = findViewById(R.id.mobile_no_text);
        next_invoice_button = findViewById(R.id.next_invoice_button);

        invoice_serial_no.setText("SN. " + getIntent().getStringExtra("INVOICE_SERIAL_NO"));
        invoice_to_text.setText(getIntent().getStringExtra("INVOICE_TO"));
        aadhaar_no_text.setText(getIntent().getStringExtra("AADHAAR_NUMBER"));
        String[] billing_address_text_input_data = getIntent().getStringArrayExtra("BILLING_ADDRESS");
        StringBuilder stringBuilder = new StringBuilder();
        int nextLineNeed = 0;
        for (String item : billing_address_text_input_data) {
            if(item.length() >= 1) {
                stringBuilder.append(item).append(", ");
                if(nextLineNeed == 2) stringBuilder.append("\n");
                nextLineNeed += 1;
            }
        }
        String concatenatedString = stringBuilder.toString();
        billing_address_text.setText(concatenatedString);
        String[] shipping_address_text_input_data = getIntent().getStringArrayExtra("SHIPPING_ADDRESS");
        StringBuilder stringShippBuilder = new StringBuilder();
        int nextLineNeedShip = 0;
        for (String item : shipping_address_text_input_data) {
            if(item.length() >= 1) {
                stringShippBuilder.append(item).append(", ");
                if(nextLineNeedShip == 2) stringShippBuilder.append("\n");
                nextLineNeedShip += 1;
            }
        }
        String concatenatedString2 = stringShippBuilder.toString();
        shipping_address_text.setText(concatenatedString2);
        same_address_checked.setChecked(getIntent().getBooleanExtra("BOTH_ADDRESS_IS_SAME", false));
        mobile_no_text.setText(getIntent().getStringExtra("MOBILE_NUMBER"));


        String[] retailers = {"Retail", "Whole Sale", "Interrior", "Hotel", "Company", "Hospital"}; // Add your list of retailers here
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, retailers);
        customer_type_options.setAdapter(adapter);
        customer_type_options.setText(getIntent().getStringExtra("CUSTOMER_TYPE"));

        billing_address_edit = findViewById(R.id.billing_address_edit);
        billing_address_edit.setOnClickListener(e -> {
            sheetDialog = new BottomSheetDialog(this, R.style.BottomSheetStyle);
            View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog,
                    (LinearLayout)findViewById(R.id.sheet));

            AutoCompleteTextView india_all_state_autocomplete_view = view.findViewById(R.id.india_all_state_autocomplete);
            String[] indianStates = getResources().getStringArray(R.array.indian_states);
            ArrayAdapter<String> state_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, indianStates);
            india_all_state_autocomplete_view.setAdapter(state_adapter);

            // Put invoice data for editing
            TextInputEditText address_1_edit_text = view.findViewById(R.id.address_1_edit_text);
            address_1_edit_text.setText(billing_address_text_input_data[0]);
            TextInputEditText address_2_edit_text = view.findViewById(R.id.address_2_edit_text);
            address_2_edit_text.setText(billing_address_text_input_data[1]);
            TextInputEditText city_edit_text = view.findViewById(R.id.city_edit_text);
            city_edit_text.setText(billing_address_text_input_data[2]);
            india_all_state_autocomplete_view.setText(billing_address_text_input_data[3]);
            TextInputEditText pincode_edit_text = view.findViewById(R.id.pincode_edit_text);
            pincode_edit_text.setText(billing_address_text_input_data[4]);

            // Address Save Button
            save_address_button = view.findViewById(R.id.save_address_button);

            sheetDialog.setContentView(view);
            sheetDialog.show();

            cross_dialog = view.findViewById(R.id.cross_dialog);
            cross_dialog.setOnClickListener(ev -> {
                sheetDialog.hide();
            });

            save_address_button.setOnClickListener(addressEv -> {

                Map<String, String> addressReqData = new HashMap<>();
                addressReqData.put("invoice_id", Invoice_ID + "");
                addressReqData.put("type", "0");
                addressReqData.put("address_1", address_1_edit_text.getText().toString());
                addressReqData.put("address_2", address_2_edit_text.getText().toString());
                addressReqData.put("city", city_edit_text.getText().toString());
                addressReqData.put("state", india_all_state_autocomplete_view.getText().toString());
                addressReqData.put("pincode", pincode_edit_text.getText().toString());
                req(addressReqData, "/api/addAddress", "Adress created/Updated successfully.", "add_billing_address");


                String []billing_address_edited = { address_1_edit_text.getText().toString(),
                        address_2_edit_text.getText().toString(),
                        city_edit_text.getText().toString(),
                        india_all_state_autocomplete_view.getText().toString(),
                        pincode_edit_text.getText().toString() };

                StringBuilder tempStringBuilder = new StringBuilder();

                int iter = 0;
                for (String item : billing_address_edited) {
                    billing_address_text_input_data[iter] = billing_address_edited[iter];
                    tempStringBuilder.append(item).append(", ");
                    if(iter == 2) tempStringBuilder.append("\n");
                    iter += 1;
                }
                String tempEditAddress = tempStringBuilder.toString();
                billing_address_text.setText(tempEditAddress);
                sheetDialog.hide();
            });

            same_address_checked.setChecked(billing_address_text_input_data == shipping_address_text_input_data);
        });

        shipping_address_edit = findViewById(R.id.shipping_address_edit);
        shipping_address_edit.setOnClickListener(e -> {
            sheetDialog = new BottomSheetDialog(this, R.style.BottomSheetStyle);
            View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog,
                    (LinearLayout)findViewById(R.id.sheet));
            TextView textView = (TextView)view.findViewById(R.id.dialog_text_main);
            textView.setText("Edit Shipping Address");

            AutoCompleteTextView india_all_state_autocomplete_view = view.findViewById(R.id.india_all_state_autocomplete);
            String[] indianStates = getResources().getStringArray(R.array.indian_states);
            ArrayAdapter<String> state_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, indianStates);
            india_all_state_autocomplete_view.setAdapter(state_adapter);

            // Put invoice data for editing
            TextInputEditText address_1_edit_text = view.findViewById(R.id.address_1_edit_text);
            address_1_edit_text.setText(shipping_address_text_input_data[0]);
            TextInputEditText address_2_edit_text = view.findViewById(R.id.address_2_edit_text);
            address_2_edit_text.setText(shipping_address_text_input_data[1]);
            TextInputEditText city_edit_text = view.findViewById(R.id.city_edit_text);
            city_edit_text.setText(shipping_address_text_input_data[2]);
            india_all_state_autocomplete_view.setText(shipping_address_text_input_data[3]);
            TextInputEditText pincode_edit_text = view.findViewById(R.id.pincode_edit_text);
            pincode_edit_text.setText(shipping_address_text_input_data[4]);

            // Address Save Button
            save_address_button = view.findViewById(R.id.save_address_button);

            sheetDialog.setContentView(view);
            sheetDialog.show();

            cross_dialog = view.findViewById(R.id.cross_dialog);
            cross_dialog.setOnClickListener(ev -> {
                sheetDialog.hide();
            });

            save_address_button.setOnClickListener(addressEv -> {

                Map<String, String> addressReqData = new HashMap<>();
                addressReqData.put("invoice_id", Invoice_ID + "");
                addressReqData.put("type", "1");
                addressReqData.put("address_1", address_1_edit_text.getText().toString());
                addressReqData.put("address_2", address_2_edit_text.getText().toString());
                addressReqData.put("city", city_edit_text.getText().toString());
                addressReqData.put("state", india_all_state_autocomplete_view.getText().toString());
                addressReqData.put("pincode", pincode_edit_text.getText().toString());
                req(addressReqData, "/api/addAddress", "Adress created/Updated successfully.", "add_shipping_address");

                String []shipping_address_edited = { address_1_edit_text.getText().toString(),
                        address_2_edit_text.getText().toString(),
                        city_edit_text.getText().toString(),
                        india_all_state_autocomplete_view.getText().toString(),
                        pincode_edit_text.getText().toString() };

                StringBuilder tempStringBuilder = new StringBuilder();

                int iter = 0;
                for (String item : shipping_address_edited) {
                    shipping_address_text_input_data[iter] = shipping_address_edited[iter];
                    tempStringBuilder.append(item).append(", ");
                    if(iter == 2) tempStringBuilder.append("\n");
                    iter += 1;
                }
                String tempEditAddress = tempStringBuilder.toString();
                shipping_address_text.setText(tempEditAddress);
                sheetDialog.hide();
            });

            same_address_checked.setChecked(billing_address_text_input_data == shipping_address_text_input_data);
        });


        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(e -> {
            onBackPressed();
        });

        // Handel Same Address
        same_address_checked.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                Shipping_Address_ID = Billing_Address_ID;
                StringBuilder temp = new StringBuilder();
                int iter = 0;
                for (String item : billing_address_text_input_data) {
                    shipping_address_text_input_data[iter] = billing_address_text_input_data[iter];
                    temp.append(item).append(", ");
                    if(iter == 2) temp.append("\n");
                    iter += 1;
                }
                shipping_address_text.setText(temp.toString());
            }
        });

        // Bill Section
        next_invoice_button.setOnClickListener(e -> {

            // Create Invoice
            Map<String, String> data = new HashMap<>();
            data.put("type", typeRequestData.get("type").toString());
            data.put("name", invoice_to_text.getText().toString());
            data.put("mobile_number", mobile_no_text.getText().toString());
            data.put("customer_type", customer_type_options.getText().toString());
            data.put("doc_type", "0");
            data.put("doc_no", aadhaar_no_text.getText().toString());
            data.put("business_id", business_id);
            data.put("location_id", location_id);
            data.put("payment_mode", "online");
            data.put("billing_address_id", Billing_Address_ID + "");
            data.put("shipping_address_id", Shipping_Address_ID + "");
            data.put("id", Invoice_ID + "");
            req(data, "/api/editInvoice", "Invoice updated successfully.", "update_invoice_with_data");

        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Handle the selected date here
        String selectedDate = formatDate(year, month, dayOfMonth);
        invoice_pike_date.setText(selectedDate); // Update button text with selected date (optional)
    }

    private String formatDate(int year, int month, int day) {
        // You can use a library like ThreeTenABP or SimpleDateFormat to format the date
        // Here's a basic example using String formatting (limited options)
        return day + " " + monthNames.get(month) + " " + year; // Month is 0-indexed
    }

    void req(Map<String, String> data, String api_endpoint, String condition, String context) {
        setLoading(true, "", context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, base_url + api_endpoint,
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
        if(context.equals("first_time_invoice_create") && loading) {
            invoice_serial_no.setText("SN. Wait...");
        }
        if(context.equals("first_time_invoice_create") && !loading) {
            if(response.length() >= 10) {
                try {
                    JSONObject myRes;
                    myRes = new JSONObject(response);
                    Invoice_ID = myRes.getJSONObject("data").getInt("id");
                    Serial_NO = myRes.getJSONObject("data").getInt("serial_no");
                    invoice_serial_no.setText("SN. " + Serial_NO);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(context.equals("update_invoice_with_data") && loading) {
            next_invoice_button.setText("Wait....");
            next_invoice_button.setEnabled(false);
        }
        if(context.equals("update_invoice_with_data") && !loading) {
            if(response.length() >= 10) {
                next_invoice_button.setText("Next");
                next_invoice_button.setEnabled(true);
                if(Billing_Address_ID == 0 || Shipping_Address_ID == 0) {
                    Toast.makeText(getApplicationContext(), "Add address first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), Create_Invoice.class);
                intent.putExtra("INVOICE_ID_CREATE", Invoice_ID + "");
                intent.putExtra("INVOICE_SERIAL_NO_CREATE", Serial_NO + "");
                intent.putExtra("BILL_TYPE", bill_type + "");
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(), "Failed to Update Invoice!", Toast.LENGTH_SHORT).show();
                next_invoice_button.setText("Next");
                next_invoice_button.setEnabled(true);
            }
        }
        if(context.equals("add_billing_address") && loading) {
            next_invoice_button.setText("Wait....");
            next_invoice_button.setEnabled(false);
        }
        if(context.equals("add_billing_address") && !loading) {
            if(response.length() >= 10) {
                try {
                    JSONObject myRes;
                    myRes = new JSONObject(response);
                    Billing_Address_ID = myRes.getJSONObject("data").getInt("id");
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
            next_invoice_button.setText("Next");
            next_invoice_button.setEnabled(true);
        }
        if(context.equals("add_shipping_address") && loading) {
            next_invoice_button.setText("Wait....");
            next_invoice_button.setEnabled(false);
        }
        if(context.equals("add_shipping_address") && !loading) {
            if(response.length() >= 10) {
                try {
                    JSONObject myRes;
                    myRes = new JSONObject(response);
                    Shipping_Address_ID = myRes.getJSONObject("data").getInt("id");
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
            next_invoice_button.setText("Next");
            next_invoice_button.setEnabled(true);
        }
    }
}