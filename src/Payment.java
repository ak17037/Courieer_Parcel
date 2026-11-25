import java.time.LocalDateTime;

/**
 * Payment entity (1 : 1 with Parcel).
 * Concepts: OOP, java.time, String formatting.
 */
public class Payment {
    private int receiptNo;
    private String waybillNo;
    private double amount;
    private String method;      // "Cash", "Card", "UPI", "Net Banking"
    private String status;      // "Pending", "Completed", "Failed"
    private LocalDateTime paymentDate;

    public Payment(int receiptNo, String waybillNo, double amount,
                   String method, String status) {
        this.receiptNo = receiptNo;
        this.waybillNo = waybillNo;
        this.amount = amount;
        this.method = method;
        this.status = status;
        if ("Completed".equals(status)) this.paymentDate = LocalDateTime.now();
    }

    public int getReceiptNo()              { return receiptNo; }
    public String getWaybillNo()           { return waybillNo; }
    public double getAmount()              { return amount; }
    public String getMethod()              { return method; }
    public String getStatus()              { return status; }
    public LocalDateTime getPaymentDate()  { return paymentDate; }

    public void setMethod(String m) { this.method = m; }
    public void setStatus(String s) {
        this.status = s;
        if ("Completed".equals(s)) this.paymentDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Receipt #" + receiptNo + " Rs " + String.format("%.2f", amount) + " [" + status + "]";
    }
}
