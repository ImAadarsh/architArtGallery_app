package in.architartgallery.archit_art_gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    private static final String SHARED_PREFS = "login_cred";
    private static final String PHONE_KEY = "Phone_Key";
    private static final String PASSWORD_KEY = "Password_Key";
    private static final String USER_LOGGED_IN_DATA_KEY = "Login_User_Info";

    BottomNavigationView bottomNavigationBar;

    SharedPreferences sharedpreferences;
    String phone, password, user_logged_in_data;

    CardView state_dashboard;

    DrawerLayout drawerLayout;
    DrawerLayout profileDrawerLayout, reportDrawerLayout;

    RecyclerView recyclerView;
    ListAdapter invoiceAdaptor;
    ArrayList<ListData> dataArrayList = new ArrayList<>();
    ArrayList<ListData> invoiceArrayList = new ArrayList<>();
    ArrayList<ListData> draftArrayList = new ArrayList<>();

    // Elements
    private ImageView dashboard_invoice_search;
    private ImageView profile_slider_back, my_profile_slider_back, my_report_slider_back;
    TextView total_actual_sale_date;
    float mButtonWidth;
    TabLayout switch_invoice_draft;

    Drawable mDeleteIcon, mEditIcon, mPdfIcon;
    ArrayList<String> myMonths;

    AutoCompleteTextView india_all_state_autocomplete_view;
    MaterialButton my_profile_logout;
    TextInputEditText my_profile_name_edit, profile_password;
    TextView report_profile_user_name, report_profile_user_role, menu_profile_user_name, menu_profile_user_role;
    Button update_profile;
    JSONObject jsonResponse = new JSONObject();
    String Business_Id;

    final static String base_url = "https://architartgallery.in/public";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Manged Current Date
        myMonths = new ArrayList<>();
        myMonths.add("January");
        myMonths.add("February");
        myMonths.add("March");
        myMonths.add("April");
        myMonths.add("May");
        myMonths.add("June");
        myMonths.add("July");
        myMonths.add("August");
        myMonths.add("September");
        myMonths.add("October");
        myMonths.add("November");
        myMonths.add("December");
        total_actual_sale_date = findViewById(R.id.total_actual_sale_date);
        LocalDate date = LocalDate.now();
        String selectedDate = formatDate(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        total_actual_sale_date.setText(selectedDate);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        phone = sharedpreferences.getString(PHONE_KEY, null);
        password = sharedpreferences.getString(PASSWORD_KEY, null);
        user_logged_in_data = sharedpreferences.getString(USER_LOGGED_IN_DATA_KEY, null);

        try {
            jsonResponse = new JSONObject(user_logged_in_data);
            Business_Id = jsonResponse.getJSONObject("user").getInt("business_id") + "";
            req(new HashMap<>(), "/api/getAllInvoices?business_id" + Business_Id, "Invoices retrieved successfully.", "fetch_invoice", "GET");
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Response get to failed!", Toast.LENGTH_SHORT).show();
        }

        recyclerView = findViewById(R.id.recycler_view);
        invoiceAdaptor = new ListAdapter(dataArrayList, this);
        recyclerView.setAdapter(invoiceAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        switch_invoice_draft = findViewById(R.id.switch_invoice_draft);

        switch_invoice_draft.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Check if the selected tab is "switch_invoice_draft"
                if (tab.getText().toString().equals("Invoiced")) {
//                    Toast.makeText(getApplicationContext(), "Invoiced", Toast.LENGTH_SHORT).show();
                    dataArrayList.clear();
                    dataArrayList.addAll(invoiceArrayList);
                    invoiceAdaptor.notifyDataSetChanged();
                }
                if(tab.getText().toString().equals("Drafts")) {
//                    Toast.makeText(getApplicationContext(), "Drafts", Toast.LENGTH_SHORT).show();
                    dataArrayList.clear();
                    dataArrayList.addAll(draftArrayList);
                    invoiceAdaptor.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // This method is not needed for your requirement, but it must be implemented
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // This method is not needed for your requirement, but it must be implemented
            }
        });

        state_dashboard = findViewById(R.id.state_dashboard);
        state_dashboard.setOnClickListener(e -> {
            Intent state_intent = new Intent(getApplicationContext(), State.class);
            startActivity(state_intent);
        });

        // Mange swipe to revel
        mEditIcon = ContextCompat.getDrawable(this, R.drawable.ic_edit);
        mDeleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete);
        mPdfIcon = ContextCompat.getDrawable(this, R.drawable.ic_pdf);
        final ColorDrawable mBackground = new ColorDrawable(Color.TRANSPARENT);
        Paint mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;

                if (dX > 0) { // Swiping to the right
                    mBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                } else if (dX < 0) { // Swiping to the left
                    mBackground.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    mBackground.setBounds(0, 0, 0, 0);
                }
                mBackground.draw(c);

                // Calculate icon position
                int iconMargin = (itemView.getHeight() - mDeleteIcon.getIntrinsicHeight()) / 2 - 20;
                int iconTop = itemView.getTop() + (itemView.getHeight() - mDeleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + mDeleteIcon.getIntrinsicHeight();
                int iconLeft1, iconRight1, iconLeft2, iconRight2, iconLeft3, iconRight3;

                // Swiping to the right
                if (dX > 0) {
                    iconLeft1 = itemView.getLeft() + iconMargin;
                    iconRight1 = itemView.getLeft() + iconMargin + mDeleteIcon.getIntrinsicWidth();

                    iconLeft2 = itemView.getLeft() + 2 * iconMargin + mDeleteIcon.getIntrinsicWidth();
                    iconRight2 = itemView.getLeft() + 2 * iconMargin + 2 * mDeleteIcon.getIntrinsicWidth();

                    iconLeft3 = itemView.getLeft() + 3 * iconMargin + 2 * mDeleteIcon.getIntrinsicWidth();
                    iconRight3 = itemView.getLeft() + 3 * iconMargin + 3 * mDeleteIcon.getIntrinsicWidth();
                } else if (dX < 0) { // Swiping to the left
                    iconLeft1 = itemView.getRight() - 3 * iconMargin - 3 * mDeleteIcon.getIntrinsicWidth();
                    iconRight1 = itemView.getRight() - 3 * iconMargin - 2 * mDeleteIcon.getIntrinsicWidth();

                    iconLeft2 = itemView.getRight() - 2 * iconMargin - 2 * mDeleteIcon.getIntrinsicWidth();
                    iconRight2 = itemView.getRight() - 2 * iconMargin - mDeleteIcon.getIntrinsicWidth();

                    iconLeft3 = itemView.getRight() - iconMargin - mDeleteIcon.getIntrinsicWidth();
                    iconRight3 = itemView.getRight() - iconMargin;
                } else {
                    iconLeft1 = 0;
                    iconRight1 = 0;

                    iconLeft2 = 0;
                    iconRight2 = 0;

                    iconLeft3 = 0;
                    iconRight3 = 0;
                }


                // Draw the icons
                mEditIcon.setBounds(iconLeft1, iconTop, iconRight1, iconBottom);
                mEditIcon.draw(c);

                mDeleteIcon.setBounds(iconLeft2, iconTop, iconRight2, iconBottom);
                mDeleteIcon.draw(c);

                mPdfIcon.setBounds(iconLeft3, iconTop, iconRight3, iconBottom);
                mPdfIcon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0;
            }

            @Override
            public int convertToAbsoluteDirection(int flags, int layoutDirection) {
                if (mButtonWidth == 0) {
                    return super.convertToAbsoluteDirection(flags, layoutDirection);
                }
                return 0;
            }

            @Override
            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                itemView.setTranslationX(-400);
                super.onChildDrawOver(c, recyclerView, viewHolder, -400, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        // Elements Initialize
        dashboard_invoice_search = findViewById(R.id.dashboard_invoice_search);

        bottomNavigationBar = findViewById(R.id.bottom_navigation);
        // Default Select
        bottomNavigationBar.setSelectedItemId(R.id.add_inv);

        // Drawer Profile
        drawerLayout = findViewById(R.id.profile_drawer);
        // My Profile Drawer
        profileDrawerLayout = findViewById(R.id.my_profile);
        // Report Drawer
        reportDrawerLayout = findViewById(R.id.report_drawer);
        NavigationView report_sidebar_nav = findViewById(R.id.report_sidebar_nav);
        // Menu drawer
        NavigationView expense_sidebar_nav = findViewById(R.id.expense_sidebar_nav);

        bottomNavigationBar.setOnItemSelectedListener(item -> {
            // Logged In User Info
            String USERNAME = "";
            String PASSWORD = "";
            String ROLE = "";
            String PHONE = "";

            try {
                jsonResponse = new JSONObject(user_logged_in_data);
                USERNAME = jsonResponse.getJSONObject("user").getString("name");
                PASSWORD = jsonResponse.getJSONObject("user").getString("passcode");
                ROLE = jsonResponse.getJSONObject("user").getString("role");
                PHONE = jsonResponse.getJSONObject("user").getString("phone");
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }

            // Profile User Details Set
            my_profile_name_edit = findViewById(R.id.my_profile_name_edit);
            my_profile_name_edit.setText(USERNAME);
            profile_password = findViewById(R.id.profile_password);
            profile_password.setText(PASSWORD);
            update_profile = findViewById(R.id.update_profile);

            // Report User Details Set
            report_profile_user_name = findViewById(R.id.report_profile_user_name);
            report_profile_user_role = findViewById(R.id.report_profile_user_role);
            report_profile_user_name.setText(USERNAME);
            report_profile_user_role.setText(ROLE);

            // Menu
            menu_profile_user_name = findViewById(R.id.menu_profile_user_name);
            menu_profile_user_role = findViewById(R.id.menu_profile_user_role);
            menu_profile_user_name.setText(USERNAME);
            menu_profile_user_role.setText(ROLE);

            if(item.getTitle().equals("Menu")) {
                drawerLayout.setElevation(10);
                drawerLayout.open();

                // Profile back button
                profile_slider_back = findViewById(R.id.profile_slider_back);
                profile_slider_back.setOnClickListener(e -> {
                    drawerLayout.close();
                    drawerLayout.setElevation(0);
                });

                // Handle menu
                expense_sidebar_nav.setNavigationItemSelectedListener(menu_item -> {
                    if(menu_item.getTitle().equals("Bill")) {
                        //
                    }
                    if(menu_item.getTitle().equals("Expense")) {
                        Intent actual_sale_intent = new Intent(getApplicationContext(), Expense.class);
                        startActivity(actual_sale_intent);
                    }
                    return false;
                });
            }
            if(item.getTitle().equals("Add Invoices")) {
                // <First_address, Second_address, City, State, Pin-code(6 digit)>
                Intent add_invoice = new Intent(getApplicationContext(), InvoicePage.class);
                add_invoice.putExtra("INVOICE_SERIAL_NO", "");
                add_invoice.putExtra("INVOICE_TO", "");
                add_invoice.putExtra("CUSTOMER_TYPE", "");
                add_invoice.putExtra("AADHAAR_NUMBER", "");
                add_invoice.putExtra("BILLING_ADDRESS", "0");
                add_invoice.putExtra("SHIPPING_ADDRESS", "0");
                add_invoice.putExtra("BOTH_ADDRESS_IS_SAME", true);
                add_invoice.putExtra("MOBILE_NUMBER", "");
                startActivity(add_invoice);
            }
            if(item.getTitle().equals("My Profile")) {
                profileDrawerLayout.setElevation(10);
                profileDrawerLayout.open();

                String finalPHONE = PHONE;
                update_profile.setOnClickListener(profile_update -> {
                    Map<String, String> data = new HashMap<>();
                    data.put("phone", finalPHONE);
                    data.put("password", profile_password.getText().toString());
                    data.put("name", my_profile_name_edit.getText().toString());
                    req(data, "/api/updateprofile", "User is Updated sucessfully.", "update_profile", "POST");
                });

                // Profile back button
                my_profile_slider_back = findViewById(R.id.my_profile_back);
                my_profile_slider_back.setOnClickListener(e -> {
                    profileDrawerLayout.close();
                    profileDrawerLayout.setElevation(0);
                });

                // Add Locations in My-Profile
                india_all_state_autocomplete_view = findViewById(R.id.locations_profile);
                String[] indianStates = getResources().getStringArray(R.array.indian_states);
                ArrayAdapter<String> state_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, indianStates);
                india_all_state_autocomplete_view.setAdapter(state_adapter);

                // Handel Profile Logout
                my_profile_logout = findViewById(R.id.my_profile_logout);
                my_profile_logout.setOnClickListener(logout -> {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    // Finish the current activity
                    finish();
                });
            }
            if(item.getTitle().equals("Report")) {
                reportDrawerLayout.setElevation(10);
                reportDrawerLayout.open();

                // Report back button
                my_report_slider_back = findViewById(R.id.report_slide_back);
                my_report_slider_back.setOnClickListener(e -> {
                    reportDrawerLayout.close();
                    reportDrawerLayout.setElevation(0);
                });

                // Handle Report menu
                report_sidebar_nav.setNavigationItemSelectedListener(report_item -> {
                    if(report_item.getTitle().equals("Purchase sale")) {
                        Intent purchase_sale_intent = new Intent(getApplicationContext(), PurchaseSale.class);
                        startActivity(purchase_sale_intent);
                    }
                    if(report_item.getTitle().equals("Actual Sale")) {
                        Intent actual_sale_intent = new Intent(getApplicationContext(), ActualSale.class);
                        startActivity(actual_sale_intent);
                    }
                    if(report_item.getTitle().equals("Expense Report")) {
                        Intent intent = new Intent(getApplicationContext(), ExpenseReport.class);
                        startActivity(intent);
                    }
                    if(report_item.getTitle().equals("Invoice List")) {
                        Intent intent = new Intent(getApplicationContext(), InvoiceList.class);
                        startActivity(intent);
                    }
                    if(report_item.getTitle().equals("Itemized Sale")) {
                        Intent intent = new Intent(getApplicationContext(), ItemizedSale.class);
                        startActivity(intent);
                    }
                    return false;
                });
            }
            if(item.getTitle().equals("Barcode")) {
                Toast.makeText(getApplicationContext(), "Coming soon...", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Called when the drawer is sliding
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Called when the drawer is fully opened
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Called when the drawer is fully closed
                drawerLayout.setElevation(0);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer state changes
            }
        });

        profileDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Called when the drawer is sliding
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Called when the drawer is fully opened
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Called when the drawer is fully closed
                profileDrawerLayout.setElevation(0);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer state changes
            }
        });

        reportDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Called when the drawer is sliding
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Called when the drawer is fully opened
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Called when the drawer is fully closed
                reportDrawerLayout.setElevation(0);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer state changes
            }
        });

        // Elements Process
        dashboard_invoice_search.setOnClickListener(e -> {
            Intent search_activity = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(search_activity);
        });

    }

    private String formatDate(int year, int month, int day) {
        return day + " " + myMonths.get(month) + " " + year;
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
        if(context.equals("update_profile")) {
            if (loading) {
                update_profile.setText("Wait....");
                update_profile.setEnabled(false);
            } else {
                update_profile.setText("Update Profile ?");
                update_profile.setEnabled(true);
                if(response.length() >= 10) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    user_logged_in_data = response.replaceFirst("data", "user");
                    try {
                        jsonResponse = new JSONObject(response.replaceFirst("data", "user"));
                        String name = jsonResponse.getJSONObject("user").getString("name");
                        my_profile_name_edit.setText(name);
                        report_profile_user_name.setText(name);
                        menu_profile_user_name.setText(name);
                        profile_password.setText(jsonResponse.getJSONObject("user").getString("passcode"));
                        editor.putString(PHONE_KEY, jsonResponse.getJSONObject("user").getString("phone"));
                        editor.putString(PASSWORD_KEY, jsonResponse.getJSONObject("user").getString("passcode"));
                        editor.putString(USER_LOGGED_IN_DATA_KEY, user_logged_in_data);
                        editor.apply();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
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
                        String type = item.getString("type");
                        int is_completed = 0;
                        int cost = 0;
                        if(item.isNull("items_sum_price_of_all") || item.getString("items_sum_price_of_all").equals("null")) {
                            cost = 0;
                        }else {
                            cost = item.getInt("items_sum_price_of_all");
                        }
                        if(item.isNull("is_completed")) {
                            cost = 0;
                        }else {
                            is_completed = item.getInt("is_completed");
                        }
                        if(!name.equals("null")
                            && !id.equals("null")
                            && !sn.equals("null")
                            && !c_t.equals("null")
                        ) {
                            if(is_completed == 1) {
                                invoiceArrayList.add(new ListData(
                                                id,
                                                sn,
                                                name,
                                                date,
                                                cost,
                                                type,
                                                c_t,
                                                aadhaar,
                                                billling,
                                                shipping,
                                                mobile
                                        )
                                );
                            }else {
                                if(!date.equals("null")) {
                                    draftArrayList.add(new ListData(
                                                    id,
                                                    sn,
                                                    name,
                                                    date,
                                                    cost,
                                                    type,
                                                    c_t,
                                                    aadhaar,
                                                    billling,
                                                    shipping,
                                                    mobile
                                            )
                                    );
                                }
                            }
                        }
                        else {
                            if(!billling.equals("null") && !shipping.equals("null") && !billling.equals("0") && !shipping.equals("0") && !date.equals("null")) {
//                                Log.d("OK", id + " | " + sn + " | " + name + " | " + date + " | " + cost + " | " + type + " | " + c_t + " | " + aadhaar + " | " + billling + " | " + shipping + " | " + mobile);
                                draftArrayList.add(new ListData(
                                                id,
                                                sn.length() > 0 ? sn.equals("null") ? "0" : sn : "0",
                                                name,
                                                date,
                                                cost,
                                                type,
                                                c_t,
                                                aadhaar,
                                                billling,
                                                shipping,
                                                mobile
                                        )
                                );
                            }
                        }
                    }
                    dataArrayList.addAll(invoiceArrayList);
                    invoiceAdaptor.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}