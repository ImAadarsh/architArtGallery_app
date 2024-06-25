package in.architartgallery.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Create_Invoice extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    LinearLayout today_date_piker;
    TextView invoice_pike_date;
    ArrayList<String> monthNames;
    ImageView back_button;

    BottomSheetDialog sheetDialog;

    ImageView cross_dialog;

    Button add_items;

    TextInputEditText item_name, has_code, rate, quantity;
    MaterialButton item_add_button, bill_cancel_btn, bill_create_btn;

    RecyclerView recyclerView;
    TextView no_item_added;
    ItemAdapter ItemsAdaptor;

    // BILL
    TextView total_ex_gst, delhi_gst_cost, cgst_gst_cost, igst_gst_cost, total_with_gst, invoice_serial_no;
    private double total_ex_gst_amount = 0;
    double dgst = 0, igst = 0, cgst = 0;
    SwitchMaterial is_gst_product;

    // Mange Payment Option and load
    ConstraintLayout bottom_payment_mode_option;
    SwitchMaterial payment_mode;
    final static String base_url = "https://architartgallery.in/public";
    String Invoice_ID, Serial_NO, Bill_Type;
    JSONObject Invoice_Old_Date;
    ArrayList<ItemsData> itemsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_invoice);

        // bill manage button
        bill_cancel_btn = findViewById(R.id.bill_cancel_btn);
        bill_create_btn = findViewById(R.id.bill_create_btn);

        // Manage Intent data
        invoice_serial_no = findViewById(R.id.invoice_serial_no);
        invoice_serial_no.setText("SN. " + getIntent().getStringExtra("INVOICE_SERIAL_NO_CREATE"));
        Serial_NO = getIntent().getStringExtra("INVOICE_SERIAL_NO_CREATE");
        Invoice_ID = getIntent().getStringExtra("INVOICE_ID_CREATE");
        Bill_Type = getIntent().getStringExtra("BILL_TYPE");
        req(new HashMap<>(), "/api/getDetailedInvoice/" + Invoice_ID, "Invoice details retrieved successfully.", "fetch_old_item", "GET");

