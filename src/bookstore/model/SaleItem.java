package bookstore.model;

public class SaleItem {
    private int saleItemId;
    private int saleId;
    private int bookId;
    private int quantity;
    private double price;

    public SaleItem() {
    }

    public SaleItem(int bookId, int quantity, double price) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.price = price;
    }

    public SaleItem(int saleItemId, int saleId, int bookId, int quantity, double price) {
        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getSaleItemId() {
        return saleItemId;
    }

    public void setSaleItemId(int saleItemId) {
        this.saleItemId = saleItemId;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}