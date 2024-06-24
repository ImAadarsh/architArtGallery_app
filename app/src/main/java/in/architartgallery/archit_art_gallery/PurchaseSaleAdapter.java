package in.architartgallery.archit_art_gallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class PurchaseSaleAdapter extends RecyclerView.Adapter<PurchaseSaleAdapter.PurchaseSaleHolder> {

    List<PurchaseSaleData> purchaseSaleDataList;
    Context context;

    public PurchaseSaleAdapter(List<PurchaseSaleData> purchaseSaleDataList, Context context) {
        this.purchaseSaleDataList = purchaseSaleDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public PurchaseSaleAdapter.PurchaseSaleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expenses_row, parent, false);
        PurchaseSaleAdapter.PurchaseSaleHolder expenseViewHolder = new PurchaseSaleAdapter.PurchaseSaleHolder(view);
        return expenseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseSaleAdapter.PurchaseSaleHolder holder, int position) {
        PurchaseSaleData purchaseData = purchaseSaleDataList.get(position);
        holder.name.setText(purchaseData.getName());
        holder.date.setText(purchaseData.getDate());
        holder.amount.setText("₹" + purchaseData.getAmount());
    }

    @Override
    public int getItemCount() {
        return purchaseSaleDataList.size();
    }

    public class PurchaseSaleHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView date;
        TextView amount;
        public PurchaseSaleHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.expense_name);
            date = itemView.findViewById(R.id.expense_date_time);
            amount = itemView.findViewById(R.id.expense_amount);

            itemView.setOnClickListener(e -> {
                int pos = getAdapterPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // set title
                builder.setTitle("Choose One of them?");
                // set message
                builder.setMessage("- View\n- Ok");
                // set two buttons.
                builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                    //
                });
//                builder.setNegativeButton("Delete", (dialogInterface, i) -> {
////                    req(new HashMap<>(), "/api/deleteExpense?id=" + expenseDataList.get(pos).getId(), "Expense deleted successfully.", "item_delete", "GET", pos);
//                });
                builder.setNeutralButton("View", (inf, i) -> {
//                    Log.d("T", expenseDataList.get(pos).file_Url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(purchaseSaleDataList.get(pos).file_url));
                    e.getContext().startActivity(intent);
                });
                // show the alert.
                builder.show();
            });
        }
    }
}
