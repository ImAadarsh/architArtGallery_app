package com.example.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_page);

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
            stringBuilder.append(item).append(", ");
            if(nextLineNeed == 2) stringBuilder.append("\n");
            nextLineNeed += 1;
        }
        String concatenatedString = stringBuilder.toString();
        billing_address_text.setText(concatenatedString);
        String[] shipping_address_text_input_data = getIntent().getStringArrayExtra("SHIPPING_ADDRESS");
        StringBuilder stringShippBuilder = new StringBuilder();
        int nextLineNeedShip = 0;
        for (String item : shipping_address_text_input_data) {
            stringShippBuilder.append(item).append(", ");
            if(nextLineNeedShip == 2) stringShippBuilder.append("\n");
            nextLineNeedShip += 1;
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
            Intent intent = new Intent(getApplicationContext(), Create_Invoice.class);
            intent.putExtra("INVOICE_SERIAL_NO_CREATE", "SN. " + getIntent().getStringExtra("INVOICE_SERIAL_NO"));
            startActivity(intent);
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
}