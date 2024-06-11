package in.architartgallery.archit_art_gallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.material.datepicker.MaterialDatePicker.Builder;

public class Expense extends AppCompatActivity {

    private ImageView back_button, purchase_sale_config, cross_dialog;
    BottomSheetDialog sheetDialog;
    Button add_expenses, file_upload_btn;
    RangeSlider rangeSlider;

    // Max Value
    double MAX_VALUE = 1000;
    double MIN_VALUE = 0;

    TextView transactions_count, total_expense;
    double full_total, adhoc_total, monthly_total;
    MaterialCheckBox monthly_filter, adhoc_filter;
    MaterialButton new_add_item_button;
    RecyclerView expense_recycler_view;
    ArrayList<ExpenseData> dataArrayList = new ArrayList<>();
    ArrayList<ExpenseData> monthlyArrayList = new ArrayList<>();
    ArrayList<ExpenseData> adhocArrayList = new ArrayList<>();
    ArrayList<ExpenseData> tempArrayList = new ArrayList<>();
    ExpenseAdapter expenseAdapter;

    JSONObject ExpenseData;

    String expense_type;

    final static String base_url = "https://architartgallery.in/public";

    private static final int REQUEST_CODE_FILE_PICKER = 2;
    File file = new File("/storage/emulated/0/Pictures/Stylize/1696935525568.png");
    String filePartName = "file";
    Bitmap bitmap;
    String encodedImageString = "";

    // filters
    MaterialButtonToggleGroup toggleButton;

    LinearLayout filter_individual_selector;
    Button filter_left_select, filter_right_select;
    TextView select_choice;
    int select_choice_number = 1;
    String SELECTED = "DAY";
    ArrayList<String> myMonths;

    // Range Date Picker
    TextView range_date_picker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        myMonths = new ArrayList<>();
        myMonths.add("Jan");
        myMonths.add("Feb");
        myMonths.add("Mar");
        myMonths.add("Apr");
        myMonths.add("May");
        myMonths.add("June");
        myMonths.add("July");
        myMonths.add("Aug");
        myMonths.add("Sep");
        myMonths.add("Oct");
        myMonths.add("Nov");
        myMonths.add("Dec");

        req(new HashMap<>(), "/api/getAllExpenses", "Data Feteched.", "fetch_old_item", "GET");

        // Managing filters
        toggleButton = findViewById(R.id.toggleButton);
        filter_individual_selector = findViewById(R.id.filter_individual_selector);
        filter_left_select = findViewById(R.id.left_arrow_button);
        filter_right_select = findViewById(R.id.right_arrow_button);
        select_choice = findViewById(R.id.select_choice_number);

