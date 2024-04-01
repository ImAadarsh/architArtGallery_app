package com.example.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class SearchInvoiceActivity extends AppCompatActivity {

    private ImageView back_button;

    SharedPreferences sharedpreferences;
    String SHARED_PREFS = "INPUT_QUERY_SHARE";
    private static final String INPUT_QUERY_KEY = "Input_Query_Key01";
    String input_query;

    RecyclerView recyclerView;
    ListAdapter invoiceAdaptor;
    ArrayList<ListData> fullDataList = new ArrayList<>();
    ListData listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_invoice);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        input_query = sharedpreferences.getString(INPUT_QUERY_KEY, "");

        fullDataList.add(new ListData("Arun kumar : 12345678", "11 Jan", 400));
        fullDataList.add(new ListData("Archit Arrora : 123456789", "11 Jan", 400));
        fullDataList.add(new ListData("ABC : 123456711", "12 Jan", 200));
        fullDataList.add(new ListData("XYZ : 123456712", "13 Jan", 800));

        ArrayList<ListData> dataListAfterFilter = new ArrayList<>();

        for(ListData row: fullDataList) {
            if(row.invoice_user_name.contains(input_query)) {
                dataListAfterFilter.add(row);
            }
        }

        recyclerView = findViewById(R.id.recycler_view_search_invoice);
        invoiceAdaptor = new ListAdapter(dataListAfterFilter, this);
        recyclerView.setAdapter(invoiceAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(e -> {
            onBackPressed();
        });
    }
}