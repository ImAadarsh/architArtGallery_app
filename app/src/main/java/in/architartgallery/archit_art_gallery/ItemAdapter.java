package in.architartgallery.archit_art_gallery;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    List<ItemsData> itemsList;
    Context context;

    final static String base_url = "https://architartgallery.in/public";
    BottomSheetDialog sheetDialog;
    TextInputEditText my_item_name, has_code, rate, quantity;
    MaterialButton item_add_button;
    SwitchMaterial is_gst_product;
    ImageView cross_dialog;
    JSONObject Invoice_Old_Date;
    private double total_ex_gst_amount = 0;
    double dgst = 0, igst = 0, cgst = 0;

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
        double total_amount = itemsData.getCOLUMN_QUANTITY() * itemsData.getCOLUMN_RATE();
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

            itemView.setOnClickListener(e -> {
                int pos = getAdapterPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // set title
                builder.setTitle("Choose One of them?");
                // set message
                builder.setMessage("- Ok(Keep Item)\n- Delete");
                // set two buttons.
                builder.setPositiveButton("Ok", (dialogInterface, i) -> {
//                    if(pos != RecyclerView.NO_POSITION) {
//                        sheetDialog = new BottomSheetDialog(itemView.getContext(), R.style.BottomSheetStyle);
//                        View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_add_dialog,
//                                (LinearLayout)e.findViewById(R.id.item_add_dialog));
//
//                        sheetDialog.setContentView(view);
//                        sheetDialog.show();
//
//                        my_item_name = view.findViewById(R.id.new_item_name);
//                        has_code = view.findViewById(R.id.new_item_has_code);
//                        rate = view.findViewById(R.id.new_item_rate);
//                        quantity = view.findViewById(R.id.new_item_quantity);
//                        item_add_button = view.findViewById(R.id.new_add_item_button);
//
//                        // is_gst button
//                        is_gst_product = view.findViewById(R.id.is_gst_product);
//
//                        my_item_name.setText(itemsList.get(pos).getCOLUMN_NAME());
//                        has_code.setText(itemsList.get(pos).getCOLUMN_HAS_CODE());
//                        rate.setText(itemsList.get(pos).getCOLUMN_RATE() + "");
//                        quantity.setText(itemsList.get(pos).getCOLUMN_QUANTITY() + "");
//                        is_gst_product.setChecked(itemsList.get(pos).getIS_GST() == 1);
//
//                        item_add_button.setOnClickListener(add -> {
//                            Map<String, String> reqData = new HashMap<>();
//                            reqData.put("item_id", itemsList.get(pos).getITEM_ID() + "");
//                            reqData.put("invoice_id", "1");
//                            reqData.put("is_completed", 1 + "");
//                            reqData.put("hsn_code", has_code.getText().toString());
//                            reqData.put("name", my_item_name.getText().toString());
//                            reqData.put("price", rate.getText().toString());
//                            reqData.put("quantity", quantity.getText().toString());
//                            reqData.put("is_gst", is_gst_product.isChecked() ? "1" : "0");
//                            Log.d("TEST@", reqData.toString());
//                            req(reqData, "/api/editProduct", "Item updated successfully.", "update_old_item", "POST");
//
//                            itemsList.get(pos).setCOLUMN_NAME(my_item_name.getText().toString());
//                            itemsList.get(pos).setCOLUMN_HAS_CODE(has_code.getText().toString());
//                            itemsList.get(pos).setCOLUMN_RATE(Integer.parseInt(rate.getText().toString()));
//                            itemsList.get(pos).setCOLUMN_QUANTITY(Integer.parseInt(quantity.getText().toString()));
//                            itemsList.get(pos).setCOLUMN_QUANTITY(is_gst_product.isChecked() ? 1 : 0);
//                            item_name.setText(my_item_name.getText().toString());
////                            item_total_amount.setText(itemsList.get(pos).getCOLUMN_RATE() * itemsList.get(pos).getCOLUMN_QUANTITY());
////                            item_rate.setText(itemsList.get(pos).getCOLUMN_RATE());
////                            item_qty.setText(itemsList.get(pos).getCOLUMN_QUANTITY());
//
//                            notifyDataSetChanged();
//
//                            sheetDialog.hide();
//                        });
//
//                        cross_dialog = view.findViewById(R.id.cross_dialog);
//                        cross_dialog.setOnClickListener(ev -> {
//                            sheetDialog.hide();
//                        });
//                    }
                });
                builder.setNegativeButton("Delete", (dialogInterface, i) -> {
                    Log.d("TE", itemsList.toString());
                    req(new HashMap<>(), "/api/item/delete/" + itemsList.get(pos).getITEM_ID(), "Item removed successfully.", "item_delete", "GET", pos);
                });
                // show the alert.
                builder.show();
            });
        }
    }

    void deleteItem(int pos, String response) {
        Toast.makeText(context, "Delete Item", Toast.LENGTH_SHORT).show();
        Log.d("TEST", response);

        if(response.length() >= 10) {
            try {
                Invoice_Old_Date = new JSONObject(response);
                try {
                    JSONObject dataArray = Invoice_Old_Date.getJSONObject("data");
                    total_ex_gst_amount = dataArray.getInt("total_amount");
                    itemsList.get(pos).total_ex_gst.setText("₹" + total_ex_gst_amount);
                    dgst = dataArray.getInt("total_dgst");
                    cgst = dataArray.getInt("total_cgst");
                    igst = dataArray.getInt("total_igst");
                    itemsList.get(pos).delhi_gst_cost.setText("₹" + String.format("%.2f", dgst));
                    itemsList.get(pos).cgst_gst_cost.setText("₹" + String.format("%.2f", cgst));
                    itemsList.get(pos).igst_gst_cost.setText("₹" + String.format("%.2f", igst));
                    itemsList.get(pos).total_with_gst.setText("₹" + String.format("%.2f", (total_ex_gst_amount + dgst + cgst + igst)));
                }catch (JSONException e) {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }

        itemsList.remove(pos);
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

                        if(message.equals(condition)) {
                            if(task.equals("item_delete")) {
                                String tempId = itemsList.get(pos).getINVOICE_ID();
                                if(response.length() > 10) {
                                    req(new HashMap<>(), "/api/getDetailedInvoice/" + tempId, "Invoice details retrieved successfully.", "fetch_old_item", "GET", pos);
                                }
                            }
                            if(task.equals("fetch_old_item")) {
                                Toast.makeText(context, "List updated!", Toast.LENGTH_SHORT).show();
                                deleteItem(pos, response);
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