//        if(Bill_Type.equals("normal")) {
//            bill_create_btn.setText("Create Bill");
//        }else if(Bill_Type.equals("performa")) {
//            bill_create_btn.setText("Create Performa");
//        }else if(Bill_Type.equals("dummy")) {
//            bill_create_btn.setText("Create Dummy");
//        }
        bill_create_btn.setText("Invoice");

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
        itemsDB = new ArrayList<>();

        // Manage Recycler Items view
        recyclerView = findViewById(R.id.recycler_view_items);
        ItemsAdaptor = new ItemAdapter(itemsDB, this);
        recyclerView.setAdapter(ItemsAdaptor);
        no_item_added = findViewById(R.id.no_item_added);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        payment_mode = findViewById(R.id.payment_mode);

        if(Invoice_Old_Date != null) {
            try {
                total_ex_gst_amount = Double.parseDouble(total_ex_gst.getText().toString().replace("₹", ""));
                JSONObject dataArray = Invoice_Old_Date.getJSONObject("data");
                JSONArray itemsArray = dataArray.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject itemObject = itemsArray.getJSONObject(i);
                    JSONObject product = itemObject.getJSONObject("product");
                    itemsDB.add(new ItemsData(itemObject.getInt("id"),
                            product.getString("name"),
                            product.getString("hsn_code"),
                            itemObject.getDouble("price_of_one"),
                            itemObject.getInt("quantity"),
                            itemObject.getInt("is_gst"),
                            Invoice_ID,
                            total_ex_gst,
                            delhi_gst_cost,
                            cgst_gst_cost,
                            igst_gst_cost,
                            total_with_gst
                    ));
                    total_ex_gst_amount += itemObject.getDouble("price_of_one") * itemObject.getInt("quantity");
                    total_ex_gst.setText("₹" + total_ex_gst_amount);
                    double dgst = total_ex_gst_amount * 0.09;
                    double cgst = total_ex_gst_amount * 0.09;
                    double igst = dgst + cgst;
                    if(!is_gst_product.isChecked()) {
                        dgst = dgst - itemObject.getDouble("price_of_one") * itemObject.getInt("quantity") * 0.09;
                        cgst = cgst - itemObject.getDouble("price_of_one") * itemObject.getInt("quantity") * 0.09;
                        igst = dgst + cgst;
                    }
                    delhi_gst_cost.setText("₹" + String.format("%.2f", dgst));
                    cgst_gst_cost.setText("₹" + String.format("%.2f", cgst));
                    igst_gst_cost.setText("₹" + String.format("%.2f", igst));
//                    total_with_gst.setText("₹" + String.format("%.2f", (total_ex_gst_amount + igst)));
                    total_with_gst.setText("₹" + String.format("%.2f", dataArray.getDouble("total_amount")));
                }
                payment_mode.setChecked(dataArray.getString("payment_mode").equals("online"));
                ItemsAdaptor.notifyDataSetChanged();
            }catch (JSONException e) {
                //
            }
        }

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
        AtomicInteger itemCnt = new AtomicInteger(0);
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

            // is_gst button
            is_gst_product = view.findViewById(R.id.is_gst_product);
            if(Bill_Type.equals("performa")) { is_gst_product.setVisibility(View.GONE); }

            item_add_button.setOnClickListener(ev -> {
                try {
                    // Mange Total and GST INFO
//                    total_ex_gst_amount += Integer.parseInt(rate.getText().toString()) * Integer.parseInt(quantity.getText().toString());
//                    total_ex_gst.setText("₹" + total_ex_gst_amount);
//                    double dgst = total_ex_gst_amount * 0.09;
//                    double cgst = total_ex_gst_amount * 0.09;
//                    double igst = dgst + cgst;
//                    if(!is_gst_product.isChecked()) {
//                        dgst = dgst - Integer.parseInt(rate.getText().toString()) * Integer.parseInt(quantity.getText().toString()) * 0.09;
//                        cgst = cgst - Integer.parseInt(rate.getText().toString()) * Integer.parseInt(quantity.getText().toString()) * 0.09;
//                        igst = dgst + cgst;
//                    }
//                    delhi_gst_cost.setText("₹" + String.format("%.2f", dgst));
//                    cgst_gst_cost.setText("₹" + String.format("%.2f", cgst));
//                    igst_gst_cost.setText("₹" + String.format("%.2f", igst));
//                    total_with_gst.setText("₹" + String.format("%.2f", (total_ex_gst_amount + igst)));

                    // Mange the item data
                    itemsDB.add(new ItemsData(itemCnt.getAndIncrement(), item_name.getText().toString(),
                            has_code.getText().toString(),
                            Double.parseDouble(String.format("%.2f", Integer.parseInt(rate.getText().toString()) / (is_gst_product.isChecked() ? 1.18 : 1))),
                            Integer.parseInt(quantity.getText().toString()),
                            is_gst_product.isChecked() ? 1 : 0,
                            Invoice_ID,
                            total_ex_gst,
                            delhi_gst_cost,
                            cgst_gst_cost,
                            igst_gst_cost,
                            total_with_gst
                    ));
                    Toast.makeText(getApplicationContext(), "Item added!", Toast.LENGTH_SHORT).show();
                    ItemsAdaptor.notifyDataSetChanged();

                    Map<String, String> reqData = new HashMap<>();
                    reqData.put("invoice_id", Invoice_ID);
                    reqData.put("hsn_code", has_code.getText().toString());
                    reqData.put("name", item_name.getText().toString());
                    reqData.put("price", rate.getText().toString());
                    reqData.put("quantity", quantity.getText().toString());
                    reqData.put("is_gst", is_gst_product.isChecked() ? "1" : "0");
                    req(reqData, "/api/addProduct", "Product Added successfully.", "add_new_item", "POST");

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
                    Toast.makeText(getApplicationContext(), "Put right data!" + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            cross_dialog = view.findViewById(R.id.cross_dialog);
            cross_dialog.setOnClickListener(ev -> {
                sheetDialog.hide();
            });
        });

        bill_cancel_btn.setOnClickListener(e -> {
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        });

        bill_create_btn.setOnClickListener(e -> {
            Map<String, String> reqData = new HashMap<>();
            reqData.put("id", Invoice_ID);
            reqData.put("payment_mode", payment_mode.isChecked() ? "online" : "cash");
            req(reqData, "/api/editInvoice", "Invoice updated successfully.", "update_payment_mode", "POST");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://invoice.architartgallery.in/invoice.html?invoiceid=" + Invoice_ID));
            Intent dashboard = new Intent(this, Dashboard.class);
            e.getContext().startActivity(dashboard);
            e.getContext().startActivity(intent);
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
        if(context.equals("add_new_item") && loading) {
            item_add_button.setText("SN. Wait...");
            item_add_button.setEnabled(false);
        }
        if(context.equals("add_new_item") && !loading) {
            item_add_button.setText("Add");
            item_add_button.setEnabled(true);
            if(response.length() >= 10) {
                total_ex_gst_amount = Double.parseDouble(total_ex_gst.getText().toString().replace("₹", ""));
                try {
                    Log.d("Test", response);
                    JSONObject myRes = new JSONObject(response);
                    JSONObject item = myRes.getJSONObject("data");

//                    total_ex_gst_amount += item.getDouble("price_of_one") * Integer.parseInt(item.getString("quantity"));
                    total_ex_gst_amount = item.getDouble("total_ex_gst_amount");
                    total_ex_gst.setText("₹" + total_ex_gst_amount);
                    dgst = item.getInt("t_dgst");
                    cgst = item.getInt("t_cgst");
                    igst = item.getInt("t_igst");
                    delhi_gst_cost.setText("₹" + String.format("%.2f", dgst));
                    cgst_gst_cost.setText("₹" + String.format("%.2f", cgst));
                    igst_gst_cost.setText("₹" + String.format("%.2f", igst));
//                    total_with_gst.setText("₹" + String.format("%.2f", (total_ex_gst_amount + dgst + cgst + igst)));
                    total_with_gst.setText("₹" + String.format("%.2f", item.getDouble("total_amount")));

//                    Invoice_ID = myRes.getJSONObject("data").getInt("id");
                    itemsDB.get(itemsDB.size() - 1).ITEM_ID = item.getInt("id");
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(context.equals("fetch_old_item") && !loading) {
            if(response.length() >= 10) {
                try {
                    Invoice_Old_Date = new JSONObject(response);
                    try {
                        total_ex_gst_amount = Double.parseDouble(total_ex_gst.getText().toString().replace("₹", ""));

                        JSONObject dataArray = Invoice_Old_Date.getJSONObject("data");
                        JSONArray itemsArray = dataArray.getJSONArray("items");
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemObject = itemsArray.getJSONObject(i);
                            JSONObject product = itemObject.getJSONObject("product");
                            Log.d("DB", itemObject.toString());
                            itemsDB.add(new ItemsData(itemObject.getInt("id"),
                                    product.getString("name"),
                                    product.getString("hsn_code"),
                                    itemObject.getDouble("price_of_one"),
                                    itemObject.getInt("quantity"),
                                    itemObject.getInt("is_gst"),
                                    Invoice_ID,
                                    total_ex_gst,
                                    delhi_gst_cost,
                                    cgst_gst_cost,
                                    igst_gst_cost,
                                    total_with_gst
                            ));
                            total_ex_gst_amount += itemObject.getDouble("price_of_one") * itemObject.getInt("quantity");
                            total_ex_gst.setText("₹" + String.format("%.2f", total_ex_gst_amount));
                            dgst += itemObject.getDouble("dgst");
                            cgst += itemObject.getDouble("cgst");
                            igst += itemObject.getDouble("igst");
                            delhi_gst_cost.setText("₹" + String.format("%.2f", dgst));
                            cgst_gst_cost.setText("₹" + String.format("%.2f", cgst));
                            igst_gst_cost.setText("₹" + String.format("%.2f", igst));
//                            total_with_gst.setText("₹" + String.format("%.2f", (total_ex_gst_amount + dgst + cgst + igst)));
                        }
                        total_with_gst.setText("₹" + String.format("%.2f", dataArray.getDouble("total_amount")));
                        if (itemsDB.isEmpty()) {
                            bottom_payment_mode_option.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            no_item_added.setVisibility(View.VISIBLE);
                        } else {
                            bottom_payment_mode_option.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            no_item_added.setVisibility(View.GONE);
                        }
                        payment_mode.setChecked(dataArray.getString("payment_mode").equals("online"));
                        ItemsAdaptor.notifyDataSetChanged();
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