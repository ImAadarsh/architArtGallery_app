package in.architartgallery.archit_art_gallery;

import android.content.Context;
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
        holder.name.setText(expenseData.getName());
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
        }
    }

    void deleteItem(int pos, String response) {
//        Toast.makeText(context, "Delete Item", Toast.LENGTH_SHORT).show();
//        Log.d("TEST", response);
//
//        if(response.length() >= 10) {
//            try {
//                Invoice_Old_Date = new JSONObject(response);
//                try {
//                    JSONObject dataArray = Invoice_Old_Date.getJSONObject("data");
//                    total_ex_gst_amount = dataArray.getInt("total_amount");
//                    itemsList.get(pos).total_ex_gst.setText("₹" + total_ex_gst_amount);
//                    dgst = dataArray.getInt("total_dgst");
//                    cgst = dataArray.getInt("total_cgst");
//                    igst = dataArray.getInt("total_igst");
//                    itemsList.get(pos).delhi_gst_cost.setText("₹" + String.format("%.2f", dgst));
//                    itemsList.get(pos).cgst_gst_cost.setText("₹" + String.format("%.2f", cgst));
//                    itemsList.get(pos).igst_gst_cost.setText("₹" + String.format("%.2f", igst));
//                    itemsList.get(pos).total_with_gst.setText("₹" + String.format("%.2f", (total_ex_gst_amount + dgst + cgst + igst)));
//                }catch (JSONException e) {
//                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                }
//            } catch (JSONException e) {
//                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
//            }
//        }

        expenseDataList.remove(pos);
        notifyDataSetChanged();
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

//                        if(message.equals(condition)) {
//                            if(task.equals("item_delete")) {
//                                String tempId = itemsList.get(pos).getINVOICE_ID();
//                                if(response.length() > 10) {
//                                    req(new HashMap<>(), "/api/getDetailedInvoice/" + tempId, "Invoice details retrieved successfully.", "fetch_old_item", "GET", pos);
//                                }
//                            }
//                            if(task.equals("fetch_old_item")) {
//                                Toast.makeText(context, "List updated!", Toast.LENGTH_SHORT).show();
//                                deleteItem(pos, response);
//                            }
//                        }else {
//                            // Handle the data accordingly
//                            Toast.makeText(context, "Invalid credential!", Toast.LENGTH_SHORT).show();
//                        }
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
