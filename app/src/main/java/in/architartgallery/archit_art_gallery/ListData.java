package in.architartgallery.archit_art_gallery;

public class ListData {

    String invoice_id, invoice_sn, invoice_user_name, invoice_data, type, customer_type, aadhaar_number, billing_address, shipping_address, mobile_no;
    int invoice_amount;

    public ListData(String invoice_id, String invoice_sn, String invoice_user_name, String invoice_data, int invoice_amount,
                    String type, String customer_type, String aadhaar_number, String billing_address, String shipping_address, String mobile_no) {
        this.invoice_id = invoice_id;
        this.invoice_sn = invoice_sn;
        this.invoice_user_name = invoice_user_name;
        this.invoice_data = invoice_data;
        this.invoice_amount = invoice_amount;
        this.type = "normal";
        this.customer_type = customer_type;
        this.aadhaar_number = aadhaar_number;
        this.billing_address = billing_address;
        this.shipping_address = shipping_address;
        this.mobile_no = mobile_no;
    }

    public ListData(String invoice_user_name, String invoice_data, int invoice_amount) {
        this.invoice_id = "";
        this.invoice_sn = "";
        this.invoice_user_name = invoice_user_name;
        this.invoice_data = invoice_data;
        this.invoice_amount = invoice_amount;
        this.type = "normal";
        this.customer_type = "";
        this.aadhaar_number = "";
        this.billing_address = "";
        this.shipping_address = "";
        this.mobile_no = "";
    }
    public ListData(String invoice_user_name, String invoice_data, int invoice_amount, String type) {
        this.invoice_id = "";
        this.invoice_sn = "";
        this.invoice_user_name = invoice_user_name;
        this.invoice_data = invoice_data;
        this.invoice_amount = invoice_amount;
        this.type = "normal";
        this.customer_type = "";
        this.aadhaar_number = "";
        this.billing_address = "";
        this.shipping_address = "";
        this.mobile_no = "";
    }

    public String getInvoice_user_name() {
        return invoice_user_name;
    }

    public void setInvoice_user_name(String invoice_user_name) {
        this.invoice_user_name = invoice_user_name;
    }

    public String getInvoice_data() {
        return invoice_data;
    }

    public void setInvoice_data(String invoice_data) {
        this.invoice_data = invoice_data;
    }

    public int getInvoice_amount() {
        return invoice_amount;
    }

    public void setInvoice_amount(int invoice_amount) {
        this.invoice_amount = invoice_amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustomer_type() {
        return customer_type;
    }

    public void setCustomer_type(String customer_type) {
        this.customer_type = customer_type;
    }

    public String getAadhaar_number() {
        return aadhaar_number;
    }

    public void setAadhaar_number(String aadhaar_number) {
        this.aadhaar_number = aadhaar_number;
    }

    public String getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(String billing_address) {
        this.billing_address = billing_address;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }
}
