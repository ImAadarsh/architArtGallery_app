package in.architartgallery.archit_art_gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;

public class ItemizedSale extends AppCompatActivity {

    private ImageView back_button, purchase_sale_config, cross_dialog;
    BottomSheetDialog sheetDialog;

    RangeSlider rangeSlider;

    RecyclerView purchase_recycler_view;
    ArrayList<ListData> dataArrayList = new ArrayList<>();
    ListAdapter invoiceAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemized_sale);

        back_button = findViewById(R.id.back_button);
        purchase_sale_config = findViewById(R.id.purchase_sale_config);

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

        dataArrayList.add(new ListData("Arun kumar : 12345678", "11 Jan", 400));
        dataArrayList.add(new ListData("Archit Arrora : 123456789", "11 Jan", 400));
        dataArrayList.add(new ListData("ABC : 123456711", "12 Jan", 200));
        dataArrayList.add(new ListData("XYZ : 123456712", "13 Jan", 800));

        purchase_recycler_view = findViewById(R.id.purchase_recycler_view);
        invoiceAdaptor = new ListAdapter(dataArrayList, this);
        purchase_recycler_view.setAdapter(invoiceAdaptor);
        purchase_recycler_view.setLayoutManager(new LinearLayoutManager(this));
    }
}