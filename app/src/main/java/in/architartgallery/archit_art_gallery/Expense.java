package in.architartgallery.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.slider.RangeSlider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Expense extends AppCompatActivity {

    private ImageView back_button, purchase_sale_config, cross_dialog;
    BottomSheetDialog sheetDialog;
    Button add_expenses;
    RangeSlider rangeSlider;

    TextView transactions_count, total_expense;
    double full_total, adhoc_total, monthly_total;
    MaterialCheckBox monthly_filter, adhoc_filter;

    RecyclerView expense_recycler_view;
    ArrayList<ExpenseData> dataArrayList = new ArrayList<>();
    ArrayList<ExpenseData> monthlyArrayList = new ArrayList<>();
    ArrayList<ExpenseData> adhocArrayList = new ArrayList<>();
    ArrayList<ExpenseData> tempArrayList = new ArrayList<>();
    ExpenseAdapter expenseAdapter;

    JSONObject ExpenseData;

    final static String base_url = "https://architartgallery.in/public";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        req(new HashMap<>(), "/api/getAllExpenses", "Data Feteched.", "fetch_old_item", "GET");

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
            if(response.length() >= 10) {
                try {
                    ExpenseData = new JSONObject(response);
                    try {
                        JSONObject myRes = new JSONObject(response);
                        JSONArray itemsArray = myRes.getJSONArray("data");
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemObject = itemsArray.getJSONObject(i);
                            dataArrayList.add(new ExpenseData(itemObject.getInt("id") + "",
                                    itemObject.getString("name"),
                                    itemObject.getString("type"),
                                    "12 Jan 2024 | 11:55 AM",
                                    itemObject.getDouble("amount"),
                                    itemObject.getString("file")
                            ));
                            if(itemObject.getString("type").equals("1")) {
                                monthlyArrayList.add(new ExpenseData(itemObject.getInt("id") + "",
                                        itemObject.getString("name"),
                                        itemObject.getString("type"),
                                        "12 Jan 2024 | 11:55 AM",
                                        itemObject.getDouble("amount"),
                                        itemObject.getString("file")
                                ));
                                monthly_total += itemObject.getDouble("amount");
                            }else {
                                adhocArrayList.add(new ExpenseData(itemObject.getInt("id") + "",
                                        itemObject.getString("name"),
                                        itemObject.getString("type"),
                                        "12 Jan 2024 | 11:55 AM",
                                        itemObject.getDouble("amount"),
                                        itemObject.getString("file")
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
    }
}