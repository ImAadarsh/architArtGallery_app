package com.example.archit_art_gallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.InvoiceViewHolder> {

    List<ListData> invoiceList;
    Context context;

    ImageView deleteButton;

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
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    class InvoiceViewHolder extends RecyclerView.ViewHolder {

        TextView invoice_user_name;
        TextView invoice_amount;
        TextView invoice_date;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            invoice_user_name = itemView.findViewById(R.id.invoice_user_name);
            invoice_amount = itemView.findViewById(R.id.invoice_amount);
            invoice_date = itemView.findViewById(R.id.invoice_date);


            String[] billing_address = {"Add1", "Add2", "City", "Madhya Pradesh", "472001"};
            String[] shipping_address = {"Add1", "Add2", "City", "Madhya Pradesh", "472001"};

            itemView.setOnClickListener(e -> {
                int pos = getAdapterPosition();

                if(pos != RecyclerView.NO_POSITION) {
                    Intent invoice_row_intent = new Intent(context, InvoicePage.class);
                    invoice_row_intent.putExtra("INVOICE_SERIAL_NO", invoice_user_name.getText().toString().split(" : ", 2)[1]);
                    invoice_row_intent.putExtra("INVOICE_TO", invoice_user_name.getText().toString().split(" : ", 2)[0]);
                    invoice_row_intent.putExtra("CUSTOMER_TYPE", "Hotel");
                    invoice_row_intent.putExtra("AADHAAR_NUMBER", "1234567893216");
                    invoice_row_intent.putExtra("BILLING_ADDRESS", billing_address);
                    invoice_row_intent.putExtra("SHIPPING_ADDRESS", shipping_address);
                    invoice_row_intent.putExtra("BOTH_ADDRESS_IS_SAME", Arrays.equals(billing_address, shipping_address));
                    invoice_row_intent.putExtra("MOBILE_NUMBER", "+91 8821000532");
                    context.startActivity(invoice_row_intent);
                }
            });
        }
    }

    void deleteItem(int pos) {

    }

}