        // Range Date Picker
        range_date_picker = findViewById(R.id.range_date_picker);
        range_date_picker.setOnClickListener(date_picker -> {
            openDateRangePicker();
        });

        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    filter_individual_selector.setVisibility(View.VISIBLE);

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
                        select_choice.setText(select_choice_number + "");
                    }else if(button.getText().toString().equals("Week")) {
                        // Week
                        query = "week_start=" + dateYesterday + "&" + "week_end=" + currentDate + "&";
                        Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                        SELECTED = "WEEK";
                        select_choice_number = 0;
                        if(select_choice_number == 0) {
                            select_choice.setText("Past Week");
                        }
                    }else if(button.getText().toString().equals("Month")) {
                        // Month
                        query = "month=" + currentDate.split("-")[1] + "&";
                        SELECTED = "MONTH";
                        select_choice_number = 6;
                        select_choice.setText(myMonths.get(select_choice_number - 1));
                    }else if(button.getText().toString().equals("Year")) {
                        // Year
                        query = "year=" + currentDate.split("-")[0] + "&";
                        SELECTED = "YEAR";
                        select_choice_number = 2024;
                        select_choice.setText(select_choice_number + "");
                    }
                    req(new HashMap<>(), "/api/getAllExpenses/?" + query, "Data Feteched.", "fetch_old_item", "GET");
                }else {
                    filter_individual_selector.setVisibility(View.GONE);
                    req(new HashMap<>(), "/api/getAllExpenses", "Data Feteched.", "fetch_old_item", "GET");
                }
            }
        });

        filter_left_select.setOnClickListener(b -> {
            String query = "";
            String currentDate = null;
            String dateYesterday = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                currentDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
                dateYesterday = LocalDate.now().minusDays(6).format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if(SELECTED.equals("DAY")) {
                if(select_choice_number > 1) {
                    select_choice_number -= 1;

                    // Day
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                    String []D = currentDate.split("-");
                    query = "day=" + D[0] + "-" + D[1] + "-" + select_choice_number + "&";
                }
                select_choice.setText(select_choice_number + "");
            }else if(SELECTED.equals("WEEK")) {
                if(select_choice_number == 1) {
                    select_choice_number = 0;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        currentDate = LocalDate.now().plusDays(1 + select_choice_number).format(DateTimeFormatter.ISO_LOCAL_DATE);
                        dateYesterday = LocalDate.now().minusDays(6 - select_choice_number).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                    query = "week_start=" + dateYesterday + "&" + "week_end=" + currentDate + "&";
                    Log.d("Query", query);
                }
                if(select_choice_number == 0) {
                    select_choice.setText("Past Week");
                }
            }else if(SELECTED.equals("MONTH")) {
                if(select_choice_number > 1) {
                    select_choice_number -= 1;

                    query = "month=" + select_choice_number + "&";
                }
                select_choice.setText(myMonths.get(select_choice_number - 1));
            }else if(SELECTED.equals("YEAR")) {
                if(select_choice_number > 1) {
                    select_choice_number -= 1;

                    query = "year=" + select_choice_number + "&";
                }
                select_choice.setText(select_choice_number + "");
            }
            if(!query.equals(""))
                req(new HashMap<>(), "/api/getAllExpenses/?" + query, "Data Feteched.", "fetch_old_item", "GET");
        });

        filter_right_select.setOnClickListener(b -> {
            String query = "";
            String currentDate = null;
            String dateYesterday = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                currentDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
                dateYesterday = LocalDate.now().minusDays(6).format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if(SELECTED.equals("DAY")) {
                if(select_choice_number < 31) {
                    select_choice_number += 1;

                    // Day
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                    String []D = currentDate.split("-");
                    query = "day=" + D[0] + "-" + D[1] + "-" + select_choice_number + "&";
                }
                select_choice.setText(select_choice_number + "");
            }else if(SELECTED.equals("WEEK")) {
                if(select_choice_number == 0) {
                    select_choice_number = 1;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        currentDate = LocalDate.now().plusDays(1 + select_choice_number).format(DateTimeFormatter.ISO_LOCAL_DATE);
                        dateYesterday = LocalDate.now().minusDays(6 - select_choice_number).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                    query = "week_start=" + dateYesterday + "&" + "week_end=" + currentDate + "&";
                    Log.d("Query", query);
                }
                if(select_choice_number == 1) {
                    select_choice.setText("Current Week");
                }
            }else if(SELECTED.equals("MONTH")) {
                if(select_choice_number < 12) {
                    select_choice_number += 1;

                    query = "month=" + select_choice_number + "&";
                }
                select_choice.setText(myMonths.get(select_choice_number - 1));
            }else if(SELECTED.equals("YEAR")) {
                if(select_choice_number < 2024) {
                    select_choice_number += 1;

                    query = "year=" + select_choice_number + "&";
                }
                select_choice.setText(select_choice_number + "");
            }
            if(!query.equals(""))
                req(new HashMap<>(), "/api/getAllExpenses/?" + query, "Data Feteched.", "fetch_old_item", "GET");
        });

        back_button = findViewById(R.id.back_button);
        purchase_sale_config = findViewById(R.id.purchase_sale_config);
        add_expenses = findViewById(R.id.add_expenses);

        transactions_count = findViewById(R.id.transactions_count);
        total_expense = findViewById(R.id.total_expense);
        monthly_filter = findViewById(R.id.monthly_filter);
        adhoc_filter = findViewById(R.id.adhoc_filter);

        back_button.setOnClickListener(e -> {
            onBackPressed();
        });

        // Manage Config purchase sale
        purchase_sale_config.setOnClickListener(e -> {
            sheetDialog = new BottomSheetDialog(this, R.style.BottomSheetStyle);
            View view = LayoutInflater.from(this).inflate(R.layout.expense_filter,
                    (LinearLayout)findViewById(R.id.expense_filter_sheet));
            sheetDialog.setContentView(view);
            sheetDialog.show();

            cross_dialog = view.findViewById(R.id.cross_dialog);
            cross_dialog.setOnClickListener(ev -> {
                sheetDialog.hide();
            });


            TextInputEditText expense_filter_input = view.findViewById(R.id.expense_filter_input);
            rangeSlider = view.findViewById(R.id.filter_range_slider);
            rangeSlider.setValueTo((float) MAX_VALUE);
            rangeSlider.setValueFrom((float) MIN_VALUE);
            MaterialButton expense_filter_apply_button = view.findViewById(R.id.expense_filter_apply_button);

            expense_filter_apply_button.setOnClickListener(b -> {
                String query = "";
                if(expense_filter_input.getText().length() >= 1) {
                    query += "name=" + expense_filter_input.getText() + "&";
                }
                List<Float> values = rangeSlider.getValues();
                float minValue = values.get(0);
                float maxValue = values.get(1);
                query += "amount_min=" + minValue + "&amount_max=" + maxValue + "&";
                req(new HashMap<>(), "/api/getAllExpenses/?" + query, "Data Feteched.", "fetch_old_item", "GET");
                sheetDialog.hide();
            });
        });

        // Add Expenses
        add_expenses.setOnClickListener(e -> {

            sheetDialog = new BottomSheetDialog(this, R.style.BottomSheetStyle);
            View view = LayoutInflater.from(this).inflate(R.layout.add_expenses_dialog,
                    (LinearLayout)findViewById(R.id.sheet));
            sheetDialog.setContentView(view);
            sheetDialog.show();

            cross_dialog = view.findViewById(R.id.cross_dialog);
            cross_dialog.setOnClickListener(ev -> {
                sheetDialog.hide();
            });

            new_add_item_button = view.findViewById(R.id.new_add_item_button);
            TextInputEditText new_item_name = view.findViewById(R.id.new_item_name);
            TextInputEditText new_item_quantity = view.findViewById(R.id.new_item_quantity);
            RadioGroup add_expense_type_input = view.findViewById(R.id.add_expense_type_input);
            add_expense_type_input.setOnCheckedChangeListener((radioGroup, i) -> {
                expense_type = String.valueOf(i - 1);
            });

            file_upload_btn = view.findViewById(R.id.file_upload_btn);
            file_upload_btn.setOnClickListener(select_file -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_FILE_PICKER);
            });

            new_add_item_button.setOnClickListener(btn -> {
                Map<String, String> data = new HashMap<>();
                data.put("name", new_item_name.getText().toString());
                data.put("amount", new_item_quantity.getText().toString());
                data.put("type", expense_type);

                if(!(data.get("name").length() >= 1 && data.get("type").length() >= 1)) {
                    Toast.makeText(getApplicationContext(), "Provide data properly!", Toast.LENGTH_SHORT).show();
                    return;
                }

//                ZonedDateTime now = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    now = ZonedDateTime.now(ZoneOffset.UTC);
//                }
//                DateTimeFormatter formatter = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
//                }
//                ZonedDateTime utcDateTime = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    utcDateTime = ZonedDateTime.parse(now.format(formatter));
//                }
//                ZonedDateTime kolkataDateTime = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    kolkataDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
//                }
//                DateTimeFormatter outputFormatter = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy | hh:mm a");
//                }
//                String formattedDate = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    formattedDate = kolkataDateTime.format(outputFormatter);
//                }
//                formattedDate = formattedDate.replaceAll("am", "AM").replaceAll("pm", "PM");
//
//                if(expense_type.equals("0")) {
//                    monthlyArrayList.add(new ExpenseData("",
//                            new_item_name.getText().toString(),
//                            "0",
//                            formattedDate,
//                            Integer.parseInt(new_item_quantity.getText().toString()),
//                            "https://architartgallery.in/storage/app/" + "public/expense/expense_" + dataArrayList.size() + ".jpeg"
//                    ));
//                }else {
//                    adhocArrayList.add(new ExpenseData("",
//                            new_item_name.getText().toString(),
//                            "1",
//                            formattedDate,
//                            Integer.parseInt(new_item_quantity.getText().toString()),
//                            "https://architartgallery.in/storage/app/" + "public/expense/expense_" + dataArrayList.size() + ".jpeg"
//                    ));
//                }
//                dataArrayList.add(new ExpenseData("",
//                        new_item_name.getText().toString(),
//                        expense_type,
//                        formattedDate,
//                        Integer.parseInt(new_item_quantity.getText().toString()),
//                        "https://architartgallery.in/storage/app/" + "public/expense/expense_" + dataArrayList.size() + ".jpeg"
//                ));
//                tempArrayList.clear();
//                tempArrayList.addAll(dataArrayList);
//                expenseAdapter.notifyDataSetChanged();


//                req(data, "/api/addExpense", "Expense created/Updated successfully.", "expense_add", "POST");

                if(data.get("name").length() >= 1 && data.get("type").length() >= 1) {
                    data.put("file", encodedImageString);
                    data.put("extension", "jpeg");
                    uploadDb(data);
//                    new_item_name.setText("");
//                    new_item_quantity.setText("");
//                    add_expense_type_input.clearCheck();
//                    file_upload_btn.setText("Upload Receipt");
                }else {
                    Toast.makeText(getApplicationContext(), "Type data properly!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        expense_recycler_view = findViewById(R.id.expense_recyler_view);
        expenseAdapter = new ExpenseAdapter(tempArrayList, this);
        expense_recycler_view.setAdapter(expenseAdapter);
        expense_recycler_view.setLayoutManager(new LinearLayoutManager(this));

        monthly_filter.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b && adhoc_filter.isChecked()) {
                tempArrayList.clear();
                tempArrayList.addAll(monthlyArrayList);
                tempArrayList.addAll(adhocArrayList);
            }else {
                if(b) {
                    tempArrayList.clear();
                    tempArrayList.addAll(monthlyArrayList);
                }else if(adhoc_filter.isChecked()) {
                    tempArrayList.clear();
                    tempArrayList.addAll(adhocArrayList);
                }else {
                    tempArrayList.clear();
                    tempArrayList.addAll(dataArrayList);
                }
            }
            double total_exp = 0;
            for(int i = 0; i < tempArrayList.size(); i++) {
                total_exp += tempArrayList.get(i).getAmount();
            }
            total_expense.setText("₹" + total_exp);
            transactions_count.setText(tempArrayList.size() + " " + "Transactions");
            expenseAdapter.notifyDataSetChanged();
        });

        adhoc_filter.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b && monthly_filter.isChecked()) {
                tempArrayList.clear();
                tempArrayList.addAll(monthlyArrayList);
                tempArrayList.addAll(adhocArrayList);
            }else {
                if(b) {
                    tempArrayList.clear();
                    tempArrayList.addAll(adhocArrayList);
                }else if(monthly_filter.isChecked()) {
                    tempArrayList.clear();
                    tempArrayList.addAll(monthlyArrayList);
                }else {
                    tempArrayList.clear();
                    tempArrayList.addAll(dataArrayList);
                }
            }
            double total_exp = 0;
            for(int i = 0; i < tempArrayList.size(); i++) {
                total_exp += tempArrayList.get(i).getAmount();
            }
            total_expense.setText("₹" + total_exp);
            transactions_count.setText(tempArrayList.size() + " " + "Transactions");
            expenseAdapter.notifyDataSetChanged();
        });
    }

    private void uploadDb(Map<String, String> data) {
        new_add_item_button.setText("Wait...");
        new_add_item_button.setClickable(false);
        StringRequest request = new StringRequest(Request.Method.POST, base_url + "/api/addExpense", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Expense added!", Toast.LENGTH_SHORT).show();
                new_add_item_button.setText("Save");
                new_add_item_button.setClickable(true);
                sheetDialog.hide();

                req(new HashMap<>(), "/api/getAllExpenses", "Data Feteched.", "fetch_old_item", "GET");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error: not saved expense!", Toast.LENGTH_SHORT).show();
                new_add_item_button.setText("Save");
                new_add_item_button.setClickable(true);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                data.put("file", encodedImageString);
                return data;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(request);
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
        if(context.equals("fetch_old_item") && loading) {
            //
        }
        if(context.equals("fetch_old_item") && !loading) {
            full_total = 0;
            tempArrayList.clear();
            dataArrayList.clear();
            adhocArrayList.clear();
            monthlyArrayList.clear();
            if(response.length() >= 10) {
                try {
                    ExpenseData = new JSONObject(response);
                    try {
                        JSONObject myRes = new JSONObject(response);
                        JSONArray itemsArray = myRes.getJSONArray("data");
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemObject = itemsArray.getJSONObject(i);
                            String inputDate = itemObject.getString("created_at");
                            ZonedDateTime utcDateTime = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                utcDateTime = ZonedDateTime.parse(inputDate);
                            }
                            ZonedDateTime kolkataDateTime = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                kolkataDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
                            }
                            DateTimeFormatter outputFormatter = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy | hh:mm a");
                            }
                            String formattedDate = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                formattedDate = kolkataDateTime.format(outputFormatter);
                            }
                            if(itemObject.getDouble("amount") > MAX_VALUE) MAX_VALUE = itemObject.getDouble("amount");
                            formattedDate = formattedDate.replaceAll("am", "AM").replaceAll("pm", "PM");
                            dataArrayList.add(new ExpenseData(itemObject.getInt("id") + "",
                                    itemObject.getString("name"),
                                    itemObject.getString("type"),
                                    formattedDate,
                                    itemObject.getDouble("amount"),
                                    "https://architartgallery.in/storage/app/" + itemObject.getString("file")
                            ));
                            if(itemObject.getString("type").equals("0")) {
                                monthlyArrayList.add(new ExpenseData(itemObject.getInt("id") + "",
                                        itemObject.getString("name"),
                                        itemObject.getString("type"),
                                        formattedDate,
                                        itemObject.getDouble("amount"),
                                        "https://architartgallery.in/storage/app/" + itemObject.getString("file")
                                ));
                                monthly_total += itemObject.getDouble("amount");
                            }else {
                                adhocArrayList.add(new ExpenseData(itemObject.getInt("id") + "",
                                        itemObject.getString("name"),
                                        itemObject.getString("type"),
                                        formattedDate,
                                        itemObject.getDouble("amount"),
                                        "https://architartgallery.in/storage/app/" + itemObject.getString("file")
                                ));
                                adhoc_total += itemObject.getDouble("amount");
                            }
                            full_total += itemObject.getDouble("amount");
                        }
                        transactions_count.setText(itemsArray.length() + " " + "Transactions");
                        total_expense.setText("₹" + full_total);
                        tempArrayList.addAll(dataArrayList);
                        expenseAdapter.notifyDataSetChanged();
                    }catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(context.equals("expense_add") && loading) {
            new_add_item_button.setText("Wait....");
            new_add_item_button.setClickable(false);
        }
        else if(context.equals("expense_add") && !loading) {
            Toast.makeText(getApplicationContext(), "Expense Added!", Toast.LENGTH_SHORT).show();
            new_add_item_button.setText("Save");
            new_add_item_button.setClickable(true);
            sheetDialog.hide();
        }

        // Fetch and save filterd data.
    }

    private String getRealPathFromURI(Uri uri) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == RESULT_OK) {

            Uri selectedFileUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                encodeBitmapImage(bitmap);
                String path = getRealPathFromURI(selectedFileUri);
                String splits[] = path.split("/");
                String file_name = splits[splits.length - 1];
                file_upload_btn.setText(file_name);
            }catch (Exception e) {
                // No exception
            }
        }
    }

    private void encodeBitmapImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] bytesOfImage = byteArrayOutputStream.toByteArray();
        encodedImageString = android.util.Base64.encodeToString(bytesOfImage, Base64.DEFAULT);

//        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText("Copied Text", encodedImageString);
//        clipboard.setPrimaryClip(clip);
    }

    private void openDateRangePicker() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Builder<Pair<Long, Long>> builder = Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");
        builder.setCalendarConstraints(constraintsBuilder.build());

        final MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                if (selection != null) {
                    range_date_picker.setText(dateFormatter.format(selection.first) + " - " + dateFormatter.format(selection.second));
                    String query = "week_start=" + dateFormatter.format(selection.first) + "&" + "week_end=" + dateFormatter.format(selection.second) + "&";
                    req(new HashMap<>(), "/api/getAllExpenses/?" + query, "Data Feteched.", "fetch_old_item", "GET");
                }
            }
        });

        picker.show(getSupportFragmentManager(), picker.toString());

    }
}