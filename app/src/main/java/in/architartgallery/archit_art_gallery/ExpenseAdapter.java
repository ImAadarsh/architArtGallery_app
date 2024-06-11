package in.architartgallery.archit_art_gallery;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    List<ExpenseData> expenseDataList;
    Context context;

    BottomSheetDialog sheetDialog;
    Button file_upload_btn;
    private ImageView cross_dialog;
    MaterialButton new_add_item_button;
    String expense_type;
    String encodedImageString = "";
    final static String base_url = "https://architartgallery.in/public";

    private static final int REQUEST_CODE_FILE_PICKER = 2;

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
        holder.name.setText(expenseData.getName() + (expenseData.getType().equals("0") ? " : Monthly" : " : Adhoc"));
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
                builder.setMessage("- View\n- Delete\n- Ok");
                // set two buttons.
                builder.setPositiveButton("Ok", (dialogInterface, i) -> {

//                    sheetDialog = new BottomSheetDialog(context, R.style.BottomSheetStyle);
//                    View view = LayoutInflater.from(context).inflate(R.layout.add_expenses_dialog,
//                            (LinearLayout)itemView.findViewById(R.id.sheet));
//                    sheetDialog.setContentView(view);
//                    sheetDialog.show();
//
//                    cross_dialog = view.findViewById(R.id.cross_dialog);
//                    cross_dialog.setOnClickListener(ev -> {
//                        sheetDialog.hide();
//                    });
//
//                    new_add_item_button = view.findViewById(R.id.new_add_item_button);
//                    TextInputEditText new_item_name = view.findViewById(R.id.new_item_name);
//                    TextInputEditText new_item_quantity = view.findViewById(R.id.new_item_quantity);
//                    RadioGroup add_expense_type_input = view.findViewById(R.id.add_expense_type_input);
//
//                    new_item_name.setText(expenseDataList.get(pos).getName());
//                    new_item_quantity.setText(expenseDataList.get(pos).getAmount() + "");
//                    if(expenseDataList.get(pos).getType().equals("0"))
//                        add_expense_type_input.check(view.findViewById(R.id.monthly).getId());
//                    else
//                        add_expense_type_input.check(view.findViewById(R.id.adhoc).getId());
//
//                    add_expense_type_input.setOnCheckedChangeListener((radioGroup, k) -> {
//                        expense_type = String.valueOf(k - 1);
//                    });
//
//                    file_upload_btn = view.findViewById(R.id.file_upload_btn);
//                    file_upload_btn.setOnClickListener(select_file -> {
//                        Intent intent = new Intent(Intent.ACTION_PICK);
//                        intent.setType("image/*");
////                        startActivityForResult(intent, REQUEST_CODE_FILE_PICKER);
//                    });
//
//                    new_add_item_button.setOnClickListener(btn -> {
//                        Map<String, String> data = new HashMap<>();
//                        data.put("name", new_item_name.getText().toString());
//                        data.put("amount", new_item_quantity.getText().toString());
//                        data.put("type", expense_type);
//
//                        if(!(data.get("name").length() >= 1 && data.get("type").length() >= 1)) {
//                            Toast.makeText(context, "Provide data properly!", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        if(data.get("name").length() >= 1 && data.get("type").length() >= 1) {
//                            data.put("file", encodedImageString);
//                            data.put("extension", "jpeg");
//                            uploadDb(data);
//                        }else {
//                            Toast.makeText(context, "Type data properly!", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                });
                builder.setNegativeButton("Delete", (dialogInterface, i) -> {
                    Log.d("TE", expenseDataList.toString());
                    req(new HashMap<>(), "/api/deleteExpense?id=" + expenseDataList.get(pos).getId(), "Expense deleted successfully.", "item_delete", "GET", pos);
                });
                builder.setNeutralButton("View", (inf, i) -> {
                    Log.d("T", expenseDataList.get(pos).file_Url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(expenseDataList.get(pos).file_Url));
                    e.getContext().startActivity(intent);
                });
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
