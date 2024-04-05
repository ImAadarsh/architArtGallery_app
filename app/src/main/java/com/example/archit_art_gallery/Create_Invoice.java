package com.example.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Create_Invoice extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    LinearLayout today_date_piker;
    TextView invoice_pike_date;
    ArrayList<String> monthNames;
    ImageView back_button;

    BottomSheetDialog sheetDialog;

    ImageView cross_dialog;

    Button add_items;

    TextInputEditText item_name, has_code, rate, quantity;
    MaterialButton item_add_button;

    RecyclerView recyclerView;
    TextView no_item_added;
    ItemAdapter ItemsAdaptor;

    // BILL
    TextView total_ex_gst, delhi_gst_cost, cgst_gst_cost, igst_gst_cost, total_with_gst, invoice_serial_no;
    private int total_ex_gst_amount;

    // Mange Payment Option and load
    ConstraintLayout bottom_payment_mode_option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_invoice);

        // Manage Intent data
        invoice_serial_no = findViewById(R.id.invoice_serial_no);
        invoice_serial_no.setText("SN. " + getIntent().getStringExtra("INVOICE_SERIAL_NO_CREATE"));

        // Manage BILLS and GST INFO and data
        total_ex_gst = findViewById(R.id.total_ex_gst);
        delhi_gst_cost = findViewById(R.id.delhi_gst_cost);
        cgst_gst_cost = findViewById(R.id.cgst_gst_cost);
        igst_gst_cost = findViewById(R.id.igst_gst_cost);
        total_with_gst = findViewById(R.id.total_with_gst);

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

        // Manage items data
        ArrayList<ItemsData> itemsDB = new ArrayList<>();

        // Manage Recycler Items view
        recyclerView = findViewById(R.id.recycler_view_items);
        ItemsAdaptor = new ItemAdapter(itemsDB, this);
        recyclerView.setAdapter(ItemsAdaptor);
        no_item_added = findViewById(R.id.no_item_added);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        today_date_piker = findViewById(R.id.today_date_piker);
        invoice_pike_date = findViewById(R.id.invoice_pike_date);

        // Mange Payment Option
        bottom_payment_mode_option = findViewById(R.id.bottom_payment_mode_option);

        // Select Default Date
        LocalDate date = LocalDate.now();
        String selectedDate = formatDate(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        invoice_pike_date.setText(selectedDate);

        today_date_piker.setOnClickListener(e -> {
            showDatePickerDialog();
        });

        // Back Button
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(e -> {
            onBackPressed();
        });

        // Manage add items
        add_items = findViewById(R.id.add_items);
        add_items.setOnClickListener(e -> {
            sheetDialog = new BottomSheetDialog(this, R.style.BottomSheetStyle);
            View view = LayoutInflater.from(this).inflate(R.layout.item_add_dialog,
                    (LinearLayout)findViewById(R.id.item_add_dialog));

            sheetDialog.setContentView(view);
            sheetDialog.show();

            item_name = view.findViewById(R.id.new_item_name);
            has_code = view.findViewById(R.id.new_item_has_code);
            rate = view.findViewById(R.id.new_item_rate);
            quantity = view.findViewById(R.id.new_item_quantity);
            item_add_button = view.findViewById(R.id.new_add_item_button);

            item_add_button.setOnClickListener(ev -> {
                try {
                    // Mange Total and GST INFO
                    total_ex_gst_amount += Integer.parseInt(rate.getText().toString()) * Integer.parseInt(quantity.getText().toString());
                    total_ex_gst.setText("₹" + total_ex_gst_amount);
                    double dgst = total_ex_gst_amount * 0.09;
                    double cgst = total_ex_gst_amount * 0.09;
                    double igst = total_ex_gst_amount * 0.18;
                    delhi_gst_cost.setText("₹" + String.format("%.2f", dgst));
                    cgst_gst_cost.setText("₹" + String.format("%.2f", cgst));
                    igst_gst_cost.setText("₹" + String.format("%.2f", igst));
                    total_with_gst.setText("₹" + String.format("%.2f", (total_ex_gst_amount + dgst + cgst + igst)));

                    // Mange the item data
                    itemsDB.add(new ItemsData(item_name.getText().toString(),
                            Integer.parseInt(has_code.getText().toString()),
                            Integer.parseInt(rate.getText().toString()),
                            Integer.parseInt(quantity.getText().toString())));
                    Toast.makeText(getApplicationContext(), "Item added!", Toast.LENGTH_SHORT).show();
                    ItemsAdaptor.notifyDataSetChanged();
                    sheetDialog.hide();

                    // Mange item show or not
                    if (itemsDB.isEmpty()) {
                        bottom_payment_mode_option.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        no_item_added.setVisibility(View.VISIBLE);
                    } else {
                        bottom_payment_mode_option.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        no_item_added.setVisibility(View.GONE);
                    }
                }catch (Exception error) {
                    Toast.makeText(getApplicationContext(), "Put right data!", Toast.LENGTH_SHORT).show();
                }
            });

            cross_dialog = view.findViewById(R.id.cross_dialog);
            cross_dialog.setOnClickListener(ev -> {
                sheetDialog.hide();
            });
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