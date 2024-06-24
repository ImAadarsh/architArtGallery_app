package in.architartgallery.archit_art_gallery;

public class PurchaseSaleData {
    public String id, name, type, date, file_url;
    public double amount;

    public PurchaseSaleData(String id, String name, String type, String date, double amount) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.amount = amount;
    }

    public PurchaseSaleData(String id, String name, String type, String date, double amount, String file) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.file_url = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }
}
