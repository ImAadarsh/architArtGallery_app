package in.architartgallery.archit_art_gallery;

import android.widget.TextView;

public class ItemsData {
    int ITEM_ID;
    String COLUMN_NAME;
    String COLUMN_HAS_CODE;
    int COLUMN_RATE;
    int COLUMN_QUANTITY;
    int IS_GST;

    TextView total_ex_gst, delhi_gst_cost, cgst_gst_cost, igst_gst_cost, total_with_gst;

    String INVOICE_ID;

    public ItemsData(int item_id, String name, String has, int rate, int qty, int is_gst, String invoice_id) {
        this.ITEM_ID = item_id;
        this.COLUMN_NAME = name;
        this.COLUMN_HAS_CODE = has;
        this.COLUMN_RATE = rate;
        this.COLUMN_QUANTITY = qty;
        this.IS_GST = is_gst;
        this.INVOICE_ID = invoice_id;
    }

    public ItemsData(int item_id, String name, String has, int rate, int qty, int is_gst, String invoice_id,
                     TextView total_ex_gst, TextView delhi_gst_cost, TextView cgst_gst_cost, TextView igst_gst_cost, TextView total_with_gst) {
        this.ITEM_ID = item_id;
        this.COLUMN_NAME = name;
        this.COLUMN_HAS_CODE = has;
        this.COLUMN_RATE = rate;
        this.COLUMN_QUANTITY = qty;
        this.IS_GST = is_gst;
        this.INVOICE_ID = invoice_id;
        this.total_ex_gst = total_ex_gst;
        this.delhi_gst_cost = delhi_gst_cost;
        this.cgst_gst_cost = cgst_gst_cost;
        this.igst_gst_cost = igst_gst_cost;
        this.total_with_gst = total_with_gst;
    }

    public ItemsData() {
        this.ITEM_ID = 0;
        this.COLUMN_NAME = "";
        this.COLUMN_HAS_CODE = "";
        this.COLUMN_RATE = 0;
        this.COLUMN_QUANTITY = 0;
        this.IS_GST = 1;
        this.INVOICE_ID = "0";
    }

    public String getCOLUMN_NAME() {
        return COLUMN_NAME;
    }

    public void setCOLUMN_NAME(String COLUMN_NAME) {
        this.COLUMN_NAME = COLUMN_NAME;
    }

    public String getCOLUMN_HAS_CODE() {
        return COLUMN_HAS_CODE;
    }

    public void setCOLUMN_HAS_CODE(String COLUMN_HAS_CODE) {
        this.COLUMN_HAS_CODE = COLUMN_HAS_CODE;
    }

    public int getCOLUMN_RATE() {
        return COLUMN_RATE;
    }

    public void setCOLUMN_RATE(int COLUMN_RATE) {
        this.COLUMN_RATE = COLUMN_RATE;
    }

    public int getCOLUMN_QUANTITY() {
        return COLUMN_QUANTITY;
    }

    public void setCOLUMN_QUANTITY(int COLUMN_QUANTITY) {
        this.COLUMN_QUANTITY = COLUMN_QUANTITY;
    }

    public int getITEM_ID() {
        return ITEM_ID;
    }

    public void setITEM_ID(int ITEM_ID) {
        this.ITEM_ID = ITEM_ID;
    }

    public int getIS_GST() {
        return IS_GST;
    }

    public void setIS_GST(int IS_GST) {
        this.IS_GST = IS_GST;
    }

    public String getINVOICE_ID() {
        return INVOICE_ID;
    }

    public void setINVOICE_ID(String INVOICE_ID) {
        this.INVOICE_ID = INVOICE_ID;
    }
}
