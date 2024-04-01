package com.example.archit_art_gallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    List<ItemsData> itemsList;
    Context context;

    public ItemAdapter(List<ItemsData> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        ItemAdapter.ItemViewHolder ItemViewHolder = new ItemAdapter.ItemViewHolder(view);
        return ItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemsData itemsData = itemsList.get(position);
        holder.item_name.setText(itemsData.getCOLUMN_NAME());
        int total_amount = itemsData.getCOLUMN_QUANTITY() * itemsData.getCOLUMN_RATE();
        holder.item_total_amount.setText("₹" + total_amount);
        holder.item_rate.setText("₹" + itemsData.getCOLUMN_RATE());
        holder.item_qty.setText("QTY " + itemsData.getCOLUMN_QUANTITY() + " x");
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView item_name;
        TextView item_total_amount;
        TextView item_rate;
        TextView item_qty;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.item_name);
            item_total_amount = itemView.findViewById(R.id.item_total_amount);
            item_rate = itemView.findViewById(R.id.item_rate);
            item_qty = itemView.findViewById(R.id.item_qty);

//            itemView.setOnClickListener(e -> {
//                int pos = getAdapterPosition();
//
//                if(pos != RecyclerView.NO_POSITION) {
//                    Intent invoice_row_intent = new Intent(context, InvoicePage.class);
//                    context.startActivity(invoice_row_intent);
//                }
//            });
        }
    }
}
