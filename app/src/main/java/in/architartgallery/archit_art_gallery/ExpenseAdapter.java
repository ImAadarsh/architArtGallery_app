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


public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    List<ExpenseData> expenseDataList;
    Context context;

    final static String base_url = "https://architartgallery.in/public";

    public ExpenseAdapter(List<ExpenseData> expenseDataList, Context context) {
        this.expenseDataList = expenseDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expenses_row, parent, false);
        ExpenseAdapter.ExpenseViewHolder expenseViewHolder = new ExpenseAdapter.ExpenseViewHolder(view);
        return expenseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseData expenseData = expenseDataList.get(position);
        holder.name.setText(expenseData.getName() + (expenseData.getType().equals("1") ? " : Monthly" : " : Adhoc"));
        holder.date.setText(expenseData.getDate());
        holder.amount.setText("₹" + expenseData.getAmount());
    }

    @Override
    public int getItemCount() {
        return expenseDataList.size();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView date;
        TextView amount;

        public ExpenseViewHolder(@NonNull View itemView) {
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
                builder.setMessage("- Ok(Keep Item)\n- Delete");
                // set two buttons.
                builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                    //
                });
                builder.setNegativeButton("Delete", (dialogInterface, i) -> {
                    Log.d("TE", expenseDataList.toString());
                    req(new HashMap<>(), "/api/deleteExpense?id=" + expenseDataList.get(pos).getId(), "Expense deleted successfully.", "item_delete", "GET", pos);
                });
//                builder.setNeutralButton("View", (inf, i) -> {
//                    Log.d("T", base_url + expenseDataList.get(pos).file_Url);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(base_url + expenseDataList.get(pos).file_Url));
//                    e.getContext().startActivity(intent);
//                });
                // show the alert.
                builder.show();
            });
        }
    }

    void req(Map<String, String> data, String api_endpoint, String condition, String task, String method, int pos) {
        StringRequest stringRequest = new StringRequest(method.equals("GET") ? Request.Method.GET : Request.Method.POST, base_url + api_endpoint,
                response -> {
                    // Handle successful response
                    try {
                        // Convert response string to JSON object
                        JSONObject jsonResponse = new JSONObject(response);
                        // Now you can access data from the JSON object
                        String message = jsonResponse.getString("message");

                        if(message.equals(condition)) {
                            if(task.equals("item_delete")) {
                                expenseDataList.remove(pos);
                                notifyDataSetChanged();
                            }
                            if(task.equals("fetch_old_item")) {
//                                Toast.makeText(context, "List updated!", Toast.LENGTH_SHORT).show();
//                                deleteItem(pos, response);
                            }
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
