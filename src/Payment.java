import java.time.LocalDate;
import java.util.Objects;

public class Payment {
    private String paymentId;
    private String loanId;
    private double amountPaid;
    private LocalDate date;

    public Payment(String paymentId, String loanId, double amountPaid, LocalDate date) {
        this.paymentId = paymentId;
        this.loanId = loanId;
        this.amountPaid = amountPaid;
        this.date = date;
    }

    public String getPaymentId() { return paymentId; }
    public String getLoanId() { return loanId; }
    public double getAmountPaid() { return amountPaid; }
    public LocalDate getDate() { return date; }

    public String toCSV() { return String.join(",", paymentId, loanId, String.valueOf(amountPaid), date.toString()); }

    public static Payment fromCSV(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 4) return null;
        return new Payment(p[0], p[1], Double.parseDouble(p[2]), LocalDate.parse(p[3]));
    }

    @Override
    public String toString() {
        return String.format("%s | Loan:%s | %.2f | %s", paymentId, loanId, amountPaid, date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode() { return Objects.hash(paymentId); }
}
