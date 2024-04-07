package com.example.archit_art_gallery;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class PdfGenerator {

    public static final String TAG = "Invoice PDF GENERATOR";

    public static void generatePDF(JSONObject content, String fileName, Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.pdf_activity, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.getDisplay().getRealMetrics(displayMetrics);
        }

        RecyclerView recyclerView;
        ItemAdapter itemAdaptor;
        TextView biller_name;
        ArrayList<ItemsData> itemsDB = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_view);
        itemAdaptor = new ItemAdapter(itemsDB, context);
        recyclerView.setAdapter(itemAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        biller_name = view.findViewById(R.id.biller_name);
        try {
            String invoice_id = content.getString("invoice_id");
        }catch (JSONException e) {
            Toast.makeText(context.getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec((displayMetrics.heightPixels), View.MeasureSpec.EXACTLY));
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        PdfDocument document = new PdfDocument();

        int viewWidth = 1080;
        int viewHeight = 1920;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        view.draw(canvas);

        document.finishPage(page);

        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String file_name = "test.pdf";
        File file = new File(downloadDir, file_name);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(context.getApplicationContext(), "Bill Created!", Toast.LENGTH_SHORT).show();
        }catch (IOException e) {
            Toast.makeText(context.getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
