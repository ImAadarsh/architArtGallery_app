package in.architartgallery.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;

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

    RecyclerView expense_recycler_view;
    ArrayList<ExpenseData> dataArrayList = new ArrayList<>();
    ExpenseAdapter expenseAdapter;

    JSONObject ExpenseData;

    final static String base_url = "https://architartgallery.in/public";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        req(new HashMap<>(), "/api/getAllExpenses", "need an msg", "fetch_old_item", "GET");

        back_button = findViewById(R.id.back_button);
        purchase_sale_config = findViewById(R.id.purchase_sale_config);
        add_expenses = findViewById(R.id.add_expenses);

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

        dataArrayList.add(new ExpenseData("1", "ABC : Monthly", "1", "12 Jan 2024 | 11:55 AM", 100));
        dataArrayList.add(new ExpenseData("2", "BCD : Adhoc", "1", "12 Jan 2024 | 11:55 AM", 100));
        dataArrayList.add(new ExpenseData("3", "KBC : Monthly", "1", "12 Jan 2024 | 11:55 AM", 100));

        expense_recycler_view = findViewById(R.id.expense_recyler_view);
        expenseAdapter = new ExpenseAdapter(dataArrayList, this);
        expense_recycler_view.setAdapter(expenseAdapter);
        expense_recycler_view.setLayoutManager(new LinearLayoutManager(this));
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
//        if(context.equals("add_new_item") && loading) {
//            item_add_button.setText("SN. Wait...");
//            item_add_button.setEnabled(false);
//        }
//        if(context.equals("add_new_item") && !loading) {
//            item_add_button.setText("Add");
//            item_add_button.setEnabled(true);
//            if(response.length() >= 10) {
//                total_ex_gst_amount = Double.parseDouble(total_ex_gst.getText().toString().replace("₹", ""));
//                try {
//                    Log.d("Test", response);
//                    JSONObject myRes = new JSONObject(response);
//                    JSONObject item = myRes.getJSONObject("data");
//
//                    total_ex_gst_amount += Integer.parseInt(item.getString("price_of_one")) * Integer.parseInt(item.getString("quantity"));
//                    total_ex_gst.setText("₹" + total_ex_gst_amount);
//                    dgst += item.getInt("dgst");
//                    cgst += item.getInt("cgst");
//                    igst += item.getInt("igst");
//                    delhi_gst_cost.setText("₹" + String.format("%.2f", dgst));
//                    cgst_gst_cost.setText("₹" + String.format("%.2f", cgst));
//                    igst_gst_cost.setText("₹" + String.format("%.2f", igst));
//                    total_with_gst.setText("₹" + String.format("%.2f", (total_ex_gst_amount + dgst + cgst + igst)));
//
////                    Invoice_ID = myRes.getJSONObject("data").getInt("id");
//                } catch (JSONException e) {
//                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//        if(context.equals("fetch_old_item") && !loading) {
//            if(response.length() >= 10) {
//                try {
//                    ExpenseData = new JSONObject(response);
//                    try {
//                        JSONObject dataArray = Invoice_Old_Date.getJSONObject("data");
//                        JSONArray itemsArray = dataArray.getJSONArray("items");
//                        for (int i = 0; i < itemsArray.length(); i++) {
//                            JSONObject itemObject = itemsArray.getJSONObject(i);
//                            JSONObject product = itemObject.getJSONObject("product");
//                            itemsDB.add(new ItemsData(itemObject.getInt("id"),
//                                    product.getString("name"),
//                                    product.getString("hsn_code"),
//                                    itemObject.getInt("price_of_one"),
//                                    itemObject.getInt("quantity"),
//                                    itemObject.getInt("is_gst"),
//                                    Invoice_ID,
//                                    total_ex_gst,
//                                    delhi_gst_cost,
//                                    cgst_gst_cost,
//                                    igst_gst_cost,
//                                    total_with_gst
//                            ));
//                            total_ex_gst_amount += itemObject.getInt("price_of_one") * itemObject.getInt("quantity");
//                            total_ex_gst.setText("₹" + total_ex_gst_amount);
//                            dgst += itemObject.getInt("dgst");
//                            cgst += itemObject.getInt("cgst");
//                            igst += itemObject.getInt("igst");
//                            delhi_gst_cost.setText("₹" + String.format("%.2f", dgst));
//                            cgst_gst_cost.setText("₹" + String.format("%.2f", cgst));
//                            igst_gst_cost.setText("₹" + String.format("%.2f", igst));
//                            total_with_gst.setText("₹" + String.format("%.2f", (total_ex_gst_amount + dgst + cgst + igst)));
//                        }
//                        ItemsAdaptor.notifyDataSetChanged();
//                    }catch (JSONException e) {
//                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
    }
}