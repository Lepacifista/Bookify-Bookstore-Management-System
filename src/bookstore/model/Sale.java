package bookstore.model;
import java.time.LocalDateTime;

public class Sale {
    private int saleId;
    private int customerId;
    private LocalDateTime saleDate;
    private double total;

    public Sale() {
    }

    public Sale(int customerId, double total) {
        this.customerId = customerId;
        this.total = total;
    }

    public Sale(int saleId, int customerId, LocalDateTime saleDate, double total) {
        this.saleId = saleId;
        this.customerId = customerId;
        this.saleDate = saleDate;
        this.total = total;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}