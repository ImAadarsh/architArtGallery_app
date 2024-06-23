package in.architartgallery.archit_art_gallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.InvoiceViewHolder> {

    List<ListData> invoiceList;
    Context context;

    ImageView deleteButton;

    final static String base_url = "https://architartgallery.in/public";

    public ListAdapter(List<ListData> invoiceList, Context context) {
        this.invoiceList = invoiceList;
        this.context = context;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invoice_row, parent, false);
        InvoiceViewHolder invoiceViewHolder = new InvoiceViewHolder(view);
        return invoiceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        ListData invoice = invoiceList.get(position);
        holder.invoice_user_name.setText(invoice.getInvoice_user_name());
        holder.invoice_amount.setText("₹" + invoice.getInvoice_amount());
        holder.invoice_date.setText(invoice.getInvoice_data());
        if(invoice.getType().equals("performa")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.color_strip.setBackgroundColor(context.getColor(R.color.primary_color));
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.color_strip.setBackgroundColor(context.getColor(R.color.white));
            }
        }
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    class InvoiceViewHolder extends RecyclerView.ViewHolder {

        TextView invoice_user_name;
        TextView invoice_amount;
        TextView invoice_date;

        LinearLayout color_strip;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            invoice_user_name = itemView.findViewById(R.id.invoice_user_name);
            invoice_amount = itemView.findViewById(R.id.invoice_amount);
            invoice_date = itemView.findViewById(R.id.invoice_date);
            color_strip = itemView.findViewById(R.id.color_strip);

            itemView.setOnClickListener(e -> {
                int pos = getAdapterPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // set title
                builder.setTitle("Choose One of them?");
                // set message
                builder.setMessage("- View(Invoice PDF)\n- Delete\n- Edit(Modify)");
                // set two buttons.
                builder.setPositiveButton("View", (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://invoice.architartgallery.in/invoice.html?invoiceid=" + invoiceList.get(pos).invoice_id));
                    context.startActivity(intent);
                });
                builder.setNegativeButton("Delete", (dialogInterface, i) -> {
                    deleteItem(pos);
                });
                builder.setNeutralButton("Edit", (dialogInterface, i) -> {
                    if(pos != RecyclerView.NO_POSITION) {
                        Intent invoice_row_intent = new Intent(context, InvoicePage.class);
                        invoice_row_intent.putExtra("INVOICE_ID", invoiceList.get(pos).invoice_id);
                        invoice_row_intent.putExtra("INVOICE_SERIAL_NO", invoiceList.get(pos).invoice_sn);
                        invoice_row_intent.putExtra("INVOICE_TO", invoiceList.get(pos).invoice_user_name);
                        invoice_row_intent.putExtra("CUSTOMER_TYPE", invoiceList.get(pos).customer_type);
                        invoice_row_intent.putExtra("AADHAAR_NUMBER", invoiceList.get(pos).aadhaar_number);
                        invoice_row_intent.putExtra("BILLING_ADDRESS", invoiceList.get(pos).billing_address);
                        invoice_row_intent.putExtra("SHIPPING_ADDRESS", invoiceList.get(pos).shipping_address);
                        invoice_row_intent.putExtra("BOTH_ADDRESS_IS_SAME", invoiceList.get(pos).billing_address == invoiceList.get(pos).shipping_address);
                        invoice_row_intent.putExtra("TYPE", invoiceList.get(pos).getType());
                        invoice_row_intent.putExtra("MOBILE_NUMBER", invoiceList.get(pos).mobile_no);
                        context.startActivity(invoice_row_intent);
                    }
                });
                // show the alert.
                builder.show();
            });
        }
    }

    void deleteItem(int pos) {
        Toast.makeText(context, "Delete Item", Toast.LENGTH_SHORT).show();
        req(new HashMap<>(), "/api/invoice/delete/" + invoiceList.get(pos).invoice_id, "Invoice Deleted", "GET");
        invoiceList.remove(pos);
        notifyDataSetChanged();
    }

    void req(Map<String, String> data, String api_endpoint, String condition, String method) {
        StringRequest stringRequest = new StringRequest(method.equals("GET") ? Request.Method.GET : Request.Method.POST, base_url + api_endpoint,
                response -> {
                    // Handle successful response
                    try {
                        // Convert response string to JSON object
                        JSONObject jsonResponse = new JSONObject(response);
                        // Now you can access data from the JSON object
                        String message = jsonResponse.getString("message");

                        if(message.equals(condition)) {
                            // Delete
                        }else {
                            // Handle the data accordingly
                            Toast.makeText(context, "Invalid credential!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        // Handle JSON parsing error
                        Toast.makeText(context, "Error: API response error!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle error
                    Toast.makeText(context, "Check Internet Connection/Server Down!", Toast.LENGTH_LONG).show();
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
        Volley.newRequestQueue(context).add(stringRequest);
    }
}
