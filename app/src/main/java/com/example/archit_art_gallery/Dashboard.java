package com.example.archit_art_gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class Dashboard extends AppCompatActivity {

    private static final String SHARED_PREFS = "login_cred";
    private static final String PHONE_KEY = "Phone_Key";
    private static final String PASSWORD_KEY = "Password_Key";

    BottomNavigationView bottomNavigationBar;

    SharedPreferences sharedpreferences;
    String phone;

    DrawerLayout drawerLayout;
    DrawerLayout profileDrawerLayout, reportDrawerLayout;

    RecyclerView recyclerView;
    ListAdapter invoiceAdaptor;
    ArrayList<ListData> dataArrayList = new ArrayList<>();
    ListData listData;

    // Elements
    private ImageView dashboard_invoice_search;
    private ImageView profile_slider_back, my_profile_slider_back, my_report_slider_back;

    boolean mIsButtonShowing = false;
    float mButtonWidth;

    Drawable mDeleteIcon, mEditIcon, mPdfIcon;

    AutoCompleteTextView india_all_state_autocomplete_view;
    MaterialButton my_profile_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dataArrayList.add(new ListData("Arun kumar : 12345678", "11 Jan", 400));
        dataArrayList.add(new ListData("Archit Arrora : 123456789", "11 Jan", 400));
        dataArrayList.add(new ListData("ABC : 123456711", "12 Jan", 200));
        dataArrayList.add(new ListData("XYZ : 123456712", "13 Jan", 800));

        recyclerView = findViewById(R.id.recycler_view);
        invoiceAdaptor = new ListAdapter(dataArrayList, this);
        recyclerView.setAdapter(invoiceAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        phone = sharedpreferences.getString("Phone_key", null);

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

        bottomNavigationBar.setOnItemSelectedListener(item -> {
//            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            if(item.getTitle().equals("Menu")) {
                drawerLayout.setElevation(10);
                drawerLayout.open();

                // Profile back button
                profile_slider_back = findViewById(R.id.profile_slider_back);
                profile_slider_back.setOnClickListener(e -> {
                    drawerLayout.close();
                    drawerLayout.setElevation(0);
                });
            }
            if(item.getTitle().equals("Add Invoices")) {
                // <First_address, Second_address, City, State, Pin-code(6 digit)>
                String[] billing_address = {"", "", "", "", ""};
                String[] shipping_address = {"", "", "", "", ""};
                Intent add_invoice = new Intent(getApplicationContext(), InvoicePage.class);

                // Generate Random Invoice Number
                long invoice_number = (long)(Math.random() * 9_000_000_000L) + 1_000_000_000L;

                add_invoice.putExtra("INVOICE_SERIAL_NO", invoice_number + "");
                add_invoice.putExtra("INVOICE_TO", "");
                add_invoice.putExtra("CUSTOMER_TYPE", "");
                add_invoice.putExtra("AADHAAR_NUMBER", "");
                add_invoice.putExtra("BILLING_ADDRESS", billing_address);
                add_invoice.putExtra("SHIPPING_ADDRESS", shipping_address);
                add_invoice.putExtra("BOTH_ADDRESS_IS_SAME", Arrays.equals(billing_address, shipping_address));
                add_invoice.putExtra("MOBILE_NUMBER", "+91 8821000532");
                startActivity(add_invoice);
            }
            if(item.getTitle().equals("My Profile")) {
                profileDrawerLayout.setElevation(10);
                profileDrawerLayout.open();

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
}