package com.example.archit_art_gallery;

public class ListData {

    String invoice_user_name;
    String invoice_data;
    int invoice_amount;

    public ListData(String invoice_user_name, String invoice_data, int invoice_amount) {
        this.invoice_user_name = invoice_user_name;
        this.invoice_data = invoice_data;
        this.invoice_amount = invoice_amount;
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
}
