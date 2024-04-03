package com.example.archit_art_gallery;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PdfGenerator {

    public static final String TAG = "Invoice PDF GENERATOR";

    public static void generatePDF(String content, String fileName, Context context) {
        // Create a new document
        Document document = new Document();

        try {
            File pdfFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Archit/" + fileName + ".pdf");
            pdfFile.mkdirs(); // Ensure the parent directory exists
            OutputStream outputStream = new FileOutputStream(pdfFile);

            // Create a PdfWriter instance
            PdfWriter.getInstance(document, outputStream);

            // Open the document
            document.open();

            // Add content to the document
            document.add(new Paragraph(content));

            // Close the document
            document.close();

            String filePath = pdfFile.getAbsolutePath();
            Log.d(TAG, "PDF generated successfully: " + filePath);

            Toast.makeText(context, "PDF generated successfully: " + filePath, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error generating PDF", e);
        }
    }
}
