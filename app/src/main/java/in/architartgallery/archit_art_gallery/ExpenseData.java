package in.architartgallery.archit_art_gallery;

public class ExpenseData {
    public String id, name, type, date;
    public double amount;

    public ExpenseData(String id, String name, String type, String date, double amount) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
